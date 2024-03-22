package com.example.servivet.ui.main.fragment.bank_module

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.bank_module.bank_list_response.response.Bank
import com.example.servivet.data.model.bank_module.bank_list_response.response.BankListResult
import com.example.servivet.databinding.FragmentMyBankAccountBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.bank_module.AddedBankAccountAdapter
import com.example.servivet.ui.main.view_model.bank_module.MyBankAccountViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode


class MyBankAccountFragment :
    BaseFragment<FragmentMyBankAccountBinding, MyBankAccountViewModel>(R.layout.fragment_my_bank_account) {
    override val binding: FragmentMyBankAccountBinding by viewBinding(FragmentMyBankAccountBinding::bind)
    override val mViewModel: MyBankAccountViewModel by viewModels()


    override fun isNetworkAvailable(boolean: Boolean) {
    }


    override fun setupViewModel() {

        binding.apply {
            clickEvents = ::onClick
        }
    }

    override fun setupViews() {
        initToolbar()
    }


    private fun initToolbar() {
        binding.idToolbar.idBack.setOnClickListener { findNavController().popBackStack() }
        binding.idToolbar.idSearch.isVisible = false
    }


    private fun onClick(type: Int) {
        when (type) {
            0 -> {
                findNavController().navigate(R.id.action_myBankAccountFragment_to_addBankAccountFragment)
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

    private fun initAdapter() {
       // binding.adapter = AddedBankAccountAdapter(bankList, requireContext(),onItemClick)
    }

    private val onItemClick: (String, Int) -> Unit = { data, position ->

    }


}