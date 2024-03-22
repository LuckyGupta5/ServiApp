package com.example.servivet.ui.main.bottom_sheet.bank_module

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.servivet.R
import com.example.servivet.databinding.FragmentConfirmBankAccountBottomSheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.view_model.bank_module.MyBankAccountViewModel


class ConfirmBankAccountBottomSheet : BaseBottomSheetDailogFragment<FragmentConfirmBankAccountBottomSheetBinding, MyBankAccountViewModel>(R.layout.fragment_confirm_bank_account_bottom_sheet) {
    override val mViewModel: MyBankAccountViewModel by viewModels()

    override fun getLayout(): Int {
        return R.layout.fragment_confirm_bank_account_bottom_sheet
    }

    override fun isNetworkAvailable(boolean: Boolean) {

    }

    override fun setupViewModel() {
    }

    override fun setupViews() {
    }

    override fun setupObservers() {
    }


}