package com.example.servivet.ui.main.bottom_sheet

import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentSuretoConformBottomSheetBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.view_model.SureToConfirmViewModel

class SuretoConfirmBottomSheet : BaseFragment<FragmentSuretoConformBottomSheetBinding,SureToConfirmViewModel>(
  R.layout.fragment_sureto_conform_bottom_sheet) {
  override val binding: FragmentSuretoConformBottomSheetBinding by viewBinding(FragmentSuretoConformBottomSheetBinding::bind)
  override val mViewModel: SureToConfirmViewModel by viewModels()

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