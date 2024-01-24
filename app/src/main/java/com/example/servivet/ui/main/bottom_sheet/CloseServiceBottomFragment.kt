package com.example.servivet.ui.main.bottom_sheet

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.servivet.R
import com.example.servivet.data.model.payment.payment_amount.response.PaymentResponseMain
import com.example.servivet.databinding.FragmentBookingCancelledBottomSheetBinding
import com.example.servivet.databinding.FragmentCloseServiceBottomBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.view_model.BookingCancelledViewModel
import com.example.servivet.ui.main.view_model.booking_models.CloseServiceViewModel
import com.example.servivet.utils.AESHelper
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.Constants
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson


class CloseServiceBottomFragment : BaseBottomSheetDailogFragment<FragmentCloseServiceBottomBinding, CloseServiceViewModel>(R.layout.fragment_close_service_bottom)  {
    override val mViewModel: CloseServiceViewModel by viewModels()
    private var startDate = ""
    private var endDate = ""

    override fun getLayout(): Int {
       return R.layout.fragment_close_service_bottom
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
            binding.clickEvent =::onClick
        }


    }

    override fun setupObservers() {
        mViewModel.getMarkAsCompleteData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            Log.e("TAG", "setupObservers: done", )
                            dialog?.dismiss()

                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            Log.e("TAG", "setupObservers: botDonne", )

                        }

                    }
                }

                Status.LOADING -> {
                    ProcessDialog.startDialog(requireContext())
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog()

                    it.message?.let {
                        showSnackBar(it)

                    }
                }

                Status.UNAUTHORIZED -> {
                    CommonUtils.logoutAlert(
                        requireContext(),
                        "Session Expired",
                        "Unauthorized User",
                        requireActivity()
                    )
                }
            }
        }
    }

    private fun onClick(type:String){
        when(type){
            getString(R.string.start_date)->{
                CommonUtils.selectFromDate(requireContext()) {
                    binding.idStartDate.text = it
                    startDate = it
                }
            }
            getString(R.string.end_date)->{
                CommonUtils.selectFromDate(requireContext()) {
                    binding.idEndDate.text = it
                    endDate = it

                }
            }
            getString(R.string.close)->{

                mViewModel.getMarkAsCompleteRequest(startDate,endDate,false)


            }
        }
    }


}