package com.abdallah.prayerapp.ui.viewmodel.quibla

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.abdallah.prayerapp.data.model.prayer.PrayerNetworkModel
import com.abdallah.prayerapp.data.model.prayer.PrayerTimesRoom
import com.abdallah.prayerapp.data.model.quibla.QuiblaModel
import com.abdallah.prayerapp.data.source.networking.RetrofitInstance
import com.abdallah.prayerapp.utils.CheckInternetConnectivity
import com.abdallah.prayerapp.utils.Constants
import com.abdallah.prayerapp.utils.common.BuildToast
import com.abdallah.prayerapp.utils.location.LocationPermission
import com.shashank.sony.fancytoastlib.FancyToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QiblaViewModel(application: Application) : AndroidViewModel(application) {

    var quiblaDirectionLiveData: MutableLiveData<QuiblaModel> = MutableLiveData()

    fun getLocation(activity: Activity, owner: LifecycleOwner) {
        LocationPermission().takeLocationPermission(activity , false)
        LocationPermission.locationLiveData.observe(owner) { map ->
            Log.d("test", "get location in qibla")
            if (CheckInternetConnectivity().hasInternetConnection(getApplication())) {
                getPrayerApi(map[Constants.LATITUDE]!!, map[Constants.LONGITUDE]!!)
            } else {
                BuildToast.showToast(
                    getApplication(),
                    "Please connect to internet .",
                    FancyToast.WARNING
                )
            }

        }
    }

    private fun getPrayerApi(
        latitude: Float,
        longitude: Float,
    ) {
        RetrofitInstance.getInstance(Constants.BASE_URL_Qibla).getQiblaDirection(latitude.toDouble(),longitude.toDouble())!!.enqueue(
            object : Callback<QuiblaModel?>{
                override fun onResponse(call: Call<QuiblaModel?>, response: Response<QuiblaModel?>) {
                    if (response.isSuccessful) {
                        quiblaDirectionLiveData.postValue(response.body())
                        Log.d("test" , "lat forom pai : ${response.body()!!.data.latitude}")
                    }
                    else{
                        Log.d("test" , "code of response in qibla : ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<QuiblaModel?>, t: Throwable) {
                    BuildToast.showToast(
                        getApplication(),
                        t.message!!,
                        FancyToast.WARNING
                    )
                    Log.d("test" , "${t.message}")

                }

            }
        )
    }
}