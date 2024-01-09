package com.example.servivet.ui.main.view_model

import android.view.View
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.ui.base.BaseViewModel

class MyConnectionModelView:BaseViewModel() {
    inner class ClickAction{
        fun goConnectionrequest(view:View){
            view.findNavController().navigate(R.id.action_fragmentMyConnection_to_connectionsRequestFragment)
        }
        fun backbtn(view: View){
            view.findNavController().popBackStack()
        }


    }
}