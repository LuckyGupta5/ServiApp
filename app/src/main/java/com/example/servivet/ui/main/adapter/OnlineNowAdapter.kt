package com.example.servivet.ui.main.adapter

import com.example.servivet.R
import com.example.servivet.databinding.OnlineNowDesignBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.utils.interfaces.ListAdapterItem

class OnlineNowAdapter(var list:ArrayList<ListAdapterItem>):BaseAdapter<OnlineNowDesignBinding,ListAdapterItem>(list) {
    override val layoutId: Int= R.layout.online_now_design
    override fun bind(binding: OnlineNowDesignBinding, item: ListAdapterItem?, position: Int) {
    }

    override fun getItemCount(): Int {
        return 10
    }
}