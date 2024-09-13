package com.example.servivet.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.data.model.service_list.response.ServiceList
import com.example.servivet.databinding.MyServiceRecyclerBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.Constants
import java.text.DecimalFormat
import kotlin.math.max
import kotlin.math.min

class MyServiceAdapter(
    var context: Context,
    private var tabPosition: Int,
    var list: ArrayList<ServiceList>,
    private val isBook: Boolean
) : BaseAdapter<MyServiceRecyclerBinding, ServiceList>(list) {

    var smallest = ""
    var largest = ""
    override val layoutId: Int = R.layout.my_service_recycler
    private var filteredList: List<ServiceList> = list.toList()

    override fun bind(binding: MyServiceRecyclerBinding, item: ServiceList?, position: Int) {
        binding.apply {
            //   binding.data=item
            binding.data = filteredList[position]
            binding.click = ClickAction(position)
        }
        if (tabPosition == 0) {
            binding.squareImage.visibility = View.GONE
            binding.circularImage.visibility = View.VISIBLE
        } else {
            binding.squareImage.visibility = View.VISIBLE
            binding.circularImage.visibility = View.GONE
        }
        if (isBook) {
            binding.idBookNow.isVisible = true
        } else {
            binding.idViewDetails.isVisible = true
        }
        smallest = min(item!!.atCenterPrice!!, item!!.atHomePrice!!).toString()
        largest = max(item!!.atCenterPrice!!, item!!.atHomePrice!!).toString()
        checkVisibility(binding)
        binding.smallest.text = commaSaparator(smallest.toDouble()).toString()
        binding.largest.text = commaSaparator(largest.toDouble()).toString()
    }

    private fun checkVisibility(binding: MyServiceRecyclerBinding) {
        // Show the smallest value only if it's greater than 0.0
        binding.smallest.isVisible = smallest != "0.0" && smallest != largest
        binding.idView.isVisible =
            binding.smallest.isVisible // Divider visibility based on smallest
        // Always show the largest value
        binding.largest.isVisible = largest != "0.0"
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
            list.filter { it.serviceName?.contains(text, ignoreCase = true)!! }
        }
        // onItemClick(context.getString(R.string.filterdata), filteredList.size.toString())
        notifyDataSetChanged()
    }

    inner class ClickAction(var position: Int) {
        fun viewProfile(view: View) {
            val bundle = Bundle()
            bundle.putSerializable(Constants.DATA, list[position])
            view.findNavController()
                .navigate(R.id.action_myServiceFragment_to_myServiceDetailFragment, bundle)
        }

        fun bookNow(view: View) {
            val bundle = Bundle()
            bundle.putSerializable(Constants.DATA, list[position])
            view.findNavController()
                .navigate(R.id.action_myServiceFragment_to_subCategoryDetailsFragment, bundle)
        }
    }

}