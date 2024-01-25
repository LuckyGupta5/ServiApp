package com.example.servivet.ui.main.adapter

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
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
        }
        Log.e("TAG", " checkbind: $typesOfUser,$types")

        Log.e("TAG", "bind432343: ${bookingList[position].startTime}", )
        binding.dateAndTimeText.text = bookingList[position].day + "," + CommonUtils.getDateTimeStampConvert(bookingList[position].startTime)


        binding.idViewDetails.setOnClickListener {
            if (types == 2 && typesOfUser == requireContext.getString(R.string.bought)) {

                findNavController.navigate(R.id.action_bookingsFragment_to_fragmentRatingUsBottomSheet)
                findNavController.navigate(BookingsFragmentDirections.actionBookingsFragmentToFragmentRatingUsBottomSheet(bookingList[position].serviceDetail._id,"BookingList"))

            } else {
                findNavController.navigate(BookingsFragmentDirections.actionBookingsFragmentToBookingDetailsFragment(Gson().toJson(bookingList[position]), types, requireContext.getString(R.string.bookinglist))
                )
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


    override fun getItemCount(): Int {
        return bookingList.size
    }
}