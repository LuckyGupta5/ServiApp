package com.example.servivet.ui.main.view_model

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.databinding.FragmentChatsBinding
import com.example.servivet.ui.base.BaseViewModel

class ChatViewModel:BaseViewModel() {
    inner class ClickAction(var context: Context,var binding:FragmentChatsBinding){
        fun gotochat(view: View){
           // binding.idChatRecycle.isVisible=true
           // binding.startnewchatlayout.isVisible=false
        }
        fun gotochatrequest(view: View){
            view.findNavController().navigate(R.id.action_chatFragment_to_chatRequestFragment)
        }

        fun clickSearch(view: View){
            binding.idSearchLayout.isVisible = true
            binding.toplayout.isVisible = false
        }
        fun closeSearch(view: View){
            binding.idSearchLayout.isVisible = false
            binding.toplayout.isVisible = true
        }

    }
}