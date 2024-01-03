package com.example.servivet.ui.main.adapter

import android.content.Context
import com.example.servivet.R
import com.example.servivet.databinding.HomeNearByRecyclerBinding
import com.example.servivet.databinding.HomeOnlineNowRecyclerBinding
import com.example.servivet.databinding.HomeServicesRecyclerBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem

class HomeNearByAdapter(var type:String,var context: Context, var list:ArrayList<ListAdapterItem>) : BaseAdapter<HomeNearByRecyclerBinding, ListAdapterItem>(list)
{
    override val layoutId: Int = R.layout.home_near_by_recycler


    override fun getItemCount(): Int {
        return 3
    }
    override fun bind(binding: HomeNearByRecyclerBinding, item: ListAdapterItem?, position: Int) {
        binding.apply {

        }

    }

    inner class ClickAction(var position: Int) {
    }


}