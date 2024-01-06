package com.example.servivet.ui.main.bottom_sheet

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.servivet.R
import com.example.servivet.data.model.date_model.DateModel
import com.example.servivet.databinding.FragmentReScheduleBookingBottomSheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.adapter.BookingTimeAdapter
import com.example.servivet.ui.main.adapter.CalenderRecyclerAdapter
import com.example.servivet.ui.main.view_model.ReScheduleBookingBottomSheetViewModel
import com.example.servivet.utils.CommonUtils


class ReScheduleBookingBottomSheet : BaseBottomSheetDailogFragment<FragmentReScheduleBookingBottomSheetBinding,ReScheduleBookingBottomSheetViewModel>(R.layout.fragment_re_schedule_booking_bottom_sheet),CalenderRecyclerAdapter.OnCurrentMonthChange {
    override val mViewModel: ReScheduleBookingBottomSheetViewModel by viewModels()
    lateinit var adapter:CalenderRecyclerAdapter
    var pos = -1
    var timepos=0


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
        setDate()
        settimeadapter()
        dismissbtn()
    }


    fun dismissbtn(){
        binding.savebtn.setOnClickListener(View.OnClickListener {
            dismiss()
        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDate() {
        val list = ArrayList<DateModel>()/*
        for (i in CommonUtils.getDateFromToday().indices) */
        for(i in CommonUtils.getDateFromToday().indices){
            list.add(DateModel(CommonUtils.getDateFromToday()[i]!!, false))
        }
        list[0].isToday = true
        //  adapter=CalenderRecyclerAdapter(requireContext(),list,this)
        adapter=  CalenderRecyclerAdapter(requireContext(), list ,pos,this)
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

    fun settimeadapter(){
        binding.timeRecycler.adapter= BookingTimeAdapter(requireContext(),ArrayList(),timepos)
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


}