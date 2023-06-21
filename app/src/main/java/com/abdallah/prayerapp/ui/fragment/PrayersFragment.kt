package com.abdallah.prayerapp.ui.fragment

import android.os.Bundle
import android.provider.SyncStateContract.Constants
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abdallah.prayerapp.R
import com.abdallah.prayerapp.databinding.FragmentPrayersBinding
import com.abdallah.prayerapp.ui.viewmodel.PrayerFragmentViewModel
import com.abdallah.prayerapp.utils.location.LocationPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.concurrent.timer


class PrayersFragment : Fragment() {
    private lateinit var binding : FragmentPrayersBinding
    private lateinit var viewModel : PrayerFragmentViewModel
    companion object{
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
        viewModel.getLocationCall(requireActivity(),viewLifecycleOwner)
        addressOnClick()
        setAddress()
        setPrayerTimes()
        progressVisibility()
        swipeToRefresh()
    }
    private fun swipeToRefresh(){
        binding.prayerSwip.setOnRefreshListener {
            viewModel.getLocationCall(requireActivity(),viewLifecycleOwner)
            binding.prayerSwip.isRefreshing = false
        }
    }
    private fun setPrayerTimes(){
        viewModel.prayerTimesLiveData.observe(viewLifecycleOwner){times->
            binding.progress.visibility = View.INVISIBLE
            if(times!= null){
                binding.prayerFragDateTxt.text = times.readableDate
                binding.prayerFragFajrTime.text = times.fajr
                binding.prayerFragSunriseTime.text = times.sunrise
                binding.prayerFragDuhrTime.text = times.duhr
                binding.prayerFragAsrTime.text = times.asr
                binding.prayerFragMaghrebTime.text = times.magreb
                binding.prayerFragIshaTime.text = times.isha
            }else{
                Log.d("test" , "get data times is null from room")
            }


        }
    }
    private fun progressVisibility(){
        progressVisibilityStateLiveData.observe(viewLifecycleOwner){
            if(it){
                binding.progress.visibility = View.VISIBLE
            }else{
                binding.progress.visibility = View.INVISIBLE
            }
        }
    }
    private fun addressOnClick(){
        binding.prayerFragLocationTxt.setOnClickListener {
            LocationPermission().takeLocationPermission(requireActivity())
        }
    }
    private fun setAddress(){
        viewModel.viewModelScope.launch (Dispatchers.IO){
           if(viewModel.sharedPreferencesApp.preferences.contains(com.abdallah.prayerapp.utils.Constants.LOCATION)){
               val location =viewModel.sharedPreferencesApp.getStringFromShared(com.abdallah.prayerapp.utils.Constants.LOCATION ,
                   "no Location")
                withContext(Dispatchers.Main){
                    binding.prayerFragLocationTxt.text =location
                }
           }
        }

        LocationPermission.locationAddressLiveData.observe(viewLifecycleOwner  ){
            binding.prayerFragLocationTxt.text =it
        }

    }

}