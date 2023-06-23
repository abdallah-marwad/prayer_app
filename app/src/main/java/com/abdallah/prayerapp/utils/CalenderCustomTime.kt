package com.abdallah.prayerapp.utils

import android.icu.util.Calendar
import java.util.*

class CalenderCustomTime(){

     fun getCalenderWithCustomTime(time : Long , hour : Int =0 , minute : Int =0) : String{
        val calender = Calendar.getInstance()
        calender.timeInMillis=time
        calender.set(Calendar.HOUR_OF_DAY , hour)
        calender.set(Calendar.MINUTE, minute)
        calender.set(Calendar.SECOND, 0)
        return Date(calender.timeInMillis).toString()
    }
    fun getCalenderWithCustomLongTime( hour : Int =0 , minute : Int =0) : Long{
        val calender = Calendar.getInstance()
        calender.set(Calendar.HOUR_OF_DAY , hour)
        calender.set(Calendar.MINUTE, minute)
        calender.set(Calendar.SECOND, 0)
        return calender.timeInMillis
    }
}