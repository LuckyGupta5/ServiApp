package com.example.servivet.ui.main.view_model

import android.content.Context
import android.view.View
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.ui.base.BaseViewModel

class SearchLocationViewModel:BaseViewModel() {
    inner class ClickAction(requireContext: Context) {
        fun backBtn(view: View){
            view.findNavController().popBackStack()
        }
        fun gottoManualMap(view: View){
            view.findNavController().navigate(R.id.action_searchLocationFragment_to_setLocationFragment)
        }
    }
}