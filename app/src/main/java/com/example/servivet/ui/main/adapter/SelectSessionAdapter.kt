package com.example.servivet.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import com.example.servivet.R
import com.example.servivet.data.model.add_service.request.ServiceListSlot
import com.example.servivet.databinding.TimerLayoutBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.CommonUtils


class SelectSessionAdapter(
    var context: Context,
    val list: ArrayList<ServiceListSlot>,
    var listener: (data: ArrayList<ServiceListSlot>) -> Unit
) :
    BaseAdapter<TimerLayoutBinding, ServiceListSlot>(list) {

    override val layoutId: Int = R.layout.timer_layout
    var isClick = false

    @SuppressLint("NotifyDataSetChanged")
    override fun bind(binding: TimerLayoutBinding, item: ServiceListSlot?, position: Int) {
        Log.e("TAG", "bindasdfghjkl;: "+item )
        binding.apply {
            data = item
            click=ClickAction(item,position)
        }

        binding.startTime.setOnClickListener {

            //TimerDialog(it,binding)
            CommonUtils.selectTime(context,binding.startTime) {
                item?.startTime = it
                binding.startTime.text=item!!.startTime
                list[position] = item!!
                listener(list)
            }

        }
        binding.endTime.setOnClickListener {
            CommonUtils.selectTime(context,binding.endTime) {
                item?.endTime = it
                binding.endTime.text=item!!.endTime
                list[position] = item!!
                listener(list)
            }


        }

        binding.crossIcon.setOnClickListener {
            if (list.size > 1)
                list.removeAt(position)
            notifyDataSetChanged()
        }
    }

    inner class ClickAction(var item: ServiceListSlot?,var  position: Int) {
    }


   /* private fun hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }*/
}