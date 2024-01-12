package com.example.servivet.ui.main.bottom_sheet

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.servivet.R
import com.example.servivet.databinding.FragmentRatingReportBottomSheetBinding
import com.example.servivet.databinding.FragmentSavedAddressesBottomsheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.view_model.SaveAddressBottomsheetViewModel
import com.example.servivet.ui.main.view_model.sub_category_models.RatingReportViewModel
import com.example.servivet.utils.Session

class RatingReportBottomSheetFragment:BaseBottomSheetDailogFragment<FragmentRatingReportBottomSheetBinding,RatingReportViewModel>(
    R.layout.fragment_rating_report_bottom_sheet) {
    override val mViewModel: RatingReportViewModel by viewModels()

    override fun getLayout(): Int {
        return R.layout.fragment_rating_report_bottom_sheet
    }

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

        binding.canceltBtn.setOnClickListener {
            dismiss()
        }

        binding.submitBtn.setOnClickListener {
            mViewModel.hitReportRatingApi()
        }

    }

    override fun setupObservers() {
    }

}