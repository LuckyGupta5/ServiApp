package com.example.servivet.ui.main.adapter

import android.content.Context
import com.example.servivet.R
import com.example.servivet.data.model.language_model.LanguageModel
import com.example.servivet.databinding.LanguageRecylerBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.Constants.SELECTED_LANGUAGE_POSITION

class LanguageAdapter(
    val requireContext: Context,
    val list: ArrayList<LanguageModel>,
    val onItemClick: (String,Int) -> Unit,
) : BaseAdapter<LanguageRecylerBinding, LanguageModel>(list) {
    override val layoutId: Int = R.layout.language_recyler
    private var rawIndex = SELECTED_LANGUAGE_POSITION

    override fun bind(binding: LanguageRecylerBinding, item: LanguageModel?, position: Int) {
        binding.apply {
            isSelected = rawIndex==position
            data = item
            executePendingBindings()
        }
        binding.mainLayout.setOnClickListener {
            val copy = rawIndex
            rawIndex = position
            notifyItemChanged(copy)
            notifyItemChanged(rawIndex)
            onItemClick(list[position].tag,position)
            SELECTED_LANGUAGE_POSITION = position
            //item!!.isSelected = !(item.isSelected)

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}