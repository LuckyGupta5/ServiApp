package com.example.servivet.ui.main.adapter

import android.content.Context
import android.view.View
import com.bumptech.glide.annotation.GlideModule
import com.example.servivet.R
import com.example.servivet.data.model.setting.faq_list.response.FaqListResult
import com.example.servivet.databinding.FaqRecyclerBinding
import com.example.servivet.ui.base.BaseAdapter

@GlideModule
class FaqAdapter(var context: Context, val list: ArrayList<FaqListResult>) :
    BaseAdapter<FaqRecyclerBinding, FaqListResult>(list) {
    override val layoutId: Int = R.layout.faq_recycler
    var isClick = false
    private var filteredList: List<FaqListResult> = list.toList()
    override fun bind(binding: FaqRecyclerBinding, item: FaqListResult?, position: Int) {
        binding.apply {
            data = item

        }
        if (!isClick) {
            binding.description.visibility = View.GONE
            binding.arrow.setImageResource(R.drawable.down_faq_arrow)
        } else {
            binding.description.visibility = View.VISIBLE
            binding.arrow.setImageResource(R.drawable.up_faq_arror)
        }
        binding.mainLayout.setOnClickListener {
            if (!isClick) {
                isClick = true
            } else {
                isClick = false
            }
            notifyItemChanged(position)
        }
    }
}