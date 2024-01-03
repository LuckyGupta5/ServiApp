package com.example.servivet.ui.main.bottom_sheet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.fragment.app.viewModels
import com.example.servivet.R
import com.example.servivet.databinding.FragmentRatingUsBottomSheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.view_model.RateUseBottomSheetViewModel

class FragmentRatingUsBottomSheet : BaseBottomSheetDailogFragment<FragmentRatingUsBottomSheetBinding,RateUseBottomSheetViewModel>(R.layout.fragment_rating_us_bottom_sheet) {
    override val mViewModel: RateUseBottomSheetViewModel by viewModels()

    override fun getLayout(): Int {
     return   R.layout.fragment_rating_us_bottom_sheet
    }

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
     }


    override fun setupViews() {
        binding.apply {

            lifecycleOwner=viewLifecycleOwner
            viewModel=mViewModel
            click=mViewModel.ClickAction(binding)
        }
         onclick()
    }
    fun onclick(){
        binding.next.setOnClickListener(View.OnClickListener {
            dismiss()
        })
        binding.skip.setOnClickListener(View.OnClickListener {
            dismiss()
        })
    }
    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    override fun setupObservers() {
    }

}