package com.abdallah.prayerapp.data.model.quibla

import com.abdallah.prayerapp.data.model.quibla.Data

data class QuiblaModel(
    val code: Int,
    val data: Data,
    val status: String
)

data class Data(
    val direction: Double,
    val latitude: Double,
    val longitude: Double
)