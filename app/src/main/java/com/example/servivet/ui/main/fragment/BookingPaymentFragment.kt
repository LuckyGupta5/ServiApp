package com.example.servivet.ui.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentBookingPaymentBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.view_model.BookingPaymentViewModel

class BookingPaymentFragment : BaseFragment<FragmentBookingPaymentBinding,BookingPaymentViewModel>(R.layout.fragment_booking_payment) {
    override val binding: FragmentBookingPaymentBinding by viewBinding(FragmentBookingPaymentBinding::bind)
    override val mViewModel: BookingPaymentViewModel by viewModels()

    override fun isNetworkAvailable(boolean: Boolean) {
    }
    override fun setupViews() {
        binding.apply {
            lifecycleOwner=viewLifecycleOwner
            viewModel=mViewModel
            click=mViewModel.ClickAction()
        }
    }

    override fun setupViewModel() {

    }



    override fun setupObservers() {
    }

}