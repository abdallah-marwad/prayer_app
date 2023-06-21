package com.abdallah.prayerapp.data.model

class ApiObjConverter {

    fun toPrayerTimesRoom(prayerNetworkModel: PrayerNetworkModel , index : Int) : PrayerTimesRoom{
        val longDate = prayerNetworkModel.data[index].date.timestamp.toLong() * 1000
        val stringDate = prayerNetworkModel.data[index].date.readable

        return PrayerTimesRoom(
            longDate = longDate,
            StringDate = stringDate,
            fajr = prayerNetworkModel.data[index].timings.Fajr,
            sunrise = prayerNetworkModel.data[index].timings.Sunrise,
            duhr = prayerNetworkModel.data[index].timings.Dhuhr,
            asr = prayerNetworkModel.data[index].timings.Asr,
            magreb = prayerNetworkModel.data[index].timings.Maghrib,
            isha = prayerNetworkModel.data[index].timings.Isha,
        )
    }
}