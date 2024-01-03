package com.example.servivet.ui.main.adapter

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController

import com.example.servivet.R
import com.example.servivet.data.model.service_list.response.ServiceList
import com.example.servivet.databinding.ServiceCategoryInfoRecyclerBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.Constants
import java.text.DecimalFormat
import kotlin.math.max
import kotlin.math.min

class ServiceListAdapter(
    var context: Context,
    var tabPosition: Int,
    var list: ArrayList<ServiceList>) : BaseAdapter<ServiceCategoryInfoRecyclerBinding, ServiceList>(list)
{
    var characterLimit = 15

    override val layoutId: Int = R.layout.service_category_info_recycler

    override fun bind(binding: ServiceCategoryInfoRecyclerBinding, item: ServiceList?, position: Int) {
        binding.apply {
            binding.data=item
            binding.click=ClickAction(position)
        }

        if (tabPosition == 0) {
            binding.squareImage.visibility = View.GONE
            binding.circularImage.visibility=View.VISIBLE
        } else {
            binding.squareImage.visibility = View.VISIBLE
            binding.circularImage.visibility=View.GONE
        }



        if (list[position].serviceName!!.length > characterLimit) {
            // Truncate the text and add a dot
            val truncatedText: String = list[position].serviceName!!.substring(0, characterLimit) + "..."

            // Set the truncated text to the TextView
            binding.serviceName.text = truncatedText
        } else {
            // If the text is shorter than the limit, just set the original text
            binding.serviceName.setText(list[position].serviceName!!)
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
        return list.size
    }


    fun updateList(list: ArrayList<ServiceList>) {
        val start = if (this.list.size > 0) this.list.size else 0
        this.list.addAll(list)
        notifyItemRangeInserted(start, this.list.size)
    }


    inner class ClickAction(var position: Int) {
        fun viewProfile(view: View){
             var bundle= Bundle()
             bundle.putSerializable(Constants.DATA,list[position])
             view.findNavController().navigate(R.id.action_servicesTypeListingFragment_to_subCategoryDetailsFragment,bundle)
        }
    }

}