package com.abdallah.prayerapp.ui.fragment

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abdallah.prayerapp.data.model.PrayerTimesRoom
import com.abdallah.prayerapp.databinding.FragmentPrayersBinding
import com.abdallah.prayerapp.ui.fragment.PrayersFragment.Companion.progressVisibilityStateLiveData
import com.abdallah.prayerapp.ui.viewmodel.PrayerFragmentViewModel
import com.abdallah.prayerapp.utils.Constants
import com.abdallah.prayerapp.utils.common.BuildToast
import com.abdallah.prayerapp.utils.location.LocationPermission
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class PrayersFragment : Fragment() {
    private lateinit var binding: FragmentPrayersBinding
    private lateinit var viewModel: PrayerFragmentViewModel

    companion object {
        val progressVisibilityStateLiveData: MutableLiveData<Boolean> = MutableLiveData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrayersBinding.inflate(inflater)
        viewModel = ViewModelProvider(this)[PrayerFragmentViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getLocationCall(requireActivity(), viewLifecycleOwner)
        addressOnClick()
        setAddress()
        updatePrayerTimes()
        progressVisibility()
        swipeToRefresh()
        forwardArrowBtnOnClick()
        backArrowBtnOnClick()
    }

    private fun swipeToRefresh() {
        binding.prayerSwip.setOnRefreshListener {
            viewModel.getLocationCall(requireActivity(), viewLifecycleOwner)
            viewModel.currentItem = 0
            binding.prayerSwip.isRefreshing = false
        }
    }

    private fun updatePrayerTimes() {
        viewModel.prayerTimesLiveData.observe(viewLifecycleOwner) { times ->
            binding.progress.visibility = View.INVISIBLE
            if (times != null) {
                // starting timer
                viewModel.isArraysClickable = true
                setPrayerTimes(times)
            } else {
                Log.d("test", "get data times is null from room")
            }


        }
    }
private fun setPrayerTimes(times: PrayerTimesRoom){
    binding.prayerFragDateTxt.text = times.readableDate
    binding.prayerFragFajrTime.text = formatTime(times.fajr!!)
    binding.prayerFragSunriseTime.text = formatTime(times.sunrise!!)
    binding.prayerFragDuhrTime.text = formatTime(times.duhr!!)
    binding.prayerFragAsrTime.text = formatTime(times.asr!!)
    binding.prayerFragMaghrebTime.text = formatTime(times.magreb!!)
    binding.prayerFragIshaTime.text = formatTime(times.isha!!)
}
    private fun forwardArrowBtnOnClick() {
        binding.prayerFragForwardArrowDate.setOnClickListener {
            if (viewModel.sharedPreferencesApp.preferences.contains(Constants.ROOM_CONTAIN_DATA)
            ) {
                if(viewModel.isArraysClickable) {
                    if (viewModel.currentItem >= 7) {
                        BuildToast.showToast(
                            requireActivity(),
                            "Connect get more than 7 days",
                            FancyToast.WARNING
                        )
                        Log.d("test", "Connect get more than 7 days")
                    } else {
                        var newDay = viewModel.currentItem+1
                        val newDate =
                            (newDay *Constants.DAY_VALUE_IN_MILLI) + Date().time
                        viewModel.viewModelScope.launch(Dispatchers.IO) {
                            val itemPrayer = viewModel.selectItemTimeNotLiveData(newDate)
                            if (itemPrayer != null) {
                                withContext(Dispatchers.Main){
                                    setPrayerTimes(itemPrayer)
                                    viewModel.currentItem++
                                }

                            } else {
                                Log.d("test", "make new api call with the next month")

                                // make new api call with the next month
                                val longitude = viewModel.sharedPreferencesApp.getFloatFromShared(Constants.LONGITUDE, 0f)
                                val latitude = viewModel.sharedPreferencesApp.getFloatFromShared(Constants.LATITUDE, 0f)
                                progressVisibilityStateLiveData.postValue(true)
                                viewModel.getPrayerApi(latitude, longitude , 1 ,  1)
                                viewModel.currentItem++
                            }
                        }
                    }
                }else{
                    Log.d("test", "arrow is not clickable")

                }
            }else{
                viewModel.getLocationCall(requireActivity(), viewLifecycleOwner)
            }
        }
    }
    private fun formatTime(timeString: String): String {
        val inputFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        val time = inputFormat.parse(getFirstFiveChars(timeString))
        return outputFormat.format(time)
    }
    private fun getFirstFiveChars(timeString: String) : String{
        var newTime= ""
        for (i in 0..4){
            newTime+= timeString[i].toString()
        }
        return newTime
    }
    private fun backArrowBtnOnClick() {
        binding.prayerFragBackArrowDate.setOnClickListener {

                if (viewModel.sharedPreferencesApp.preferences.contains(Constants.ROOM_CONTAIN_DATA)
                ) {
                    if(viewModel.isArraysClickable) {
                        if (viewModel.currentItem <= 0) {
                            BuildToast.showToast(
                                requireActivity(),
                                "Connect get less than today",
                                FancyToast.WARNING
                            )
                            Log.d("test", "Connect get more than 7 days")
                        } else {
                            viewModel.currentItem--
                            val newDate =
                                (viewModel.currentItem *Constants.DAY_VALUE_IN_MILLI) + Date().time
                            viewModel.viewModelScope.launch(Dispatchers.IO) {
                                val itemPrayer = viewModel.selectItemTimeNotLiveData(newDate)
                                if (itemPrayer != null) {
                                    withContext(Dispatchers.Main){
                                        setPrayerTimes(itemPrayer)
                                    }
                                }
                            }
                        }
                    }else{
                        Log.d("test", "arrow is not clickable")

                    }
                }else{
                    viewModel.getLocationCall(requireActivity(), viewLifecycleOwner)
                }

        }
    }

    private fun progressVisibility() {
        progressVisibilityStateLiveData.observe(viewLifecycleOwner) {
            if (it) {
                binding.progress.visibility = View.VISIBLE
            } else {
                binding.progress.visibility = View.INVISIBLE
            }
        }
    }

    private fun addressOnClick() {
        binding.prayerFragLocationTxt.setOnClickListener {
            LocationPermission().takeLocationPermission(requireActivity())
        }
    }

    private fun setAddress() {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            if (viewModel.sharedPreferencesApp.preferences.contains(com.abdallah.prayerapp.utils.Constants.LOCATION)) {
                val location = viewModel.sharedPreferencesApp.getStringFromShared(
                    com.abdallah.prayerapp.utils.Constants.LOCATION,
                    "no Location"
                )
                withContext(Dispatchers.Main) {
                    binding.prayerFragLocationTxt.text = location
                }
            }
        }

        LocationPermission.locationAddressLiveData.observe(viewLifecycleOwner) {
            binding.prayerFragLocationTxt.text = it
        }

    }

}