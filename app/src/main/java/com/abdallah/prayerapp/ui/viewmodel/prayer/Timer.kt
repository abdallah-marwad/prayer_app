package com.abdallah.prayerapp.ui.viewmodel.prayer

import android.os.Build.VERSION_CODES.P
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.abdallah.prayerapp.ui.fragment.PrayersFragment
import com.abdallah.prayerapp.ui.fragment.PrayersFragment.Companion.remainingTime
import com.abdallah.prayerapp.utils.CalenderCustomTime
import com.abdallah.prayerapp.utils.Constants
import com.abdallah.prayerapp.utils.DateModifier
import java.util.*

class Timer(mapOfStrings : MutableMap<String,String> ){
    private var dateModifier = DateModifier()
    private val calenderCustomTime = CalenderCustomTime()
    private var mapOfPrayersLongTimes: MutableMap<String, Long> = mutableMapOf()
    private var prayersInLongCallBack: MutableLiveData<Map<String, Long>> = MutableLiveData()
    init {
        handleTodayPrayerLongTime(mapOfStrings )
    }



    private fun todayPrayerLongTime(mapOfStrings : MutableMap<String,String>, prayerName : String){
        val hours =dateModifier.getChars(mapOfStrings[prayerName]!! , 0,1).toInt()
        val minutes=dateModifier.getChars(mapOfStrings[prayerName]!! , 3,4).toInt()
        mapOfPrayersLongTimes[prayerName] = calenderCustomTime.getCalenderWithCustomLongTime( hours,minutes)
        Log.d("test" , "$prayerName at $hours:$minutes")
    }

    private fun handleTodayPrayerLongTime(mapOfStrings : MutableMap<String,String> ){
        todayPrayerLongTime(mapOfStrings,Constants.FAJR)
        todayPrayerLongTime(mapOfStrings,Constants.SUNRISE)
        todayPrayerLongTime(mapOfStrings,Constants.DUHR)
        todayPrayerLongTime(mapOfStrings,Constants.ASR)
        todayPrayerLongTime(mapOfStrings,Constants.MAGHREB)
        todayPrayerLongTime(mapOfStrings,Constants.ISHA)
        prayersInLongCallBack.postValue(mapOfPrayersLongTimes)
    }

    fun checkTheRangeOfPrayerTimes(lifecycleOwner: LifecycleOwner) {
        prayersInLongCallBack.observe(lifecycleOwner){prayerTimes->
            val date = Date().time
            var dateDifference = -1L
            var PrayerName = ""
            if (prayerTimes[Constants.FAJR]!! < date && date < prayerTimes[Constants.SUNRISE]!!) {
                Log.d("test", "in 1 Range")
                dateDifference = prayerTimes[Constants.SUNRISE]!! - date
                PrayerName = "Sunrise"
            } else if (prayerTimes[Constants.SUNRISE]!! < date && date < prayerTimes[Constants.DUHR]!!) {
                Log.d("test", "in 2 Range")
                dateDifference = prayerTimes[Constants.DUHR]!! - date
                PrayerName = "Duhr"
            } else if (prayerTimes[Constants.DUHR]!! < date && date < prayerTimes[Constants.ASR]!!) {
                Log.d("test", "in 3 Range")
                dateDifference = prayerTimes[Constants.ASR]!! - date
                PrayerName = "Asr"
            } else if (prayerTimes[Constants.ASR]!! < date && date < prayerTimes[Constants.MAGHREB]!!) {
                Log.d("test", "in 4 Range")
                dateDifference = prayerTimes[Constants.MAGHREB]!! - date
                PrayerName = "Magreb"
            } else if (prayerTimes[Constants.MAGHREB]!! < date && date < prayerTimes[Constants.ISHA]!! ) {
                Log.d("test", "in 5 Range")
                dateDifference = prayerTimes[Constants.ISHA]!! - date
                PrayerName = "Isha"
            }
            // before 12 AM
            else if ((prayerTimes[Constants.ISHA]!!) < date && date < prayerTimes[Constants.FAJR]!!+86400000) {
                Log.d("test", "in 6 Range")
                dateDifference = date -(prayerTimes[Constants.FAJR]!!+86400000)
                PrayerName = "Fajr"
            }
            //after 12 AM
            else if ((prayerTimes[Constants.ISHA]!!-86400000) < date && date < prayerTimes[Constants.FAJR]!!) {
                Log.d("test", "in 6 Range")
                dateDifference = date -prayerTimes[Constants.FAJR]!!
                PrayerName = "Fajr"
            }else{
                Log.d("test", "didnt found range")

            }

            if (dateDifference < 0) {
                dateDifference *= -1
            }


            PrayersFragment.remainingTime.value = mapOf(PrayerName to dateDifference)

        }
    }

        }