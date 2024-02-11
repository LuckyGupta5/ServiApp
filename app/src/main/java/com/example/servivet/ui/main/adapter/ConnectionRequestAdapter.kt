package com.example.servivet.ui.main.adapter

import android.content.Context

import com.example.servivet.R
import com.example.servivet.data.model.connection.connection_list.responnse.MyConnection
import com.example.servivet.databinding.ConnectionRequestDesignRecyclerviewBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem

class ConnectionRequestAdapter(
    val requireContext: Context,
    val requestList: ArrayList<MyConnection>,
    val onItemClick: (Int, String) -> Unit
) : BaseAdapter<ConnectionRequestDesignRecyclerviewBinding, MyConnection>(
    requestList
) {
    override val layoutId: Int = R.layout.connection_request_design_recyclerview

    override fun bind(
        binding: ConnectionRequestDesignRecyclerviewBinding,
        item: MyConnection?,
        position: Int,
    ) {
        binding.apply {
            listData = item
            idAccept.setOnClickListener{onItemClick(1, item?._id ?: "")}
            idDeclineBtn.setOnClickListener {onItemClick(2,item?._id ?: "")}
        }


    }

    override fun getItemCount(): Int {
        return requestList.size
    }

}

