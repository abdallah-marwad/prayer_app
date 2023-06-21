package com.abdallah.prayerapp.utils

import android.icu.util.Calendar
import java.util.*

class CalenderZeroTime(){

     fun getCalenderZeroTime(time : Long) : String{
        val calender = Calendar.getInstance()
        calender.timeInMillis=time
        calender.set(Calendar.HOUR_OF_DAY , 0)
        calender.set(Calendar.MINUTE, 0)
        calender.set(Calendar.SECOND, 0)
        return Date(calender.timeInMillis).toString()
    }
}