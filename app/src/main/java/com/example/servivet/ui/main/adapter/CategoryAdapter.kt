package com.example.servivet.ui.main.adapter

import android.content.Context
import com.example.servivet.R
import com.example.servivet.data.model.home.response.nearbyprovider.Category
import com.example.servivet.data.model.home.response.nearbyprovider.Provider
import com.example.servivet.databinding.CustomCategoryLayoutBinding
import com.example.servivet.databinding.HomeNearByRecyclerBinding
import com.example.servivet.ui.base.BaseAdapter

class CategoryAdapter(val requireContext: Context, val categoryList: List<Category>) : BaseAdapter<CustomCategoryLayoutBinding, Category>(categoryList) {
    override val layoutId: Int = R.layout.custom_category_layout


    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun bind(binding: CustomCategoryLayoutBinding, item: Category?, position: Int) {
        binding.apply {
            binding.idText.text = categoryList[position].name
        }

    }



}