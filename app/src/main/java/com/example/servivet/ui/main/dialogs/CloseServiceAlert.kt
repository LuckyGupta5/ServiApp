package com.example.servivet.ui.main.dialogs

import android.content.Intent
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
import com.example.servivet.ui.main.activity.MainActivity
import com.example.servivet.ui.main.view_model.SettingsViewModel
import com.example.servivet.ui.main.view_model.SplashViewModel
import com.example.servivet.ui.main.view_model.booking_models.CloseServiceViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson
class CloseServiceAlert : DialogFragment() {
    private var saveLanguage: String?=""
    private lateinit var binding: FragmentCloseServiceAlertBinding
    val mViewModel: CloseServiceViewModel by viewModels()
    val logoutModel: SettingsViewModel by viewModels()
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
        initLogoutModel()
        binding.clickEvent = ::onClick
        return binding.root
    }
    private fun getDates() {
        when (closingDates.from) {
            "closeService" /*getString(R.string.closeservice)*/ -> {
                Log.d("TAG", "getDates:  ${closingDates.from}   and ${closingDates.data} and closeService is $ ")
                sendDate = Gson().fromJson(closingDates.data, SendDate::class.java)
                binding.dates = sendDate
            }
           "logOut" /*getString(R.string.logout)*/ -> {
                binding.from = closingDates.from
            }
        }

    }

    private fun onClick(type: String)
    {
        Log.d("TAG", "onClick:1 $type")
        when (type) {
            getString(R.string.cancel) -> {
                Log.d("TAG", "onClick:2 $type")
                dismiss()
            }

            getString(R.string.close_service) -> {
                when (closingDates.from)
                {
                    getString(R.string.closeservice) -> {
                        findNavController().previousBackStackEntry?.savedStateHandle?.set(
                            getString(R.string.close_service),
                            getString(R.string.close_service)
                        )
                        dismiss()
                    }
                  "logOut" /* getString(R.string.logout)*/ -> {
                        logoutModel.hitLogoutApi()
                    }
                }

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


    private fun initLogoutModel() {
        logoutModel.getLogoutData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            saveLanguage=Session.language
                            Session.logout()
                           // Session.saveIsLanguage(saveLanguage!!)
                            SplashViewModel.isLogout = false
                            Session.isLogin = false
                            Session.saveIsLogin(false)
                            var intent = Intent(context, MainActivity::class.java)
                            requireActivity().startActivity(intent)


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