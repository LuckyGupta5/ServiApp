package com.example.servivet.ui.main.bottom_sheet


import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.example.servivet.R
import com.example.servivet.databinding.FragmentRatingReportBottomSheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.view_model.sub_category_models.RatingReportViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showToast
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode

class RatingReportBottomSheetFragment :
    BaseBottomSheetDailogFragment<FragmentRatingReportBottomSheetBinding, RatingReportViewModel>(
        R.layout.fragment_rating_report_bottom_sheet
    ) {
    private var ratingId: String? = ""
    override val mViewModel: RatingReportViewModel by viewModels()

    override fun getLayout(): Int {
        return R.layout.fragment_rating_report_bottom_sheet
    }

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
    }

    override fun setupViews() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction()

        }
        ratingId = arguments?.getString("id")
        binding.canceltBtn.setOnClickListener {
            dismiss()
        }
        setSpinner()

        binding.submitBtn.setOnClickListener {
            if (binding.descriptionEditText.text!!.isNotEmpty()) {
                mViewModel.reportRequest.contentType = "rating"
                mViewModel.reportRequest.userFeedBack = binding.descriptionEditText.text.toString()
                mViewModel.reportRequest.contentId = ratingId
                mViewModel.hitReportRatingApi()
            } else {
                showToast("Please enter the feedback")
            }

        }

        textwatcher()


    }

    private fun setSpinner() {
        val options = listOf(
            "Quality of Service",
            "Customer Support",
            "Ease of Use/User Experience",
            "Security and Privacy",
            "Misleading Advertising or Marketing",
            "Ethical or Legal Concerns"
        )

        val adapter =
            ArrayAdapter(requireContext(), R.layout.spinner_text_layout, options).apply {
                setDropDownViewResource(R.layout.spinner_text_layout)
            }

        binding.idReportSpinner.adapter = adapter

        binding.idReportSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    // Handle the selected item
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Optionally handle the case where no item was selected
                }
            }
    }

    private fun textwatcher() {
        binding.descriptionEditText.doAfterTextChanged {
            binding.wordCount.text = it!!.length.toString() + "/150"

        }
    }

    override fun setupObservers() {
        mViewModel.ratingMData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            showToast(it.data.message ?: "Something went wrong")
                            dismiss()
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showToast(it.data.message ?: "Something went wrong")
                        }

                        else -> {
                            showToast(it.data?.message ?: "Something went wrong")
                        }
                    }
                }

                Status.LOADING -> {
                    ProcessDialog.startDialog(requireContext())
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog()

                    it.message?.let {
                        showToast(it)
                    }
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