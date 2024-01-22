package com.example.servivet.ui.main.adapter

import android.content.Context
import android.view.View
import com.example.servivet.R
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.databinding.ReasonforcanceldesignBinding
import com.example.servivet.utils.interfaces.ListAdapterItem

class ReasonForCancelAdapterbottomsheet(
    var context: Context,
    var list: ArrayList<ListAdapterItem>,
    val callBack: (String, Int) -> Unit,
    val reasonList: List<String>
) : BaseAdapter<ReasonforcanceldesignBinding, ListAdapterItem>(list) {
    var isSelect: Int = 0
    var isFirst = true

    inner class ClickAction {

    }

    override fun getItemCount(): Int {
        return reasonList.size
    }

    override val layoutId: Int = R.layout.reasonforcanceldesign

    override fun bind(
        binding: ReasonforcanceldesignBinding,
        item: ListAdapterItem?,
        position: Int,
    ) {
        binding.reasonData = reasonList[position]
        if (position == isSelect) {
            binding.img.setBackgroundResource(R.drawable.selected_icon)
        } else {
            binding.img.setBackgroundResource(R.drawable.unselected_icon)
        }

        if (isFirst) {
            callBack(reasonList[0], 0)
            isFirst = false
        }
        binding.textlayout.setOnClickListener(View.OnClickListener {
            callBack(reasonList[position], 0)
            notifyItemChanged(isSelect)
            isSelect = position
            notifyItemChanged(position)

        })
    }

}