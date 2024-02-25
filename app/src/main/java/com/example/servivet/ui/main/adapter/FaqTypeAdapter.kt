package com.example.servivet.ui.main.adapter

import android.content.Context
import android.view.View
import com.bumptech.glide.annotation.GlideModule
import com.example.servivet.R
import com.example.servivet.data.model.home.response.HomeSubCategory
import com.example.servivet.data.model.setting.faq_type_list.response.FaqTypeListResult
import com.example.servivet.databinding.FaqTypeRecyclerBinding
import com.example.servivet.databinding.SubCategoryServicesRecyclerBinding
import com.example.servivet.ui.base.BaseAdapter

@GlideModule
class FaqTypeAdapter(var context: Context, list: ArrayList<FaqTypeListResult>,var adapterFaqTypeInterFace: FaqTypeInterFace) :
    BaseAdapter<FaqTypeRecyclerBinding, FaqTypeListResult>(list) {
    var isSelect = 0
    override val layoutId: Int = R.layout.faq_type_recycler


    override fun bind(
        binding: FaqTypeRecyclerBinding,
        item: FaqTypeListResult?,
        position: Int
    ) {
        binding.apply {

        }
        binding.subCat.text = item!!.faqType
        if (isSelect == position) {
            binding.view.visibility= View.VISIBLE
            binding.subCat.setTextColor(context.resources.getColor(R.color.app_theme))
        } else {
            binding.view.visibility= View.GONE
            binding.subCat.setTextColor(context.resources.getColor(R.color.black))
        }
        binding.subCat.setOnClickListener {
            adapterFaqTypeInterFace.onSubCatSelected(item._id!!)
            notifyItemChanged(isSelect)
            isSelect = position
            notifyItemChanged(position)
        }
    }

    interface FaqTypeInterFace{
        fun onSubCatSelected(id:String)
    }

}