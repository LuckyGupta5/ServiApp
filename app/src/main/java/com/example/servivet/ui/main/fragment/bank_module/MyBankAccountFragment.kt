package com.example.servivet.ui.main.fragment.bank_module

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentMyBankAccountBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.bank_module.AddedBankAccountAdapter
import com.example.servivet.ui.main.view_model.bank_module.MyBankViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode


class MyBankAccountFragment :
    BaseFragment<FragmentMyBankAccountBinding, MyBankViewModel>(R.layout.fragment_my_bank_account) {
    override val binding: FragmentMyBankAccountBinding by viewBinding(FragmentMyBankAccountBinding::bind)
    override val mViewModel: MyBankViewModel by viewModels()


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
        binding.idToolbar.idTitle.textSize = 25.0f
        binding.idToolbar.idTitle.text= getString(R.string.my_bank_account)
    }


    private fun onClick(type: Int) {
        when (type) {
            0 -> {
                findNavController().navigate(R.id.action_myBankAccountFragment_to_addBankAccountFragment)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun setupObservers() {
        mViewModel.getBankListRequest()

        mViewModel.getAddBankListData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            mViewModel.createBankResult = it.data.result
                            mViewModel.bankList.clear()
                            mViewModel?.createBankResult?.userBankList?.let { it1 ->
                                mViewModel.bankList.addAll(it1)
                            }
                            initAdapter()
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


        /*remove bank data observer */

        mViewModel.getRemoveBankData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                           // showSnackBar(it.data.message)

                        }
                        StatusCode.STATUS_CODE_FAIL -> {
                           // showSnackBar(it.data.message)
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
         binding.adapter = AddedBankAccountAdapter(mViewModel.bankList, requireContext(),onItemClick)
    }
    private val onItemClick: (String, Int) -> Unit = { data, position ->

        when(position){
            1->{
                if(mViewModel.bankList.size<=1){
                    initAdapter()
                    mViewModel.getRemoveBankRequest(data)
                }else{
                    mViewModel.getRemoveBankRequest(data)
                }
            }
        }
    }
}