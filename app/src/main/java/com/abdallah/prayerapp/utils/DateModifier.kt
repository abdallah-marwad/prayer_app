package com.abdallah.prayerapp.utils

class DateModifier {
     fun getFirstFiveChars(timeString: String) : String{
        var newTime= ""
        for (i in 0..4){
            newTime+= timeString[i].toString()
        }
        return newTime
    }
}