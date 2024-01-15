package com.example.servivet.ui.main.adapter

import android.content.Context
import android.view.View
import com.example.servivet.R
import com.example.servivet.data.model.booking_module.coupon.response.CouponList
import com.example.servivet.databinding.CouponDesignRecyclerBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.Constants
import com.google.gson.Gson

class CouponAdapter(
    var context: Context,
    var list: ArrayList<CouponList>,
    var onItemClick: (Int, String) -> Unit
):BaseAdapter<CouponDesignRecyclerBinding,CouponList>(list) {
    override val layoutId: Int= R.layout.coupon_design_recycler

    override fun bind(binding: CouponDesignRecyclerBinding, item: CouponList?, position: Int) {
        binding.couponData = item
        binding.applyCoupon.setOnClickListener(View.OnClickListener {
            Constants.APPLIED_COUPON="APPLIED_COUPON"
            onItemClick(position, list[position].couponCode)
        })


    }

    override fun getItemCount(): Int {
        return list.size
    }



}