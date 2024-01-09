package com.example.servivet.ui.main.fragment

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.booking_module.booking_summary.response.ServiceDetail
import com.example.servivet.data.model.date_model.DateModel
import com.example.servivet.databinding.FragmentBookingSummaryBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.BookingTimeAdapter
import com.example.servivet.ui.main.adapter.CalenderRecyclerAdapter
import com.example.servivet.ui.main.view_model.booking_models.BookingSummaryViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.getDateFromToday
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BookingSummaryFragment : BaseFragment<FragmentBookingSummaryBinding, BookingSummaryViewModel>(R.layout.fragment_booking_summary),CalenderRecyclerAdapter.OnCurrentMonthChange {
    override val binding: FragmentBookingSummaryBinding by viewBinding(FragmentBookingSummaryBinding::bind)
    override val mViewModel: BookingSummaryViewModel by viewModels()
    private var calendarList: java.util.ArrayList<DateModel> = java.util.ArrayList<DateModel>()
    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val cal = Calendar.getInstance(Locale.ENGLISH)
    var pos = 0
    private val dates = java.util.ArrayList<Date>()
    private val serviceId:BookingSummaryFragmentArgs by navArgs()
    lateinit var adapter:CalenderRecyclerAdapter
    private lateinit var serviceDetail: ServiceDetail


    override fun isNetworkAvailable(boolean: Boolean) {
    }

    @RequiresApi(Build.VERSION_CODES.O)

    override fun setupViewModel() {

     /*   binding.apply {
            lifecycleOwner=viewLifecycleOwner
            viewModel=mViewModel
            click=mViewModel.ClickAction(requireContext(),binding)
        }
     //   setadapter()
        setDate()
        settimeadapter()
    */}
    @RequiresApi(Build.VERSION_CODES.O)
    override fun setupViews() {
        binding.apply {
            lifecycleOwner=viewLifecycleOwner
            viewModel=mViewModel
            click=mViewModel.ClickAction(requireContext(),binding)
        }
        //   setadapter()

        setDate()
        settimeadapter()


    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDate() {
        val list = ArrayList<DateModel>()
        for (i in getDateFromToday().indices) {
            list.add(DateModel(getDateFromToday()[i]!!, false))
        }
        list[0].isToday = true
      //  adapter=CalenderRecyclerAdapter(requireContext(),list,this)
         adapter=  CalenderRecyclerAdapter(requireContext(), list ,pos,this)
        binding.tvDateMonth.setText(CommonUtils.monthYearFromDate(list[0].date))
//        binding.tvDateMonth.setText(CommonUtils.dateFromDate(list.toString()))
        binding.recyclerview.adapter=adapter
    }
    fun settimeadapter(){
        binding.timeRecycler.adapter=BookingTimeAdapter(requireContext(),ArrayList(),pos)
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

    override fun setupObservers() {
        mViewModel.getReportRatingRequest(serviceId.data)
        mViewModel.getSummaryData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            showSnackBar(it.data.message!!)
                            serviceDetail = it.data.result.serviceDetail
                            binding.summaryData= serviceDetail
                            Log.e("TAG", "setupObservers: ${Gson().toJson(serviceDetail)}", )
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
        Log.d("TAG", "onDahgfteClick: "+Gson().toJson(date))
    }

}