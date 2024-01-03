package com.example.servivet.ui.main.view_model

import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.CalendarView.OnDateChangeListener
import android.widget.Toast
import com.example.servivet.databinding.FragmentChangedateBinding
import com.example.servivet.ui.base.BaseViewModel
import java.text.SimpleDateFormat
import java.time.Year
import java.util.Calendar
import java.util.Locale

class ChangeDateViewModel:BaseViewModel() {
    inner class ClickAction(var binding: FragmentChangedateBinding,var context: Context){
        lateinit var calenderView:CalendarView

      /*  fun calenderset()
        {
            var calendar=Calendar.getInstance()
            var getCurrentmonth=calendar.get(Calendar.MONTH)
            var getcurrentYear=calendar.get(Calendar.YEAR)
            //get the list of the month in current month
            var daysinMonths=getDayinMonth(getCurrentmonth,getcurrentYear)
            val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, daysinMonths)
            binding.calendarGridView.adapter = adapter
            binding.calendariew.setOnDateChangeListener(
                fun(view: CalendarView, year: Int, month: Int, dayOfMonth: Int)
                {
                    var selectdate="$dayOfMonth-${month+1}-$year"
                Toast.makeText(context,selectdate,Toast.LENGTH_LONG).show()
            })
        }
        fun setdaate(day:Int,month:Int,year:Int){


        }*/

    }

    /*private fun getDayinMonth(startMonth: Int, getcurrentYear: Int):List<String> {
        var calender=Calendar.getInstance()
        calender.set(Calendar.YEAR,getcurrentYear)
        calender.set(Calendar.MONTH,startMonth)
        calender.set(Calendar.DAY_OF_MONTH,1)
        val daysInMonth = mutableListOf<String>()

        while (calender.get(Calendar.MONTH) == startMonth) {
            val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
            daysInMonth.add(dateFormat.format(calender.time))
            calender.add(Calendar.DAY_OF_MONTH, 1)
        }

        return daysInMonth

    }
*/
}