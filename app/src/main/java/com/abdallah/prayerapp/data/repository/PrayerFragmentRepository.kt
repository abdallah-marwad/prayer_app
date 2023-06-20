package com.abdallah.prayerapp.data.repository

import com.abdallah.prayerapp.data.model.PrayerNetworkModel
import com.abdallah.prayerapp.data.source.networking.PrayerRetrofit
import com.abdallah.prayerapp.utils.common.MyApplication
import retrofit2.Call
import retrofit2.http.Query

class PrayerFragmentRepository(app: MyApplication) {

    fun getPrayers(
        latitude: Double?,
        longitude: Double?,
        month: Int,
        year: Int
    ): Call<PrayerNetworkModel?>? =
        PrayerRetrofit.getInstance().getPrayerTimes(latitude,longitude,month,year)
}
