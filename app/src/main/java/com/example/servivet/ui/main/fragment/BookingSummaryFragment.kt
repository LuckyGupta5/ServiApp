package com.example.servivet.ui.main.fragment

import android.os.Build
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.booking_module.booking_slot.BookedSlot
import com.example.servivet.data.model.booking_module.booking_summary.response.AtCenterAvailability
import com.example.servivet.data.model.booking_module.booking_summary.response.ServiceDetail
import com.example.servivet.data.model.date_model.DateModel
import com.example.servivet.databinding.FragmentBookingSummaryBinding
import com.example.servivet.databinding.SaveAndChangeAddressBottomsheetBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.BookingTimeAdapter
import com.example.servivet.ui.main.adapter.CalenderRecyclerAdapter
import com.example.servivet.ui.main.view_model.booking_models.BookingSlotViewModel
import com.example.servivet.ui.main.view_model.booking_models.BookingSummaryViewModel
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
    private var calendarList: java.util.ArrayList<DateModel> = java.util.ArrayList<DateModel>()
    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val cal = Calendar.getInstance(Locale.ENGLISH)
    var pos = 0
    var monthCount = 0
    private var position = 0

    @RequiresApi(Build.VERSION_CODES.O)
    private var date = setCurrentDate()
    private val dates = java.util.ArrayList<Date>()
    private val serviceId: BookingSummaryFragmentArgs by navArgs()
    lateinit var adapter: CalenderRecyclerAdapter
    private lateinit var serviceDetail: ServiceDetail
    private var atCenterList = ArrayList<AtCenterAvailability>()
    private var bookedSlot = ArrayList<BookedSlot>()
    private var bottomSheetDialog: BottomSheetDialog? = null


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
        }
        //   setadapter()


        initYearAdapter()
        setDate()

        if(Session.saveAddress!=null ){
            binding.addAddressLayout.visibility=View.GONE
            binding.changeAddressLayout.visibility=View.VISIBLE
            binding.nameInAddress.text = Session.saveAddress.name
            binding.locationName.text = Session.saveAddress.fullAddress
        }else{
            binding.addAddressLayout.visibility=View.VISIBLE
            binding.changeAddressLayout.visibility=View.GONE
        }
        binding.changelocation.setOnClickListener{ findNavController().navigate(R.id.action_bookingSummaryFragment_to_savedAddressesBottomsheet) }
    }

    private fun openBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        val bottomSheetBinding: SaveAndChangeAddressBottomsheetBinding = DataBindingUtil.inflate(layoutInflater, R.layout.save_and_change_address_bottomsheet, null, false)

        if(Session.saveAddress!=null ){
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
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
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
       mViewModel.getReportRatingRequest(serviceId.data)
        mViewModel.getSummaryData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            mViewModel.result = it.data.result
                            serviceDetail = (it.data.result.serviceDetail?:"") as ServiceDetail
                            it.data.result.serviceDetail?.atCenterAvailability?.let { centerList ->
                                atCenterList.addAll(centerList)
                                mViewModel.result.serviceDetail?.date = setCurrentDate()
                                this.position = findListIndex(atCenterList.indexOfFirst { it.day == getDayOfWeek() })
                            }
                            binding.summaryData = serviceDetail
                            initSlotModel()
                            setPriceValue()
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

    private fun setPriceValue() {
        mViewModel.atHome.observe(viewLifecycleOwner) {
            Log.e("TAG", "setupObserver123: ${it}")
            if (it) {
                binding.amount.text = serviceDetail.atHomePrice
                mViewModel.result.serviceDetail?.serviceModeLocal = getString(R.string.at_home)
            }
        }
        mViewModel.atCenter.observe(viewLifecycleOwner) {
            if (it) {
                binding.amount.text = serviceDetail.atCenterPrice
                mViewModel.result.serviceDetail?.serviceModeLocal= getString(R.string.at_center)
            }
        }
    }

    data class DataA(val id: Int, val count: Int, val name: String)
    data class DataB(val id: Int, val count: Int, val value: String)


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initSlotModel() {
        slotViewModel.getSlotRequest(serviceId.data, date)
        slotViewModel.getSlotData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {

                            bookedSlot.clear()
                            bookedSlot.addAll(it.data.result.bookedSlot)
                            if (this.position != -1) {
                                binding.timeRecycler.adapter = BookingTimeAdapter(requireContext(), atCenterList[position].slot, bookedSlot, onItemClick)
                            } else {
                            //    showSnackBar("slot not Found")
                            }
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
                this.position = findListIndex(atCenterList.indexOfFirst { it.day == position })
                date = dayMonthYearFromDate(data) ?: ""
                mViewModel.result.serviceDetail?.date = data

                initSlotModel()
            }
            getString(R.string.slot)->{
                mViewModel.result.serviceDetail?.time = data
                mViewModel.result.serviceDetail?.slotId = position

            }

        }
    }

}