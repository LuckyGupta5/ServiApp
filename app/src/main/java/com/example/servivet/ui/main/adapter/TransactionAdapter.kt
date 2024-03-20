package com.example.servivet.ui.main.adapter

import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.example.servivet.R
import com.example.servivet.data.model.booking_module.wallte_transaction.response.WalletTransaction
import com.example.servivet.databinding.TransactionsDesignRecyclerviewBinding
import com.example.servivet.ui.base.BaseAdapter
import com.google.gson.Gson

class TransactionAdapter(var list: ArrayList<WalletTransaction>, val requireContext: Context) :
    BaseAdapter<TransactionsDesignRecyclerviewBinding, WalletTransaction>(list) {
    override val layoutId: Int = R.layout.transactions_design_recyclerview

    override fun bind(
        binding: TransactionsDesignRecyclerviewBinding,
        item: WalletTransaction?,
        position: Int,
    ) {
        binding.apply {
            data = item
            Log.e("TAG", "bind: ${Gson().toJson(item?.service?.images?.get(0) ?: "")}", )

            Glide.with(requireContext).load(item?.service?.images?.get(0) ?: "").error(R.drawable.userprofile).into(binding.profileImage)
        }
    }

    fun updateList(list: ArrayList<WalletTransaction>) {
        val start = if (this.list.size > 0) this.list.size else 0
        this.list.addAll(list)
        notifyItemRangeInserted(start, this.list.size)
    }
}