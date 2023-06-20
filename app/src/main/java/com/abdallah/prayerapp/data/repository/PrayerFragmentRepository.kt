package com.abdallah.prayerapp.data.repository

import com.abdallah.prayerapp.data.model.PrayerNetworkModel
import com.abdallah.prayerapp.data.source.networking.PrayerRetrofit
import com.abdallah.prayerapp.utils.common.MyApplication
import retrofit2.Call
import retrofit2.http.Query

class PrayerFragmentRepository(app: MyApplication) {

    fun getPrayers(
        latitude: Float?,
        longitude: Float?,
        month: Int,
        year: Int
    ): Call<PrayerNetworkModel?>? =
        PrayerRetrofit.getInstance().getPrayerTimes(month,year,latitude!!.toDouble(),longitude!!.toDouble())
}
