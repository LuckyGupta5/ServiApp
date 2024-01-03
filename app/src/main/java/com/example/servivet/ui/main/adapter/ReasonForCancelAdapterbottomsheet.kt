package com.example.servivet.ui.main.adapter

import android.content.Context
import android.view.View
import com.example.servivet.R
import com.example.servivet.databinding.FragmentReasonForCancelBottomsheetBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.databinding.ReasonforcanceldesignBinding
import com.example.servivet.utils.interfaces.ListAdapterItem
class ReasonForCancelAdapterbottomsheet(var context: Context,var list:ArrayList<ListAdapterItem>):BaseAdapter<ReasonforcanceldesignBinding,ListAdapterItem>(list)
{
    var isSelect:Int=0

    inner class ClickAction{

    }

    override fun getItemCount(): Int {
        return 4
    }

    override val layoutId: Int= R.layout.reasonforcanceldesign

    override fun bind(binding: ReasonforcanceldesignBinding, item: ListAdapterItem?, position: Int, ) {
        if(position==isSelect){
            binding.img.setBackgroundResource(R.drawable.selected_icon)
        }
        else
        {
         binding.img.setBackgroundResource(R.drawable.unselected_icon)
        }
        binding.textlayout.setOnClickListener(View.OnClickListener {
             notifyItemChanged(isSelect)
               isSelect=position
            notifyItemChanged(position)


        })
        }

}