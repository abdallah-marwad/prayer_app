package com.abdallah.prayerapp.utils

class Constants {
    companion object {
        const val BASE_URL = "https://api.aladhan.com/v1/calendar/"
        const val LOCATION_REQUEST_CODE = 1
        const val LONGITUDE = "longitude"
        const val LATITUDE = "latitude"
        const val ROOM_CONTAIN_DATA = "Room Contain Data"
        const val LOCATION = "location"
        const val DAY_VALUE_IN_MILLI = 86400000L

        // Prayer`s Name
        const val FAJR = "fajr"
        const val SUNRISE = "sunrise"
        const val DUHR = "duhr"
        const val ASR = "asr"
        const val MAGHREB = "maghreb"
        const val ISHA = "isha"
    }
}