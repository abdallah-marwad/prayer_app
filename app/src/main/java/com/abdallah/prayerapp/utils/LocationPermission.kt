package com.abdallah.prayerapp.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.abdallah.prayerapp.ui.activity.MainActivity
import com.abdallah.prayerapp.utils.common.SharedPreferencesApp
import com.abdallah.prayertimequran.common.BuildDialog
import com.abdallah.prayertimequran.common.BuildToast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.*

class LocationPermission {
    val job: Job = Job()
    val coroutineScope = CoroutineScope(Dispatchers.IO + job)
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    companion object{
        val locationLiveData: MutableLiveData<Map<String , Float>> = MutableLiveData()
    }


    fun takeLocationPermission(activity: Activity) {

        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                Log.d("test", "show dialog then permisiion")
                val dialogMessage =
                    "location permission must taken to determined prayer time "
                val title = "Location Permission"
                val positiveMessage = "ok"
                val negativeMessage = "cancel"
                BuildDialog(
                    activity, dialogMessage, title, positiveMessage, negativeMessage, {
                        showPermission(activity)
                    })
            } else {
                Log.d("test", "show permisiion without dialog")

                showPermission(activity)
            }

        } else {
            detectLocation(activity)
        }

    }

    private fun showPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            Constants.LOCATION_REQUEST_CODE
        )
    }


    @SuppressLint("MissingPermission")
    fun detectLocation(context: Activity) {
        val sharedInstance = SharedPreferencesApp.getInstance(context.application)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationProviderClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                    Log.d("test", "onCanceledRequested")
                    return CancellationTokenSource().token
                }
                override fun isCancellationRequested() : Boolean {
                    Log.d("test", "isCancellationRequested")
                    return false
                }
            }
        ).addOnFailureListener {
            Log.d("test", it.message!!)
        }
            .addOnSuccessListener { location: Location? ->
                if (location == null) {
                    BuildToast.showToast(
                        context,
                        "Please make sure that the location service is activated",
                        FancyToast.ERROR
                    )

                    Log.d("test", "Location is null LocationPermission.Class")

                } else {

                    coroutineScope.launch {
                        sharedInstance.writeInShared(Constants.LONGITUDE, location.longitude.toFloat())
                        sharedInstance.writeInShared(Constants.LATITUDE, location.latitude.toFloat())
                        locationLiveData.postValue(
                            mapOf(
                                Constants.LONGITUDE to location.longitude.toFloat(),
                                Constants.LATITUDE to location.latitude.toFloat()
                                )
                        )
                        Log.d("test" , "Location Taken Successfully long: ${location.longitude}")

                        withContext(Dispatchers.Main) {
                            BuildToast.showToast(
                                context,
                                "Location Taken Successfully",
                                FancyToast.SUCCESS
                            )
                        }

                    }
                }
            }

    }


}


