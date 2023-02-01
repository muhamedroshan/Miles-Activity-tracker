package com.example.miles

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlankFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _view : View? = null
    private val Fview get() = _view!!
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private  lateinit var ViewModel: ceikliViewModel
    private var locationManager: LocationManager? = null
    private lateinit var text: TextView
    private lateinit var text2 : TextView
    private lateinit var radioGroupActivity : RadioGroup
    private lateinit var cycleRadioButton : RadioButton
    private lateinit var walkingRadioButton : RadioButton
    private lateinit var runningRadioButton: RadioButton
    private lateinit var text3: TextView
    private lateinit var text4 : TextView
    private var _activity: Activity? = null
    private val viewModelDB: dataBaseViewModel by activityViewModels {
        dataBaseViewModelFactory(
            (activity?.application as LocationApp).database.itemDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        setHasOptionsMenu(true)
        _activity = getActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _view = inflater.inflate(R.layout.fragment_blank, container, false)
        return Fview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val application = requireActivity().application as LocationApp
        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        ViewModel = application.appViewModel
        text = Fview.findViewById(R.id.speedTextView)
        text2 = Fview.findViewById(R.id.distanceTextView)
        text3 = Fview.findViewById(R.id.timerTextView)
        text4 = Fview.findViewById(R.id.caloriesTextView)
        if (Fview.findViewById<RadioGroup>(R.id.radioGroup)!=null){
            radioGroupActivity = Fview.findViewById(R.id.radioGroup)
            cycleRadioButton = Fview.findViewById(R.id.radioButtonCycling)
            walkingRadioButton = Fview.findViewById(R.id.radioButtonWalking)
            runningRadioButton = Fview.findViewById(R.id.radioButtonRunning)
            radioGroupActivity.setOnCheckedChangeListener{ group,checkedId ->
                when (checkedId){
                    R.id.radioButtonCycling ->{
                        ViewModel.updateActvity("cy")
                    }
                    R.id.radioButtonWalking ->{
                        ViewModel.updateActvity("wa")
                    }
                    R.id.radioButtonRunning ->{
                        ViewModel.updateActvity("ru")
                    }
                }
            }
            ViewModel.activity.observe(viewLifecycleOwner){
                when(ViewModel.activity.value){
                    "cy" -> {radioGroupActivity.check(R.id.radioButtonCycling)}
                    "wa" -> {radioGroupActivity.check(R.id.radioButtonWalking)}
                    "ru" -> {radioGroupActivity.check(R.id.radioButtonRunning)}
                }
            }
        }




        ViewModel.speed.observe(
            viewLifecycleOwner
        ) { speed ->
            val formattedFloat = String.format("%.1f", speed)
            text.text = "$formattedFloat"
        }
        ViewModel.distance.observe(
            viewLifecycleOwner
        ) { distance ->
            text2.text = "${distance}"
        }
        ViewModel.timer.observe(
            viewLifecycleOwner
        ){
            val hours = ViewModel.timer.value!!/3600
            val minutes = (ViewModel.timer.value!! % 3600) / 60
            val second = ViewModel.timer.value!! % 60
            val time = String.format(Locale.getDefault(),"%d:%02d:%02d",hours,minutes,second)
            text3.text = time

            //calculation of calorie
            ViewModel.currentLocation.observe(viewLifecycleOwner){
                when(ViewModel.activity.value){
                    "cy" -> {

                    }
                    "wa" -> {

                    }
                    "ru" -> {

                    }
                }
            }

        }
        ViewModel.startRecording.observe(
            viewLifecycleOwner
        ){ it ->
            if (it){
                //some code when recording is on
            }else{
                Intent(requireContext().applicationContext, locationService::class.java).apply {
                    action = locationService.ACTION_STOP
                    requireContext().startService(this)
                }
            }
        }

    }
    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.layout_menu, menu)
        val layoutButton = menu.findItem(R.id.action_switch_layout)
        val layoutButtonStop = menu.findItem(R.id.action_stop)
        val layoutButtonWarning = menu.findItem(R.id.action_warning)
        ViewModel.startRecording.observe(viewLifecycleOwner){
            setIcon(layoutButton,it)
            if (!checkBatteryOptimizationIgnorePermission()){
                layoutButtonWarning.isVisible = it
            }
            layoutButtonStop.isVisible = it
        }

    }
    private fun checkBatteryOptimizationIgnorePermission ():Boolean {
        var isIgnoring = false
        val powerManager = requireActivity().getSystemService(Context.POWER_SERVICE) as? PowerManager
        if (powerManager!=null){
            isIgnoring = powerManager.isIgnoringBatteryOptimizations("com.example.miles")
        }
        return isIgnoring
    }
    private fun requestBatteryIgnore(){
        try {
            val batteryOptInt =
                Intent().setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                    .setData(Uri.parse("package:" + requireContext().packageName))

            if (requireContext().packageManager.resolveActivity(batteryOptInt, 0) != null) {
                startActivity(batteryOptInt)
            }else{
                Toast.makeText(requireActivity(), "nobody to resolve", Toast.LENGTH_SHORT).show()
            }
        }catch (e: java.lang.Exception){
            Log.e("_error", "requestBatteryIgnore: ", e)
        }
    }
    private fun setIcon(menuItem: MenuItem?,isRecrd :Boolean) {
        if (menuItem == null)
            return

        menuItem.icon =
            if (!isRecrd)
                ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_baseline_play_arrow_24)
            else ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_baseline_pause_24)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_switch_layout -> {
                if (hasGps()) {
                    if (!ViewModel.startRecording.value!!) {
                        turnOn()
                        val stateRecrd = !ViewModel.startRecording.value!!
                        ViewModel.updateStartRecording(stateRecrd)
                    } else {
                        turnOff()
                        ViewModel.updateLocation(null)
                        val stateRecrd = !ViewModel.startRecording.value!!
                        ViewModel.updateStartRecording(stateRecrd)
                    }
                }else{
                    val toast = Toast.makeText(requireContext(), "Enable Location", Toast.LENGTH_SHORT)
                    toast.show()
                }
                return true
            }
            R.id.action_stop -> {
                turnOff()
                saveToDataDb()
                ViewModel.updateDistance(00.0f)
                ViewModel.updateLocation(null)
                ViewModel.updateSpeed(00.0f)
                ViewModel.resetTimer(0)
                ViewModel.updateStartRecording(!ViewModel.startRecording.value!!)
                ViewModel.updateCalorie(0)
                return true
            }
            R.id.action_warning -> {
                requestBatteryIgnore()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveToDataDb(){

        viewModelDB.insertItem(
            System.currentTimeMillis(),
             averageSpeed(),
            ViewModel.distance.value?:0,
            ViewModel.timer.value!!,
            ViewModel.activity.value!!,
            ViewModel.calorie.value?:0
        )
    }

    private fun averageSpeed() : Int {

        val distanceMs   =  ViewModel.distance.value!!
        val seconds : Int = ViewModel.timer.value!!
        val mtPerSec = if(distanceMs!=0)distanceMs/seconds else 0
        val kmPh : Int = mtPerSec * 60 * 60 / 1000
        return kmPh.toInt()
    }

    private fun requestLocationPermission(activity: Activity) {
        // Request the permission
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        permissionLuancherSingle.launch(permission)

    }

    private fun checkLocationPermission(activity: Activity): Boolean {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val granted = ContextCompat.checkSelfPermission(activity, permission)
        val permission2 = Manifest.permission.ACCESS_COARSE_LOCATION
        val granted2 = ContextCompat.checkSelfPermission(activity, permission2)
        return granted == PackageManager.PERMISSION_GRANTED && granted2 == PackageManager.PERMISSION_GRANTED
    }

    private fun hasGps(): Boolean {
        return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!
    }
    private fun turnOn (){
        if (hasGps()) {

            if (checkLocationPermission(requireActivity())) {
                Intent(requireContext().applicationContext, locationService::class.java).apply {
                    action = locationService.ACTION_START

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        requireActivity().startForegroundService(this)
                    } else {
                        requireContext().startService(this)
                    }
                }
            }else{
                requestLocationPermission(requireActivity())
            }
        } else {
            val toast = Toast.makeText(requireContext(), "Enable Location", Toast.LENGTH_SHORT)
            toast.show()
            ViewModel.updateStartRecording(false)
        }
    }

    private fun turnOff() {
        Intent(requireContext().applicationContext, locationService::class.java).apply {
            action = locationService.ACTION_STOP
            requireContext().startService(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _view = null
    }

    //permission for location
    private val permissionLuancherSingle = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->

        if (isGranted){
            turnOn()
        }else{
            ViewModel.updateStartRecording(false)
        }

    }
}