package com.abdallah.prayerapp.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.abdallah.prayerapp.data.model.prayer.PrayerTimesRoom


@Database(entities = [PrayerTimesRoom::class ], version = 2)
abstract class RoomDB : RoomDatabase() {
    abstract fun prayersDao(): PrayerDao?

    companion object {
        private var instance: RoomDB? = null
        fun getInstance(context: Context): RoomDB? {
            if (instance == null) {
                synchronized(RoomDB::class.java) {
                    if (instance == null) {
                        try {
                            instance = Room.databaseBuilder(
                                context.applicationContext,
                                RoomDB::class.java,
                                "prayer.db"
                            ).fallbackToDestructiveMigration().build()
                        } catch (e: Exception) {
                            return null
                        }
                    }
                }
            }
            return instance
        }
    }





    override fun clearAllTables() {
    }

    override fun createInvalidationTracker(): InvalidationTracker {
        TODO("Not yet implemented")
    }

    override fun createOpenHelper(config: DatabaseConfiguration): SupportSQLiteOpenHelper {
        TODO("Not yet implemented")
    }
}