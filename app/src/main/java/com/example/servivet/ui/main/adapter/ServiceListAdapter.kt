package com.example.servivet.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.data.model.service_list.response.ServiceList
import com.example.servivet.databinding.ServiceCategoryInfoRecyclerBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.commaSaparator
import kotlin.math.max
import kotlin.math.min

class ServiceListAdapter(
    var context: Context,
    private var tabPosition: Int,
    var list: ArrayList<ServiceList>
) : BaseAdapter<ServiceCategoryInfoRecyclerBinding, ServiceList>(list) {

    private var characterLimit = 15
    var smallest = ""
    var largest = ""
    private var filteredList: List<ServiceList> = list.toList()

    override val layoutId: Int = R.layout.service_category_info_recycler

    override fun bind(
        binding: ServiceCategoryInfoRecyclerBinding,
        item: ServiceList?,
        position: Int
    ) {
        binding.apply {
            binding.data = item
            binding.click = ClickAction(position)

            if (data?.address.isNullOrEmpty() == true) {
                binding.kmAwayText.visibility = View.GONE
            } else {
                binding.kilometers.visibility = View.VISIBLE
            }
        }

        if (tabPosition == 0) {
            binding.squareImage.visibility = View.GONE
            binding.circularImage.visibility = View.VISIBLE
        } else {
            binding.squareImage.visibility = View.VISIBLE
            binding.circularImage.visibility = View.GONE
        }

        if (list[position].serviceName!!.length > characterLimit) {
            // Truncate the text and add a dot
            val truncatedText: String =
                list[position].serviceName!!.substring(0, characterLimit) + "..."

            // Set the truncated text to the TextView
            binding.serviceName.text = truncatedText
        } else {
            // If the text is shorter than the limit, just set the original text
            binding.serviceName.setText(list[position].serviceName!!)
        }
        smallest = min(item!!.atCenterPrice ?: 0.0, item.atHomePrice ?: 0.0).toString()
        largest = max(item.atCenterPrice ?: 0.0, item.atHomePrice ?: 0.0).toString()
        checkVisibility(binding)
        binding.smallest.text = "₹ " + commaSaparator(smallest.toDouble()).toString()
        binding.largest.text = "₹ " + commaSaparator(largest.toDouble()).toString()

    }

    private fun checkVisibility(binding: ServiceCategoryInfoRecyclerBinding) {
        binding.smallest.isVisible = smallest != "0.0"
        binding.idView.isVisible = smallest != "0.0"
        binding.largest.isVisible = largest != "0.0"
    }

    fun filter(text: String): List<ServiceList> {
        filteredList = if (text.isEmpty()) {
            list
        } else {
            list.filter { it.createdBy?.name?.contains(text, ignoreCase = true)!! }
        }
        return filteredList
    }

    fun roundTheNumber(numInDouble: Double): String {
        return "%2f.".format(numInDouble)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        this.list.clear()
        notifyDataSetChanged()
    }

    fun updateList(list: ArrayList<ServiceList>) {
        val start = if (this.list.size > 0) this.list.size else 0
        this.list.addAll(list)
        notifyItemRangeInserted(start, this.list.size)
    }

    inner class ClickAction(var position: Int) {
        fun viewProfile(view: View) {
            view.findNavController().navigate(
                R.id.action_servicesTypeListingFragment_to_subCategoryDetailsFragment,
                bundleOf("serviceId" to list[position]._id)
            )
        }
    }

}