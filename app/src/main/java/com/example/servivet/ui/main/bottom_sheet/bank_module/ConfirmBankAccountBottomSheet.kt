package com.example.servivet.ui.main.bottom_sheet.bank_module

import android.content.DialogInterface
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.servivet.R
import com.example.servivet.data.model.bank_module.create_bankAccount.request.CreateBankAccountRequest
import com.example.servivet.databinding.FragmentConfirmBankAccountBottomSheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.view_model.bank_module.BankListViewModel
import com.google.gson.Gson


class ConfirmBankAccountBottomSheet :
    BaseBottomSheetDailogFragment<FragmentConfirmBankAccountBottomSheetBinding, BankListViewModel>(R.layout.fragment_confirm_bank_account_bottom_sheet) {
    override val mViewModel: BankListViewModel by viewModels()
    private val argument: ConfirmBankAccountBottomSheetArgs by navArgs()

    override fun getLayout(): Int {
        return R.layout.fragment_confirm_bank_account_bottom_sheet
    }

    override fun isNetworkAvailable(boolean: Boolean) {

    }

    override fun setupViewModel() {

        binding.apply {
            viewModel = mViewModel
            clickEvents = ::onClick
        }


    }

    override fun setupViews() {
        getArgumentData()
    }


    private fun onClick(type: Int) {
        when (type) {
            0 -> {
                dismiss()
            }

            1 -> {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    "acc_number",
                    binding.idConfirmNumber.text.toString()
                )
                dismiss()
            }
        }
    }


    override fun setupObservers() {
    }


    private fun getArgumentData() {
        val data = Gson().fromJson(argument.data, CreateBankAccountRequest::class.java)
        binding.data = data
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)


        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            "acc_number",
            ""
        )
    }


}