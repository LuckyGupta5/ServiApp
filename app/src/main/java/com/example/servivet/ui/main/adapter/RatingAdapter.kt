package com.example.servivet.ui.main.adapter

import android.content.Context
import com.example.servivet.R
import com.example.servivet.databinding.CustomRatingbarLayoutBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem

class RatingAdapter(
    var context: Context,
    var list: ArrayList<ListAdapterItem>,
    onItemClick: (String, String) -> Unit
):
    BaseAdapter<CustomRatingbarLayoutBinding, ListAdapterItem>(list)
{
    var isSelect:Int=0

    inner class ClickAction{

    }

    override fun getItemCount(): Int {
        return 5
    }

    override val layoutId: Int= R.layout.custom_ratingbar_layout
    override fun bind(
        binding: CustomRatingbarLayoutBinding,
        item: ListAdapterItem?,
        position: Int
    ) {

    }


}