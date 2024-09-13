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
    val onItemClick: (Int, String) -> Unit
) : BaseAdapter<MyConnectionDesignRecyclerviewBinding, MyConnection>(connectionList) {
    override val layoutId: Int = R.layout.my_connection_design_recyclerview
    private var filteredList: List<MyConnection> = connectionList.toList()


    override fun bind(
        binding: MyConnectionDesignRecyclerviewBinding,
        item: MyConnection?,
        position: Int,
    ) {
        binding.apply {
            listData = item
            idRemove.setOnClickListener {
                onItemClick(2, item!!._id)
            }
        }


    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    fun filter(text: String) {
        filteredList = if (text.isEmpty()) {
            connectionList
        } else {
            connectionList.filter {
                it.userDetail.name.contains(text, ignoreCase = true)
            }
        }
        onItemClick(3, filteredList.size.toString())
        notifyDataSetChanged()
    }
}