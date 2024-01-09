package com.example.servivet.ui.main.bottom_sheet

import android.view.View
import androidx.fragment.app.viewModels
import com.example.servivet.R
import com.example.servivet.databinding.FragmentMyWalletBottomsheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.view_model.wallet.MyWalletBottomsheetViewModel

class MyWalletBottomsheet : BaseBottomSheetDailogFragment<FragmentMyWalletBottomsheetBinding, MyWalletBottomsheetViewModel>(R.layout.fragment_my_wallet_bottomsheet) {
    override val mViewModel: MyWalletBottomsheetViewModel by viewModels()

    override fun getLayout()=R.layout.fragment_my_wallet_bottomsheet
    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
    }

    override fun setupViews() {
        binding.apply {
            lifecycleOwner=viewLifecycleOwner
            viewModel=mViewModel
            click=mViewModel.ClickAction(requireContext())

        }
        dismissbottomsheet()
    }

    fun dismissbottomsheet(){
     /*   binding.cancelButton.setOnClickListener(View.OnClickListener {
            val fragment=BookingCancelledBottomSheet()
            fragment.show(childFragmentManager,"jhgfds")
        })
        binding.paybutton.setOnClickListener(View.OnClickListener {
            val fragment=BookingCancelledBottomSheet()
            fragment.show(childFragmentManager,"jhgfds")
        })
*/    }



    override fun setupObservers() {
    }

}