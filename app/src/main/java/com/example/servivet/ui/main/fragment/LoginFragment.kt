package com.example.servivet.ui.main.fragment

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentLoginBinding
import com.example.servivet.databinding.TermConditionPolicyLayoutBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.view_model.LoginViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.CommonUtils.showToast
import com.example.servivet.utils.Constants
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.android.material.bottomsheet.BottomSheetDialog


class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>(R.layout.fragment_login) {
    private var isTerm: Boolean = false
    private var isPolicy: Boolean = false
    private var bottomSheetDialog: BottomSheetDialog? = null
    override val binding: FragmentLoginBinding by viewBinding(FragmentLoginBinding::bind)
    override val mViewModel: LoginViewModel by viewModels()


    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
    }

    override fun setupViews() {

        if (isAdded)
            binding.apply {
                lifecycleOwner = viewLifecycleOwner
                viewModel = mViewModel
                click = mViewModel.ClickAction(requireContext(), binding,requireActivity(),requireActivity().isFinishing)
            }


        if(isAdded)
            requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        isTerm = false
        isPolicy = false
        binding.nextBtn.isEnabled = false
        mViewModel.isCheck.postValue(false)
        mViewModel.countryCode = binding.countryCode.selectedCountryCode.toString()
        mViewModel.sendOtpRequest.countryCode = binding.countryCode.selectedCountryCode.toString()
        binding.countryCode.setOnCountryChangeListener {
            mViewModel.countryCode = binding.countryCode.selectedCountryCode.toString()
            mViewModel.sendOtpRequest.countryCode =
                binding.countryCode.selectedCountryCode.toString()

        }
        if(Session.type!=null && Session.type!=""){
            Session.saveType(Session.type)
        }else{
            Session.saveType("1")
        }
        setTermConditionPrivacy()
        setClick()
        Handler().postDelayed({
            binding.nextBtn.visibility = View.VISIBLE
        }, 10)

        onBackCall()

    }

    private fun onBackCall() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                @SuppressLint("SetTextI18n")
                override fun handleOnBackPressed() {
                    requireActivity().finishAffinity()

                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }



    private fun setClick() {
        binding.termConditionPolicyLayout.setOnClickListener {
            if (!isTerm) {
                showToast(getString(R.string.accept_the_term_and_condition))
            } else if (!isPolicy) {
                showToast(getString(R.string.accept_the_privacy_policy))
            } else {
                if (mViewModel.isCheck.value == true) {
                    mViewModel.isCheck.postValue(false)
                } else {
                    mViewModel.isCheck.postValue(true)
                }
            }
        }
        mViewModel.countryCode = binding.countryCode.selectedCountryCode


    }

    private fun setTermConditionPrivacy()
    {
        val ss = SpannableString(getString(R.string.i_agree_to_terms_and_conditions_and_data_privacy_policies))
        val value = getString(R.string.i_agree_to_terms_and_conditions_and_data_privacy_policies)
        val firstString = getString(R.string.term_and_condition)
        val secondString = getString(R.string.privacy_policy)
        val firstIndex = value.indexOf(firstString)
        val firstLastCharIndex = firstIndex + firstString.length
        val secondIndex = value.indexOf(secondString)
        val secondLastIndex = secondIndex + secondString.length
        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openBottomSheet("1")
            }

            @RequiresApi(Build.VERSION_CODES.Q)
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = resources.getColor(R.color.black)
                ds.isUnderlineText = true
                ds.underlineColor = resources.getColor(R.color.black)
                ds.typeface = Typeface.create("bold", Typeface.BOLD)
            }
        }
        val clickableSpan2: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openBottomSheet("2")
            }

            @RequiresApi(Build.VERSION_CODES.Q)
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = resources.getColor(R.color.black)
                ds.isUnderlineText = true
                ds.underlineColor = resources.getColor(R.color.black)
                ds.typeface = Typeface.create("bold", Typeface.BOLD)
            }
        }
        ss.setSpan(clickableSpan1, firstIndex, firstLastCharIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(clickableSpan2, secondIndex, secondLastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.termConditionPolicy.text = ss
        binding.termConditionPolicy.movementMethod = LinkMovementMethod.getInstance()

    }

    private fun openBottomSheet(type: String) {
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        val bottomSheetBinding: TermConditionPolicyLayoutBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.term_condition_policy_layout,
            null,
            false
        )
        if (type == "1") {
            bottomSheetBinding.text.text = getString(R.string.terms_conditions)
        } else {
            bottomSheetBinding.text.text = getString(R.string.privacy_policy)
        }
        bottomSheetDialog!!.setContentView(bottomSheetBinding.root)
        bottomSheetBinding.rejectBtn.setOnClickListener {
            bottomSheetDialog!!.dismiss()
        }
        bottomSheetBinding.acceptBtn.setOnClickListener {
            if (type == "1") {
                isTerm = true
            } else {
                isPolicy = true
            }
            bottomSheetDialog!!.dismiss()
        }
        bottomSheetDialog!!.show()
    }


    override fun setupObservers() {
        mViewModel.errorMessage.observe(viewLifecycleOwner) {
            showSnackBar(it)
        }
        mViewModel.sendOtpResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            showSnackBar(it.data.message)
                            var bundle = Bundle()
                            bundle.putString(Constants.MOBILE_NUMBER, mViewModel.mobileNumber)
                            bundle.putString(Constants.COUNTRY_CODE, mViewModel.countryCode)
                            bundle.putString(Constants.OTP_NUMBER,it.data.result.otpOrderNumber)
                            findNavController().navigate(R.id.action_loginFragment_to_otpVarificationFragment2, bundle)
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(it.data.message)
                        }

                        else -> {
                            showSnackBar(it.data!!.message)
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




