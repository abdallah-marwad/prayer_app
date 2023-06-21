package com.abdallah.prayerapp.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.abdallah.prayerapp.data.model.PrayerNetworkModel
import com.abdallah.prayerapp.data.model.Timings
import com.abdallah.prayerapp.data.repository.PrayerFragmentRepository
import com.abdallah.prayerapp.utils.Constants
import com.abdallah.prayerapp.utils.location.LocationPermission
import com.abdallah.prayerapp.utils.common.SharedPreferencesApp
import com.abdallah.prayertimequran.common.BuildToast
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class PrayerFragmentViewModel(app : Application) : AndroidViewModel(app) {
    val repository = PrayerFragmentRepository(getApplication())
    var sharedPreferencesApp = SharedPreferencesApp(getApplication())
    private var longitude : Float? = null
    private var latitude : Float? = null
    val responseLiveData: MutableLiveData<Timings> = MutableLiveData()
    private val longAndLatStateFlow: MutableStateFlow<Boolean> =
        MutableStateFlow(false)


    fun getLocation( activity: Activity ,owner: LifecycleOwner){
        if (isSharedContainLocation()){
            Log.d("test" , "Location in shared")
            getLocationDataFromShared()
            viewModelScope.launch (Dispatchers.Main){
                longAndLatStateFlow.collect{
                    getPrayer(latitude!!, longitude!!)
                }

            }


        }else{
            Log.d("test" , "Location not in shared")
            LocationPermission().takeLocationPermission(activity)


        LocationPermission.locationLiveData.observe(owner) { map ->
            Log.d("test", "Location not in shared")
            longitude = map[Constants.LONGITUDE]
            latitude = map[Constants.LATITUDE]
            getPrayer(latitude!!, longitude!!)
        }
        }

    }

    private fun getLocationDataFromShared(){
        viewModelScope.launch (Dispatchers.IO){
            longitude = sharedPreferencesApp.getFloatFromShared(Constants.LONGITUDE , 0f)
            latitude = sharedPreferencesApp.getFloatFromShared(Constants.LATITUDE , 0f)
            longAndLatStateFlow.emit(true)
        }

    }
    private fun getPrayer(latitude : Float, longitude : Float){
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)+1
        repository.getPrayers(
            latitude,
            longitude,
            currentYear,
            currentMonth
        )!!.enqueue(object : Callback<PrayerNetworkModel?>{
            override fun onResponse(
                call: Call<PrayerNetworkModel?>,
                response: Response<PrayerNetworkModel?>
            ) {

                if(response.isSuccessful){
                    Log.d("Test" ,"Success Respnse with Fajr in : "+ response.body()!!.data[5].timings.Fajr)
                    responseLiveData.postValue(response.body()!!.data[5].timings)
                }else {
                    if(response.code() == 500){
                        BuildToast.showToast(getApplication(),"Error in the server with code : 500 ,try again" ,FancyToast.ERROR)
                    }
                    Log.d("Test", response.code().toString() + response.errorBody())
                }
            }
            override fun onFailure(call: Call<PrayerNetworkModel?>, t: Throwable) {
                BuildToast.showToast(getApplication(),t.message!! ,FancyToast.ERROR)
                Log.d("Test" ,t.message!!)
            }
        })
    }

    private fun isSharedContainLocation() =
         sharedPreferencesApp.preferences.contains(Constants.LATITUDE)

}