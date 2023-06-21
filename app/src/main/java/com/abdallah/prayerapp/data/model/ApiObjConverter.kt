package com.abdallah.prayerapp.model

import android.icu.util.Calendar
import com.abdallah.prayerapp.data.model.Data
import com.abdallah.prayerapp.data.model.PrayerTimesRoom
import com.abdallah.prayerapp.utils.CalenderZeroTime
import java.util.*

class ApiObjConverter {

    fun toPrayerTimesRoom(itemData: Data) : PrayerTimesRoom {
        val longDate = itemData.date.timestamp.toLong() * 1000
        val stringDate =itemData.date.readable

        return PrayerTimesRoom(
            longDate = longDate,
            StringDate = CalenderZeroTime().getCalenderZeroTime(longDate),
            readableDate = stringDate,
            fajr = itemData.timings.Fajr,
            sunrise = itemData.timings.Sunrise,
            duhr = itemData.timings.Dhuhr,
            asr = itemData.timings.Asr,
            magreb = itemData.timings.Maghrib,
            isha = itemData.timings.Isha,
        )
    }

}