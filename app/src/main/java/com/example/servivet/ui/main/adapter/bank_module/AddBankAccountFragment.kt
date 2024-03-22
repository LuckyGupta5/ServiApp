package com.example.servivet.ui.main.adapter.bank_module

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.bank_module.bank_list_response.response.Bank
import com.example.servivet.data.model.bank_module.bank_list_response.response.BankListResult
import com.example.servivet.databinding.FragmentAddBankAccountBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.view_model.bank_module.AddBankViewModel
import com.example.servivet.ui.main.view_model.bank_module.MyBankAccountViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode


class AddBankAccountFragment :
    BaseFragment<FragmentAddBankAccountBinding, AddBankViewModel>(R.layout.fragment_add_bank_account) {
    override val binding: FragmentAddBankAccountBinding by viewBinding(FragmentAddBankAccountBinding::bind)
    override val mViewModel: AddBankViewModel by viewModels()
    private var bankList = ArrayList<Bank>()
    private lateinit var bankListResult: BankListResult

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
        binding.apply {
            clickEvents = ::onClick
        }
    }


    override fun setupViews() {
    }


    private fun onClick(type: Int) {
        when (type) {
            0 -> {
                findNavController().navigate(R.id.action_addBankAccountFragment_to_bankListBottomSheet)
            }
            1->{
                findNavController().navigate(R.id.action_addBankAccountFragment_to_confirmBankAccountBottomSheet)
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
                            bankListResult = it.data.result
                            bankListResult.bank?.let { list ->
                                bankList.addAll(list)
                               // initAdapter()
                            }


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