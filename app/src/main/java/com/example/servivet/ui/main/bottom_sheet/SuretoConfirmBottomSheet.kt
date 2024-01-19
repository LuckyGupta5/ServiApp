package com.example.servivet.ui.main.bottom_sheet

import android.content.DialogInterface
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.servivet.R
import com.example.servivet.databinding.FragmentSuretoConformBottomSheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.view_model.SureToConfirmViewModel

class SuretoConfirmBottomSheet :
    BaseBottomSheetDailogFragment<FragmentSuretoConformBottomSheetBinding, SureToConfirmViewModel>(R.layout.fragment_sureto_conform_bottom_sheet) {
    override val mViewModel: SureToConfirmViewModel by viewModels()

    override fun getLayout() = R.layout.fragment_sureto_conform_bottom_sheet

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {

    }

    override fun setupViews() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = ::onClick
        }
    }

    private fun onClick(value: Int) {
        when (value) {
            0 -> {
                dialog?.dismiss()
            }

            1 -> {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(getString(R.string.confirm),"")
                dialog?.dismiss()

            }
        }
    }





    override fun setupObservers() {
    }
}