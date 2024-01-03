package com.example.servivet.ui.main.adapter

import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.example.servivet.R
import com.example.servivet.data.model.SimpleImageModel
import com.example.servivet.databinding.AddServiceImageRecyclerBinding
import com.example.servivet.databinding.EditServiceImageRecyclerBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem
import com.google.gson.Gson

@GlideModule
class EditServiceImageAdapter(
    var context: Context,
    var imageList: ArrayList<SimpleImageModel>,
    var listener: (imageList: SimpleImageModel) -> Unit

) :  BaseAdapter<EditServiceImageRecyclerBinding, SimpleImageModel>(imageList) {

    init {
        Log.e("TAG", "ImageListing: "+ Gson().toJson(imageList) )
    }
    override val layoutId: Int = R.layout.edit_service_image_recycler


    override fun bind(binding: EditServiceImageRecyclerBinding, item: SimpleImageModel?, position: Int) {

        Glide.with(context).load(item!!.image).into(binding.image)
        binding.crossIcon.setOnClickListener {
            listener(this.imageList[position])
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return imageList.size

    }
}