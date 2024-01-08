package com.example.servivet.ui.main.adapter

import android.content.Context
import android.view.View
import com.example.servivet.R
import com.example.servivet.databinding.CouponDesignRecyclerBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.Constants
import com.example.servivet.utils.interfaces.ListAdapterItem

class CouponAdapter(var context: Context,var list:ArrayList<ListAdapterItem>,var callback:CallBack):BaseAdapter<CouponDesignRecyclerBinding,ListAdapterItem>(list) {
    override val layoutId: Int= R.layout.coupon_design_recycler

    override fun bind(binding: CouponDesignRecyclerBinding, item: ListAdapterItem?, position: Int) {
        binding.applyCoupon.setOnClickListener(View.OnClickListener {
            Constants.APPLIED_COUPON="APPLIED_COUPON"
            callback.getSelectedCoupon()
        })
    }

    override fun getItemCount(): Int {
        return 10
    }

    interface CallBack{
        fun getSelectedCoupon()
    }

}