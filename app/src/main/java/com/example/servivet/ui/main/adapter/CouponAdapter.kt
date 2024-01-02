package com.example.servivet.ui.main.adapter

import android.content.Context
import com.example.servivet.R
import com.example.servivet.databinding.CouponDesignRecyclerBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem

class CouponAdapter(var context: Context,var list:ArrayList<ListAdapterItem>):BaseAdapter<CouponDesignRecyclerBinding,ListAdapterItem>(list) {
    override val layoutId: Int= R.layout.coupon_design_recycler

    override fun bind(binding: CouponDesignRecyclerBinding, item: ListAdapterItem?, position: Int) {
    }

    override fun getItemCount(): Int {
        return 10
    }

}