package com.example.servivet.ui.main.adapter

import android.content.Context
import android.util.Log
import com.example.servivet.R
import com.example.servivet.data.model.date_model.DateModel
import com.example.servivet.databinding.CalenderRecyclerDesignBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.dateFromDate
import com.example.servivet.utils.CommonUtils.dayFromDate
import com.example.servivet.utils.CommonUtils.monthYearFromDate
import com.example.servivet.utils.interfaces.ListAdapterItem
import com.google.gson.Gson


class CalenderRecyclerAdapter(context: Context,var list:ArrayList<DateModel>,var selectedPos:Int, monthChange: OnCurrentMonthChange?):BaseAdapter<CalenderRecyclerDesignBinding,DateModel>(list) {
    var context: Context? = null
    private var monthChange: OnCurrentMonthChange? =null

    // selectedPos = -1
    /*fun CalenderRecyclerAdapter(context: Context?, calendarList: java.util.ArrayList<DateModel?>, monthChange: OnCurrentMonthChange?, selectedPos: Int, ) {
        this.list = list
        this.context = context
        this.monthChange = monthChange
        this.selectedPos = selectedPos
    }*/
    override val layoutId: Int= R.layout.calender_recycler_design
    override fun getItemCount(): Int {
        return list.size
    }

    override fun bind(binding: CalenderRecyclerDesignBinding, item: DateModel?, position: Int)
    {
        binding.apply {

        }
        setCalender(binding,position)
    }
    fun setCalender(binding: CalenderRecyclerDesignBinding, position: Int)
    {
        Log.d("TAG", "setCalenhgfdrrder: "+Gson().toJson(list))/*
        if(list!![position].date!=null&&!list!![position].date.toString().isBlank()&&list!![position].date.toString().isEmpty()){*/
            binding.tvCalendarDay.setText(dayFromDate(list!![position].date.toString()))
            binding.tvCalendarDate.setText(dateFromDate(list!![position].date.toString()))
        /*}*/
        if (list!![position].isToday) {
       //   monthChange!!.onDateClick(CommonUtils.dayMonthYearFromDate(list!![position].date.toString()))
           binding.dateLayout.setBackgroundResource(R.drawable.calender_background)
           binding.circleWhite.setBackgroundResource(R.drawable.calender_white_background)
           binding.tvCalendarDay.setTextColor(binding.tvCalendarDate.getResources().getColor(R.color.white))
           binding.tvCalendarDate.setTextColor(
               binding.dateLayout.getResources().getColor(R.color.app_theme)
            )
        } else
        {
          // binding.dateLayout.setBackgroundResource(0)
            binding.dateLayout.setBackgroundResource(R.drawable.white_top_round_corner_greay_32)
           binding.tvCalendarDate.setBackgroundResource(0)
           binding.tvCalendarDate.setTextColor(
               binding.tvCalendarDay.getResources().getColor(R.color.black)
            )
           binding.tvCalendarDay.setTextColor(
               binding.dateLayout.getResources().getColor(R.color.black)
            )
        }
       binding.dateLayout.setOnClickListener { view ->
            if (list[position].isToday) {
//                list.get(position).setToday(false);
//                monthChange.onDateClick("");
            } else {
                unselectedOther()
                Log.d("TAG", "setCjhgfalender: "+list[position].date)
                list[position].isToday=true
               // monthChange!!.onDateClick(CommonUtils.dayMonthYearFromDate(list[position].date.toString()))
            }
//            notifyItemChanged(selectedPos)
//            notifyItemChanged(position)
           notifyDataSetChanged()
           selectedPos = position
        }
        if(monthChange!=null)
               monthChange!!.onMonthChange(monthYearFromDate(list!![position].date.toString()))

    }
    private fun unselectedOther() {
        for (i in list.indices) {
            list[i].isToday=false
        }
    }

    interface OnCurrentMonthChange {
        fun onMonthChange(month: String?)
        fun onDateClick(date: String?)
    }


}