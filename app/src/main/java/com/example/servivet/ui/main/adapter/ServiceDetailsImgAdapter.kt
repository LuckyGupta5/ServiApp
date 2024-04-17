package com.example.servivet.ui.main.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.servivet.R
import com.example.servivet.databinding.ServiceDatailsImgDesignBinding

class ServiceDetailsImgAdapter(
    var context: Context,
    var list: ArrayList<String>,
    val onItemClick: (String, String, Int) -> Unit
) :
    RecyclerView.Adapter<ServiceDetailsImgAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: ServiceDatailsImgDesignBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: ServiceDatailsImgDesignBinding

        init {
            binding = itemView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.service_datails_img_design,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(list[position]).into(holder.binding.image)
        holder.binding.playPauseButton.isVisible = list[position].endsWith(".mp4")
        holder.binding.idCardView.setOnClickListener {
            onItemClick(context.getString(R.string.openmedia), "", position)
        }
        holder.binding.playPauseButton.setOnClickListener {
            onItemClick(context.getString(R.string.openmedia), "", position)
        }
    }

}