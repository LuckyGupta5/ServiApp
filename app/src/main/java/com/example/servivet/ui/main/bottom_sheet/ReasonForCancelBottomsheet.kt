package com.example.servivet.ui.main.bottom_sheet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.servivet.R
import com.example.servivet.databinding.FragmentReScheduleBookingBottomSheetBinding
import com.example.servivet.databinding.FragmentReasonForCancelBottomsheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.adapter.ReasonForCancelAdapterbottomsheet
import com.example.servivet.ui.main.view_model.ReasonForCancellationViewModel

class ReasonForCancelBottomsheet() : BaseBottomSheetDailogFragment<FragmentReasonForCancelBottomsheetBinding,ReasonForCancellationViewModel>(R.layout.fragment_reason_for_cancel_bottomsheet) {
    override val mViewModel: ReasonForCancellationViewModel by viewModels()



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
        setadapter()
        dismissbottomsheet()
    }


    fun dismissbottomsheet(){
        binding.submitbtn.setOnClickListener(View.OnClickListener {
            val fragment=BookingCancelledBottomSheet()
            fragment.show(childFragmentManager,"jhgfds")
        })
    }
   fun setadapter(){
       binding.recyclerviewcancel.adapter=ReasonForCancelAdapterbottomsheet(requireContext(),ArrayList())
   }
    override fun setupObservers() {
    }
    override fun getLayout(): Int {
        return R.layout.fragment_reason_for_cancel_bottomsheet
    }

}