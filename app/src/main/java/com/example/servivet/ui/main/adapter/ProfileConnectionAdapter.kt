package com.example.servivet.ui.main.adapter

import android.content.Context
import com.bumptech.glide.Glide
import com.example.servivet.R
import com.example.servivet.data.model.connection.connection_list.responnse.MyConnection
import com.example.servivet.databinding.HomeServicesRecyclerBinding
import com.example.servivet.databinding.MyConnectionDesignRecyclerviewBinding
import com.example.servivet.ui.base.BaseAdapter

class ProfileConnectionAdapter( val requireContext: Context, val connectionList: ArrayList<MyConnection>, val onItemClick: (Int, String) -> Unit) : BaseAdapter<HomeServicesRecyclerBinding, MyConnection>(connectionList) {
    override val layoutId: Int = R.layout.home_services_recycler

    override fun bind(
        binding: HomeServicesRecyclerBinding,
        item: MyConnection?,
        position: Int,
    ) {
        binding.apply {
            serviceName.text = item?.userDetail?.name
            Glide.with(requireContext).load(item?.userDetail?.image).error(R.drawable.userprofile)
                .into(binding.serviceImage)

        }


    }

    override fun getItemCount(): Int {
        return connectionList.size
    }
}