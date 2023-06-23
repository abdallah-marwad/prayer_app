package com.abdallah.prayerapp.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.room.Update
import com.abdallah.prayerapp.R
import com.abdallah.prayerapp.databinding.FragmentQuiblaBinding
import com.abdallah.prayerapp.ui.viewmodel.quibla.QiblaViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions


class QuiblaFragment : Fragment(), OnMapReadyCallback {
    private var sensorManager: SensorManager? = null
    private var gyroscope: Sensor? = null
    private lateinit var binding: FragmentQuiblaBinding
    private lateinit var viewModel: QiblaViewModel
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager =  requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager!!.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuiblaBinding.inflate(layoutInflater)
        binding.appbar.back.setOnClickListener {
            findNavController().navigate(QuiblaFragmentDirections.actionQuiblaFragmentToPrayersFragment())
        }
        viewModel = ViewModelProvider(this)[QiblaViewModel::class.java]
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mapInit()
        viewModel.getLocation(requireActivity(), viewLifecycleOwner)
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
        viewModel.quiblaDirectionLiveData.observe(viewLifecycleOwner) {
            val myPoint = LatLng(it.data.latitude, it.data.longitude)
            val kabaPoint = LatLng(21.422487, 39.826206)
            val polylineOptions = PolylineOptions()
                .add(myPoint, kabaPoint)
                .color(Color.BLACK)
                .width(8f)
            mMap.addMarker(MarkerOptions()
                .position(myPoint))
            mMap.addMarker(MarkerOptions()
                .position(kabaPoint)
                .rotation(it.data.direction.toFloat()))
            mMap.addPolyline(polylineOptions)

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPoint, 4F))

        }
    }
//    private val gyroscopeListener: SensorEventListener = object : SensorEventListener {
//        override fun onSensorChanged(event: SensorEvent ) {
//            val rotationMatrix = FloatArray(9)
//            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
//            val azimuthRadians =
//                -rotationMatrix[2]
//            val azimuthDegrees =
//                Math.toDegrees(azimuthRadians.toDouble()).toFloat() // Convert to degrees
//
////             Update the camera bearing
//            val currentCameraPosition: CameraPosition = mMap.getCameraPosition()
//            val newCameraPosition = CameraPosition.Builder(currentCameraPosition)
//                .bearing(azimuthDegrees)
//                .build()
//            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition))
//
//        }
//
//        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
//        }
//    }
//     override fun onResume() {
//        super.onResume()
//        sensorManager!!.registerListener(
//            gyroscopeListener,
//            gyroscope,
//            SensorManager.SENSOR_DELAY_NORMAL
//        )
//    }
//
//     override fun onPause() {
//        super.onPause()
//        sensorManager!!.unregisterListener(gyroscopeListener)
//    }
}