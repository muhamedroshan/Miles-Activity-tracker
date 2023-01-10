package com.example.ceikli

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

// this function not needed
//    private fun getLastKnownLocation(context:Context): Location? {
//
//        var lastKnownLocationByGps: Location? = null
//        if (checkLocationPermission(this)) {
//            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//            lastKnownLocationByGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//            return lastKnownLocationByGps
//        } else {
//            requestLocationPermission(this)
//            return lastKnownLocationByGps
//        }
//
//    }
}
