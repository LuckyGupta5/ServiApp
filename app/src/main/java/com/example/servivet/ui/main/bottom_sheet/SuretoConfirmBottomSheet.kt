package com.example.servivet.ui.main.bottom_sheet

import androidx.fragment.app.viewModels
import com.example.servivet.R
import com.example.servivet.databinding.FragmentSuretoConformBottomSheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.view_model.SureToConfirmViewModel

class SuretoConfirmBottomSheet(
    private val confirmCallBack: () -> (Unit)
) : BaseBottomSheetDailogFragment<FragmentSuretoConformBottomSheetBinding, SureToConfirmViewModel>(R.layout.fragment_sureto_conform_bottom_sheet) {
    override val mViewModel: SureToConfirmViewModel by viewModels()

    override fun getLayout() = R.layout.fragment_sureto_conform_bottom_sheet

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {

    }

    override fun setupViews() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            cancelButton.setOnClickListener {
                dismiss()
            }
            idConfirm.setOnClickListener {
                dismiss()
                confirmCallBack.invoke()
            }
        }
    }

    override fun setupObservers() {
    }
}