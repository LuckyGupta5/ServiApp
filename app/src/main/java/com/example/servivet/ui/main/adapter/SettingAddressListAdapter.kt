package com.example.servivet.ui.main.adapter

import android.content.Context
import com.example.servivet.R
import com.example.servivet.data.model.setting.address_list.SettingAddress
import com.example.servivet.databinding.SaveAadressesRecyclerBinding

import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem
import com.google.android.material.bottomsheet.BottomSheetDialog

class SettingAddressListAdapter(
    var context: Context,
    var list: ArrayList<SettingAddress>,
    val onItemClick: (String,SettingAddress) -> Unit
) :
    BaseAdapter<SaveAadressesRecyclerBinding, SettingAddress>(list) {
    override val layoutId: Int = R.layout.save_aadresses_recycler
    override fun bind(binding: SaveAadressesRecyclerBinding, item: SettingAddress?, position: Int) {
        binding.apply {
            data=item
            edit.setOnClickListener {onItemClick("update",item!!) }
            deleteIcon.setOnClickListener {onItemClick("delete",item!!)  }
            useAsDefault.setOnClickListener {onItemClick("makedefault",item!!)  }
        }
    }
}


