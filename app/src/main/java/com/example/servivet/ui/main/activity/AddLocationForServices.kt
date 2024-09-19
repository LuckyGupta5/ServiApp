package com.example.servivet.ui.main.activity

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
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.servivet.R
import com.example.servivet.data.model.save_address.request.SaveAddressRequest
import com.example.servivet.databinding.ActivityAddLocationForServicesBinding
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.GPSTracker
import com.google.android.gms.common.api.Status
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
import com.google.gson.Gson
import java.io.IOException

@RequiresApi(Build.VERSION_CODES.O)
class AddLocationForServices : AppCompatActivity(), OnMapReadyCallback {

    private val LOCATION_PERMISSION_REQUEST_CODE: Int = 101
    private var isFirstTime: Boolean = true
    private var lastKnownLocation: Location? = null
    private var dialog: Dialog? = null
    private var isDialogShow: Boolean = false
    private var longitude: Double = 0.0
    private var latitude: Double = 0.0
    private var isLocationSerach = false
    private var permissions_denied: Boolean = false
    var saveAddressRequest = SaveAddressRequest()
    private lateinit var binding: ActivityAddLocationForServicesBinding
    private var mMap: GoogleMap? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var autoCompleteSupportFragment: AutocompleteSupportFragment


    private fun setClick() {
        binding.apply {
            saveBtn.setOnClickListener {
                setResultAndFinish()
                finish()
            }
            backBtn.setOnClickListener {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_location_for_services)
        binding.apply {
            lifecycleOwner = this@AddLocationForServices
        }
        mapInitialization()
        setClick()
    }

    private fun mapInitialization() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        if (!Places.isInitialized()) {
            Places.initialize(this, getString(R.string.google_map_key))
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        searchLocation()
        isPermissionCheck()
    }

    private fun isPermissionCheck() {
        if (checkLocationPermission()) {
            getDeviceCurrentLocation()
        }
    }

    private fun getDeviceCurrentLocation() {
        try {
            val locationResult = fusedLocationProviderClient!!.lastLocation
            locationResult.addOnCompleteListener(this) { task: Task<Location> ->
                if (task.isSuccessful) {
                    // Set the map's camera position to the current location of the device.
                    lastKnownLocation = task.result
                    mMap?.clear()
                    if (task.result != null) {
                        Log.d("latitude: ", "" + lastKnownLocation?.latitude)
                        Log.d("longitude: ", "" + lastKnownLocation?.longitude)

                        val latLng = LatLng(task.result.latitude, task.result.longitude)

                        this.mMap?.isMyLocationEnabled = false


                        //move camera to current location.

                        // calculate address.
                        if (task.result?.latitude != null) {
                            val address = CommonUtils.getAddressFromLatLng(
                                this, task.result?.latitude!!, task.result?.longitude!!
                            )
                            autoCompleteSupportFragment.setText(address!!.getAddressLine(0))
                            saveAddressRequest.addressActionType = "add"
                            saveAddressRequest.addressId = ""
                            saveAddressRequest.city = address.locality
                            saveAddressRequest.country = address.countryName
                            saveAddressRequest.fullAddress = address.getAddressLine(0)
                            saveAddressRequest.latitute = task.result.latitude.toString()
                            saveAddressRequest.longitute = task.result.longitude.toString()
                            saveAddressRequest.postalCode = address.postalCode

                            Log.e("TAG", "getDeviceCurrentLocation: " + address.postalCode)

                        }
                        this.mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))

                        mMap?.addMarker(
                            MarkerOptions().position(latLng).icon(
                                bitmapDescriptorFromVector(this, R.drawable.currentlocationicon)
                            ).anchor(0.8f, 0.5f)
                        )
                    }

                }
            }

        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message.toString())
            Toast.makeText(
                this, resources.getString(R.string.location_is_not_avialable), Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkConnectivity() {
        isFirstTime = false
        if (!isNetworkAvailable()) {
            Toast.makeText(this, R.string.please_connect_to_the_internet, Toast.LENGTH_LONG).show()
        } else {
            if (!isLocationSerach) {
                getDeviceCurrentLocation()
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        var connectivityManager: ConnectivityManager? = null
        connectivityManager =
            getSystemService(HomeActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            ), LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onStart() {
        super.onStart()
        if (!checkLocationPermission()) requestLocationPermission()
    }

    // Check if the user has granted location permission
    private fun checkLocationPermission(): Boolean {
        val finePermissionState = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarsePermissionState = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return finePermissionState == PackageManager.PERMISSION_GRANTED && coarsePermissionState == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
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
                Toast.makeText(
                    this,
                    R.string.please_allow_location_permission_to_move_ahead,
                    Toast.LENGTH_SHORT
                ).show()
                permissions_denied = true
            }
            return
        }
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun checkLocationEnable() {
        if (!(getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(
                LocationManager.GPS_PROVIDER
            )
        ) {
            if (!isDialogShow) {
                showDialog(
                    R.string.gps_enable_message,
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                    101,
                    2
                )
            }
        } else {
            val gpsTracker = GPSTracker(this)
            latitude = gpsTracker.latitude.toDouble()
            longitude = gpsTracker.longitude.toDouble()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            checkLocationEnable()
            val gpsTracker = GPSTracker(this)
            latitude = gpsTracker.latitude.toDouble()
            longitude = gpsTracker.longitude.toDouble()
            getDeviceCurrentLocation()
            mapInitialization()
        }
    }


    override fun onResume() {
        super.onResume()
//        if (isFirstTime) checkConnectivity()
    }

    private fun showDialog(msg: Int, intent: Intent?, requestCode: Int, type: Int) {
        isDialogShow = true
        dialog = Dialog(this)
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
                val intent1 = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", "com.app.csc_mobile", null)
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
            Places.initialize(this, getString(R.string.google_map_key))
        }
        autoCompleteSupportFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autoCompleteSupportFragment.view?.findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_search_button)?.visibility =
            View.GONE

        (autoCompleteSupportFragment.view?.findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_search_input) as EditText).textSize =
            15f

        (autoCompleteSupportFragment.view?.findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_search_input) as EditText).setTextColor(
            ContextCompat.getColor(this, R.color.black)
        )

        (autoCompleteSupportFragment.view?.findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_search_input) as EditText).hint =
            getString(R.string.search_location)

        autoCompleteSupportFragment.setPlaceFields(
            listOf(
                Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG
            )
        )


        autoCompleteSupportFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status) {

            }

            override fun onPlaceSelected(place: Place) {
                mMap?.clear()
                val geocoder = Geocoder(this@AddLocationForServices)
                var addressList: List<Address> = ArrayList()
                try {
                    addressList = geocoder.getFromLocationName(place.toString(), 2)!!
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if (addressList.isNotEmpty()) {
                    val address: Address = addressList[0]
                    val fullAddress = address.getAddressLine(0)

                    //set source data in singleton

                    autoCompleteSupportFragment.setText(address.getAddressLine(0))
                    saveAddressRequest.addressActionType = "add"
                    saveAddressRequest.addressId = ""
                    saveAddressRequest.city = address.locality
                    saveAddressRequest.country = address.countryName
                    saveAddressRequest.fullAddress = address.getAddressLine(0)
                    saveAddressRequest.latitute = address.latitude.toString()
                    saveAddressRequest.longitute = address.longitude.toString()
                    saveAddressRequest.postalCode = address.postalCode

                    Log.e(
                        "TAG",
                        "getDeviceCurrentLocation: " + CommonUtils.getPostalCodeByCoordinates(
                            this, address.latitude, address.longitude, this@AddLocationForServices
                        )
                    )

                    val latLng = LatLng(address.latitude, address.longitude)
                    mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))
                    mMap?.addMarker(
                        MarkerOptions().position(latLng).icon(
                            bitmapDescriptorFromVector(
                                this@AddLocationForServices, R.drawable.currentlocationicon
                            )
                        )
                    )

                }
            }

        })
    }

    private fun setResultAndFinish() {
        val resultIntent = Intent()
        resultIntent.putExtra("LocationResult", Gson().toJson(saveAddressRequest))
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    override fun onMapReady(mMap: GoogleMap) {
        this.mMap = mMap
    }

}