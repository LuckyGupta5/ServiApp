package com.example.servivet.ui.main.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import com.example.servivet.R
import com.example.servivet.databinding.BookingTimeRecyclerviewDesignBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem

class BookingTimeAdapter(var context: Context,var list: ArrayList<ListAdapterItem>,var isSelectpost:Int):BaseAdapter<BookingTimeRecyclerviewDesignBinding,ListAdapterItem>(list){


    inner class ClickAction(){

    }

    override fun getItemCount(): Int {
        return 9
    }
    override val layoutId: Int=R.layout.booking_time_recyclerview_design

    override fun bind(binding: BookingTimeRecyclerviewDesignBinding, item: ListAdapterItem?, position: Int, ) {
   settime(binding,position)

    }

   fun settime(binding: BookingTimeRecyclerviewDesignBinding, position: Int) {
       if(isSelectpost==position){
           binding.timeTxt.setBackgroundResource(R.drawable.round_corner_app_theme_8dp)
           binding.timeTxt.setTextColor(ContextCompat.getColor(context,R.color.white))
       }
       else{
           binding.timeTxt.setBackgroundResource(R.drawable.all_fill_white_greyborder_8dp)
           binding.timeTxt.setTextColor(ContextCompat.getColor(context,R.color.grey_6A6A6A))
       }
       binding.timeTxt.setOnClickListener(View.OnClickListener {
           notifyItemChanged(isSelectpost)
           isSelectpost=position
           notifyItemChanged(position)

       })

   }

}