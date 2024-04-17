package com.example.servivet.ui.main.adapter

import android.content.Context
import android.util.Log

import com.example.servivet.R
import com.example.servivet.data.model.chat_models.request_list.response.Chatlist
import com.example.servivet.data.model.connection.connection_list.responnse.MyConnection
import com.example.servivet.databinding.ConnectionRequestDesignRecyclerviewBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.setSpannable
import com.google.gson.Gson

class ConnectionRequestAdapter(
    val requireContext: Context,
    val requestList: ArrayList<MyConnection>,
    val onItemClick: (Int, String) -> Unit
) : BaseAdapter<ConnectionRequestDesignRecyclerviewBinding, MyConnection>(
    requestList
) {
    override val layoutId: Int = R.layout.connection_request_design_recyclerview
    private var filteredList: List<MyConnection> = requestList.toList()


    override fun bind(
        binding: ConnectionRequestDesignRecyclerviewBinding,
        item: MyConnection?,
        position: Int,
    ) {
        binding.apply {

            listData = filteredList[position]

            idAccept.setOnClickListener { onItemClick(1, item?._id ?: "") }
            idDeclineBtn.setOnClickListener { onItemClick(2, item?._id ?: "") }
            if (filteredList != null && filteredList.isNotEmpty())
                binding.idName.text = setSpannable(
                    filteredList[position].userDetail.name,
                    requireContext.getString(R.string.ravi_bishnoi_sent_you_a_connections_request)
                )

        }


    }

    fun filter(text: String) {
        filteredList = if (text.isEmpty()) {
            requestList
        } else {
            requestList.filter {
                it.userDetail.name.contains(text, ignoreCase = true)
            }
        }
        onItemClick(3, filteredList.size.toString())
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return filteredList.size
    }

}

