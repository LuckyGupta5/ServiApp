package com.example.servivet.ui.main.bottom_sheet

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentLoginBinding
import com.example.servivet.databinding.FragmentRatingUsBottomSheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.view_model.RateUseBottomSheetViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status

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
//        binding.next.setOnClickListener(View.OnClickListener {
//            dismiss()
//        })
        binding.skip.setOnClickListener(View.OnClickListener {
            dismiss()
        })
    }
    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    override fun setupObservers() {

        mViewModel.getRatingData().observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS->{
                    ProcessDialog.dismissDialog()
                    Log.e("TAG", "setupObservers: ${it.data!!.message}", )

                }
                Status.LOADING->{
                    ProcessDialog.startDialog(requireContext())

                }
                Status.ERROR->{
                    ProcessDialog.dismissDialog()

                }
                Status.UNAUTHORIZED->{
                    CommonUtils.logoutAlert(
                        requireContext(),
                        "Session Expired",
                        "Unauthorized User",
                        requireActivity()
                    )
                }
            }
        }
    }

}