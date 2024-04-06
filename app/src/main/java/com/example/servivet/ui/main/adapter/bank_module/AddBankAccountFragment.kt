package com.example.servivet.ui.main.adapter.bank_module

import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.bank_module.bank_list_response.response.Bank
import com.example.servivet.data.model.common.response.CommonResponse
import com.example.servivet.databinding.FragmentAddBankAccountBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.view_model.bank_module.BankListViewModel
import com.example.servivet.utils.AESHelper
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.Constants
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson


class AddBankAccountFragment :
    BaseFragment<FragmentAddBankAccountBinding, BankListViewModel>(R.layout.fragment_add_bank_account) {
    override val binding: FragmentAddBankAccountBinding by viewBinding(FragmentAddBankAccountBinding::bind)
    override val mViewModel: BankListViewModel by viewModels()
    private var bankList = ArrayList<Bank>()

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
        binding.apply {
            binding.lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            clickEvents = ::onClick

        }
    }


    override fun setupViews() {
        initToolbar()
        getBottomSheetCallBack()
    }

    private fun initToolbar() {
        binding.idToolbar.idBack.setOnClickListener { findNavController().popBackStack() }
        binding.idToolbar.idTitle.text = getString(R.string.add_bank_account)
        binding.idToolbar.idSearch.isVisible = false
    }


    private fun onClick(type: Int) {
        when (type) {
            0 -> {
                findNavController().navigate(
                    AddBankAccountFragmentDirections.actionAddBankAccountFragmentToBankListBottomSheet(
                        Gson().toJson(mViewModel.bankListResult?.bank), getString(R.string.addbank)
                    )
                )
            }
        }
    }

    override fun setupObservers() {

        mViewModel.getListRequest()

        mViewModel.getBalListData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            showSnackBar(it.data.message)
                            mViewModel.bankListResult = it.data.result

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

        /*validation observer */
        mViewModel.errorMessage.observe(viewLifecycleOwner) {
            when (getString(it)) {
                getString(R.string.openbottomsheet) -> {
                    //   findNavController().navigate(R.id.action_addBankAccountFragment_to_confirmBankAccountBottomSheet)
                    findNavController().navigate(
                        AddBankAccountFragmentDirections.actionAddBankAccountFragmentToConfirmBankAccountBottomSheet(
                            Gson().toJson(mViewModel.bankAccountRequest),
                            getString(R.string.addbank)
                        )
                    )
                }
                else -> {
                    showSnackBar(getString(it))

                }
            }
        }


        /*createBank Observer*/

        mViewModel.getLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()

                    val data = Gson().fromJson(
                        AESHelper.decrypt(Constants.SECURITY_KEY, it.data),
                        CommonResponse::class.java
                    )
                    when (data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            showSnackBar(data.message)
                            // mViewModel.bankListResult = it.data.result
                        }
                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(data.message)
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

    private fun getBottomSheetCallBack() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("list")
            ?.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    mViewModel.bankData = Gson().fromJson(it, Bank::class.java)
                    mViewModel.bankListResult?.bankName = mViewModel.bankData?.name
                    binding.viewModel = mViewModel
                }
            }


        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("acc_number")
            ?.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    mViewModel.bankAccountRequest.account_number = it
                    mViewModel.getCreateBankRequest()
                    Log.e("TAG", "checkAfter: ${Gson().toJson(mViewModel.bankAccountRequest)}")

                }
            }
    }


}