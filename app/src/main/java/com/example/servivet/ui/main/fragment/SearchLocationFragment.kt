package com.example.servivet.ui.main.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.location.LocationInfo
import com.example.servivet.databinding.FragmentSearchLocationBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.activity.HomeActivity
import com.example.servivet.ui.main.view_model.SearchLocationViewModel
import com.example.servivet.utils.GPSTracker
import com.example.servivet.utils.Session

import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.io.IOException
import java.util.Locale


class SearchLocationFragment :
    BaseFragment<FragmentSearchLocationBinding, SearchLocationViewModel>(R.layout.fragment_search_location) {
    private var permissions_denied: Boolean = false
    private val LOCATION_PERMISSION_REQUEST_CODE: Int=101
    private var isLocationSerach=false
    private var isFirstTime: Boolean=false

    private var fullAddress: String? = ""
    private var manager: LocationManager? = null
    private var dialog: Dialog? = null
    private var isDialogShow: Boolean = false
    private var longitude: Double = 0.0
    private var lattitude: Double = 0.0
    private var locationName: String = ""
    private val LOCATION_PERMISSION_REQ_CODE: Int = 100
    override val binding: FragmentSearchLocationBinding by viewBinding(FragmentSearchLocationBinding::bind)
    override val mViewModel: SearchLocationViewModel by viewModels()
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var autoCompleteSupportFragment: AutocompleteSupportFragment
    private lateinit var locationManager: LocationManager


    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
    }

    override fun setupViews() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction(requireContext())
        }
        mapInitialization()
        getCurrentLocation()
        binding.currentLocationLayout.setOnClickListener {
            findNavController().popBackStack()
        }
    }



    private fun mapInitialization() {
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.google_map_key))
        }
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        searchLocation()
    }
    override fun onResume() {
        super.onResume()
        if (isFirstTime)
            checkConnectivity()
    }

    fun checkConnectivity() {
        isFirstTime = false
        if (!isNetworkAvailable()) {
            Toast.makeText(context, R.string.please_connect_to_the_internet, Toast.LENGTH_LONG)
                .show()
        } else {
            if (!isLocationSerach) {
                getCurrentLocation()
            }
        }
    }
    private fun isNetworkAvailable(): Boolean {
        var connectivityManager: ConnectivityManager? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager = requireActivity().getSystemService(HomeActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        }
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onStart() {
        super.onStart()
        if (!checkLocationPermission())
            requestLocationPermission()
    }

    // Check if the user has granted location permission
    private fun checkLocationPermission(): Boolean {
        val finePermissionState = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        val coarsePermissionState = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
        return finePermissionState == PackageManager.PERMISSION_GRANTED && coarsePermissionState == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            var finePermissionGranted = false
            var coarsePermissionGranted = false
            for (i in permissions.indices) {
                if (permissions[i] == Manifest.permission.ACCESS_FINE_LOCATION && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    finePermissionGranted = true
                }
                if (permissions[i] == Manifest.permission.ACCESS_COARSE_LOCATION && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    coarsePermissionGranted = true
                }
            }
            if (finePermissionGranted || coarsePermissionGranted) {
                permissions_denied = false
                if (dialog != null) {
                    isDialogShow = false
                    dialog!!.dismiss()
                }
                mapInitialization()
                getCurrentLocation()
            } else {
                Toast.makeText(requireContext(), R.string.please_allow_location_permission_to_move_ahead, Toast.LENGTH_SHORT).show()
                permissions_denied = true
            }
            return
        }
    }
    private fun checkLocationEnable() {
        manager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        if (!manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (!isDialogShow) {
                showDialog(
                    R.string.gps_enable_message,
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                    101,
                    2
                )
            }
        } else {
            val gpsTracker = GPSTracker(requireActivity())
            lattitude = gpsTracker.latitude.toDouble()
            longitude = gpsTracker.longitude.toDouble()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            checkLocationEnable()
            val gpsTracker = GPSTracker(requireActivity())
            lattitude = gpsTracker.latitude.toDouble()
            longitude = gpsTracker.longitude.toDouble()
            getCurrentLocation()
            mapInitialization()
        }
    }


    @SuppressLint("CutPasteId")
    private fun searchLocation() {
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.google_map_key))
        }
        autoCompleteSupportFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autoCompleteSupportFragment.view?.findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_search_button)?.visibility =
            View.GONE

        (autoCompleteSupportFragment.view?.findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_search_input) as EditText).textSize =
            15f
        (autoCompleteSupportFragment.view?.findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_search_input) as EditText).typeface =
            Typeface.DEFAULT
        (autoCompleteSupportFragment.view?.findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_search_input) as EditText).hint =
            getString(R.string.search_for_area_street_name_etc)
        (autoCompleteSupportFragment.view?.findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_search_input) as EditText).setHintTextColor(
            resources.getColor(R.color.light_gray_5)
        )
        (autoCompleteSupportFragment.view?.findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_search_input) as EditText).setTextColor(
            ContextCompat.getColor(requireContext(), R.color.black)
        )
        autoCompleteSupportFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
        )
        autoCompleteSupportFragment.view?.findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_clear_button)?.visibility = View.VISIBLE

        autoCompleteSupportFragment.view?.findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_clear_button)?.setOnClickListener {  }

        autoCompleteSupportFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {


            override fun onPlaceSelected(place: Place) {
                val geocoder = Geocoder(requireContext())
                var addressList: List<Address> = ArrayList()
                try {
                    addressList = geocoder.getFromLocationName(place.toString(), 2)!!
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if (addressList.isNotEmpty()) {
                    val address: Address = addressList[0]
                    autoCompleteSupportFragment.setText(address.getAddressLine(0))
                    Session.saveLocationInfo(LocationInfo(address.getAddressLine(0),address.featureName, address.latitude.toString(), address.longitude.toString()))
                    Log.e("TAG", "onPlaceSelected: "+address.locality )
                    findNavController().popBackStack()

                }
            }

            override fun onError(status: Status) {
                Log.e("TAG", "onError: $status")
            }
        })

    }

    fun showDialog(msg: Int, intent: Intent?, requestCode: Int, type: Int) {
        isDialogShow = true
        dialog = Dialog(requireContext())
        dialog!!.setCancelable(false)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setContentView(R.layout.alert_dialog)
        dialog!!.window!!.setBackgroundDrawableResource(R.drawable.gray_5_round_outline_corner_12_dp)
        val title = dialog!!.findViewById<TextView>(R.id.title)
        title.setText(R.string.app_name)
        val message = dialog!!.findViewById<TextView>(R.id.message)
        message.setText(msg)
        val view = dialog!!.findViewById<View>(R.id.viewCenter)
        view.visibility = View.GONE
        val dialogButton = dialog!!.findViewById<TextView>(R.id.okCenter)
        dialogButton.visibility = View.VISIBLE
        dialogButton.gravity = Gravity.CENTER_HORIZONTAL
        dialogButton.setOnClickListener { v: View? ->
            isDialogShow = false
            dialog!!.dismiss()
            if (type == 1) {
                val intent1 =
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri =
                    Uri.fromParts("package", "com.app.csc_mobile", null)
                intent1.data = uri
                startActivity(intent1)
            } else {
                startActivityForResult(intent!!, requestCode)
            }
        }
        dialog!!.show()
    }


    private fun getCurrentLocation() {
        locationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        val gpsTracker = GPSTracker(requireActivity())
        lattitude = gpsTracker.latitude.toDouble()
        longitude = gpsTracker.longitude.toDouble()

        try {
            var geocoder = Geocoder(requireContext());
            val adr: List<Address> = geocoder.getFromLocation(lattitude, longitude, 1)!!
            fullAddress = adr[0].getAddressLine(0)
            lattitude = adr[0].latitude
            lattitude = adr[0].longitude
            Session.saveLocationInfo(LocationInfo(adr[0].getAddressLine(0),adr[0].featureName, adr[0].latitude.toString(), adr[0].longitude.toString()))
        } catch (e: Exception) {
            Log.e("TAG", "getCurrentLocation: " + e)
        }
    }


    override fun setupObservers() {
    }


    fun getAddress(lat: Double, lng: Double): String? {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        try {
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (addresses!!.isNotEmpty()) {
                val address = addresses[0]
                val addressLine = address.getAddressLine(0)
                return addressLine
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }
}