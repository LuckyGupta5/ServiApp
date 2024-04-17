package com.example.servivet.ui.main.bottom_sheet.bank_module

import android.content.DialogInterface
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.servivet.R
import com.example.servivet.data.model.bank_module.bank_list_response.response.Bank
import com.example.servivet.databinding.FragmentBankListBottomSheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.adapter.bank_module.BankListAdapter
import com.example.servivet.ui.main.view_model.bank_module.BankListViewModel
import com.google.gson.Gson


class BankListBottomSheet :
    BaseBottomSheetDailogFragment<FragmentBankListBottomSheetBinding, BankListViewModel>(R.layout.fragment_bank_list_bottom_sheet) {
    override val mViewModel: BankListViewModel by viewModels()
    private val argumentData: BankListBottomSheetArgs by navArgs()
    private val bankList = ArrayList<Bank>()

    override fun getLayout(): Int {
        return R.layout.fragment_bank_list_bottom_sheet
    }


    override fun isNetworkAvailable(boolean: Boolean) {

    }

    override fun setupViewModel() {


    }

    override fun setupViews() {
        binding.apply {

        }
        initAdapter()

    }

    private fun initAdapter() {
        val bankLis = Gson().fromJson(argumentData.data, Array<Bank>::class.java)
        bankList.addAll(bankLis)
        binding.adapter = BankListAdapter(bankList, requireContext(),onItemClick)
    }

    override fun setupObservers() {

    }

    private val onItemClick: (String) -> Unit = { data ->

        findNavController().previousBackStackEntry?.savedStateHandle?.set("list",data)
        dialog?.dismiss()

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        findNavController().previousBackStackEntry?.savedStateHandle?.set("list","")

    }


}