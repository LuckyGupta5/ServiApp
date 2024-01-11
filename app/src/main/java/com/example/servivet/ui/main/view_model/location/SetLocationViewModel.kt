package com.example.servivet.ui.main.view_model.location

import android.view.View
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.ui.base.BaseViewModel

class SetLocationViewModel:BaseViewModel() {
    inner class ClickAction{
        fun backbtn(view:View){
            view.findNavController().popBackStack()
        }

        fun gottomHomeFragment(view: View){
            view.findNavController().navigate(R.id.action_setLocationFragment_to_homeFragment)
        }

    }
}