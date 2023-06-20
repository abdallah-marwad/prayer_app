package com.abdallah.prayerapp.data.source.networking

import com.abdallah.prayerapp.data.model.PrayerNetworkModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface PrayerApi {
    @GET("{year}/{month}")
    fun getPrayerTimes(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?,

    ): Call<PrayerNetworkModel?>?
}