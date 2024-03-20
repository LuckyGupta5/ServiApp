package com.example.servivet.ui.main.bottom_sheet

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.servivet.R
import com.example.servivet.data.model.booking_module.local_request.SendDate
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class CloseServiceBottomFragment :
    BaseBottomSheetDailogFragment<FragmentCloseServiceBottomBinding, CloseServiceViewModel>(R.layout.fragment_close_service_bottom) {
    override val mViewModel: CloseServiceViewModel by viewModels()
    private var sendDate = SendDate()


    override fun getLayout(): Int {
        return R.layout.fragment_close_service_bottom
    }

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
    }

    override fun setupViews() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction()
            binding.clickEvent = ::onClick
        }
        getBottomSheetCallBack()


    }


    override fun setupObservers() {
        mViewModel.getMarkAsCompleteData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            findNavController().previousBackStackEntry?.savedStateHandle?.set(getString(R.string.leave), false)
                            dialog?.dismiss()
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            Log.e("TAG", "setupObservers: botDonne")

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

    private fun onClick(type: String) {
        when (type) {
            getString(R.string.start_date) -> {
                CommonUtils.selectFromDate(requireContext(), "") {
                    binding.idStartDate.text = it
                }
            }

            getString(R.string.end_date) -> {
                CommonUtils.selectFromDate(requireContext(), binding.idStartDate.text.toString()) {
                    binding.idEndDate.text = it
                }

            }

            getString(R.string.close) -> {
                if (binding.idStartDate.text.toString()
                        .isEmpty() || binding.idEndDate.text.toString().isEmpty()
                ) {
                    Toast.makeText(requireContext(), "Please select both dates", Toast.LENGTH_SHORT)
                        .show()

                } else if (!binding.idCheckBox.isChecked) {
                    Toast.makeText(requireContext(), "Please check checkBox ", Toast.LENGTH_SHORT)
                        .show()

                } else {

                    sendDate.startTime = binding.idStartDate.text.toString()
                    sendDate.endTime = binding.idEndDate.text.toString()

                    findNavController().navigate(
                        CloseServiceBottomFragmentDirections.actionCloseServiceBottomFragmentToCloseServiceAlert(
                            Gson().toJson(sendDate), getString(
                                R.string.closeservice
                            )
                        )
                    )
                    //  mViewModel.getMarkAsCompleteRequest(binding.idStartDate.text.toString(), binding.idEndDate.text.toString(), false)

                }
            }
        }
    }


    private fun getBottomSheetCallBack() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(getString(R.string.close_service))
            ?.observe(viewLifecycleOwner) {
                Log.e("TAG", "getBottomSheetCallBack:data ")
                mViewModel.getMarkAsCompleteRequest(binding.idStartDate.text.toString(), binding.idEndDate.text.toString(), false)

            }
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        findNavController().previousBackStackEntry?.savedStateHandle?.set(getString(R.string.leave), true)

    }

}


