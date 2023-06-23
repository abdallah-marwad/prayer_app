package com.abdallah.prayerapp.data.model.prayer

import com.abdallah.prayerapp.utils.CalenderCustomTime

class ApiObjConverter {

    fun toPrayerTimesRoom(itemData: Data) : PrayerTimesRoom {
        val longDate = itemData.date.timestamp.toLong() * 1000
        val stringDate =itemData.date.readable

        return PrayerTimesRoom(
            longDate = longDate,
            StringDate = CalenderCustomTime().getCalenderWithCustomTime(longDate),
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