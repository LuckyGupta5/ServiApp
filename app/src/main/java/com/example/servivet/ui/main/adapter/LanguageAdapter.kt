package com.example.servivet.ui.main.adapter

import android.content.Context
import android.util.Log
import com.example.servivet.R
import com.example.servivet.data.model.language_model.LanguageModel
import com.example.servivet.databinding.LanguageRecylerBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.Constants
import com.example.servivet.utils.Session
import kotlin.math.log

class LanguageAdapter(
    val requireContext: Context,
    val list: ArrayList<LanguageModel>,
    val onItemClick: (String,Int) -> Unit,
    var check:Int
) : BaseAdapter<LanguageRecylerBinding, LanguageModel>(list) {
    override val layoutId: Int = R.layout.language_recyler
    private var rawIndex =   Session.position


    override fun bind(binding: LanguageRecylerBinding, item: LanguageModel?, position: Int) {
        binding.apply {
            isSelected = (rawIndex==position)
            data = item
            executePendingBindings()
            Log.i("TAG", "bind1234: ${Session.position} ")
        }
        binding.mainLayout.setOnClickListener {
            val copy = rawIndex
            rawIndex = position
            onItemClick(list[position].tag,position)
//            Session.savePosition(position)
            notifyItemChanged(copy)
            notifyItemChanged(rawIndex)


        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}