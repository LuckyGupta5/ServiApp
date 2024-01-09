package com.example.servivet.ui.main.view_model

import android.view.View
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.ui.main.fragment.BookingDetailsFragmentDirections

class BookingDetailsViewModel :BaseViewModel() {
    inner class ClickAction(){
        fun backbtn(view:View){
            view.findNavController().popBackStack()
        }
        fun gotoBookingSummary(view: View){
            view.findNavController().navigate(BookingDetailsFragmentDirections.actionBookingDetailsFragmentToBookingSummaryFragment("",""))        }

    }
}