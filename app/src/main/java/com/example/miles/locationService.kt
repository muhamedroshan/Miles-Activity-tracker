package com.example.miles

import android.Manifest
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

import kotlinx.coroutines.*
import kotlinx.coroutines.Runnable
import java.util.*

class locationService: Service(), ViewModelStoreOwner {
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private var locationManager: LocationManager? = null
    private var listener: LocationListener? = null
    private  lateinit var ViewModel: ceikliViewModel
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val handler = Handler()
    private lateinit var  runnable : Runnable
    private lateinit var notificationManager: NotificationManager
    private lateinit var notification : NotificationCompat.Builder

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val application = this.application as LocationApp
        ViewModel = application.appViewModel
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        runnable = object : Runnable{
            override fun run() {
                ViewModel.updateTimer(1)
                val hours = ViewModel.timer.value!!/3600
                val minutes = (ViewModel.timer.value!! % 3600) / 60
                val second = ViewModel.timer.value!! % 60
                val time = String.format(Locale.getDefault(),"%d:%02d:%02d",hours,minutes,second)
                val updatedNotification = notification.setContentText(
                    time
                )
                notificationManager.notify(1, updatedNotification.build())
                handler.postDelayed(this,1000)
            }

        }
    }
    private fun stop() {
        handler.removeCallbacks(runnable)
        if(listener!=null){
                    locationManager?.removeUpdates(listener!!)
                }
        stopForeground(true)
        stopSelf()
    }
    private fun start() {
        handler.post(runnable)
             notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location...")
            .setContentText("00:00")
            .setSmallIcon(R.drawable.ic_baseline_social_distance_24)
            .setOngoing(true)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager




        startLocationUpdates(this)
        startForeground(1, notification.build())
    }
    private fun startLocationUpdates(context: Context) {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // Do something with the new location data
                serviceScope.launch {
                    if (ViewModel.currentLocation.value == null){
                        ViewModel.updateLocation(location)
                    }else{
                        val prevLocation: Location = ViewModel.currentLocation.value!!
                        val curntLocation = location
                        val distance =
                            (ViewModel.distance.value ?: 0) + curntLocation.distanceTo(prevLocation)
                        val speed = curntLocation.speed
                        ViewModel.updateSpeed(speed)
                        ViewModel.updateDistance(distance)
                        ViewModel.updateLocation(location)
                    }
                }
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {
                stop()
            }
        }
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            stop()
            return
        }
        locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                100,
                1.0f,
                listener!!)
    }
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }

    override fun getViewModelStore(): ViewModelStore {
        TODO("Not yet implemented")
    }
}