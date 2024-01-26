package com.example.servivet.ui.main.bottom_sheet

import android.os.Build
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.example.servivet.R
import com.example.servivet.data.model.booking_module.booking_summary.response.Slot
import com.example.servivet.data.model.date_model.DateModel
import com.example.servivet.databinding.FragmentReScheduleBookingBottomSheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.adapter.BookingTimeAdapter
import com.example.servivet.ui.main.adapter.CalenderRecyclerAdapter
import com.example.servivet.ui.main.view_model.ReScheduleBookingBottomSheetViewModel
import com.example.servivet.ui.main.view_model.booking_models.BookingSlotViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.generateMonthStrings
import com.google.gson.Gson


class ReScheduleBookingBottomSheet : BaseBottomSheetDailogFragment<FragmentReScheduleBookingBottomSheetBinding,ReScheduleBookingBottomSheetViewModel>(R.layout.fragment_re_schedule_booking_bottom_sheet),CalenderRecyclerAdapter.OnCurrentMonthChange {
    override val mViewModel: ReScheduleBookingBottomSheetViewModel by viewModels()
    lateinit var adapter:CalenderRecyclerAdapter
    private val slotViewModel: BookingSlotViewModel by viewModels()
    private var date = CommonUtils.setCurrentDate()

    var pos = -1
    var timepos=0
    var monthCount = 0
    private var position = 0



    override fun getLayout(): Int {
     return R.layout.fragment_re_schedule_booking_bottom_sheet
    }

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setupViews() {
        binding.apply {
            lifecycleOwner=viewLifecycleOwner
            viewModel=mViewModel
            click=mViewModel.ClickAction(binding,requireContext())
        }
        initYearAdapter()
        setDate()
        settimeadapter()
        dismissbtn()
    }


    fun dismissbtn(){
        binding.savebtn.setOnClickListener(View.OnClickListener {
            dismiss()
        })

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
        val list = ArrayList<DateModel>()/*
        for (i in CommonUtils.getDateFromToday().indices) */
        for(i in CommonUtils.getDateFromToday(monthCount).indices){
            list.add(DateModel(CommonUtils.getDateFromToday(monthCount)[i]!!, false))
        }
        list[0].isToday = true
        //  adapter=CalenderRecyclerAdapter(requireContext(),list,this)
        adapter=  CalenderRecyclerAdapter(requireContext(), list, pos, this, onItemClick)
 //       binding.tvDateMonth.setText(CommonUtils.monthYearFromDate(list[0].date))
//        binding.tvDateMonth.setText(CommonUtils.dateFromDate(list.toString()))
        gotochangedate()
        binding.recyclerview.adapter=adapter
    }

   fun gotochangedate(){
//       binding.tvDateMonth.setOnClickListener(View.OnClickListener {
//           mViewModel.setnavcontroller(findNavController())
//       })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun settimeadapter(){
        binding.timeRecycler.adapter= BookingTimeAdapter(
            requireContext(),
            ArrayList(),
            ArrayList(),
            onItemClick,
            date
        )
        adapter.notifyDataSetChanged()
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    override fun setupObservers() {
    }

    override fun onMonthChange(month: String?) {
    }

    override fun onDateClick(date: String?) {
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val onItemClick: (String, String, String) -> Unit = { position, name, data ->
        when (name) {
            getString(R.string.calendar) -> {
             //   this.position = CommonUtils.findListIndex(atCenterList.indexOfFirst { it.day == position })
                date = CommonUtils.dayMonthYearFromDate(data) ?: ""
             //   mViewModel.result.serviceDetail?.date = data
             //   initSlotModel()
            }

            getString(R.string.slot) -> {
                val data = Gson().fromJson(data, Slot::class.java)
//                mViewModel.result.serviceDetail?.startTime = data.startTime
//                mViewModel.result.serviceDetail?.endTime = data.endTime
//                mViewModel.result.serviceDetail?.slotId = position

            }

        }
    }


}