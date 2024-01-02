package com.example.servivet.ui.main.adapter

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.se.omapi.Session
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.databinding.BookingListDesignBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.ui.main.bottom_sheet.FragmentRatingUsBottomSheet
import com.example.servivet.ui.main.fragment.BookingDetailsFragment
import com.example.servivet.utils.Constants
import com.example.servivet.utils.interfaces.ListAdapterItem
class BookingAdapter(var context:Context, var type: Int,var typeReschdule:Int,var list:ArrayList<ListAdapterItem>,var callback: Callback,): BaseAdapter<BookingListDesignBinding, ListAdapterItem>(list)
{
    override val layoutId: Int=R.layout.booking_list_design

    override fun bind(binding: BookingListDesignBinding, item: ListAdapterItem?, position: Int)
    {
        binding.apply {
            binding.click=ClickAction(position)

            if(type==0){
                binding.reScheduleLayout.isVisible=true
                    binding.reSchedule.isVisible = true
                  binding.rateUs.isVisible=false

            }
            else if(type==1) {
                binding.reScheduleLayout.isVisible = true
                binding.rateUs.isVisible = true
                binding.reSchedule.isVisible = false
            }
            else

                binding.reScheduleLayout.isVisible=false


        }
        binding.textprice2.paintFlags=Paint.STRIKE_THRU_TEXT_FLAG

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
          //  bundle.putString("type",type.toString())
            view.findNavController().navigate(R.id.action_bookingsFragment_to_bookingDetailsFragment,bundle)

        }

    }
    interface Callback{
        fun onCallback(type:String)
    }

    override fun getItemCount(): Int {
        return 5
    }
}