package com.example.servivet.ui.main.bottom_sheet

import androidx.fragment.app.viewModels
import com.example.servivet.R
import com.example.servivet.databinding.FragmentSavedAddressesBottomsheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.view_model.SaveAddressBottomsheetViewModel

class SavedAddressesBottomsheet:BaseBottomSheetDailogFragment<FragmentSavedAddressesBottomsheetBinding,SaveAddressBottomsheetViewModel>(
    R.layout.fragment_saved_addresses_bottomsheet) {
    override val mViewModel: SaveAddressBottomsheetViewModel by viewModels()

    override fun getLayout(): Int {
        return R.layout.fragment_saved_addresses_bottomsheet
    }

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
           }

    override fun setupViews() {
        binding.apply {
            lifecycleOwner=viewLifecycleOwner
            viewModel=mViewModel
            click=mViewModel.ClickAction()

        }

    }

    override fun setupObservers() {
    }

}