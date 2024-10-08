package com.example.servivet.ui.main.adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.servivet.R
import com.example.servivet.data.model.booking_list.response.MyBooking
import com.example.servivet.databinding.BookingListDesignBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.ui.main.fragment.BookingsFragmentDirections
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.Session
import com.google.gson.Gson
import kotlin.math.log

class BookingListAdapter(
    val requireContext: Context,
    val bookingList: ArrayList<MyBooking>,
    val onItemClick: (String, String, String) -> Unit,
    private val types: Int,
    private val findNavController: NavController,
    private val typesOfUser: String
) : BaseAdapter<BookingListDesignBinding, MyBooking>(bookingList) {

    override val layoutId: Int = R.layout.booking_list_design
    override fun bind(binding: BookingListDesignBinding, item: MyBooking?, position: Int) {
        binding.apply {
            data = item
            type = types
            typeOfUser = typesOfUser
           click = ClickAction(position)
        } 
        Log.e("TAG", " checkbind: $typesOfUser,$types")
        Log.e("TAG", "bind432343: ${bookingList[position].startTime}", )
        binding.dateAndTimeText.text = bookingList[position].day + "," + CommonUtils.getDateTimeStampConvert(bookingList[position].startTime)
        if(bookingList[position].myRating!=null && types == 2){
            binding.ratingbar.isVisible = true
            binding.idViewDetails.isVisible = false
            binding.ratingbar.rating = bookingList[position].myRating?.rating?.toFloat()!!
        }else{
            binding.ratingbar.isVisible = false
            binding.idViewDetails.isVisible = true
        }

        binding.idViewDetails.setOnClickListener {
            if (types == 2 && typesOfUser == requireContext.getString(R.string.bought)) {
                findNavController.navigate(BookingsFragmentDirections.actionBookingsFragmentToFragmentRatingUsBottomSheet(bookingList[position].serviceDetail._id,"BookingList",bookingList[position].serviceDetail.serviceName))

            } else {
                findNavController.navigate(BookingsFragmentDirections.actionBookingsFragmentToBookingDetailsFragment(Gson().toJson(bookingList[position]), types,typesOfUser, requireContext.getString(R.string.bookinglist)))
            }
        }




//        for (i in Session.category.indices) {
//            for (j in Session.category[i].subCategory!!.indices) {
//                if (bookingList[position].serviceDetail.subCategory == Session.category[i].subCategory!![j].id) {
//                    binding.subCatName.text = Session.category[i].subCategory!![j].name
//                    Glide.with(requireContext)
//                        .load("https://ride-chef-dev.s3.ap-south-1.amazonaws.com/" + Session.category[i].subCategory!![j].image)
//                        .into(binding.image2)
//                }
//            }
//        }
    }

    inner class ClickAction(var position: Int) {
        fun gotoBookingDetails(view: View) {
            findNavController.navigate(BookingsFragmentDirections.actionBookingsFragmentToBookingDetailsFragment(Gson().toJson(bookingList[position]), types,typesOfUser, requireContext.getString(R.string.bookinglist)))
        }

        fun gotoRateUs(view: View) {
           // callback.onCallback("1")
        }

        fun clickreschdulegotoBookingdetail(view: View) {
//            val bundle=Bundle()
//            bundle.putString("istype","1")
//            bundle.putString("type",type.toString())
//            bundle.putString("bookingId",list[position]._id)
//            view.findNavController().navigate(R.id.action_bookingsFragment_to_bookingDetailsFragment,bundle)

        }

    }

    fun updateList(list: ArrayList<MyBooking>) {
        val start = if (bookingList.size > 0) bookingList.size else 0
        bookingList.addAll(list)
        notifyItemRangeInserted(start, bookingList.size)
    }


    override fun getItemCount(): Int {
        return bookingList.size
    }
}