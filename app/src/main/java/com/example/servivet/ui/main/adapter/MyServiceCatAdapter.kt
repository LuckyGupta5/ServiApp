package com.example.servivet.ui.main.adapter

import android.content.Context
import android.view.View
import com.bumptech.glide.annotation.GlideModule
import com.example.servivet.R
import com.example.servivet.data.model.home.response.HomeServiceCategory
import com.example.servivet.data.model.home.response.HomeSubCategory
import com.example.servivet.databinding.MyServiceCategoryRecyclerBinding
import com.example.servivet.databinding.SubCategoryServicesRecyclerBinding
import com.example.servivet.ui.base.BaseAdapter

@GlideModule
class MyServiceCatAdapter(var context: Context, list: ArrayList<HomeServiceCategory>, var adapterInterface: SubCategoryAdapterInterFace) :
    BaseAdapter<MyServiceCategoryRecyclerBinding, HomeServiceCategory>(list) {
    var isSelect = 0
    override val layoutId: Int = R.layout.my_service_category_recycler


    override fun bind(
        binding: MyServiceCategoryRecyclerBinding,
        item: HomeServiceCategory?,
        position: Int
    ) {
        binding.apply {

        }
        binding.subCat.text = item!!.name
        if (isSelect == position) {
            binding.view.visibility= View.VISIBLE
            binding.subCat.setTextColor(context.resources.getColor(R.color.app_theme))
        } else {
            binding.view.visibility= View.GONE
            binding.subCat.setTextColor(context.resources.getColor(R.color.black))
        }
        binding.subCat.setOnClickListener {
            adapterInterface.onSubCatSelected(item.id!!)
            notifyItemChanged(isSelect)
            isSelect = position
            notifyItemChanged(position)
        }
    }

    interface SubCategoryAdapterInterFace{
        fun onSubCatSelected(id:String)
    }

}