package com.abdallah.prayerapp.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.hardware.SensorManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.abdallah.prayerapp.R
import com.abdallah.prayerapp.databinding.FragmentQuiblaBinding
import com.abdallah.prayerapp.ui.viewmodel.prayer.PrayerFragmentViewModel
import com.abdallah.prayerapp.ui.viewmodel.quibla.QiblaViewModel
import com.abdallah.prayerapp.utils.common.BuildToast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.shashank.sony.fancytoastlib.FancyToast
import kotlin.math.sin

class QuiblaFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentQuiblaBinding
    private lateinit var viewModel: QiblaViewModel
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuiblaBinding.inflate(layoutInflater)
        binding.appbar.back.setOnClickListener {
            findNavController().navigateUp()
        }
        viewModel = ViewModelProvider(this)[QiblaViewModel::class.java]
        viewModel. getLocation(requireActivity() , viewLifecycleOwner)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mapInit()
        return binding.root
    }

    private fun mapInit() {
        val locationRequest = LocationRequest()
        locationRequest.interval = 5000L
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.smallestDisplacement = 16F
        locationRequest.fastestInterval = 3000L
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
            mMap = p0
        viewModel.quiblaDirectionLiveData.observe(viewLifecycleOwner){
                        val latLng = LatLng(it.data.latitude, it.data.longitude)
                        val kabaPoint = LatLng(21.422487, 39.826206)
                        val polylineOptions = PolylineOptions()
                            .add(latLng, kabaPoint)
                            .color(Color.BLACK)
                            .width(8f)
                        mMap.addPolyline(polylineOptions)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 3F))
                    }
                }
}