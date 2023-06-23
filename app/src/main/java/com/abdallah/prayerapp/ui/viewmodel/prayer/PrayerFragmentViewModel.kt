package com.abdallah.prayerapp.ui.viewmodel.prayer

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.abdallah.prayerapp.data.model.prayer.PrayerNetworkModel
import com.abdallah.prayerapp.data.model.prayer.PrayerTimesRoom
import com.abdallah.prayerapp.data.repository.PrayerFragmentRepository
import com.abdallah.prayerapp.data.model.prayer.ApiObjConverter
import com.abdallah.prayerapp.ui.fragment.PrayersFragment.Companion.progressVisibilityStateLiveData
import com.abdallah.prayerapp.utils.CalenderCustomTime
import com.abdallah.prayerapp.utils.CheckInternetConnectivity
import com.abdallah.prayerapp.utils.Constants
import com.abdallah.prayerapp.utils.location.LocationPermission
import com.abdallah.prayerapp.utils.common.SharedPreferencesApp
import com.abdallah.prayerapp.utils.common.BuildToast
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class PrayerFragmentViewModel(app: Application) : AndroidViewModel(app) {
    val repository = PrayerFragmentRepository(getApplication())
    var sharedPreferencesApp = SharedPreferencesApp(getApplication())
    var currentItem: Int = 0
    var isArraysClickable = false
    var longitude: Float? = null
    var latitude: Float? = null
    var prayerTimesLiveData: MutableLiveData<PrayerTimesRoom> = MutableLiveData()
    private val longAndLatStateFlow: MutableStateFlow<Boolean> =
        MutableStateFlow(false)


//    fun handleTimer(map : MutableMap<String,String> , lifecycleOwner: LifecycleOwner){
//        Timer(map).checkTheRangeOfPrayerTimes(lifecycleOwner)
//    }
    fun getLocationCall(activity: Activity, owner: LifecycleOwner) {
        if (!sharedPreferencesApp.preferences.contains(Constants.ROOM_CONTAIN_DATA)) {
            Log.d("Test", "Room dont have data will make api call")
            if (CheckInternetConnectivity().hasInternetConnection(getApplication())) {
                getLocation(activity, owner)
            } else {
                BuildToast.showToast(
                    getApplication(),
                    "Please connect to internet then (swipe to refresh).",
                    FancyToast.WARNING
                )
            }

        } else {
            // get data from database but if not found the date make new api call
            Log.d("Test", "Room alredy have data and show from it")
            viewModelScope.launch(Dispatchers.IO) {

                val prayerTimes = selectItemTimeNotLiveData(Date().time)
                if (prayerTimes != null) {
                    prayerTimesLiveData.postValue(prayerTimes!!)
                } else {
                    // Api Chain validation failed when make it
                    getLocation(activity , owner)
                }
            }

        }
    }

    private fun getLocation(activity: Activity, owner: LifecycleOwner) {
        if (isSharedContainLocation()) {
            Log.d("test", "Location in shared")
            getLocationDataFromShared()
            viewModelScope.launch(Dispatchers.Main) {
                longAndLatStateFlow.collect {
                    progressVisibilityStateLiveData.postValue(true)
                    getPrayerApi(
                        latitude!!,
                        longitude!!,
                        dayOfMonth = dayOfMonth()
                    )
                }
            }
        } else {
            Log.d("test", "Location not in shared")
            LocationPermission().takeLocationPermission(activity)


            LocationPermission.locationLiveData.observe(owner) { map ->
                Log.d("test", "Location not in shared")
                longitude = map[Constants.LONGITUDE]
                latitude = map[Constants.LATITUDE]
                progressVisibilityStateLiveData.postValue(true)
                getPrayerApi(latitude!!, longitude!!, dayOfMonth = dayOfMonth())
            }
        }
    }

    private fun getLocationDataFromShared() {
        viewModelScope.launch(Dispatchers.IO) {
            longitude = sharedPreferencesApp.getFloatFromShared(Constants.LONGITUDE, 0f)
            latitude = sharedPreferencesApp.getFloatFromShared(Constants.LATITUDE, 0f)
            longAndLatStateFlow.emit(true)
        }

    }

    fun getPrayerApi(
        latitude: Float,
        longitude: Float,
        nextMonth: Int = 0,
        dayOfMonth: Int
    ) {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1 + nextMonth
        repository.getPrayers(
            latitude,
            longitude,
            currentYear,
            currentMonth
        )!!.enqueue(object : Callback<PrayerNetworkModel?> {
            override fun onResponse(
                call: Call<PrayerNetworkModel?>,
                response: Response<PrayerNetworkModel?>
            ) {

                if (response.isSuccessful) {
                    Log.d(
                        "Test",
                        "Success Respnse with Fajr in : " + response.body()!!.data[5].timings.Fajr
                    )
                    val prayerTimesRoomList = ArrayList<PrayerTimesRoom>()
                    val converter = ApiObjConverter()
                    viewModelScope.launch(Dispatchers.IO) {
                        response.body()!!.data.forEach {
                            prayerTimesRoomList.add(converter.toPrayerTimesRoom(it))
                        }
                        repository.insertTime(prayerTimesRoomList)
                        Log.d("Test", "Data inserted to room")
                        sharedPreferencesApp.writeInShared(Constants.ROOM_CONTAIN_DATA, true)
                        progressVisibilityStateLiveData.postValue(false)
                        prayerTimesLiveData.postValue(
                            converter.toPrayerTimesRoom(response.body()!!.data[dayOfMonth - 1]))


                    }

                } else {
                    if (response.code() == 500) {
                        progressVisibilityStateLiveData.postValue(false)
                        BuildToast.showToast(
                            getApplication(),
                            "Error in the server with code : 500 ,try again",
                            FancyToast.ERROR
                        )
                        Log.d("Test", response.code().toString())
                    }
                }
            }

            override fun onFailure(call: Call<PrayerNetworkModel?>, t: Throwable) {
                progressVisibilityStateLiveData.postValue(false)
                BuildToast.showToast(getApplication(), t.message!!, FancyToast.ERROR)
                Log.d("Test", "onFailure Api" + t.message!!)
            }
        })
    }

    private fun dayOfMonth() = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    private fun isSharedContainLocation() =
        sharedPreferencesApp.preferences.contains(Constants.LATITUDE)

    fun selectItemTimeNotLiveData(time: Long) =
        repository.selectItemTimeNotLiveData(CalenderCustomTime().getCalenderWithCustomTime(time))

}


