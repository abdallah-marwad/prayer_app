package com.abdallah.prayerapp.utils

class DateModifier {
     fun getChars(timeString: String, initialValue : Int =0, endValue : Int = 4) : String{
        var newTime= ""
        for (i  in initialValue..endValue){
            newTime+= timeString[i].toString()
        }
        return newTime
    }
}