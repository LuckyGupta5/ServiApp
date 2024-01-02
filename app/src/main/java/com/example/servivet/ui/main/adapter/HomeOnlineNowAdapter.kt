package com.example.servivet.ui.main.adapter

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.databinding.HomeOnlineNowRecyclerBinding
import com.example.servivet.databinding.HomeServicesRecyclerBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem

class HomeOnlineNowAdapter(var type:String,var context: Context,var list:ArrayList<ListAdapterItem>) :
    BaseAdapter<HomeOnlineNowRecyclerBinding, ListAdapterItem>(list) {
    override val layoutId: Int = R.layout.home_online_now_recycler


    override fun getItemCount(): Int {
        if(type=="1")
            if(list.size<5)
            return 3
          else
              return 3
        else
           return 10
    }
    override fun bind(binding: HomeOnlineNowRecyclerBinding, item: ListAdapterItem?, position: Int) {
        binding.apply {
            binding.click=ClickAction(position)
            if(type=="1"){
                binding.view1.isVisible=true
                binding.viewtop.isVisible=false
            }
            else{
                binding.viewtop.isVisible=true
                binding.view1.isVisible=false
            }

        }

    }

    inner class ClickAction(var position: Int) {
        /*fun gotoConnetProfile(view:View){
            view.findNavController().navigate(R.id.action_onlineNowFragment_to_connectionProfileFragment)

        }*/

    }


}