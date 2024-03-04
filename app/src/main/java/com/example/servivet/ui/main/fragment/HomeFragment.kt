package com.example.servivet.ui.main.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.home.request.NearByProviderRequest
import com.example.servivet.data.model.home.response.HomeBanner
import com.example.servivet.data.model.home.response.HomeServiceCategory
import com.example.servivet.data.model.home.response.nearbyprovider.NearByProviderResponse
import com.example.servivet.data.model.home.response.nearbyprovider.Provider
import com.example.servivet.data.model.notification_data.NotificationData
import com.example.servivet.databinding.FragmentHomeBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.NearByProviderAdapter
import com.example.servivet.ui.main.adapter.HomeOnlineNowAdapter
import com.example.servivet.ui.main.adapter.HomePagerAdapter
import com.example.servivet.ui.main.adapter.HomeServiceAdapter
import com.example.servivet.ui.main.view_model.HomeViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.CommonUtils.showToast
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.SocketManager
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson
import io.socket.client.Socket
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {

    override val binding: FragmentHomeBinding by viewBinding(FragmentHomeBinding::bind)
    override val mViewModel: HomeViewModel by activityViewModels()
    private lateinit var mContext: Context
    private lateinit var socket: Socket
    private var nearByRequest = NearByProviderRequest()
    private lateinit var nearByProviderResponse: NearByProviderResponse
    private var providerList = ArrayList<Provider>()
    private val type = 1

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun isNetworkAvailable(boolean: Boolean) {}

    override fun setupViewModel() {
        if (isAdded)
            binding.apply {
                lifecycleOwner = viewLifecycleOwner
                viewModel = mViewModel
                click = mViewModel.ClickAction()
                clickEvents = ::onClick
                setLocationValue()

                setBack()
                val data = Gson().fromJson(Session.notificationData, NotificationData::class.java)

                if (Session.notificationData != null && data.bookingId != null) {
                    findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToBookingDetailsFragment(
                            Session.notificationData,
                            data.serviceStatus!!.minus(1),
                            "",
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

    private fun setLocationValue() {
        if (Session.saveLocationInfo != null) {
            binding.shortAddress.text = Session.saveLocationInfo.smallLocation
            binding.fullAddress.text = Session.saveLocationInfo.location
        }
    }


    private fun onClick(type: String) {
        when (type) {
            getString(R.string.provider_view_all) -> {
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
                            showToast(it.data.message)
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
                            showSnackBar(it.data.message)
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


            Log.e("TAG", "initSocket:${socket.connected()} ")
            initSocketEvents()

            val data = JSONObject()
            data.put("userId", Session.userDetails._id)
            data.put("latitude", 28.6230811)
            data.put("longitude", 77.2975935)
            data.put("page", 1)
            data.put("limit", 10)

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
                            providerList.addAll(nearByProviderResponse.result.providerList)
                            initProviderAdapter()


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

        binding.adapter = NearByProviderAdapter(requireContext(), providerList, onItemClick, type)
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
                    dialog.setPositiveButton("Ok") { dialog, which ->
                        dialog.dismiss()
                        activity?.finishAffinity()
                    }
                    dialog.setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }

                    dialog.show()
                }
            }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, callback)
    }

}