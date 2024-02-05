package com.example.servivet.ui.main.adapter

import android.content.Context
import com.example.servivet.R
import com.example.servivet.data.model.home.response.nearbyprovider.Provider
import com.example.servivet.databinding.HomeNearByRecyclerBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.formatDecimalNumber

class NearByProviderAdapter(
    val requireContext: Context,
    val providerList: ArrayList<Provider>,
    val onItemClick: (String, String) -> Unit,
    val type: Int
) : BaseAdapter<HomeNearByRecyclerBinding, Provider>(providerList) {
    override val layoutId: Int = R.layout.home_near_by_recycler


    override fun getItemCount(): Int {
        if (type == 1 && providerList.size < 4 && providerList.size>0) {
            return 3
        } else {
            return providerList.size
        }
    }

    override fun bind(binding: HomeNearByRecyclerBinding, item: Provider?, position: Int) {
        binding.apply {
            if (providerList != null && providerList.size > 0) {

                providerList[position].distance =
                    formatDecimalNumber(providerList[position].distance.toDouble())
                data = providerList[position]
                idCategoryAdapter.adapter =
                    CategoryAdapter(requireContext, providerList[position].category)
                idContainer.setOnClickListener {
                    onItemClick("", providerList[position]._id)
                }
            }

        }


    }


}