package com.example.servivet.ui.main.adapter

import android.content.Context
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.example.servivet.R
import com.example.servivet.data.model.booking_module.booking_slot.BookedSlot
import com.example.servivet.data.model.booking_module.booking_summary.response.Slot
import com.example.servivet.databinding.BookingTimeRecyclerviewDesignBinding
import com.example.servivet.ui.base.BaseAdapter

class BookingTimeAdapter(
    var context: Context,
    var list: List<Slot>,
    var isSelectpost: Int,
    val bookedSlot: ArrayList<BookedSlot>,
    val onItemClick: (String, String, String) -> Unit
) : BaseAdapter<BookingTimeRecyclerviewDesignBinding, Slot>(list) {
    var isFirst = true


    inner class ClickAction() {

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override val layoutId: Int = R.layout.booking_time_recyclerview_design

    override fun bind(binding: BookingTimeRecyclerviewDesignBinding, item: Slot?, position: Int) {
        settime(binding, position)
        binding.timeTxt.text = list[position].startTime

        val dataA = list[position]
        val countInList2 = bookedSlot.count { it.slotId == dataA._id }
        if (countInList2 < dataA.numOfSlot) {
            Log.e("TAG", "bind: dis", )
        } else {
            Log.e("TAG", "bind: ena", )
        }
        if(isFirst){
            list[0]?.let {
                onItemClick("", context.getString(R.string.slot),list[0].startTime)
            }
        }


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
            notifyItemChanged(isSelectpost)
            isSelectpost = position
            notifyItemChanged(position)

        })

    }

}