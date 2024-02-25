package com.example.servivet.ui.main.fragment.settings_module

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.location.Location
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
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.setting.address_list.SettingAddress
import com.example.servivet.databinding.FragmentSettingAddLocationBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.activity.HomeActivity
import com.example.servivet.ui.main.fragment.AddLocationFragmentArgs
import com.example.servivet.ui.main.view_model.AddLocationViewModel
import com.example.servivet.ui.main.view_model.SettingAddLocationViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.Constants
import com.example.servivet.utils.GPSTracker
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.io.IOException

class SettingAddLocationFragment :
    BaseFragment<FragmentSettingAddLocationBinding, SettingAddLocationViewModel>(R.layout.fragment_setting_add_location),
    OnMapReadyCallback {
    private var permissions_denied: Boolean = false
    private val LOCATION_PERMISSION_REQUEST_CODE: Int=101
    private var isLocationSerach=false
    private var isFirstTime: Boolean=false
    private var fullAddress: String? = ""
    private var lastKnownLocation: Location? = null
    private var manager: LocationManager? = null
    private var dialog: Dialog? = null
    private var isDialogShow: Boolean = false
    private var longitude: Double = 0.0
    private var lattitude: Double = 0.0
    override val binding: FragmentSettingAddLocationBinding by viewBinding(FragmentSettingAddLocationBinding::bind)
    override val mViewModel: SettingAddLocationViewModel by viewModels()
    private lateinit var mMap: GoogleMap
    private val argumentData: AddLocationFragmentArgs by navArgs()
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var autoCompleteSupportFragment: AutocompleteSupportFragment
    private lateinit var locationManager: LocationManager
    var addressForEdit:SettingAddress?=null
    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {

    }

    private fun setClick() {

        if(Session.userDetails!=null){
            binding.fullName.setText(Session.userDetails.name)
            binding.mobileNumberText.setText(Session.userDetails.mobile)
            mViewModel.name.value=true
            mViewModel.number.value=true
        }
        binding.backBtn.setOnClickListener{
            findNavController().popBackStack()
        }
    }


    override fun setupViews() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction()
        }

        mapInitialization()
        setClick()
        if(arguments?.getString("action")!=null){
            mViewModel.action=arguments?.getString("action")!!
        }

        if(arguments?.getSerializable(Constants.DATA)!=null){
            addressForEdit= arguments?.getSerializable(Constants.DATA) as SettingAddress?
            mViewModel.saveAddressRequest.addressId =addressForEdit!!._id
            mViewModel.saveAddressRequest.city =addressForEdit!!.city
            mViewModel.saveAddressRequest.fullAddress =addressForEdit!!.fullAddress
            mViewModel.saveAddressRequest.latitute = addressForEdit!!.location!!.coordinates[0].toString()
            mViewModel.saveAddressRequest.longitute =addressForEdit!!.location!!.coordinates[1].toString()
            mViewModel.saveAddressRequest.postalCode = addressForEdit!!.postalCode.toString()

        }

    }



    private fun mapInitialization() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.google_map_key))
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        searchLocation()
        isPermissionCheck()
    }

    private fun isPermissionCheck() {
        if (checkLocationPermission()) {
//            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)

            getDeviceCurrentLocation()
        }
    }
    private fun getDeviceCurrentLocation() {

        /*
        * Get the best and most recent location of the device, which may be null in rare
        * cases when a location is not available.
        */
        try {
            val locationResult = fusedLocationProviderClient!!.lastLocation
            locationResult.addOnCompleteListener(requireActivity()) { task: Task<Location> ->
                if (task.isSuccessful) {
                    // Set the map's camera position to the current location of the device.
                    lastKnownLocation = task.result
                    mMap.clear()
                    if (task.result != null) {

                        this.mMap.isMyLocationEnabled = false



                        //move camera to current location.


                        // calculate address.

                        if(arguments?.getSerializable(Constants.DATA)!=null){
                            var latLngs=LatLng(addressForEdit!!.location!!.coordinates[0],addressForEdit!!.location!!.coordinates[1])
                            this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs, 17f))
                            mMap.addMarker(MarkerOptions().position(latLngs).icon(bitmapDescriptorFromVector(requireContext(), R.drawable.currentlocationicon)).anchor(0.8f, 0.5f))
                            binding.address.text =addressForEdit!!.fullAddress
                            autoCompleteSupportFragment.setText(addressForEdit!!.fullAddress)
                            mViewModel.saveAddressRequest.addressId =addressForEdit!!._id
                            mViewModel.saveAddressRequest.city =addressForEdit!!.city
                            mViewModel.saveAddressRequest.fullAddress =addressForEdit!!.fullAddress
                            mViewModel.saveAddressRequest.latitute = addressForEdit!!.location!!.coordinates[0].toString()
                            mViewModel.saveAddressRequest.longitute =addressForEdit!!.location!!.coordinates[1].toString()
                            mViewModel.saveAddressRequest.postalCode = addressForEdit!!.postalCode.toString()
                        }else{
                            if (task.result?.latitude != null) {
                                val address = CommonUtils.getAddressFromLatLng(requireContext(), task.result?.latitude!!, task.result?.longitude!!)
                                autoCompleteSupportFragment.setText(address!!.getAddressLine(0))
                                binding.address.text = address!!.getAddressLine(0)
                                mViewModel.saveAddressRequest.addressActionType="add"
                                mViewModel.saveAddressRequest.addressId =""
                                mViewModel.saveAddressRequest.city =address.locality
                                mViewModel.saveAddressRequest.country =address.countryName
                                mViewModel.saveAddressRequest.fullAddress =address.getAddressLine(0)
                                mViewModel.saveAddressRequest.latitute =task.result.latitude.toString()
                                mViewModel.saveAddressRequest.longitute =task.result.longitude.toString()
                                mViewModel.saveAddressRequest.postalCode =address.postalCode


                                Log.e("TAG", "getDeviceCurrentLocation: "+address.postalCode )

                            }
                            val latLng = LatLng(task.result.latitude, task.result.longitude)

                            this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
                            mMap.addMarker(MarkerOptions().position(latLng).icon(bitmapDescriptorFromVector(requireContext(), R.drawable.currentlocationicon)).anchor(0.8f, 0.5f))

                        }


                    }

                }
            }

        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message.toString())
            Toast.makeText(requireContext(), resources.getString(
                R.string.location_is_not_avialable), Toast.LENGTH_SHORT).show()
        }
    }

    fun checkConnectivity() {
        isFirstTime = false
        if (!isNetworkAvailable()) {
            Toast.makeText(context, R.string.please_connect_to_the_internet, Toast.LENGTH_LONG)
                .show()
        } else {
            if (!isLocationSerach) {
                getDeviceCurrentLocation()
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
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
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
                getDeviceCurrentLocation()
            } else {
                Toast.makeText(requireContext(), R.string.please_allow_location_permission_to_move_ahead, Toast.LENGTH_SHORT).show()
                permissions_denied = true
            }
            return
        }
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
    private fun checkLocationEnable() {
        manager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
            getDeviceCurrentLocation()
            mapInitialization()
        }
    }



    override fun onResume() {
        super.onResume()
        if (isFirstTime)
            checkConnectivity()
    }
    override fun setupObservers() {
        mViewModel.saveAddressResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            showSnackBar(it.data.message)
                            //Session.saveAddress(mViewModel.saveAddressRequest)
                            findNavController().popBackStack()
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(it.data.message)
                        }

                        else -> {
                            showSnackBar(it.data!!.message)
                        }
                    }
                }

               Status.LOADING -> {
                    ProcessDialog.startDialog(requireContext())
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog()

                    it.message?.let {
                        showSnackBar(it)
                    }
                }

                com.example.servivet.utils.Status.UNAUTHORIZED -> {
                    CommonUtils.logoutAlert(
                        requireContext(),
                        "Session Expired",
                        "Unauthorized User",
                        requireActivity()
                    )
                }
            }
        }

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

    private fun searchLocation() {

        if (!Places.isInitialized()) {
            Places.initialize(requireActivity(), getString(R.string.google_map_key))
        }
        autoCompleteSupportFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autoCompleteSupportFragment.view?.findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_search_button)?.visibility = View.GONE

        (autoCompleteSupportFragment.view?.findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_search_input) as EditText).textSize = 15f

        (autoCompleteSupportFragment.view?.findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_search_input) as EditText).setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

        (autoCompleteSupportFragment.view?.findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_search_input) as EditText).hint = requireActivity().getString(
            R.string.search_location)

        autoCompleteSupportFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG))


        autoCompleteSupportFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {


            override fun onError(p0: com.google.android.gms.common.api.Status) {
            }

            override fun onPlaceSelected(place: Place) {
                mMap.clear()
                val geocoder = Geocoder(requireContext())
                var addressList: List<Address> = ArrayList()
                try {
                    addressList = geocoder.getFromLocationName(place.toString(), 2)!!
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if (addressList.isNotEmpty())
                {
                    val address: Address = addressList[0]
                    val fullAddress = address.getAddressLine(0)

                    //set source data in singleton

                    autoCompleteSupportFragment.setText(address.getAddressLine(0))
                    binding.address.text = address!!.getAddressLine(0)
                    mViewModel.saveAddressRequest.addressActionType="add"
                    mViewModel.saveAddressRequest.addressId =""
                    mViewModel.saveAddressRequest.city =address.locality
                    mViewModel.saveAddressRequest.country =address.countryName
                    mViewModel.saveAddressRequest.fullAddress =address.getAddressLine(0)
                    mViewModel.saveAddressRequest.latitute =address.latitude.toString()
                    mViewModel.saveAddressRequest.longitute =address.longitude.toString()
                    mViewModel.saveAddressRequest.postalCode =CommonUtils.getPostalCodeByCoordinates(this,address.latitude,address.longitude,requireContext())

                    Log.e("TAG", "getDeviceCurrentLocation: "+CommonUtils.getPostalCodeByCoordinates(this,address.latitude,address.longitude,requireContext()) )

                    val latLng = LatLng(address.latitude, address.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))
                    mMap.addMarker(MarkerOptions().position(latLng).icon(bitmapDescriptorFromVector(requireContext(), R.drawable.currentlocationicon)))

                }
            }

        })
    }



    override fun onMapReady(mMap: GoogleMap) {
        this.mMap = mMap


    }

}