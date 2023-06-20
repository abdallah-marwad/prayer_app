package com.abdallah.prayerapp.ui.viewmodel

import androidx.lifecycle.AndroidViewModel
import com.abdallah.prayerapp.data.repository.PrayerFragmentRepository
import com.abdallah.prayerapp.utils.common.MyApplication
import java.util.*

class PrayerFragmentViewModel(app : MyApplication) : AndroidViewModel(app) {
    val repository = PrayerFragmentRepository(getApplication())

    fun getPrayers(){
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)+1
//        repository.getPrayers(
//
//            currentYear,
//            currentMonth
//        )
    }

}