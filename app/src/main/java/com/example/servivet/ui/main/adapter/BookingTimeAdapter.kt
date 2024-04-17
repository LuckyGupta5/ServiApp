package com.example.servivet.ui.main.adapter

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.servivet.R
import com.example.servivet.data.model.add_service.request.ServiceListSlot
import com.example.servivet.data.model.booking_module.booking_slot.BookedSlot
import com.example.servivet.data.model.booking_module.booking_summary.response.Slot
import com.example.servivet.databinding.BookingTimeRecyclerviewDesignBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.convertDateTimeFormat
import com.example.servivet.utils.getCurrentTimeInFormat
import com.example.servivet.utils.isTimeGapGreaterThan24Hours
import com.google.gson.Gson

class BookingTimeAdapter(
    var context: Context,
    var list: List<Slot>,
    val bookedSlot: ArrayList<BookedSlot>,
    val onItemClick: (String, String, String) -> Unit,
    val date: String
) : BaseAdapter<BookingTimeRecyclerviewDesignBinding, Slot>(list) {
    var isFirst = true
    var isSelectpost = -1

    inner class ClickAction() {

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override val layoutId: Int = R.layout.booking_time_recyclerview_design

    @RequiresApi(Build.VERSION_CODES.O)
    override fun bind(
        binding: BookingTimeRecyclerviewDesignBinding,
        item: Slot?,
        position: Int
    ) {
        binding.timeTxt.text = list[position].startTime

        val dataA = list[position]
        val countInList2 = bookedSlot.count { it.slotId == dataA._id }
        if (countInList2 < dataA.numOfSlot!!.toInt()) {
            binding.timeTxt.isEnabled = true
            if (isFirst) {
                isSelectpost = position
                list[position]?.let {
                    onItemClick(list[position]._id, context.getString(R.string.slot), Gson().toJson(list[position]))
                }
                isFirst = false
            }
        } else {
            binding.timeTxt.alpha = 0.5f
            binding.timeTxt.isEnabled = false

        }
//        if(isTimeGapGreaterThan24Hours(getCurrentTimeInFormat(), convertDateTimeFormat(date + " " + list[position].startTime),1)){
//            binding.timeTxt.alpha = 1.0f
//            binding.timeTxt.isEnabled = true
//        }else{
//            binding.timeTxt.alpha = 0.5f
//            binding.timeTxt.isEnabled = false
//        }



        settime(binding, position)


    }

    fun settime(binding: BookingTimeRecyclerviewDesignBinding, position: Int) {
        if (isSelectpost == position) {
            binding.timeTxt.setBackgroundResource(R.drawable.round_corner_app_theme_8dp)
            binding.timeTxt.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            binding.timeTxt.setBackgroundResource(R.drawable.all_fill_white_greyborder_8dp)
            binding.timeTxt.setTextColor(ContextCompat.getColor(context, R.color.grey_6A6A6A))
        }
        binding.timeTxt.setOnClickListener(View.OnClickListener {
            list[position]?.let {
                onItemClick(
                    list[position]._id,
                    context.getString(R.string.slot),
                    Gson().toJson(list[position])
                )
            }
            notifyItemChanged(isSelectpost)
            isSelectpost = position
            notifyItemChanged(position)

        })

    }

}