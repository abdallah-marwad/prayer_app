package com.abdallah.prayerapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.abdallah.prayerapp.R
import com.abdallah.prayerapp.databinding.FragmentPrayersBinding
import com.abdallah.prayerapp.ui.viewmodel.PrayerFragmentViewModel
import kotlin.concurrent.timer


class PrayersFragment : Fragment() {
    private lateinit var binding : FragmentPrayersBinding
    private lateinit var viewModel : PrayerFragmentViewModel
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
        viewModel.getLocation(requireActivity(),viewLifecycleOwner)
        viewModel.responseLiveData.observe(viewLifecycleOwner){times->
            binding.prayerFragFajrTime.text = times.Fajr
            binding.prayerFragSunriseTime.text = times.Sunrise
            binding.prayerFragDuhrTime.text = times.Dhuhr
            binding.prayerFragAsrTime.text = times.Asr
            binding.prayerFragMaghrebTime.text = times.Maghrib
            binding.prayerFragIshaTime.text = times.Isha

        }
    }

}