package com.abdallah.prayerapp.data.source.networking

import com.abdallah.prayerapp.data.model.PrayerNetworkModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface PrayerApi {
    @GET("calendar")
    fun getPrayerTimes(
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?,
        @Query("month") month: Int,
        @Query("year") year: Int
    ): Call<PrayerNetworkModel?>?
}