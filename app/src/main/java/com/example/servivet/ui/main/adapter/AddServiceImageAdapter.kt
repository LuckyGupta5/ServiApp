package com.example.servivet.ui.main.adapter

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.example.servivet.R
import com.example.servivet.databinding.AddServiceImageRecyclerBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem

@GlideModule
class AddServiceImageAdapter(
    var context: Context,
    var list: ArrayList<String>,
    var imageList: ArrayList<String>,
    var listener: (imageList: ArrayList<String>) -> Unit

) :  BaseAdapter<AddServiceImageRecyclerBinding, ListAdapterItem>(ArrayList()) {

    override val layoutId: Int = R.layout.add_service_image_recycler


    override fun bind(binding: AddServiceImageRecyclerBinding, item: ListAdapterItem?, position: Int) {

        Glide.with(context).load(list[position]).into(binding.image)
        binding.crossIcon.setOnClickListener {
            list.remove(list[position])
            this.list=list
            listener(this.list)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return list.size

    }
}