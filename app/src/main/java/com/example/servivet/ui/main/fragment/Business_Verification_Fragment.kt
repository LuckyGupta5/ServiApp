package com.example.servivet.ui.main.fragment

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentBusinessVerificationBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.activity.HomeActivity
import com.example.servivet.ui.main.view_model.BusinessVerificationViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode


class Business_Verification_Fragment :
    BaseFragment<FragmentBusinessVerificationBinding, BusinessVerificationViewModel>(R.layout.fragment_business__verification) {
    override val binding: FragmentBusinessVerificationBinding by viewBinding(FragmentBusinessVerificationBinding::bind)
    override val mViewModel: BusinessVerificationViewModel by viewModels()
    override fun isNetworkAvailable(boolean: Boolean) {
    }


    override fun setupViewModel() {
    }

    override fun setupViews() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction(requireContext(), binding,requireActivity(),requireActivity().isFinishing)
        }
        mViewModel.businessVerificationRequest.userType= Session.type.toInt()
        mViewModel.businessVerificationRequest.businessType = "3"
    }




    override fun setupObservers() {
        mViewModel.errorMessage.observe(viewLifecycleOwner) { showSnackBar(it) }
        mViewModel.businessVerificationResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            showSnackBar(it.data.message)
                            Session.saveIsLogin(true)
                            var intent= Intent(context, HomeActivity::class.java)
                            startActivity(intent)
                            (context as Activity).finish()
                        }
                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(it.data.message)
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