package com.example.ceikli

import android.location.Location
import android.widget.Switch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class ceikliViewModel : ViewModel() {
    private val _currentLocation : MutableLiveData<Location> = MutableLiveData<Location>()
    val currentLocation : LiveData<Location>
        get() {
            return _currentLocation
        }

    fun updateLocation(location: Location) {
        _currentLocation.postValue(location)
    }
    private val _speed : MutableLiveData<Float> = MutableLiveData<Float>()
    val speed : LiveData<Float>
        get() {
            return _speed
        }
    fun updateSpeed (speed: Float){
        _speed.postValue(speed)
    }
    private val _distance : MutableLiveData<Float> = MutableLiveData<Float>()
    val  distance : LiveData<Float>
        get() {
            return _distance
        }
    fun updateDistance (distance : Float){
        _distance.postValue(distance)
    }
    private val _startRecording : MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val startRecording : LiveData<Boolean>
        get() {
            return _startRecording
        }
    fun updateStartRecording ( switch: Boolean){
        _startRecording.postValue(switch)
    }
    private val _timer : MutableLiveData<Int> = MutableLiveData<Int>(0)
    val  timer : LiveData<Int>
        get() {
            return _timer
        }
    fun updateTimer (Seconds : Int){
        if (_timer.value==null){
            _timer.postValue( Seconds)
        }else{
            _timer.postValue(_timer.value!! + Seconds)
        }
    }
}