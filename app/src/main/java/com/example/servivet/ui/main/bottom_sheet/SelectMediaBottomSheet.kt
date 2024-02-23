package com.example.servivet.ui.main.bottom_sheet

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.servivet.R
import com.example.servivet.databinding.FragmentSavedAddressesBottomsheetBinding
import com.example.servivet.databinding.FragmentSelectMediaBottomSheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.view_model.SaveAddressBottomsheetViewModel
import com.example.servivet.ui.main.view_model.SureToConfirmViewModel


class SelectMediaBottomSheet : BaseBottomSheetDailogFragment<FragmentSelectMediaBottomSheetBinding, SureToConfirmViewModel>(R.layout.fragment_select_media_bottom_sheet) {

    private val argumentData:SelectMediaBottomSheetArgs by navArgs()

    override val mViewModel: SureToConfirmViewModel by viewModels()
    override fun getLayout(): Int {
        return R.layout.fragment_select_media_bottom_sheet
    }

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
    }

    override fun setupViews() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = ::onClick
            checkType = argumentData.from
        }
    }

    override fun setupObservers() {
    }

    private fun onClick(value: Int) {
        when (value) {
            0 -> {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(getString(R.string.videos),getString(R.string.image))
                dialog?.dismiss()
            }

            1 -> {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(getString(R.string.videos),getString(R.string.videos))
                dialog?.dismiss()
            }
            2->{
                findNavController().previousBackStackEntry?.savedStateHandle?.set(getString(R.string.videos),getString(R.string.files))
                dialog?.dismiss()
            }
            3->{
                findNavController().previousBackStackEntry?.savedStateHandle?.set(getString(R.string.videos),getString(R.string.video_call))
                dialog?.dismiss()
            }
            4->{
                findNavController().previousBackStackEntry?.savedStateHandle?.set(getString(R.string.videos),getString(R.string.audio_call))
                dialog?.dismiss()
            }
        }
    }


}