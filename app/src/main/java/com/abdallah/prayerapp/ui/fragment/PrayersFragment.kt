package com.abdallah.prayerapp.ui.fragment

import android.R
import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.abdallah.prayerapp.data.model.prayer.PrayerTimesRoom
import com.abdallah.prayerapp.databinding.FragmentPrayersBinding
import com.abdallah.prayerapp.ui.viewmodel.prayer.PrayerFragmentViewModel
import com.abdallah.prayerapp.utils.CalenderCustomTime
import com.abdallah.prayerapp.utils.Constants
import com.abdallah.prayerapp.utils.DateModifier
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
    private lateinit var mapOfPrayers: MutableMap<String, String>
    var timer: CountDownTimer? = null

    companion object {
        val progressVisibilityStateLiveData: MutableLiveData<Boolean> = MutableLiveData()
        var remainingTime: MutableLiveData<Map<String, Long>> = MutableLiveData()
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
        Log.d("test", "Now is ${Date().time}")
        addressOnClick()
        setAddress()
        updatePrayerTimes()
        progressVisibility()
        swipeToRefresh()
        forwardArrowBtnOnClick()
        backArrowBtnOnClick()
        qiblaBtnOnclick()
    }

    private fun swipeToRefresh() {
        binding.prayerSwip.setOnRefreshListener {
            viewModel.getLocationCall(requireActivity(), viewLifecycleOwner)
            viewModel.currentItem = 0
            binding.prayerSwip.isRefreshing = false
        }
    }
    private fun qiblaBtnOnclick() {
        binding.prayerFragButton.setOnClickListener {
            findNavController().navigate(PrayersFragmentDirections.actionPrayersFragmentToQuiblaFragment())
        }
    }

    private fun updatePrayerTimes() {
        viewModel.prayerTimesLiveData.observe(viewLifecycleOwner) { times ->
            binding.progress.visibility = View.INVISIBLE
            if (times != null) {
                // starting timer but check first if the date equal today
                viewModel.isArraysClickable = true
                setPrayerTimes(times)
                setPrayerMap(times)
                if (times.StringDate!! == CalenderCustomTime().getCalenderWithCustomTime(Date().time)) {
                    getTimer()
                }
            } else {
                Log.d("test", "get data times is null from room")
            }


        }
    }
    private fun getTimer(){
        viewModel.handleTimer(mapOfPrayers)
        viewModel.checkTimer (viewLifecycleOwner)
    }

    private fun setPrayerMap(times: PrayerTimesRoom) {
        mapOfPrayers = mutableMapOf()
        mapOfPrayers[Constants.FAJR] = times.fajr!!
        mapOfPrayers[Constants.SUNRISE] = times.sunrise!!
        mapOfPrayers[Constants.DUHR] = times.duhr!!
        mapOfPrayers[Constants.ASR] = times.asr!!
        mapOfPrayers[Constants.MAGHREB] = times.magreb!!
        mapOfPrayers[Constants.ISHA] = times.isha!!
    }

    private fun setPrayerTimes(times: PrayerTimesRoom) {
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
            checkRoomData()
            roomHaveData = {
                if (it) {
                if (viewModel.isArraysClickable) {
                    if (viewModel.currentItem >= 7) {
                        BuildToast.showToast(
                            requireActivity(),
                            "Connect get more than 7 days",
                            FancyToast.WARNING
                        )
                        Log.d("test", "Connect get more than 7 days")
                    } else {
                        var newDay = viewModel.currentItem + 1
                        val newDate =
                            (newDay * Constants.DAY_VALUE_IN_MILLI) + Date().time
                        viewModel.viewModelScope.launch(Dispatchers.IO) {
                            val itemPrayer = viewModel.selectItemTimeNotLiveData(newDate)
                            if (itemPrayer != null) {
                                withContext(Dispatchers.Main) {
                                    setPrayerTimes(itemPrayer)
                                    viewModel.currentItem++
                                }

                            } else {
                                Log.d("test", "make new api call with the next month")

                                // make new api call with the next month
                                val longitude = viewModel.sharedPreferencesApp.getFloatFromShared(
                                    Constants.LONGITUDE,
                                    0f
                                )
                                val latitude = viewModel.sharedPreferencesApp.getFloatFromShared(
                                    Constants.LATITUDE,
                                    0f
                                )
                                progressVisibilityStateLiveData.postValue(true)
                                viewModel.getPrayerApi(latitude, longitude, 1, 1)
                                viewModel.currentItem++
                            }
                        }
                    }
                } else {
                    Log.d("test", "arrow is not clickable")

                }
            } else {
                viewModel.getLocationCall(requireActivity(), viewLifecycleOwner)
            }
            }
        }
    }

    private fun formatTime(timeString: String): String {
        val inputFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        val time = inputFormat.parse(DateModifier().getChars(timeString))
        return outputFormat.format(time)
    }
    var roomHaveData : ((Boolean)->Unit )? = null
    private fun checkRoomData(){
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            viewModel.sharedPreferencesApp.preferences.contains(Constants.ROOM_CONTAIN_DATA)
            withContext(Dispatchers.Main){
                roomHaveData?.let { it(true) }
            }
        }
    }
    private fun backArrowBtnOnClick() {
        binding.prayerFragBackArrowDate.setOnClickListener {
            checkRoomData()
            roomHaveData = {
            if (it) {
                if (viewModel.isArraysClickable) {
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
                            (viewModel.currentItem * Constants.DAY_VALUE_IN_MILLI) + Date().time
                        viewModel.viewModelScope.launch(Dispatchers.IO) {
                            val itemPrayer = viewModel.selectItemTimeNotLiveData(newDate)
                            if (itemPrayer != null) {
                                withContext(Dispatchers.Main) {
                                    setPrayerTimes(itemPrayer)
                                }
                            }
                        }
                    }
                } else {
                    Log.d("test", "arrow is not clickable")

                }
            } else {
                viewModel.viewModelScope.launch(Dispatchers.Main) {
                    viewModel.getLocationCall(requireActivity(), viewLifecycleOwner)
                }
            }

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


    private fun timerForSecondPrayer() {
        remainingTime.observe(requireActivity()) {
            if (it.values.first() != 1L) {
                if (timer != null) {
                    timer!!.cancel()
                    timer = null
                    binding.prayerFragTimerTxt.text = "time left \n 00 hr 00 min"
                    binding.prayerFragNextPrayerName.text = "Not Found"
                }
                    timer = object : CountDownTimer(it.values.first(), 60000) {
                        override fun onTick(p0: Long) {
                            updateTimer(p0, it.keys.first())
                        }

                        override fun onFinish() {
                            getTimer()
                            timerForSecondPrayer()

                        }
                    }.start()
                }

        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun updateTimer(remainingTime: Long, prayerName: String) {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            val dateDifference = remainingTime / 1000
            var hour = (dateDifference / 3600)
            var minute = (dateDifference / 60) % 60
            var result = java.lang.StringBuilder()
            result.append("time left \n")
            result.append(String.format("%02d", hour))
            result.append("hr")
            result.append(String.format("%02d", minute))
            result.append("min")
            withContext(Dispatchers.Main) {
                binding.prayerFragTimerTxt.text = result
                binding.prayerFragNextPrayerName.text = prayerName
            }
        }
    }

    override fun onResume() {
        super.onResume()
        timerForSecondPrayer()
    }


}