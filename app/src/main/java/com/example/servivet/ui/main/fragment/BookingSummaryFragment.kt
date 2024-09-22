package com.example.servivet.ui.main.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.add_service.request.ServiceListSlot
import com.example.servivet.data.model.booking_detail.response.BookingDetail
import com.example.servivet.data.model.booking_module.booking_slot.BookedSlot
import com.example.servivet.data.model.booking_module.booking_summary.response.AtCenterAvailability
import com.example.servivet.data.model.booking_module.booking_summary.response.AtHomeAvailability
import com.example.servivet.data.model.booking_module.booking_summary.response.ProviderLeave
import com.example.servivet.data.model.booking_module.booking_summary.response.ServiceDetail
import com.example.servivet.data.model.date_model.DateModel
import com.example.servivet.databinding.FragmentBookingSummaryBinding
import com.example.servivet.databinding.SaveAndChangeAddressBottomsheetBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.BookingTimeAdapter
import com.example.servivet.ui.main.adapter.CalenderRecyclerAdapter
import com.example.servivet.ui.main.view_model.booking_models.BookingSlotViewModel
import com.example.servivet.ui.main.view_model.booking_models.BookingSummaryViewModel
import com.example.servivet.ui.main.view_model.booking_models.RescheduleBookingViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.dayMonthYearFromDate
import com.example.servivet.utils.CommonUtils.findListIndex
import com.example.servivet.utils.CommonUtils.getDateFromToday
import com.example.servivet.utils.CommonUtils.getDayOfWeek
import com.example.servivet.utils.CommonUtils.setCurrentDate
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.example.servivet.utils.compareTwoDates
import com.example.servivet.utils.generateMonthStrings
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BookingSummaryFragment :
    BaseFragment<FragmentBookingSummaryBinding, BookingSummaryViewModel>(R.layout.fragment_booking_summary),
    CalenderRecyclerAdapter.OnCurrentMonthChange {
    override val binding: FragmentBookingSummaryBinding by viewBinding(FragmentBookingSummaryBinding::bind)
    override val mViewModel: BookingSummaryViewModel by viewModels()
    private val slotViewModel: BookingSlotViewModel by viewModels()
    private val rescheduleBookingViewModel: RescheduleBookingViewModel by viewModels()
    private var calendarList: java.util.ArrayList<DateModel> = java.util.ArrayList<DateModel>()
    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val cal = Calendar.getInstance(Locale.ENGLISH)
    var pos = 0
    var monthCount = 0
    private var homePosition = 0
    private var centerPosition = 0

    @RequiresApi(Build.VERSION_CODES.O)
    private var date = setCurrentDate()
    private lateinit var bookingDetails: BookingDetail
    private val dates = java.util.ArrayList<Date>()
    private var atHome = false
    private val argumetData: BookingSummaryFragmentArgs by navArgs()
    lateinit var adapter: CalenderRecyclerAdapter
    private lateinit var serviceDetail: ServiceDetail
    private lateinit var providerLeave: ProviderLeave
    private var atCenterList = ArrayList<AtCenterAvailability>()
    private var atHomeList = ArrayList<AtHomeAvailability>()
    private var bookedSlot = ArrayList<BookedSlot>()
    private var bottomSheetDialog: BottomSheetDialog? = null
    private lateinit var serviceId: String


    override fun isNetworkAvailable(boolean: Boolean) {
    }

    @RequiresApi(Build.VERSION_CODES.O)

    override fun setupViewModel() {

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setupViews() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction(requireContext(), binding)
            clickEvents = ::onClick
        }
        //   setadapter()


        initRescheduleModel()


        binding.changelocation.setOnClickListener { findNavController().navigate(R.id.action_bookingSummaryFragment_to_savedAddressesBottomsheet) }
    }


    private fun onClick(type: String) {
        when (type) {
            getString(R.string.save) -> {
                rescheduleBookingViewModel.getRescheduleRequest(
                    mViewModel.result.serviceDetail,
                    bookingDetails
                )

            }
        }
    }


    private fun getArgumentData() {
        when (argumetData.from) {
            getString(R.string.reschedule) -> {
                bookingDetails = Gson().fromJson(argumetData.data, BookingDetail::class.java)
                binding.idHeading.text = getString(R.string.reschedule)
                //binding.idLayoutTopContainer.visibility = View.INVISIBLE
                binding.idProceedBtn.isVisible = false
                binding.idSaveBtn.isVisible = true
                binding.idLayoutTopContainer.isVisible = false
                serviceId = bookingDetails.serviceId


            }

            getString(R.string.booking_details) -> {
                bookingDetails = Gson().fromJson(argumetData.data, BookingDetail::class.java)
                serviceId = bookingDetails.serviceId
            }

            getString(R.string.sub_category) -> {
                serviceId = argumetData.data

            }
        }
    }

    private fun openBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        val bottomSheetBinding: SaveAndChangeAddressBottomsheetBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.save_and_change_address_bottomsheet,
            null,
            false
        )
        if (Session.saveAddress != null) {
            bottomSheetBinding.name.text = Session.saveAddress.name
            bottomSheetBinding.address.text = Session.saveAddress.fullAddress
            bottomSheetBinding.number.text = Session.saveAddress.mobileNumber
        }
        bottomSheetBinding.useSameAddress.setOnClickListener {
            bottomSheetDialog!!.dismiss()
        }

        bottomSheetBinding.addNewAddress.setOnClickListener {
            findNavController().navigate(R.id.action_bookingSummaryFragment_to_addLocationFragment)
            bottomSheetDialog!!.dismiss()
        }
        bottomSheetDialog!!.show()
    }


    private fun initYearAdapter() {
        val items = generateMonthStrings()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.idYearSpinner.adapter = adapter
        binding.idYearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                monthCount = position
                setDate()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle situation where nothing is selected
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDate() {
        val list = ArrayList<DateModel>()
        for (i in getDateFromToday(monthCount).indices) {
            list.add(DateModel(getDateFromToday(monthCount)[i]!!, false))
        }
        list[0].isToday = true
        adapter = CalenderRecyclerAdapter(requireContext(), list, pos, this, onItemClick)
        binding.recyclerview.adapter = adapter
    }


    /*
        fun setDate() {
            calendarList = java.util.ArrayList<DateModel>()
            val calendarList2: java.util.ArrayList<DateModel> = java.util.ArrayList<DateModel>()
            binding.tvDateMonth.setText(sdf.format(cal.time))
            val monthCalendar = cal.clone() as Calendar
            val maxDaysInMonth: Int = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            dates.clear()
            monthCalendar[Calendar.DAY_OF_MONTH] = 1
            while (dates.size < maxDaysInMonth) {
                dates.add(monthCalendar.time)
                calendarList.add(DateModel(monthCalendar.time.toString()))
                monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            calendarList2.addAll(calendarList)
            for (i in calendarList2.indices) {
                if (CommonUtils.getCurrentDate().equals(
                        dayMonthYearFromDate(
                            calendarList2[i].date.toString()
                        )
                    )
                ) {
                    calendarList2[i].isToday=true
                    pos = i
                }
            }

            Log.d("TAG", "setDate: "+Gson().toJson(calendarList))


           // adapter=CalenderRecyclerAdapter(requireContext(),calendarList2,this,pos)
            adapter=CalenderRecyclerAdapter(requireContext(),calendarList2,pos,this)
            binding.recyclerview.adapter=adapter
            binding.recyclerview.scrollToPosition(pos)


            binding.recyclerview.adapter=CalenderRecyclerAdapter(requireContext(), calendarList2 ,pos,this)
            binding.recyclerview.scrollToPosition(pos)

        }
    */


    /*   fun setadapter(){
           adapter=CalenderRecyclerAdapter(context)
       }
    */

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setupObservers() {
        getArgumentData()
        mViewModel.getReportRatingRequest(serviceId)
        mViewModel.getSummaryData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            mViewModel.result = it.data.result
                            serviceDetail = (it.data.result.serviceDetail ?: "") as ServiceDetail
                            if (it.data.result.providerLeaveList != null) {
                                providerLeave =
                                    (it.data.result.providerLeaveList ?: "") as ProviderLeave
                            }

                            it.data.result.serviceDetail?.atHomeAvailability?.let { homeList ->
                                atHomeList.addAll(homeList)
                                mViewModel.result.serviceDetail?.date = setCurrentDate()
                                this.homePosition =
                                    findListIndex(atHomeList.indexOfFirst { it.day == getDayOfWeek() })
                            }

                            it.data.result.serviceDetail?.atCenterAvailability?.let { centerList ->
                                atCenterList.addAll(centerList)
                                mViewModel.result.serviceDetail?.date = setCurrentDate()
                                this.centerPosition =
                                    findListIndex(atCenterList.indexOfFirst { it.day == getDayOfWeek() })

                            }




                            binding.summaryData = serviceDetail
                            checkMode()
                            initYearAdapter()
                            setDate()
                            setPriceValue()
                            initSlotModel()
                            Log.e("TAG", "setupObservers: ${Gson().toJson(it.data.result ?: "")}")
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(it.data.message!!)
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

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun setPriceValue() {

        mViewModel.atHome.observe(viewLifecycleOwner) {
            Log.e("TAG", "setupObserver123: ${it}")
            if (it) {
                atHome = true
                binding.amount.text = "ZAR" + " " + serviceDetail.atHomePrice
                mViewModel.result.serviceDetail?.serviceModeLocal = getString(R.string.athome)
                binding.addAddressLayout.isVisible = Session.saveAddress == null
                if (Session.saveAddress != null)
                    serviceDetail.addressLocal = Session.saveAddress.fullAddress
                binding.changeAddressLayout.isVisible = Session.saveAddress != null
                initSlotModel()
            }
        }
        mViewModel.atCenter.observe(viewLifecycleOwner) {
            if (it) {
                atHome = false
                binding.amount.text =  "ZAR"+ " " + serviceDetail.atCenterPrice
                mViewModel.result.serviceDetail?.serviceModeLocal = getString(R.string.atcenter)
                binding.addAddressLayout.isVisible = false
                serviceDetail.addressLocal = ""
                binding.changeAddressLayout.isVisible = false
                initSlotModel()
            }
        }
    }
    data class DataA(val id: Int, val count: Int, val name: String)
    data class DataB(val id: Int, val count: Int, val value: String)


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initSlotModel() {
        slotViewModel.getSlotRequest(
            serviceId,
            date,
            mViewModel.result.serviceDetail?.serviceModeLocal
        )
        slotViewModel.getSlotData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            Log.e("TAG", "initSlotModel:${Gson().toJson(it.data.result)} ")
                            bookedSlot.clear()
                            bookedSlot.addAll(it.data.result.bookedSlot)
                            if (!atHome) {
                                if (this.centerPosition != -1) {

                                    binding.checkVisibility = true
                                    binding.idNoDataFound.root.isVisible = false
                                    mViewModel.result.serviceDetail?.day =
                                        atCenterList[centerPosition].day
                                    binding.sloatAdaper = BookingTimeAdapter(
                                        requireContext(),
                                        atCenterList[centerPosition].slot,
                                        bookedSlot,
                                        onItemClick,
                                        date
                                    )
                                } else {
                                    binding.idNoDataFound.root.isVisible = true
                                    binding.checkVisibility = false
                                }
//                                if(atHome){
//                                    mViewModel.result.serviceDetail?.day = atHomeList[homePosition].day
//                                }else {
//                                    mViewModel.result.serviceDetail?.day = atCenterList[centerPosition].day
//                                }
//                                if(atHome){
//                                    binding.sloatAdaper = BookingTimeAdapter(requireContext(), atHomeList[homePosition].slot!!, bookedSlot, onItemClick)
//                                }else {
//                                    binding.sloatAdaper = BookingTimeAdapter(requireContext(), atCenterList[centerPosition].slot, bookedSlot, onItemClick)
//                                }

                            } else if (atHome) {
                                if (this.homePosition != -1) {
                                    binding.checkVisibility = true
                                    binding.idNoDataFound.root.isVisible = false
                                    mViewModel.result.serviceDetail?.day =
                                        atHomeList[homePosition].day
                                    binding.sloatAdaper = BookingTimeAdapter(
                                        requireContext(),
                                        atHomeList[homePosition].slot!!,
                                        bookedSlot,
                                        onItemClick,
                                        date
                                    )
                                } else {
                                    binding.idNoDataFound.root.isVisible = true
                                    binding.checkVisibility = false
                                }
                            } else {
                                binding.idNoDataFound.root.isVisible = true
                                binding.checkVisibility = false
                            }

                            /* if (this.centerPosition != -1 || this.homePosition !=-1) {
                                 binding.checkVisibility = true
                                 binding.idNoDataFound.root.isVisible = false
                                 if(atHome){
                                     mViewModel.result.serviceDetail?.day = atHomeList[homePosition].day
                                 }else {
                                     mViewModel.result.serviceDetail?.day = atCenterList[centerPosition].day
                                 }
                                 if(atHome){
                                     binding.sloatAdaper = BookingTimeAdapter(requireContext(), atHomeList[homePosition].slot!!, bookedSlot, onItemClick)
                                 }else {
                                     binding.sloatAdaper = BookingTimeAdapter(requireContext(), atCenterList[centerPosition].slot, bookedSlot, onItemClick)
                                 }
                             } else {
                                 binding.idNoDataFound.root.isVisible = true
                                 binding.checkVisibility = false

                             }*/
                            getArgumentData()

                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(it.data.message!!)
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

    private fun initRescheduleModel() {

        rescheduleBookingViewModel.getRescheduleData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            showSnackBar(it.data.message!!)
                            findNavController().popBackStack()


                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(it.data.message!!)
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

    override fun onMonthChange(month: String?) {

    }

    override fun onDateClick(date: String?) {
        Log.d("TAG", "onDahgfteClick: " + Gson().toJson(date))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val onItemClick: (String, String, String) -> Unit = { position, name, data ->
        when (name) {
            getString(R.string.calendar) -> {
                date = dayMonthYearFromDate(data) ?: ""
                Log.e("TAG", "checkDate:${data} ")
                Log.e("TAG", "checkDate:${date} ")
                if (atHome) {
                    this.homePosition =
                        findListIndex(atHomeList.indexOfFirst { it.day == position })

                } else {
                    this.centerPosition =
                        findListIndex(atCenterList.indexOfFirst { it.day == position })

                }
                if (::providerLeave.isInitialized) {
                    Log.e("TAG", "leave:${Gson().toJson(providerLeave)} ")
                    if (!compareTwoDates(
                            providerLeave.leaveStartDate,
                            providerLeave.leaveEndDate,
                            date
                        )
                    ) {
                        mViewModel.result.serviceDetail?.date = date
                        binding.timeRecycler.isVisible = true
                        initSlotModel()
                    } else {
                        binding.timeRecycler.isVisible = false
                        Toast.makeText(
                            requireContext(),
                            "sloat not found on this date",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    if (atHome) {
                        mViewModel.result.serviceDetail?.date = date
                        initSlotModel()
                    } else {
                        mViewModel.result.serviceDetail?.date = date
                        initSlotModel()
                    }
                }
            }

            getString(R.string.slot) -> {
                val data = Gson().fromJson(data, ServiceListSlot::class.java)
                Log.e("TAG", "checkData:${Gson().toJson(data)} ")
                mViewModel.result.serviceDetail?.startTime = data.startTime
                mViewModel.result.serviceDetail?.endTime = data.endTime
                mViewModel.result.serviceDetail?.slotId = position

            }

        }
    }

    private fun checkMode() {
        if (serviceDetail.serviceMode?.atHome!!) {
            if (Session.saveAddress != null) {
                binding.addAddressLayout.visibility = View.GONE
                binding.changeAddressLayout.visibility = View.VISIBLE
                binding.nameInAddress.text = Session.saveAddress.name
                binding.locationName.text = Session.saveAddress.fullAddress
            } else {
                binding.addAddressLayout.visibility = View.VISIBLE
                binding.changeAddressLayout.visibility = View.GONE
            }
        }
    }

}