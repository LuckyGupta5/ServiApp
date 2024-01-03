package com.example.servivet.ui.main.adapter

import android.content.Context
import com.bumptech.glide.Glide
import com.example.servivet.R
import com.example.servivet.databinding.PersonalDocumentImgRecyclerimgBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem

class PersonalDocumentImgAdapter(var context: Context,var list:ArrayList<String>,var imglist:ArrayList<String>,var listener: (imageList: ArrayList<String>)->Unit ):BaseAdapter<PersonalDocumentImgRecyclerimgBinding,ListAdapterItem>(ArrayList()){
    override val layoutId: Int= R.layout.personal_document_img_recyclerimg

    override fun bind(binding: PersonalDocumentImgRecyclerimgBinding, item: ListAdapterItem?, position: Int, ) {
        Glide.with(context).load(list[position]).into(binding.image)
        binding.crossIcon.setOnClickListener{
            list.remove(list[position])
            this.list = list
            listener(this.list)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return 5
    }
}