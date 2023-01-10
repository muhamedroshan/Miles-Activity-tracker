package com.example.ceikli

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    private lateinit var text3: TextView
    private lateinit var text4 : TextView
    private var _activity: Activity? = null
    private val activity get() = _activity!!
    private var listener: LocationListener? = null

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


        ViewModel.speed.observe(
            viewLifecycleOwner
        ) { speed ->
            text.text = "${speed}m/h"
        }
        ViewModel.distance.observe(
            viewLifecycleOwner
        ) { distance ->
            text2.text = "${distance}m"
        }
        ViewModel.timer.observe(
            viewLifecycleOwner
        ){
            val hours = ViewModel.timer.value!!/3600
            val minutes = (ViewModel.timer.value!! % 3600) / 60
            val second = ViewModel.timer.value!! % 60
            val time = String.format(Locale.getDefault(),"%d:%02d:%02d",hours,minutes,second)
            text3.text = time
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
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.layout_menu, menu)
        val layoutButton = menu.findItem(R.id.action_switch_layout)
        var isRecrd = ViewModel.startRecording.value!!
        setIcon(layoutButton, isRecrd)
        layoutButton.setOnMenuItemClickListener { it ->
            if (ViewModel.startRecording.value!!){
                Intent(requireContext().applicationContext, locationService::class.java).apply {
                    action = locationService.ACTION_STOP
                    requireContext().startService(this)
                }
            }else{
                turnOn()
            }
            ViewModel.updateStartRecording(!ViewModel.startRecording.value!!)
            if (hasGps()){
                isRecrd = !ViewModel.startRecording.value!!
            }
            setIcon(it,isRecrd)
            return@setOnMenuItemClickListener true
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

    private fun requestLocationPermission(activity: Activity) {
            // Request the permission
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )

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
                    requireContext().startService(this)
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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted. Do the camera-related task you need to do.
                turnOn()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _view = null
    }
}