package com.example.servivet.ui.main.adapter

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.example.servivet.R
import com.example.servivet.databinding.AddServiceImageRecyclerBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem

@GlideModule
class AddServiceImageAdapter(var context: Context, var list: ArrayList<String>, var imageList: ArrayList<String>, var listener: (imageList: ArrayList<String>) -> Unit
) :  BaseAdapter<AddServiceImageRecyclerBinding, ListAdapterItem>(ArrayList()) {

    override val layoutId: Int = R.layout.add_service_image_recycler


    override fun bind(binding: AddServiceImageRecyclerBinding, item: ListAdapterItem?, position: Int) {

        Log.e("TAG", "bind: ", )

        binding.crossIcon.setOnClickListener {
            list.remove(list[position])
            this.list=list
            listener(this.list)
            notifyDataSetChanged()
        }
    }

    private fun getVideoThumbnail(videoPath: String): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(videoPath)
            retriever.frameAtTime
        } catch (e: IllegalArgumentException) {
            null
        } finally {
            retriever.release()
        }
    }

    override fun getItemCount(): Int {
        return list.size

    }
}