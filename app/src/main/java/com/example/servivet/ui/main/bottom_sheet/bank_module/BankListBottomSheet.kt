package com.example.servivet.ui.main.bottom_sheet.bank_module

import androidx.fragment.app.viewModels
import com.example.servivet.R
import com.example.servivet.databinding.FragmentBankListBottomSheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.adapter.bank_module.BankListAdapter
import com.example.servivet.ui.main.view_model.bank_module.MyBankAccountViewModel


class BankListBottomSheet :
    BaseBottomSheetDailogFragment<FragmentBankListBottomSheetBinding, MyBankAccountViewModel>(R.layout.fragment_bank_list_bottom_sheet) {
    override val mViewModel: MyBankAccountViewModel by viewModels()

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
        binding.adapter = BankListAdapter(ArrayList())
    }

    override fun setupObservers() {

    }


}