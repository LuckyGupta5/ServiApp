package com.example.servivet.ui.main.adapter

import android.content.Context
import com.example.servivet.R
import com.example.servivet.data.model.booking_module.wallte_transaction.response.WalletTransaction
import com.example.servivet.databinding.TransactionsDesignRecyclerviewBinding
import com.example.servivet.ui.base.BaseAdapter

class TransactionAdapter(var list: ArrayList<WalletTransaction>, requireContext: Context) :
    BaseAdapter<TransactionsDesignRecyclerviewBinding, WalletTransaction>(list) {
    override val layoutId: Int = R.layout.transactions_design_recyclerview

    override fun bind(
        binding: TransactionsDesignRecyclerviewBinding,
        item: WalletTransaction?,
        position: Int,
    ) {
        binding.apply {
            data = item
        }
    }

    fun updateList(list: ArrayList<WalletTransaction>) {
        val start = if (this.list.size > 0) this.list.size else 0
        this.list.addAll(list)
        notifyItemRangeInserted(start, this.list.size)
    }
}