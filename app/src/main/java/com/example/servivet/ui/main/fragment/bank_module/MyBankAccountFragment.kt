package com.example.servivet.ui.main.fragment.bank_module

import android.os.Build
import android.view.Gravity
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.common.response.RemoveAccountResponse
import com.example.servivet.databinding.FragmentMyBankAccountBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.bank_module.AddedBankAccountAdapter
import com.example.servivet.ui.main.view_model.bank_module.MyBankViewModel
import com.example.servivet.utils.AESHelper
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.CommonUtils.showToast
import com.example.servivet.utils.Constants
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson


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
        binding.idToolbar.idTitle.textSize = 20.0f
        binding.idToolbar.idTitle.gravity = Gravity.CENTER

        binding.idToolbar.idTitle.text = getString(R.string.my_bank_account)
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
                            mViewModel.createBankResult?.userBankList?.let { it1 ->
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
                    val data = Gson().fromJson(
                        AESHelper.decrypt(Constants.SECURITY_KEY, it.data),
                        RemoveAccountResponse::class.java
                    )
                    when (data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            if (mViewModel.bankList.size == 0) {
                                binding.idAlreadyCreatedBankAccountContainer.visibility = View.GONE
                                binding.idRecycler.visibility = View.GONE
                                binding.idAddAccountContainer.visibility = View.VISIBLE
                            } else {
                                binding.idAlreadyCreatedBankAccountContainer.visibility =
                                    View.VISIBLE
                                binding.idRecycler.visibility = View.VISIBLE
                                binding.idAddAccountContainer.visibility = View.GONE
                            }
                            showToast(data.message ?: "Account removed successfully")
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(
                                data.message
                                    ?: getString(R.string.something_went_wrong_please_try_again_later)
                            )
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
        if (mViewModel.bankList.isEmpty()) {
            binding.idRecycler.visibility = View.GONE
            binding.idAddAccountContainer.visibility = View.VISIBLE
            binding.idAlreadyCreatedBankAccountContainer.visibility = View.GONE
        } else {
            binding.idRecycler.visibility = View.VISIBLE
            binding.idAddAccountContainer.visibility = View.GONE
            binding.idAlreadyCreatedBankAccountContainer.visibility = View.VISIBLE
        }
        binding.adapter =
            AddedBankAccountAdapter(mViewModel.bankList, requireContext(), onItemClick)
    }

    private val onItemClick: (String, Int) -> Unit = { data, position ->
        when (position) {
            1 -> {
                mViewModel.getRemoveBankRequest(data)
            }
        }
    }
}