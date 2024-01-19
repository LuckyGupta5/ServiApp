package com.example.servivet.ui.main.view_model.wallet

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.example.servivet.databinding.FragmentAddServiceBinding
import com.example.servivet.databinding.FragmentPaymentBinding
import com.example.servivet.ui.base.BaseViewModel

class PaymentViewModel: BaseViewModel() {

    inner class ClickAction(
        var context: Context,
        var binding: FragmentPaymentBinding,
        var requireActivity: FragmentActivity,
        var finishing: Boolean
    ) {

    }
}