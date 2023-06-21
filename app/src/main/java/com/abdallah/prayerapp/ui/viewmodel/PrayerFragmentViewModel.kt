package com.abdallah.prayerapp.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.lifecycle.*
import com.abdallah.prayerapp.data.model.PrayerNetworkModel
import com.abdallah.prayerapp.data.model.PrayerTimesRoom
import com.abdallah.prayerapp.data.model.Timings
import com.abdallah.prayerapp.data.repository.PrayerFragmentRepository
import com.abdallah.prayerapp.model.ApiObjConverter
import com.abdallah.prayerapp.ui.fragment.PrayersFragment.Companion.progressVisibilityStateLiveData
import com.abdallah.prayerapp.utils.CalenderZeroTime
import com.abdallah.prayerapp.utils.Constants
import com.abdallah.prayerapp.utils.location.LocationPermission
import com.abdallah.prayerapp.utils.common.SharedPreferencesApp
import com.abdallah.prayerapp.utils.common.BuildToast
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class PrayerFragmentViewModel(app: Application) : AndroidViewModel(app) {
    val repository = PrayerFragmentRepository(getApplication())
    var sharedPreferencesApp = SharedPreferencesApp(getApplication())
    private var longitude: Float? = null
    private var latitude: Float? = null
    var prayerTimesLiveData: LiveData<PrayerTimesRoom> = MutableLiveData()
    private val longAndLatStateFlow: MutableStateFlow<Boolean> =
        MutableStateFlow(false)

    fun getLocationCall(activity: Activity, owner: LifecycleOwner) {
        if (!sharedPreferencesApp.preferences.contains(Constants.ROOM_CONTAIN_DATA)) {
            Log.d("Test", "Room dont have data will make api call")
            getLocation(activity, owner)
        } else {
            // get data from database
            Log.d("Test", "Room alredy have data and show from it")
            prayerTimesLiveData =
                repository.getTimingItem(CalenderZeroTime().getCalenderZeroTime(Date().time))
        }
    }

    private fun getLocation(activity: Activity, owner: LifecycleOwner) {
        if (isSharedContainLocation()) {
            Log.d("test", "Location in shared")
            getLocationDataFromShared()
            viewModelScope.launch(Dispatchers.Main) {
                longAndLatStateFlow.collect {
                    progressVisibilityStateLiveData.postValue(true)
                    getPrayerApi(latitude!!, longitude!!)
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
                getPrayerApi(latitude!!, longitude!!)
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

     fun getPrayerApi(latitude: Float, longitude: Float, nextMonth: Int = 0) {
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


                            prayerTimesLiveData =
                                repository.getTimingItem(CalenderZeroTime().getCalenderZeroTime(Date().time))


                        progressVisibilityStateLiveData.postValue(false)
                    }

                } else {
                    if (response.code() == 500) {
                        progressVisibilityStateLiveData.postValue(false)
                        BuildToast.showToast(
                            getApplication(),
                            "Error in the server with code : 500 ,try again",
                            FancyToast.ERROR
                        )
                        Log.d("Test", response.code().toString() + response.errorBody())
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


    private fun isSharedContainLocation() =
        sharedPreferencesApp.preferences.contains(Constants.LATITUDE)

}