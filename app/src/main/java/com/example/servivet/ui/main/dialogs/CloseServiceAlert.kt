package com.example.servivet.ui.main.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.servivet.R
import com.example.servivet.data.model.booking_module.local_request.SendDate
import com.example.servivet.databinding.FragmentCloseServiceAlertBinding
import com.example.servivet.ui.main.view_model.booking_models.CloseServiceViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson


class CloseServiceAlert : DialogFragment() {
    private lateinit var binding: FragmentCloseServiceAlertBinding
    val mViewModel: CloseServiceViewModel by viewModels()
    private val closingDates: CloseServiceAlertArgs by navArgs()
    private lateinit var sendDate: SendDate


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCloseServiceAlertBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //  initCloseServiceModel()
        getDates()
        binding.clickEvent = ::onClick

        return binding.root
    }

    private fun getDates() {
        sendDate = Gson().fromJson(closingDates.data,SendDate::class.java)
        binding.dates = sendDate

    }

    private fun onClick(type: String) {
        when (type) {
            getString(R.string.cancel) -> {
                dismiss()

            }

            getString(R.string.close_service) -> {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    getString(R.string.close_service),
                    getString(R.string.close_service)
                )
                dismiss()
            }
        }
    }

    private fun initCloseServiceModel() {
        mViewModel.getMarkAsCompleteData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                                getString(
                                    R.string.leave
                                ), "false"
                            )
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
}