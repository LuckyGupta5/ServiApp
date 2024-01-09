package com.example.servivet.ui.main.bottom_sheet

import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentSuretoConformBottomSheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.view_model.SureToConfirmViewModel

class SuretoConfirmBottomSheet : BaseBottomSheetDailogFragment<FragmentSuretoConformBottomSheetBinding,SureToConfirmViewModel>(R.layout.fragment_sureto_conform_bottom_sheet){
  override val mViewModel: SureToConfirmViewModel by viewModels()

  override fun getLayout()=R.layout.fragment_sureto_conform_bottom_sheet

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


  fun cancelbtn(view:View){
    binding.cancelButton.setOnClickListener(View.OnClickListener {

    })
  }

  override fun setupObservers() {
  }
}