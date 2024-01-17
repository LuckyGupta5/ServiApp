package com.example.servivet.ui.main.bottom_sheet

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.servivet.R
import com.example.servivet.databinding.FragmentRatingReportBottomSheetBinding
import com.example.servivet.databinding.FragmentSavedAddressesBottomsheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.view_model.SaveAddressBottomsheetViewModel
import com.example.servivet.ui.main.view_model.sub_category_models.RatingReportViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.CommonUtils.showToast
import com.example.servivet.utils.Constants
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode

class RatingReportBottomSheetFragment:BaseBottomSheetDailogFragment<FragmentRatingReportBottomSheetBinding,RatingReportViewModel>(
    R.layout.fragment_rating_report_bottom_sheet) {
    private var ratingId: String?=""
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
        ratingId=arguments?.getString("id")
        binding.canceltBtn.setOnClickListener {
            dismiss()
        }

        binding.submitBtn.setOnClickListener {
           if(binding.descriptionEditText.text!!.isNotEmpty()){
               mViewModel.reportRequest.contentType="rating"
               mViewModel.reportRequest.userFeedBack=binding.descriptionEditText.text.toString()
               mViewModel.reportRequest.contentId=ratingId
               mViewModel.hitReportRatingApi()
           }else{
               showToast("Please enter the feedback")
        }

    }




    }

    override fun setupObservers() {
        mViewModel.ratingMData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            showToast(it.data.message)
                            dismiss()
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showToast(it.data.message)
                        }

                        else -> {
                            showToast(it.data!!.message)
                        }
                    }
                }

                Status.LOADING -> {
                    ProcessDialog.startDialog(requireContext())
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog()

                    it.message?.let {
                        showToast(it)
                    }
                }

                Status.UNAUTHORIZED -> {
                    CommonUtils.logoutAlert(requireContext(), "Session Expired", "Unauthorized User", requireActivity())
                }
            }
        }
    }

}