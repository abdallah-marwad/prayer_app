package com.abdallah.prayerapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_times")
data class PrayerTimesRoom (
    @PrimaryKey(autoGenerate = true)
    var id : Int? =null,
    val longDate : Long?,
    val StringDate : String?,
    val readableDate : String?,
    val fajr : String?,
    val sunrise : String?,
    val duhr : String?,
    val asr : String?,
    val magreb : String?,
    val isha : String?,

        )