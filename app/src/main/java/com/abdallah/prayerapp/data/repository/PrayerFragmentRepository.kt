package com.abdallah.prayerapp.data.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.abdallah.prayerapp.data.model.PrayerNetworkModel
import com.abdallah.prayerapp.data.model.PrayerTimesRoom
import com.abdallah.prayerapp.data.source.local.RoomDB
import com.abdallah.prayerapp.data.source.networking.PrayerRetrofit
import com.abdallah.prayerapp.utils.common.MyApplication
import retrofit2.Call
import retrofit2.http.Query

class PrayerFragmentRepository(val app: Application) {
    private val roomInstance by lazy { RoomDB.getInstance(app) }

    // Api
    fun getPrayers(
        latitude: Float?,
        longitude: Float?,
        month: Int,
        year: Int
    ): Call<PrayerNetworkModel?>? =
        PrayerRetrofit.getInstance().getPrayerTimes(month,year,latitude!!.toDouble(),longitude!!.toDouble())


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
