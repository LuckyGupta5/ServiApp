package com.example.servivet.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.servivet.utils.interfaces.ListAdapterItem


abstract class BaseAdapter<BINDING : ViewDataBinding, T : ListAdapterItem>(
    var data: List<T>,
) : RecyclerView.Adapter<BaseViewHolder<BINDING>>() {


    @get:LayoutRes
    abstract val layoutId: Int

    abstract fun bind(binding: BINDING, item: T?, position: Int)


    fun updateData(list: List<T>) {
        this.data = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<BINDING> {
        val binder = DataBindingUtil.inflate<BINDING>(
            LayoutInflater.from(parent.context),
            layoutId,
            parent,
            false
        )

        return BaseViewHolder(binder)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<BINDING>, position: Int) {
        // val obj = ListAdapterItem

        if (data.isEmpty()) {
            bind(holder.binder, null, position)
        } else
            bind(holder.binder, data[position], position)
    }

    override fun getItemCount(): Int = data.size
}
