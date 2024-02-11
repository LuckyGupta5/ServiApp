package com.example.servivet.ui.main.adapter

import android.content.Context
import com.example.servivet.R
import com.example.servivet.data.model.connection.connection_list.responnse.MyConnection
import com.example.servivet.databinding.MyConnectionDesignRecyclerviewBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem

class MyConnectionAdapter(
    val requireContext: Context,
    val connectionList: ArrayList<MyConnection>,
    val onItemClick: (String, String) -> Unit
) : BaseAdapter<MyConnectionDesignRecyclerviewBinding, ListAdapterItem>(connectionList) {
    override val layoutId: Int = R.layout.my_connection_design_recyclerview

    override fun bind(
        binding: MyConnectionDesignRecyclerviewBinding,
        item: ListAdapterItem?,
        position: Int,
    ) {


    }

    override fun getItemCount(): Int {
        return connectionList.size
    }
}