package com.example.servivet.ui.main.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.verifyotp.response.VerifyOTPResult
import com.example.servivet.databinding.FragmentOtpVarificationBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.activity.HomeActivity
import com.example.servivet.ui.main.view_model.OtpViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.Constants
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode


class OtpVarificationFragment :
    BaseFragment<FragmentOtpVarificationBinding, OtpViewModel>(R.layout.fragment_otp_varification) {

    override val binding: FragmentOtpVarificationBinding by viewBinding(
        FragmentOtpVarificationBinding::bind
    )
    override val mViewModel: OtpViewModel by viewModels()
    private var type = ""
    var verifyOtpResult: VerifyOTPResult? = null
    override fun isNetworkAvailable(boolean: Boolean) {}

    override fun setupViewModel() {
    }

    @SuppressLint("SetTextI18n")
    override fun setupViews() {
        if (isAdded)
            binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction(requireContext(), binding, type,requireActivity(),requireActivity().isFinishing)


        }
            mViewModel.counterDownTimmer(requireActivity(), binding)

        showSoftKeyboard(requireContext(), binding.otpPin)
        mViewModel.mobilenumber = arguments?.getString(Constants.MOBILE_NUMBER) ?: ""
        mViewModel.otpNumber = arguments?.getString(Constants.OTP_NUMBER) ?: ""
        mViewModel.countrycode = arguments?.getString(Constants.COUNTRY_CODE) ?: ""
        binding.number.text = "+${mViewModel.countrycode} ${mViewModel.mobilenumber}"
        mViewModel.verifyOtpRequest.countryCode = mViewModel.countrycode
        mViewModel.verifyOtpRequest.mobile = mViewModel.mobilenumber

        mViewModel.sendOtpRequest.countryCode = mViewModel.countrycode
        mViewModel.sendOtpRequest.mobile = mViewModel.mobilenumber
        setRequest()
        binding.resendOtp.isVisible = true

        binding.otpPin.setOnClickListener {
//            binding.scrollview.scrollTo(0,binding.scrollview.maxScrollAmount)
        }

    }

    private fun setRequest() {
        mViewModel.verifyOtpRequest.otpOrderNumber = mViewModel.otpNumber
        mViewModel.verifyOtpRequest.deviceId = "sdhsbnsdahjksjhkjsdhfsjgfjsdhjfhskfhkjds-53"//change when the firebase is implement
        mViewModel.verifyOtpRequest.deviceModelNo = "mozila"//change when the firebase is implement
        mViewModel.verifyOtpRequest.deviceVersion = "14"//change when the firebase is implement
        mViewModel.verifyOtpRequest.deviceType = "website"//change when the firebase is implement
        mViewModel.verifyOtpRequest.deviceToken = Session.fcmToken//change when the firebase is implement
        mViewModel.verifyOtpRequest.userType = Session.type.toInt()   // 1-> consumer, 2-> Business

        Log.e("TAG", "setRequest1234: ${Session.fcmToken}", )
    }

    fun showSoftKeyboard(context: Context, editText: EditText) {
        try {
            editText.requestFocus()
            editText.postDelayed(
                {
                    val keyboard =
                        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    keyboard.showSoftInput(editText, 0)
                }, 200
            )
        } catch (npe: NullPointerException) {
            npe.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun setupObservers() {
        mViewModel.sendOtpResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            showSnackBar(it.data.message)
                            mViewModel.verifyOtpRequest.otpOrderNumber = it.data.result.otpOrderNumber
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
        mViewModel.verifyOtpResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            showSnackBar(it.data.message)
                            Session.saveToken(it.data.result.token)
                            Session.saveVerifyUserData(it.data.result)
                            if (Session.type.equals("1")) {
                                if (it.data.result.isProfileVerify) {
                                    Session.saveIsLogin(true)
                                    startActivity(Intent(context, HomeActivity::class.java))
                                    (context as Activity).finish()
                                    binding.otpPin.setText("")
                                } else {
                                    var bundle = Bundle()
                                    Constants.MOBNUMBER = mViewModel.mobilenumber
                                    Constants.C_Code = mViewModel.countrycode
                                    bundle.putString(Constants.MOBILE_NUMBER, mViewModel.mobilenumber)
                                    bundle.putString(Constants.COUNTRY_CODE, mViewModel.countrycode)
                                    findNavController().navigate(R.id.action_otpVarificationFragment_to_completeProfileFragment, bundle)
                                }
                            } else if (Session.type.equals("2"))
                                if (it.data.result.isBusinessVerify==0) {
                                    Constants.MOBNUMBER = mViewModel.mobilenumber
                                    Constants.C_Code = mViewModel.countrycode
                                    var bundle = Bundle()
                                    bundle.putString(Constants.MOBILE_NUMBER, mViewModel.mobilenumber)
                                    bundle.putString(Constants.COUNTRY_CODE, mViewModel.countrycode)
                                    findNavController().navigate(R.id.action_otpVarificationFragment_to_business_Verification_Fragment,bundle)
                                } else{
                                    val intent = Intent(requireContext(), HomeActivity::class.java)
                                    startActivity(intent)
                                    Session.saveIsLogin(true)
                                }


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

    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel.stopCountDown()
        binding.resendOtp.isVisible = false
    }
}

