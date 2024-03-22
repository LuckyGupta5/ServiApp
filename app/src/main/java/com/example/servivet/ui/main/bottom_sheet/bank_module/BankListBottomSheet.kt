package com.example.servivet.ui.main.bottom_sheet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.servivet.R
import com.example.servivet.databinding.FragmentBankListBottomSheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.adapter.bank_module.BankListAdapter
import com.example.servivet.ui.main.view_model.bank_module.MyBankAccountViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


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