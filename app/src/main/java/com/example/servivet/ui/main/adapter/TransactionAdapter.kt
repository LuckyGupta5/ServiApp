package com.example.servivet.ui.main.adapter

import com.example.servivet.R
import com.example.servivet.databinding.TransactionsDesignRecyclerviewBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem

class TransactionAdapter(var list:ArrayList<ListAdapterItem>):BaseAdapter<TransactionsDesignRecyclerviewBinding,ListAdapterItem>(list) {
    override val layoutId: Int= R.layout.transactions_design_recyclerview

    override fun bind(binding: TransactionsDesignRecyclerviewBinding, item: ListAdapterItem?, position: Int, )
    {
    }

    override fun getItemCount(): Int {
        return 10
    }
}