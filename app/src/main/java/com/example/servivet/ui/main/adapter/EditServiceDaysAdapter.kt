package com.example.servivet.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View.OnTouchListener
import androidx.core.view.isVisible
import com.example.servivet.R
import com.example.servivet.databinding.EditServiceAvailabilityDaysRecyclerBinding
import com.example.servivet.databinding.ServiceAvailabilityDaysRecyclerBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.ui.main.fragment.AddServiceFragment


data class EditServiceDaysAdapter(var list: ArrayList<AddServiceFragment.Days>?, val context: Context, var daysAdapterInterface: EditServiceModePriceAdapter, var showDayList:ArrayList<String>, var listener:(data:AddServiceFragment.Days)->Unit) :
    BaseAdapter<EditServiceAvailabilityDaysRecyclerBinding, AddServiceFragment.Days>(list!!) {

    override val layoutId: Int = R.layout.edit_service_availability_days_recycler


    @SuppressLint(
        "ClickableViewAccessibility", "UseCompatLoadingForDrawables",
        "NotifyDataSetChanged"
    )
    override fun bind(binding: EditServiceAvailabilityDaysRecyclerBinding, item: AddServiceFragment.Days?, position: Int) {
        if (list?.get(position)?.isSelected == true) {
            binding.headerll.setBackgroundResource(R.drawable.blue_round_corner_12_dp)
            binding.line.isVisible = true
            binding.dayTv.setTextColor(context.resources.getColor(R.color.white))
            item?.position=position
            listener(item!!)
            daysAdapterInterface.onPositionSelected(position)

        } else {
            binding.headerll.setBackgroundResource(R.drawable.light_gray_3_corner_outline_drawable_12_dp)
            binding.line.isVisible = false
            binding.dayTv.setTextColor(context.resources.getColor(R.color.black))
        }

        binding.dayTv.text = showDayList.get(position)
        binding.dayTv.setOnClickListener {
            if (!list?.get(position)!!.isSelected) {
                unSelectOther()
                list!![position].isSelected = true
            }
            listener(list!![position])
            daysAdapterInterface.onPositionSelected(position)
            notifyDataSetChanged()

        }



        binding.headerll.setOnTouchListener(OnTouchListener { v, event ->
            // Disallow the touch request for parent scroll on touch of  child view
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        })
    }

    private fun unSelectOther() {
        for (i in list!!.indices) {
            list!![i].isSelected = false
        }
    }


    interface DaysAdapterInterface{
        fun onPositionSelected(position: Int)
    }
}


