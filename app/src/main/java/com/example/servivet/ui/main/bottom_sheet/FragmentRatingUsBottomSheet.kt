package com.example.servivet.ui.main.bottom_sheet

import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.servivet.R
import com.example.servivet.databinding.FragmentRatingUsBottomSheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.view_model.RateUseBottomSheetViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode

class FragmentRatingUsBottomSheet :
    BaseBottomSheetDailogFragment<FragmentRatingUsBottomSheetBinding, RateUseBottomSheetViewModel>(R.layout.fragment_rating_us_bottom_sheet) {
    override val mViewModel: RateUseBottomSheetViewModel by viewModels()
    private val serviceId: FragmentRatingUsBottomSheetArgs by navArgs()


    override fun getLayout(): Int {
        return R.layout.fragment_rating_us_bottom_sheet
    }

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
    }


    override fun setupViews() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction(binding)
        }
        onclick()
        getData()
    }

    private fun getData() {
        binding.name.text = serviceId.serviceName
        mViewModel.ratingRequest.serviceId = serviceId.serviceId
    }

    fun onclick() {
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

        mViewModel.getRatingData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()


                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT)
                                .show()
                            dialog?.dismiss()
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                    }

                }

                Status.LOADING -> {
                    ProcessDialog.startDialog(requireContext())

                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog()

                }

                Status.UNAUTHORIZED -> {
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