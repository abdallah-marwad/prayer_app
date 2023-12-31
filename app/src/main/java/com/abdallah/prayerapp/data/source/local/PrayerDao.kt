package com.abdallah.prayerapp.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.abdallah.prayerapp.data.model.prayer.PrayerTimesRoom

@Dao
interface PrayerDao {
    @Insert
    fun insertTimes(prayerTimesRoom: PrayerTimesRoom)
    @Insert
    fun insertTimes(prayerTimesRoom: List<PrayerTimesRoom>)

    @Query("DELETE FROM prayer_times WHERE id = :id")
    fun deleteTimes(id: Int)
    @Query("SELECT * From prayer_times Where id = :id")
    fun selectItemTime(id : Int) : LiveData<PrayerTimesRoom>

    @Query("SELECT * From prayer_times Where StringDate = :stringDate")
    fun selectItemTime(stringDate : String) : LiveData<PrayerTimesRoom>
    @Query("SELECT * From prayer_times Where StringDate = :stringDate")
    fun selectItemTimeNotLiveData(stringDate : String) : PrayerTimesRoom?

}