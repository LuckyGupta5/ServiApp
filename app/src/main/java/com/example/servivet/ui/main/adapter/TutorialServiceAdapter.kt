package com.example.servivet.ui.main.adapter

import android.content.Context
import com.bumptech.glide.Glide
import com.example.servivet.R
import com.example.servivet.data.model.TutorialModel
import com.example.servivet.data.model.home.response.nearbyprovider.Provider
import com.example.servivet.data.model.language_model.LanguageModel
import com.example.servivet.databinding.HomeNearByRecyclerBinding
import com.example.servivet.databinding.LanguageRecylerBinding
import com.example.servivet.databinding.TutorialServiceRecyclerBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.formatDecimalNumber

class TutorialServiceAdapter(
    val requireContext: Context,
    val list: ArrayList<TutorialModel>,
) : BaseAdapter<TutorialServiceRecyclerBinding, TutorialModel>(list) {
    override val layoutId: Int = R.layout.tutorial_service_recycler

    override fun bind(binding: TutorialServiceRecyclerBinding, item: TutorialModel?, position: Int) {
        binding.apply {
        }
        binding.serviceName.text = list[position].serviceName
        binding.serviceImage.setImageResource(list[position].serviceImage!!)
    }
}