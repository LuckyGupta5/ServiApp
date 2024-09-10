package com.example.servivet.ui.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.servivet.R
import com.example.servivet.data.model.notification_list.response.NotificationList
import com.example.servivet.databinding.NotificationOuterLayoutBinding

class NotificationListAdapter() :
    RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {
    val arrNotification: MutableList<NotificationList> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(list: MutableList<NotificationList>) {
        arrNotification.clear()
        arrNotification.addAll(list)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        this.arrNotification.clear()
        notifyDataSetChanged()
    }

    fun updateItemList(itemList: MutableList<NotificationList>?) {
        if (itemList.isNullOrEmpty() || arrNotification.isEmpty()) return
        arrNotification.addAll(itemList)
        notifyItemRangeChanged(itemList.size - itemList.size, itemList.size)
    }

    inner class ViewHolder(private val notificationBinding: NotificationOuterLayoutBinding) :
        RecyclerView.ViewHolder(notificationBinding.root) {
        fun bind(data: NotificationList) {
            notificationBinding.apply {
                notificationList = data
                this.notificationIcon.setImageResource(R.drawable.notification_new_img)
                if (absoluteAdapterPosition > 0) {
                    notificationDate.isVisible = arrNotification.getOrNull(absoluteAdapterPosition)
                        ?.formattedDate() != arrNotification.getOrNull(absoluteAdapterPosition - 1)
                        ?.formattedDate()
                } else {
                    notificationDate.isVisible = true
                }
                view.isVisible = (absoluteAdapterPosition != arrNotification.size - 1)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.notification_outer_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return arrNotification.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        arrNotification.getOrNull(position)?.let { dataModel -> holder.bind(dataModel) }
    }
}