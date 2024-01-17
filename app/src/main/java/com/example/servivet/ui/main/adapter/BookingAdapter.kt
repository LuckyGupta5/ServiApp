package com.example.servivet.ui.main.adapter

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.servivet.R
import com.example.servivet.data.model.booking_list.response.MyBooking
import com.example.servivet.data.model.service_list.response.ServiceList
import com.example.servivet.databinding.BookingListDesignBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.ui.main.bottom_sheet.FragmentRatingUsBottomSheet
import com.example.servivet.ui.main.fragment.BookingDetailsFragment
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.Constants
import com.example.servivet.utils.Session
import com.example.servivet.utils.interfaces.ListAdapterItem
class BookingAdapter(var context:Context, var type: Int,var typeReschdule:Int,var list:ArrayList<MyBooking>,var callback: Callback,): BaseAdapter<BookingListDesignBinding, MyBooking>(list)
{
    override val layoutId: Int=R.layout.booking_list_design

    override fun bind(binding: BookingListDesignBinding, item: MyBooking?, position: Int)
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

    }

    fun updateList(list: ArrayList<MyBooking>) {
        val start = if (this.list.size > 0) this.list.size else 0
        this.list.addAll(list)
        notifyItemRangeInserted(start, this.list.size)
    }
    inner class ClickAction(var position: Int)
    {
        fun gotoBookingDetails(view:View){
            val bundle = Bundle()
            bundle.putString("type",type.toString())
            view.findNavController().navigate(R.id.action_bookingsFragment_to_bookingDetailsFragment,bundle)
        }
        fun gotoRateUs(view: View){
            callback.onCallback("1")
        }
        fun clickreschdulegotoBookingdetail(view: View){
            val bundle=Bundle()
            bundle.putString("istype","1")
            bundle.putString("type",type.toString())
            view.findNavController().navigate(R.id.action_bookingsFragment_to_bookingDetailsFragment,bundle)

        }

    }
    interface Callback{
        fun onCallback(type:String)
        fun rejectBooking(id:String)
        fun acceptBooking(id:String)
    }


}