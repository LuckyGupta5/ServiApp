package com.example.servivet.ui.main.fragment

import android.util.Log
import android.view.LayoutInflater
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.DeleteAccountBottomSheetBinding
import com.example.servivet.databinding.FragmentSettingsBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.view_model.SettingsViewModel
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.CommonUtils.showToast
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson

class SettingsFragment:BaseFragment<FragmentSettingsBinding,SettingsViewModel>(R.layout.fragment_settings) {
    override val binding: FragmentSettingsBinding by viewBinding(FragmentSettingsBinding::bind)
    override val mViewModel: SettingsViewModel by viewModels()
    private var deleteUserBottomSheetDialog: BottomSheetDialog? = null

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
    }


    override fun setupViews() {
        binding.apply {
            lifecycleOwner=viewLifecycleOwner
            viewModel=mViewModel
            click=mViewModel.ClickAction(requireActivity())
        }

        if(Session.notificationStatus!=null){
            if(Session.notificationStatus==true){
                binding.switchBtn.isChecked=true
            }else{
                binding.switchBtn.isChecked=false
            }
        }
        setClick()
    }
    private fun openBottomSheetForDelete() {
        deleteUserBottomSheetDialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        val bottomSheetBinding: DeleteAccountBottomSheetBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.delete_account_bottom_sheet, null, false)
        deleteUserBottomSheetDialog!!.setContentView(bottomSheetBinding.root)
        bottomSheetBinding.noBtn.setOnClickListener { deleteUserBottomSheetDialog!!.dismiss() }
        bottomSheetBinding.yesBtn.setOnClickListener {
            mViewModel.hitDeleteAccountApi()

        }
        deleteUserBottomSheetDialog!!.show()
    }

    private fun setClick() {
        binding.switchBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mViewModel.notificationRequest.notificationStatus = true
                mViewModel.hitNotificationStatusApi()
                Session.saveNotificationStatus(true)
            } else {
                mViewModel.notificationRequest.notificationStatus = false
                mViewModel.hitNotificationStatusApi()
                Session.saveNotificationStatus(false)
            }
        }



        binding.deleteAccount.setOnClickListener { openBottomSheetForDelete() }
    }

    override fun setupObservers() {
        mViewModel.logoutResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            showToast(it.data.message)
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showToast(it.data.message)
                        }

                    }
                }

                Status.LOADING -> {
                    ProcessDialog.dismissDialog()
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog()
                    it.message?.let {
                        showSnackBar(it)
                    }
                }

                Status.UNAUTHORIZED -> {
                    ProcessDialog.dismissDialog()
                    it.message?.let {
                        showSnackBar(it)
                    }
                }
            }
        }

        mViewModel.notificationResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showToast(it.data.message)
                        }

                    }
                }

                Status.LOADING -> {
                    ProcessDialog.dismissDialog()
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog()
                    it.message?.let {
                        showSnackBar(it)
                    }
                }

                Status.UNAUTHORIZED -> {
                    ProcessDialog.dismissDialog()
                    it.message?.let {
                        showSnackBar(it)
                    }
                }
            }
        }


        mViewModel.deleteResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            deleteUserBottomSheetDialog!!.dismiss()
                            showToast(it.data.message)
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showToast(it.data.message)
                        }

                    }
                }

                Status.LOADING -> {
                    ProcessDialog.dismissDialog()
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog()
                    it.message?.let {
                        showSnackBar(it)
                    }
                }

                Status.UNAUTHORIZED -> {
                    ProcessDialog.dismissDialog()
                    it.message?.let {
                        showSnackBar(it)
                    }
                }
            }
        }

    }
}