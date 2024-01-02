package com.example.servivet.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData

import com.example.servivet.R
import com.example.servivet.data.model.add_service.request.ServiceListSlot
import com.example.servivet.databinding.EditTimerLayoutBinding
import com.example.servivet.databinding.TimerLayoutBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.ui.main.view_model.EditServiceViewModel
import com.example.servivet.utils.CommonUtils
import com.google.gson.Gson

class EditSelectSessionAdapter(
    var context: Context,
    val list: ArrayList<ServiceListSlot>,
    var viewModel: EditServiceViewModel,
    var editSelectSessionInterface: EditSelectSessionInterface,
    var listener: (data: ArrayList<ServiceListSlot>) -> Unit
) :
    BaseAdapter<EditTimerLayoutBinding, ServiceListSlot>(list) {

    override val layoutId: Int = R.layout.edit_timer_layout
    var isClick = false

    @SuppressLint("NotifyDataSetChanged")
    override fun bind(binding: EditTimerLayoutBinding, item: ServiceListSlot?, position: Int) {
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

            editSelectSessionInterface.onCrossBtn(list,position)
            Log.e("TAG", "bind:EditSelectSessionAdapter "+Gson().toJson(list))
            notifyDataSetChanged()
        }
    }

    inner class ClickAction(var item: ServiceListSlot?,var  position: Int) {
    }

    interface EditSelectSessionInterface{
        fun onCrossBtn(list: ArrayList<ServiceListSlot>,position: Int)
    }

}