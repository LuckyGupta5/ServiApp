package com.example.servivet.ui.main.adapter

import android.content.Context
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.servivet.R
import com.example.servivet.data.model.booking_list.response.MyBooking
import com.example.servivet.data.model.sold_booking_list.response.MySoldBooking
import com.example.servivet.databinding.BookingListDesignBinding
import com.example.servivet.databinding.SoldBookingListDesignBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.Session
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.concurrent.TimeUnit

class SoldBookingAdapter(var context:Context, var type: Int, var typeReschdule:Int, var list:ArrayList<MySoldBooking>, var callback: Callback,): BaseAdapter<SoldBookingListDesignBinding, MySoldBooking>(list)
{
    override val layoutId: Int=R.layout.booking_list_design
    private var newCurrentTime: String=""
    @RequiresApi(Build.VERSION_CODES.O)
    override fun bind(binding: SoldBookingListDesignBinding, item: MySoldBooking?, position: Int)
    {
        binding.apply {
            binding.click=ClickAction(position)
            binding.data=list[position]

            if(type==0){
                binding.reScheduleLayout.isVisible=false
                binding.reSchedule.isVisible = false
                binding.rateUs.isVisible=false
                binding.requestLayout.isVisible=true
                binding.view.isVisible=false
            }else if(type==1){
                binding.reScheduleLayout.isVisible=true
                binding.reSchedule.isVisible = true
                binding.rateUs.isVisible=false
                binding.requestLayout.isVisible=false
                binding.view.isVisible=true

            }
            else if(type==2) {
                binding.reScheduleLayout.isVisible = true
                binding.rateUs.isVisible = true
                binding.reSchedule.isVisible = false
                binding.requestLayout.isVisible=false
                binding.view.isVisible=true
            } else if(type==3) {
                binding.reScheduleLayout.isVisible = false
                binding.rateUs.isVisible = false
                binding.reSchedule.isVisible = false
                binding.requestLayout.isVisible=false
                binding.view.isVisible=false
            }


        }
        binding.textprice2.paintFlags=Paint.STRIKE_THRU_TEXT_FLAG

        binding.dateAndTimeText.text=list[position].day+","+ CommonUtils.getDateTimeStampConvert(list[position].bookingDate)

        if(list[position].serviceDetail.serviceMode.atHome==true)
            binding.serviceMode.text="At Home"

        if(list[position].serviceDetail.serviceMode.atCenter==true)
            binding.serviceMode.text="At Center"

        for(i in Session.category.indices){
            for(j in Session.category[i].subCategory!!.indices){
                if(list[position].serviceDetail.subCategory== Session.category[i].subCategory!![j].id){
                    binding.subCatName.text=Session.category[i].subCategory!![j].name
                    Glide.with(context).load("https://ride-chef-dev.s3.ap-south-1.amazonaws.com/"+Session.category[i].subCategory!![j].image).into(binding.image2)
                }
            }
        }

        binding.reject.setOnClickListener { callback.rejectBooking(list[position]._id) }
        binding.accept.setOnClickListener { callback.acceptBooking(list[position]._id) }
        currentTime()
        updateButtonState(
            CommonUtils.getDateFromTimeStamp(list[position].bookingDate)!!,
            binding.reSchedule
        )

        Log.e("TAG", "BookingAdapter: "+CommonUtils.getDateFromTimeStamp(list[position].bookingDate))

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateButtonState(exceedTime: String, yourButton: Button) {
        try {
            val startTime = LocalTime.parse(newCurrentTime, DateTimeFormatter.ofPattern("hh:mm a"))
            val endTime = LocalTime.parse(exceedTime, DateTimeFormatter.ofPattern("hh:mm a"))

            // Calculate time difference in milliseconds
            val timeDifference = calculateTimeDifference(startTime, endTime)

            // Check if the time difference is less than 24 hours
            val isWithin24Hours = timeDifference < TimeUnit.HOURS.toMillis(24)

            // Enable or disable the button accordingly
            yourButton.isEnabled = isWithin24Hours

            /*if(isWithin24Hours==false){
                yourButton.isEnabled=true
            }else {
                yourButton.isEnabled=false
            }*/
        } catch (e: DateTimeParseException) {
            // Handle parsing error
            e.printStackTrace()
            Log.e("TAG", "Parsing error: $newCurrentTime")
        }   
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateTimeDifference(startTime: LocalTime, endTime: LocalTime): Long {
        val startMillis = startTime.toSecondOfDay() * 1000L
        val endMillis = endTime.toSecondOfDay() * 1000L

        return endMillis - startMillis
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun currentTime() {
        val currentTime = LocalTime.now()

        // Define the desired format (in AM/PM format)
        val dateFormat = DateTimeFormatter.ofPattern("hh:mm a")

        // Format the current time
        val formattedTime = currentTime.format(dateFormat)

        newCurrentTime=formattedTime
        // Return the formatted time

    }


    fun updateList(list: ArrayList<MySoldBooking>) {
        val start = if (this.list.size > 0) this.list.size else 0
        this.list.addAll(list)
        notifyItemRangeInserted(start, this.list.size)
    }
    inner class ClickAction(var position: Int)
    {
        fun gotoBookingDetails(view:View){
            val bundle = Bundle()
            bundle.putString("type",type.toString())
            bundle.putString("bookingId",list[position]._id)
           // view.findNavController().navigate(R.id.action_businessBookingFragment_to_bookingDetailsFragment,bundle)
        }
        fun gotoRateUs(view: View){
            callback.onCallback("1")
        }
        fun clickreschdulegotoBookingdetail(view: View){
            val bundle=Bundle()
            bundle.putString("istype","1")
            bundle.putString("type",type.toString())
            bundle.putString("bookingId",list[position]._id)
           // view.findNavController().navigate(R.id.action_businessBookingFragment_to_bookingDetailsFragment,bundle)

        }

    }
    interface Callback{
        fun onCallback(type:String)
        fun rejectBooking(id:String)
        fun acceptBooking(id:String)
    }


}