package com.example.servivet.ui.main.view_model

import android.view.View
import androidx.navigation.findNavController
import com.example.servivet.ui.base.BaseViewModel

class MyWalletViewModel:BaseViewModel() {
    inner class ClickAction{
        fun backbtn(view:View){
            view.findNavController().popBackStack()
        }
    }
}