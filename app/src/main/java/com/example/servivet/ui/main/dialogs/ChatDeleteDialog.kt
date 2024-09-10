package com.example.servivet.ui.main.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.servivet.R
import com.example.servivet.databinding.FragmentChatDeleteDialogBinding
import com.example.servivet.databinding.FragmentCloseServiceAlertBinding

class ChatDeleteDialog : DialogFragment(){
    private  lateinit var binding:FragmentChatDeleteDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatDeleteDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return  binding.root


    }
}