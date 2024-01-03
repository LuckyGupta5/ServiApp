package com.example.servivet.ui.main.view_model

import android.view.View
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.ui.base.BaseViewModel

class BookingPaymentViewModel:BaseViewModel() {
    inner class ClickAction{
        fun backbtn(view:View){
            view.findNavController().popBackStack()
        }
        fun gotoCoupon(view: View){
            view.findNavController().navigate(R.id.action_bookingPaymentFragment_to_couponsFragment)
        }

    }
}