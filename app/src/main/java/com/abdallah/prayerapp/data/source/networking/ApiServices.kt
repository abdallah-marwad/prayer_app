package com.abdallah.prayerapp.data.source.networking

import com.abdallah.prayerapp.data.model.prayer.PrayerNetworkModel
import com.abdallah.prayerapp.data.model.quibla.QuiblaModel
import com.abdallah.prayerapp.ui.viewmodel.quibla.QiblaViewModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiServices {
    @GET("{year}/{month}")
    fun getPrayerTimes(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?,

    ): Call<PrayerNetworkModel?>?

    @GET("{latitude}/{longitude}")
    fun getQiblaDirection(
        @Path("latitude") latitude: Double?,
        @Path("longitude") longitude: Double?,

    ): Call<QuiblaModel?>?
}