package com.example.servivet.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.findNavController

import com.example.servivet.R
import com.example.servivet.data.model.chat_models.request_list.response.Chatlist
import com.example.servivet.data.model.service_list.response.ServiceList
import com.example.servivet.databinding.MyServiceRecyclerBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.Constants
import java.text.DecimalFormat
import kotlin.math.max
import kotlin.math.min

class MyServiceAdapter(
    var context: Context,
    var tabPosition: Int,
    var list: ArrayList<ServiceList>,
    val isBook: Boolean
) : BaseAdapter<MyServiceRecyclerBinding, ServiceList>(list)
{
    override val layoutId: Int = R.layout.my_service_recycler
    private var filteredList: List<ServiceList> = list.toList()


    override fun bind(binding: MyServiceRecyclerBinding, item: ServiceList?, position: Int) {
        binding.apply {
         //   binding.data=item
            binding.data=filteredList[position]
            binding.click=ClickAction(position)
        }

        if (tabPosition == 0) {
            binding.squareImage.visibility = View.GONE
            binding.circularImage.visibility=View.VISIBLE
        } else {
            binding.squareImage.visibility = View.VISIBLE
            binding.circularImage.visibility=View.GONE
        }

        if(isBook){
            binding.idBookNow.isVisible = true
        }else{
            binding.idViewDetails.isVisible = true
        }
        val smallest: String = min(item!!.atCenterPrice!!, item!!.atHomePrice!!).toString()
        val largest: String = max(item!!.atCenterPrice!!,item!!.atHomePrice!!).toString()
        binding.smallest.text=commaSaparator(smallest.toDouble()).toString()
        binding.largest.text=commaSaparator(largest.toDouble()).toString()
    }
    fun commaSaparator(number: Double?): String? {
        val formatter = DecimalFormat("#,###,###")
        return formatter.format(number)
    }


    fun roundTheNumber(numInDouble: Double): String {
        return "%2f.".format(numInDouble)
    }


    override fun getItemCount(): Int {
        return filteredList.size
    }


    fun updateList(list: ArrayList<ServiceList>) {
        val start = if (this.list.size > 0) this.list.size else 0
        this.list.addAll(list)
        notifyItemRangeInserted(start, this.list.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(text: String) {
        filteredList = if (text.isEmpty()) {
            list
        } else {
            list.filter { it.serviceName?.contains(text, ignoreCase = true)!!}

        }

       // onItemClick(context.getString(R.string.filterdata), filteredList.size.toString())
        notifyDataSetChanged()
    }


    inner class ClickAction(var position: Int) {
        fun viewProfile(view: View){
             var bundle= Bundle()
             bundle.putSerializable(Constants.DATA,list[position])
             view.findNavController().navigate(R.id.action_myServiceFragment_to_myServiceDetailFragment,bundle)
        }
        fun bookNow(view: View){
            var bundle= Bundle()
            bundle.putSerializable(Constants.DATA,list[position])
            view.findNavController().navigate(R.id.action_myServiceFragment_to_subCategoryDetailsFragment,bundle)
        }
    }

}