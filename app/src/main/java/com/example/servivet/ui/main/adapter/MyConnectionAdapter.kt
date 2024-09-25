package com.example.servivet.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import com.example.servivet.R
import com.example.servivet.data.model.connection.connection_list.responnse.MyConnection
import com.example.servivet.databinding.MyConnectionDesignRecyclerviewBinding
import com.example.servivet.ui.base.BaseAdapter

class MyConnectionAdapter(
    val requireContext: Context,
    val connectionList: ArrayList<MyConnection>,
    val onItemClick: (Int, String) -> Unit
) : BaseAdapter<MyConnectionDesignRecyclerviewBinding, MyConnection>(connectionList) {
    override val layoutId: Int = R.layout.my_connection_design_recyclerview
     var filteredList: MutableList<MyConnection> = connectionList.toMutableList()

    override fun bind(
        binding: MyConnectionDesignRecyclerviewBinding,
        item: MyConnection?,
        position: Int,
    ) {
        binding.apply {
            listData = item
//            root.setOnClickListener {
//                onItemClick(1, item?.userDetail?._id ?: "")
//            }
            idRemove.setOnClickListener {
                onItemClick(2, item?._id ?: "")
            }
        }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(text: String) {
        filteredList = if (text.isEmpty()) {
            connectionList
        } else {
            connectionList.filter {
                it.userDetail.name.contains(text, ignoreCase = true)
            } as MutableList<MyConnection>
        }
        onItemClick(3, filteredList.size.toString())
        notifyDataSetChanged()
    }
}