package com.abdallah.prayerapp.data.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.abdallah.prayerapp.data.model.prayer.PrayerNetworkModel
import com.abdallah.prayerapp.data.model.prayer.PrayerTimesRoom
import com.abdallah.prayerapp.data.source.local.RoomDB
import com.abdallah.prayerapp.data.source.networking.RetrofitInstance
import com.abdallah.prayerapp.utils.Constants
import retrofit2.Call

class PrayerFragmentRepository(val app: Application) {
    private val roomInstance by lazy { RoomDB.getInstance(app) }

    // Api
    fun getPrayers(
        latitude: Float?,
        longitude: Float?,
        month: Int,
        year: Int
    ): Call<PrayerNetworkModel?>? =
        RetrofitInstance.getInstance(Constants.BASE_URL).getPrayerTimes(month,year,latitude!!.toDouble(),longitude!!.toDouble())


    // Room
    fun insertTime(timingList : List<PrayerTimesRoom>){
        roomInstance!!.prayersDao()!!.insertTimes(timingList)
    }
    fun getTimingItem(stringDate : String): LiveData<PrayerTimesRoom> {
        Log.d("test" , "Repository class call selectItemTime fun from Dao")
        return roomInstance!!.prayersDao()!!.selectItemTime(stringDate)

    }
fun selectItemTimeNotLiveData(stringDate : String): PrayerTimesRoom? {
        return roomInstance!!.prayersDao()!!.selectItemTimeNotLiveData(stringDate)

    }

}
