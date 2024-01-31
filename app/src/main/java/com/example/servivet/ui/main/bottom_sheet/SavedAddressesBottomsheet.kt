package com.example.servivet.ui.main.bottom_sheet

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.servivet.R
import com.example.servivet.databinding.FragmentSavedAddressesBottomsheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.view_model.SaveAddressBottomsheetViewModel
import com.example.servivet.utils.Session

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


        if(Session.saveAddress!=null ){
            binding.name.text = Session.saveAddress.name
            binding.address.text = Session.saveAddress.fullAddress
            binding.number.text = Session.saveAddress.mobileNumber
        }
        binding.useSameAddress.setOnClickListener {
            dismiss()
        }

        binding.changeAddress.setOnClickListener {
            findNavController().navigate(R.id.action_savedAddressesBottomsheet_to_addLocationFragment)
            dismiss()
        }


    }

    override fun setupObservers() {
    }

}