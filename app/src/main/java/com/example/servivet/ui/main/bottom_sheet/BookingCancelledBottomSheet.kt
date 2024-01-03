package com.example.servivet.ui.main.bottom_sheet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.servivet.R
import com.example.servivet.databinding.FragmentBookingCancelledBottomSheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.view_model.BookingCancelledViewModel

class BookingCancelledBottomSheet : BaseBottomSheetDailogFragment<FragmentBookingCancelledBottomSheetBinding,BookingCancelledViewModel>(R.layout.fragment_booking_cancelled_bottom_sheet) {
    override val mViewModel: BookingCancelledViewModel by viewModels()

    override fun getLayout(): Int {
     return R.layout.fragment_booking_cancelled_bottom_sheet   }

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
    }

    override fun setupViews() {
        binding.apply {
            lifecycleOwner=viewLifecycleOwner
            viewModel=mViewModel
            click=mViewModel.ClickAction()
        }
    }

    override fun setupObservers() {
    }

}