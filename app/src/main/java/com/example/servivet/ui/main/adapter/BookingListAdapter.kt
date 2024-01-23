package com.example.servivet.ui.main.adapter

import android.content.Context
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.example.servivet.R
import com.example.servivet.data.model.booking_list.response.MyBooking
import com.example.servivet.databinding.BookingListDesignBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.ui.main.fragment.BookingsFragmentDirections
import com.example.servivet.utils.Session
import com.google.gson.Gson

class BookingListAdapter(
    val requireContext: Context,
    val bookingList: ArrayList<MyBooking>,
    val onItemClick: (String, String, String) -> Unit,
    private val type: Int,
    private val findNavController: NavController,
    private val typeOfUser: String
) : BaseAdapter<BookingListDesignBinding, MyBooking>(bookingList) {

    override val layoutId: Int = R.layout.booking_list_design
    override fun bind(binding: BookingListDesignBinding, item: MyBooking?, position: Int) {
        if(type == 2){
            binding.type = type
            binding.typeOfUser = typeOfUser

        }else {
            binding.data = item
            binding.type = type
            binding.idViewDetails.setOnClickListener {
                findNavController.navigate(
                    BookingsFragmentDirections.actionBookingsFragmentToBookingDetailsFragment(
                        Gson().toJson(bookingList[position]),
                        type,
                        requireContext.getString(R.string.bookinglist)
                    )
                )
            }
            for (i in Session.category.indices) {
                for (j in Session.category[i].subCategory!!.indices) {
                    if (bookingList[position].serviceDetail.subCategory == Session.category[i].subCategory!![j].id) {
                        binding.subCatName.text = Session.category[i].subCategory!![j].name
                        Glide.with(requireContext)
                            .load("https://ride-chef-dev.s3.ap-south-1.amazonaws.com/" + Session.category[i].subCategory!![j].image)
                            .into(binding.image2)
                    }
                }
            }
        }
    }



    override fun getItemCount(): Int {
        return 2
    }
}