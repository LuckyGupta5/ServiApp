package com.example.servivet.ui.main.view_model

import android.content.Context
import android.view.View
import androidx.navigation.findNavController
import com.example.servivet.ui.base.BaseViewModel

class SearchLocationViewModel:BaseViewModel() {
    inner class ClickAction(requireContext: Context) {
        fun backBtn(view: View){
            view.findNavController().popBackStack()
        }
    }
}