package com.abdallah.prayerapp.ui.activity

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.abdallah.prayerapp.databinding.ActivityMainBinding
import com.abdallah.prayerapp.utils.Constants
import com.abdallah.prayerapp.utils.location.LocationPermission

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (Constants.LOCATION_REQUEST_CODE == requestCode) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                val locationPermission = LocationPermission()

                //Location Permission taken
                Log.d("test", "location permission applied")
                locationPermission.detectLocation(this)

            }
        }
    }
}