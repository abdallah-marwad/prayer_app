package com.abdallah.prayerapp.utils.location

import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.abdallah.prayerapp.utils.Constants
import com.abdallah.prayerapp.utils.common.SharedPreferencesApp
import com.abdallah.prayerapp.utils.common.BuildToast
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class LocationCityDetector {
     fun setAddress(
        myLat: Double,
        myLong: Double,
        coroutineScope: CoroutineScope,
        sharedPreferencesApp: SharedPreferencesApp,
        context : Context
    ) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(myLat, myLong, 1)
                if (addresses!!.isNotEmpty()) {
                    var governorateName = addresses[0].adminArea
                    val areaName = addresses[0].subAdminArea
                    val countryName = addresses[0].countryName
                    var streetName = addresses[0].thoroughfare
                    if(governorateName == null){
                        governorateName = areaName
                    }
                    if(streetName == null){
                        streetName =""
                    }else{
                        streetName+=" st"
                    }
                    val address = " $governorateName,$countryName,$streetName"
                    Log.d("test", "location is : $address")
                    LocationPermission.locationAddressLiveData.postValue(address)
                    sharedPreferencesApp.writeInShared(Constants.LOCATION, " $address")
                }
            } catch (e: Exception) {
                Log.d("test","error in Location Detector"+ e.message!!)
                coroutineScope.launch (Dispatchers.Main) {
                    BuildToast.showToast(
                        context,
                        e.message!!,
                        FancyToast.ERROR
                    )
                }

            }
        }

    }