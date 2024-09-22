package com.example.servivet.ui.main.fragment

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.home.request.NearByProviderRequest
import com.example.servivet.data.model.home.response.HomeBanner
import com.example.servivet.data.model.home.response.HomeServiceCategory
import com.example.servivet.data.model.home.response.nearbyprovider.NearByProviderResponse
import com.example.servivet.data.model.home.response.nearbyprovider.Provider
import com.example.servivet.data.model.location.LocationInfo
import com.example.servivet.data.model.notification_data.NotificationData
import com.example.servivet.databinding.FragmentHomeBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.HomePagerAdapter
import com.example.servivet.ui.main.adapter.HomeServiceAdapter
import com.example.servivet.ui.main.adapter.NearByProviderAdapter
import com.example.servivet.ui.main.view_model.HomeViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.CommonUtils.showToast
import com.example.servivet.utils.GPSTracker
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.SocketManager
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import io.socket.client.Socket
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {

    private var permissions_denied: Boolean = false
    override val binding: FragmentHomeBinding by viewBinding(FragmentHomeBinding::bind)
    override val mViewModel: HomeViewModel by activityViewModels()
    private lateinit var mContext: Context
    private lateinit var socket: Socket
    private var nearByRequest = NearByProviderRequest()
    private lateinit var nearByProviderResponse: NearByProviderResponse
    private var providerList = ArrayList<Provider>()
    private val type = 1
    private var longitude: Double = 0.0
    private var lattitude: Double = 0.0
    private var fullAddress: String? = ""
    private var dialog: Dialog? = null
    private var isDialogShow: Boolean = false
    private val LOCATION_PERMISSION_REQUEST_CODE: Int = 101
    var boolean: Boolean = false
    val msg = R.string.allow_gps_location // Replace with your message resource ID
    val intent: Intent? = null // Replace null with your intent
    val requestCode = 1 // Replace with your request code
    val type1 = 1 // Replace with your type
    private lateinit var locationManager: LocationManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun isNetworkAvailable(boolean: Boolean) {}
    override fun onStart() {
        super.onStart()
        if (SearchLocationFragment.isSearchLocation == true) {
            setLocationValue()
            SearchLocationFragment.isSearchLocation = false
        } else {
            if (!checkLocationPermission())
                requestLocationPermission()

            if (isLocationEnabled()) {
                getCurrentLocation()
                setLocationValue()
            } else {
                showDialog(msg, intent, requestCode, type1)
            }
        }
    }
    // Check if the user has granted location permission

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
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
                getCurrentLocation()
                setLocationValue()
                initSocket()
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.please_allow_location_permission_to_move_ahead,
                    Toast.LENGTH_SHORT
                ).show()
                permissions_denied = true
            }
            return
        }
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigation =
            requireActivity().findViewById<BottomNavigationView>(R.id.navigation_bar)
        val menu = bottomNavigation.menu

        val languageSelectInPreference =
            Session.language   // viewModel.preference.retrieveLanguage(languageKey)

        if (languageSelectInPreference != null) {
            val titleMap = changeLocale(requireContext(), languageSelectInPreference)
            // Update titles for each menu item based on the selected language
            titleMap.forEach { (itemId, title) ->
                menu.findItem(itemId).title = title
            }
        }


//        if (Session.notificationData!=null && Session.notificationData.isNotEmpty() && data.bookingId != null) {
//            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToBookingDetailsFragment(Session.notificationData, data.serviceStatus!!.minus(1), data.userType?:"", getString(R.string.home)))
//        } else {
//            activity?.let {
//                mViewModel.hitHomeApi(
//                    mContext,
//                    it,
//                    activity?.isFinishing == true
//                )
//            }
//
//        }
    }


    fun changeLocale(context: Context, lang: String?): Map<Int, String?> {
        Session.language
        // viewModel.preference.retrieveLanguage(languageKey)
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)
        configuration.locale = locale
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)

        // Return a map of menu item IDs to their localized titles
        return mapOf(
            R.id.homeFragment to getString(R.string.home),
            R.id.bookingsFragment to getString(R.string.bookings),
            R.id.chatFragment to getString(R.string.chats),
            R.id.profileFragment to getString(R.string.profile)
            // Add more items as needed
        )
    }

    override fun setupViewModel() {
        if (isAdded)
            binding.apply {
                lifecycleOwner = viewLifecycleOwner
                viewModel = mViewModel
                click = mViewModel.ClickAction()
                clickEvents = ::onClick
                /*if(!isLocationEnabled())
                    showDialog(msg,intent,requestCode,type1)
                else{
                    getCurrentLocation()
                    setLocationValue()
                }*/
                //  setLocationValue()
                /* if(checkLocationPermission()){
                    requestLocationPermission()
                 }*/
                /*
                                if(Session.saveLocationInfo ==null){
                                    getCurrentLocation()
                                }*/
                if (Session.saveLocationInfo != null) {
                    setLocationValue()
                }
                setBack()
                val data = Gson().fromJson(Session.notificationData, NotificationData::class.java)

                if (Session.notificationData != null && Session.notificationData.isNotEmpty() && data.bookingId != null) {
                    findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToBookingDetailsFragment(
                            Session.notificationData,
                            data.serviceStatus!!.minus(1),
                            data.userType ?: "",
                            getString(R.string.home)
                        )
                    )
                } else {
                    activity?.let {
                        mViewModel.hitHomeApi(
                            mContext,
                            it,
                            activity?.isFinishing == true
                        )
                    }

                }


//                if (NOTIFICATION_DATA == null) {
//                    activity?.let {
//                        mViewModel.hitHomeApi(mContext, it, activity?.isFinishing == true)
//                    }
//                } else {
//                    findNavController().navigate(
//                        HomeFragmentDirections.actionHomeFragmentToBookingDetailsFragment(
//                            "",
//                            NOTIFICATION_DATA.serviceStatus?.minus(1) ?:0,
//                            "",
//                            "Home"
//                        )
//                    )
//                }
                setOnlineAdapter("1")
                setClick()
            }
        Log.e("TAG", "setupViewModelFCM: ${Session.fcmToken}")
        Log.e("TAG", "setupViewModelDAtaVALL: ${Session.userDetails}")

    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), LOCATION_PERMISSION_REQUEST_CODE
        )
    }
    private fun isLocationEnabled(): Boolean {
        boolean = true
        val locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    private fun checkLocationPermission(): Boolean {
        val finePermissionState = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarsePermissionState = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return finePermissionState == PackageManager.PERMISSION_GRANTED && coarsePermissionState == PackageManager.PERMISSION_GRANTED
    }


    private fun getCurrentLocation() {
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsTracker = GPSTracker(requireActivity())
        lattitude = gpsTracker.latitude.toDouble()
        longitude = gpsTracker.longitude.toDouble()

        try {
            var geocoder = Geocoder(requireContext());
            val adr: List<Address> = geocoder.getFromLocation(lattitude, longitude, 1)!!
            fullAddress = adr[0].getAddressLine(0)
            lattitude = adr[0].latitude
            lattitude = adr[0].longitude
            binding.fullAddress.text = fullAddress
            Log.d(
                "TAG",
                "getCugfdrrentLocation:$fullAddress ${
                    LocationInfo(
                        adr[0].getAddressLine(0),
                        adr[0].featureName
                    )
                } "
            )
            Session.saveLocationInfo(
                LocationInfo(
                    adr[0].getAddressLine(0),
                    adr[0].featureName,
                    adr[0].latitude.toString(),
                    adr[0].longitude.toString()
                )
            )

        } catch (e: Exception) {
            Log.e("TAG", "getCurrentLocation: " + e)
        }
    }


    private fun setLocationValue() {
        if (Session.saveLocationInfo != null) {
            binding.shortAddress.text = Session.saveLocationInfo.smallLocation
            binding.fullAddress.text = Session.saveLocationInfo.location
        }
    }

    private fun onClick(type: String) {
        when (type) {
            getString(R.string.provider_view_all) -> {
                if (!this::nearByProviderResponse.isInitialized) {
                    nearByProviderResponse = NearByProviderResponse(null, null, null)
                }
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToOnlineNowFragment(
                        Gson().toJson(nearByProviderResponse),
                        getString(R.string.home_fr)
                    )
                )
            }

        }
    }

    private fun setOnlineAdapter(type: String) {
        //  binding.idNearByUser.adapter = HomeOnlineNowAdapter(type, requireContext(), ArrayList())
    }

    private fun setClick() {
        binding.notification.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
        }
        binding.viewAll.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_servicesFragment)
        }
        binding.addservice.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addServiceFragment)
        }
        binding.locationLayout.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchLocationFragment)
        }
    }

    private fun setServiceAdapter(type: String, list: List<HomeServiceCategory>) {
        binding.homeServiceAdapter = HomeServiceAdapter(requireContext(), type, list)
        // binding.serviceRecycler.adapter = HomeServiceAdapter(requireContext(), type, list)

    }
    private fun setViewPagerAdapter(banner: List<HomeBanner>) {
        binding.viewPager.adapter = HomePagerAdapter(requireContext(), banner)
        binding.viewPager.pageMargin = 20
        binding.tabLayout.setupWithViewPager(binding.viewPager)
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
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                //  val uri = Uri.fromParts("package", "com.app.csc_mobile", null)
                //intent1.data = uri
                startActivity(intent1)
            } else {
                startActivityForResult(intent!!, requestCode)
            }
        }
        dialog!!.show()
    }


    override fun setupViews() {
    }

    override fun setupObservers() {
        mViewModel.currentResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            Log.e("TAG", "setupObservers: ${Gson().toJson(it.data.result.user)}")
                            Session.saveUserDetails(it.data.result.user)
                            Session.saveMasterData(it.data.result.masterData)
                            Session.type = it.data.result.user.role.toString()
                            setVisibility(it.data.result.user.role)
                            if (isAdded)
                                initSocket()
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showToast(it.data.message ?: "Something went wrong")
                        }

                    }
                }

                Status.LOADING -> {
                    ProcessDialog.dismissDialog()
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog()
                    it.message?.let {
                        showSnackBar(it)
                    }
                }

                Status.UNAUTHORIZED -> {
                    ProcessDialog.dismissDialog()
                    it.message?.let {
                        showSnackBar(it)
                    }
                }
            }
        }

        mViewModel.homeResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            //set service adapter
                            if (it.data.result.serviceCategory != null && it.data.result.serviceCategory!!.isNotEmpty()) {
                                setServiceAdapter("1", it.data.result.serviceCategory)
                                binding.viewPagerLayout.visibility = View.VISIBLE
                                binding.singleBannerImage.visibility = View.GONE

                            } else {
                                binding.viewPagerLayout.visibility = View.GONE
                                binding.singleBannerImage.visibility = View.VISIBLE
                            }
                            //set banner viewpager
                            if (it.data.result.banner != null && it.data.result.banner!!.isNotEmpty()) {
                                setViewPagerAdapter(it.data.result.banner)
                            }
                            activity?.let { activity ->
                                mViewModel.hitCurrentApi(
                                    requireContext(),
                                    activity,
                                    activity?.isFinishing == true
                                )
                            }

                            Session.saveCategory(it.data.result.serviceCategory)
                        }
                        StatusCode.STATUS_CODE_FAIL -> {
                            //showSnackBar(it.data.message)
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

                Status.UNAUTHORIZED -> {
                    activity?.let { activity ->
                        CommonUtils.logoutAlert(
                            requireContext(),
                            "Session Expired",
                            "Unauthorized User",
                            activity
                        )
                    }
                }
            }
        }
    }

    private fun initSocket() {
        try {
            SocketManager.connect()
            socket = SocketManager.getSocket()
            Log.e("TAG", "initSocketCheckConnect: ${socket.connected()}")
            Log.e("TAG", "checkLocationInfo: ${Session.saveLocationInfo}")
            Log.e("TAG", "initSocket:${socket.connected()} ")

            initSocketEvents()
            val data = JSONObject()
            data.put("userId", Session.userDetails._id)
            data.put("latitude", Session.saveLocationInfo?.latitude?.toDouble() ?: "")
            data.put("longitude", Session.saveLocationInfo?.longitude?.toDouble() ?: "")
            data.put("page", 1)
            data.put("limit", 5)
            socket.emit("nearByProvider", data)
            socket.on("nearByProvider", fun(args: Array<Any?>) {
                if (isAdded) {
                    requireActivity().runOnUiThread {
                        val providerData = args[0] as JSONObject
                        try {
                            nearByProviderResponse = Gson().fromJson(
                                JSONArray().put(providerData)[0].toString(),
                                NearByProviderResponse::class.java
                            )
                            Log.e(
                                "TAG",
                                "nearByProviderResponse:${Gson().toJson(nearByProviderResponse)} ",
                            )
                            providerList.clear()
                            nearByProviderResponse.result?.providerList?.let {
                                if (isAdded) {
                                    providerList.addAll(it)
                                    initProviderAdapter()
                                }
                            }
                        } catch (ex: JSONException) {
                            ex.printStackTrace()
                        }
                    }
                }
            })

        } catch (ex: Exception) {

        }


    }

    private fun initSocketEvents() {
        val data = JSONObject()
        data.put("userId", Session.userDetails._id)
        socket.emit("online", data)
        socket.on("online", fun(args: Array<Any?>) {
            if (isAdded) {
//                requireActivity().runOnUiThread{
//                    val onlineData = args[0] as JSONObject
//                    try {
//
//                    }
//                    catch (ex:JSONException){
//                        ex.printStackTrace()
//                    }
//
//                }
            }
        })

        // internal server error
        socket.on("internalServer_error", fun(args: Array<Any?>) {
            if (isAdded) {
                requireActivity().runOnUiThread {
                    val onlineData = args[0] as JSONObject
                    try {

                    } catch (ex: JSONException) {
                        ex.printStackTrace()
                    }

                }
            }
        })

        //invalidData
        socket.on("invalidData", fun(args: Array<Any?>) {
            if (isAdded) {
                requireActivity().runOnUiThread {
                    val onlineData = args[0] as JSONObject
                    try {

                    } catch (ex: JSONException) {
                        ex.printStackTrace()
                    }

                }
            }
        })
    }

    private fun initProviderAdapter() {
        if (isAdded)
            binding.adapter =
                NearByProviderAdapter(requireContext(), providerList, onItemClick, type)
    }
    private fun setVisibility(role: Int) {
        if (role == 1) {
            binding.onlinenowLayout.visibility = View.VISIBLE
            binding.idByProviderContainer.visibility = View.GONE
            binding.addServiceLayout.visibility = View.GONE

        } else {
            binding.onlinenowLayout.visibility = View.GONE
            binding.idByProviderContainer.visibility = View.VISIBLE
            binding.addServiceLayout.visibility = View.VISIBLE
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Toast.makeText(context, "testing", Toast.LENGTH_SHORT).show()
    }

    private val onItemClick: (String, String) -> Unit = { identifire, data ->
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToProviderProfileFragment(
                data,
                getString(R.string.home_fr)
            )
        )
    }


    private fun setBack() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val dialog = AlertDialog.Builder(activity)
                    dialog.setMessage(mContext.getString(R.string.are_you_logout))
                    dialog.setTitle(mContext.resources.getString(R.string.app_name))
                    dialog.setPositiveButton(R.string.ok) { dialog, which ->
                        dialog.dismiss()
                        activity?.finishAffinity()
                    }
                    dialog.setNegativeButton(R.string.cancel) { dialog, which ->
                        dialog.dismiss()
                    }
                    dialog.show()
                }
            }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, callback)
    }

}