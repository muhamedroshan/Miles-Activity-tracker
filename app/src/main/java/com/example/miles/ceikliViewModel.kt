package com.example.miles

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class ceikliViewModel : ViewModel() {
    private var _currentLocation : MutableLiveData<Location> = MutableLiveData<Location>()
    val currentLocation : LiveData<Location>
        get() {
            return _currentLocation
        }

    fun updateLocation(location: Location?) {
        if (location == null){
         _currentLocation = MutableLiveData<Location>()
        }else{
            _currentLocation.postValue(location!!)
        }
    }
    private val _speed : MutableLiveData<Float> = MutableLiveData<Float>()
    val speed : LiveData<Float>
        get() {
            return _speed
        }
    fun updateSpeed (speed: Float){
        _speed.postValue(speed*60*60/1000)
    }
    private val _activity : MutableLiveData<String> = MutableLiveData<String>("cy")
    val activity : LiveData<String>
        get() {
            return _activity
        }
    fun updateActvity (activity: String){
        _activity.postValue(activity)
    }
    private val _weight : MutableLiveData<Int> = MutableLiveData<Int>(70)
    val weight : LiveData<Int>
        get() {
            return _calorie
        }
    fun updateWeight ( weight : Int){
        _calorie.postValue(weight)
    }
    private val _calorie : MutableLiveData<Int> = MutableLiveData<Int>()
    val calorie : LiveData<Int>
        get() {
            return _calorie
        }
    fun updateCalorie ( cal : Int){
        _calorie.postValue(cal)
    }
    private val _distance : MutableLiveData<Int> = MutableLiveData<Int>(0)
    val  distance : LiveData<Int>
        get() {
            return _distance
        }
    fun updateDistance (distance : Float){
        _distance.postValue(distance.toInt())
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
        if (_startRecording.value!!){
            _timer.postValue(_timer.value!! + Seconds)
        }else{
            _timer.postValue( Seconds)
        }
    }
    fun resetTimer (seconds : Int){
        _timer.postValue(seconds)

    }
}