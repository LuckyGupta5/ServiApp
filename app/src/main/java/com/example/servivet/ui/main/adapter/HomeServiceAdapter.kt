package com.example.servivet.ui.main.adapter

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.servivet.R
import com.example.servivet.data.model.home.response.HomeServiceCategory
import com.example.servivet.databinding.HomeServicesRecyclerBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.Constants

class HomeServiceAdapter(var context: Context, var type: String, var list: List<HomeServiceCategory>, ) : BaseAdapter<HomeServicesRecyclerBinding, HomeServiceCategory>(list) {
    override val layoutId: Int = R.layout.home_services_recycler


    override fun getItemCount(): Int {
        if (type == "1") {
            if (list.size < 5)
                return list.size
            else
                return 5
        } else {
            return list.size
        }
    }

    override fun bind(
        binding: HomeServicesRecyclerBinding,
        item: HomeServiceCategory?,
        position: Int,
    ) {
        binding.apply {
            binding.click = ClickAction(position,item)

        }

        binding.serviceName.text = list[position].name
        Glide.with(context).load(list[position].imageUrl).error(R.drawable.flower_img)
            .into(binding.serviceImage)
    }

    inner class ClickAction(var position: Int,var  item: HomeServiceCategory?) {
        var bundle = Bundle()
        fun goToServiceList(view: View) {
            when (type) {
                "1" -> {
                    bundle.putString(Constants.SERVICE_ID, list[position].id)
                    bundle.putSerializable(Constants.DATA, item)
                    view.findNavController()
                        .navigate(R.id.action_homeFragment_to_servicesTypeListingFragment,bundle)
                }
                "2" -> {
                    bundle.putString(Constants.SERVICE_ID, list[position].id)
                    bundle.putSerializable(Constants.DATA,item)
                    view.findNavController()
                        .navigate(R.id.action_servicesFragment_to_servicesTypeListingFragment,bundle)
                }
                else -> {
                    bundle.putString(Constants.SERVICE_ID, list[position].id)
                    bundle.putSerializable(Constants.DATA,item)
                    view.findNavController()
                        .navigate(R.id.action_profileFragment_to_servicesTypeListingFragment,bundle)
                }
            }
        }
    }


}