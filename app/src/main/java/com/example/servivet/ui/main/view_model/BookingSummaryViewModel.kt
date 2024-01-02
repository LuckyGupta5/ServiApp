package com.example.servivet.ui.main.view_model

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.databinding.FragmentBookingSummaryBinding
import com.example.servivet.ui.base.BaseViewModel

class BookingSummaryViewModel:BaseViewModel() {
    var atCenter=MutableLiveData(true)
    var atHome=MutableLiveData(false)
    inner class ClickAction(var context: Context,var binding:FragmentBookingSummaryBinding){
        fun backbtn(view:View){
            view.findNavController().popBackStack()
        }
        fun atCenter(view: View){
            atCenter.postValue(true)
            atHome.postValue(false)
            binding.addAddressLayout.isVisible=false

        }
        fun atHome(view: View){
            atHome.postValue(true)
            binding.addAddressLayout.isVisible=true
            atCenter.postValue(false)

        }
        fun gotopayment(view: View){
            view.findNavController().navigate(R.id.action_bookingSummaryFragment_to_bookingPaymentFragment)
        }
        fun gotoaddlocation(view: View){
            view.findNavController().navigate(R.id.action_bookingSummaryFragment_to_addLocationFragment)
        }


    }
}