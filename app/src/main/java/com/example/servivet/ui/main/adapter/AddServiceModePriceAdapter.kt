package com.example.servivet.ui.main.adapter

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData

import com.bumptech.glide.annotation.GlideModule
import com.example.servivet.R
import com.example.servivet.data.model.CustomeServiceModeData
import com.example.servivet.data.model.add_service.request.ServiceListSlot
import com.example.servivet.data.model.add_service.request.AtCenterAvailability
import com.example.servivet.data.model.add_service.request.AtHomeAvailability
import com.example.servivet.databinding.ServiceModePriceLayoutBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.ui.main.fragment.AddServiceFragment
import com.example.servivet.ui.main.view_model.AddServiceViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.Constants
import com.google.gson.Gson

@GlideModule
class AddServiceModePriceAdapter(var context: Context, var list: ArrayList<CustomeServiceModeData>, var mviewModel: AddServiceViewModel, var showDayList:ArrayList<String>, var listener: (data: CallBackData) -> Unit) :
    BaseAdapter<ServiceModePriceLayoutBinding, CustomeServiceModeData>(list),
    DaysAdapter.DaysAdapterInterface {
    var daysPosition: Int? = null
    var isClick=false
    var days = AddServiceFragment.Days()
    override val layoutId: Int = R.layout.service_mode_price_layout


    override fun bind(
        binding: ServiceModePriceLayoutBinding,
        item: CustomeServiceModeData?,
        position: Int
    ) {
        binding.apply {
            binding.serviceModeTv.text = item?.type.toString()
            setDaysAdapter(this, item, position,showDayList)
            binding.click = ClickAction(item, position)
        }

        binding.addHour.setOnClickListener {
            if (item?.daysList!![daysPosition!!].slotList?.get(item?.daysList!![daysPosition!!].slotList?.size!! - 1)!!.startTime == "") {
                Toast.makeText(context, "Choose the start time", Toast.LENGTH_SHORT).show()
            } else if (item.daysList!![daysPosition!!].slotList?.get(item.daysList!![daysPosition!!].slotList?.size!! - 1)!!.endTime == "") {
                Toast.makeText(context, "Choose the end time", Toast.LENGTH_SHORT).show()
            }else if(!CommonUtils.checkDates(item.daysList!![daysPosition!!].slotList?.get(item.daysList!![daysPosition!!].slotList?.size!! - 1)!!.startTime!!, item.daysList!![daysPosition!!].slotList?.get(item.daysList!![daysPosition!!].slotList?.size!! - 1)!!.endTime!!)){
                Toast.makeText(context, "Start time should not be greater than end time", Toast.LENGTH_SHORT).show()
            } else if (item?.daysList!![daysPosition!!].slotList?.size!! < 11) {

                item.daysList!![daysPosition!!].slotList?.add(ServiceListSlot("", "", ""))
                listener(CallBackData(item!!, position))
            }
        }


        binding.servicell.setOnClickListener {
            if (binding.downArrow.rotation == 360F) {
                binding.serviceModePrice.visibility = View.VISIBLE
                binding.downArrow.rotation = 180F
            } else {
                binding.serviceModePrice.visibility = View.GONE
                binding.downArrow.rotation = 360F
            }

        }
    }

    private fun setSessionAdapter(serviceModePriceLayoutBinding: ServiceModePriceLayoutBinding, slotList: ArrayList<ServiceListSlot>?, item: CustomeServiceModeData?) {

        serviceModePriceLayoutBinding.timeRv.adapter = slotList?.let {
            SelectSessionAdapter(context,it)

            {

                var newDayslist = ArrayList<AddServiceFragment.Days>()
                for (i in item?.daysList!!.indices) {
                    if (item.daysList!![i].slotList?.get(0)?.startTime != "") {
                        newDayslist.add(item.daysList!![i])

                    }
                }
                Log.e("TAG", "setSessionAdapter: "+Gson().toJson(newDayslist))
                setRequestParameter(item, newDayslist)
            }
        }
    }

    private fun setDaysAdapter(serviceModePriceLayoutBinding: ServiceModePriceLayoutBinding, item: CustomeServiceModeData?, position: Int, showDayList: ArrayList<String>) {
        serviceModePriceLayoutBinding.daysRecycler.adapter =
            DaysAdapter(item?.daysList, context, this,showDayList) {
                if (it.isSelected) {
                    days = it
                    daysPosition = it.position
                }
                setSessionAdapter(serviceModePriceLayoutBinding, it.slotList, item)
            }
    }

    private fun setRequestParameter(data: CustomeServiceModeData?, newDayslist: ArrayList<AddServiceFragment.Days>) {
        when (data?.type) {
            Constants.AT_HOME -> {
                var atHomeAvailability = ArrayList<AtHomeAvailability>()
                for (i in newDayslist.indices)
                {
                    var athome = AtHomeAvailability()
                    athome.slot=newDayslist[i].slotList
                    athome.day=newDayslist[i].days
                    atHomeAvailability.add(athome)
                }
                mviewModel.addServicesRequest.atHomeAvailability = atHomeAvailability
            }

            Constants.AT_CENTER -> {
                var atCenterAvailability = ArrayList<AtCenterAvailability>()
                for (i in newDayslist.indices)
                {
                    var atCenter = AtCenterAvailability()
                    atCenter.slot=newDayslist[i].slotList
                    atCenter.day=newDayslist[i].days
                    atCenterAvailability.add(atCenter)
                }
                mviewModel.addServicesRequest.atCenterAvailability = atCenterAvailability


            }

        }
    }


    data class CallBackData(var data: CustomeServiceModeData, var position: Int)

    inner class ClickAction(var item: CustomeServiceModeData?, var position: Int) {
        var price = MutableLiveData(false)

        fun onPriceChange(text: CharSequence) {

            when (item?.type) {
                Constants.AT_HOME -> {
                    price.value = text.isNotEmpty()
                    mviewModel.addServicesRequest.atHomePrice = text.toString()
                }

                Constants.AT_CENTER -> {
                    price.value = text.isNotEmpty()
                    mviewModel.addServicesRequest.atCenterPrice = text.toString()
                }


            }


        }
    }

    override fun onPositionSelected(position: Int) {
        daysPosition = position
    }
}

