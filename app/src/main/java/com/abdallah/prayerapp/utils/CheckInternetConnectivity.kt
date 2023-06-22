package com.abdallah.prayerapp.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*

class CheckInternetConnectivity {

     fun hasInternetConnection(app :Application): Boolean {
        val connectivityManager = app.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capability = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capability.hasTransport(TRANSPORT_WIFI) -> true
            capability.hasTransport(TRANSPORT_CELLULAR) -> true
            capability.hasTransport(TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}