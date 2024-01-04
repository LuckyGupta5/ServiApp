package com.example.servivet.ui.main.adapter

import com.example.servivet.R
import com.example.servivet.databinding.MyConnectionDesignRecyclerviewBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem

class MyConnectionAdapter(var list:ArrayList<ListAdapterItem>):BaseAdapter<MyConnectionDesignRecyclerviewBinding,ListAdapterItem>(list) {
    override val layoutId: Int= R.layout.my_connection_design_recyclerview

    override fun bind(binding: MyConnectionDesignRecyclerviewBinding, item: ListAdapterItem?, position: Int, ) {
    }

    override fun getItemCount(): Int {
        return 10
    }
}