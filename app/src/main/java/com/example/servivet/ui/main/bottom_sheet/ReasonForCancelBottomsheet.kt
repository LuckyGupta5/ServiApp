package com.example.servivet.ui.main.bottom_sheet

import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.servivet.R
import com.example.servivet.databinding.FragmentReasonForCancelBottomsheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.adapter.ReasonForCancelAdapterbottomsheet
import com.example.servivet.ui.main.view_model.BookingViewModel
import com.example.servivet.ui.main.view_model.ReasonForCancellationViewModel
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.Constants
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson

class ReasonForCancelBottomsheet() :
    BaseBottomSheetDailogFragment<FragmentReasonForCancelBottomsheetBinding, ReasonForCancellationViewModel>(
        R.layout.fragment_reason_for_cancel_bottomsheet
    ) {
    override val mViewModel: ReasonForCancellationViewModel by viewModels()
    private val bookingId:ReasonForCancelBottomsheetArgs by navArgs()
    private lateinit var reasonList: List<String>
    private val cancelModel: BookingViewModel by viewModels()
    private lateinit var reason:String
    private lateinit var cancelBy:String



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
        getCancelReason()
        setadapter()
        dismissbottomsheet()
        hitCancelBookingApi()

    }

    private fun getCancelReason() {
        reasonList = listOf<String>(
            "Changed plans",
            "Found a better option",
            "Financial reasons",
            "Service quality concerns",
            "Unexpected circumstances",
            "Not satisfied with customer support",
            "Moved to a different location",
            "Service no longer needed",
            "Personal reasons",
            "Unforeseen events",
            "Service provider-related issues",
            "Lack of communication",
            "Difficulty using the service",
            "Service didn't meet expectations",
            "Better pricing elsewhere",
            "Job loss",
            "Health issues",
            "Service outages or disruptions",
            "Unresponsive service provider",
            "Poor user experience",
            "Technical issues",
            "Dissatisfaction with terms and conditions",
            "Company policy changes",
            "Service delays",
            "Service not as advertised",
            "Contractual disputes",
            "Security concerns",
            "Environmental concerns",
            "Not happy with billing practices",
            "Service provider went out of business"
        )
        binding.adapter = ReasonForCancelAdapterbottomsheet(requireContext(), ArrayList(), onItemClick, reasonList)

    }


    fun dismissbottomsheet() {
        if(Constants.TYPEOFUSERS=="bought" || bookingId.notificationData == "user" ){
            cancelModel.cancelBookingRequest.isWantRefundInWallet= true
            binding.idRefundContainer.isVisible = true
        }else{
            binding.idRefundContainer.isVisible = false
        }
        binding.idRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton: RadioButton = group.findViewById(checkedId)
            val position = group.indexOfChild(selectedRadioButton)
            when(position){
                0->{
                    cancelModel.cancelBookingRequest.isWantRefundInWallet= true
                }
                1->{
                    cancelModel.cancelBookingRequest.isWantRefundInWallet= false
                }
            }

        }

        binding.submitbtn.setOnClickListener(View.OnClickListener {
//            val fragment=BookingCancelledBottomSheet()
//            fragment.show(childFragmentManager,"jhgfds")

            if(Constants.TYPEOFUSERS == "bought"){
                cancelBy = "user"
            }else{
                cancelBy = "provider"
            }

            cancelModel.cancelBookingRequest.bookingId= bookingId.cancelReq
            cancelModel.cancelBookingRequest.reason= reason
            cancelModel.cancelBookingRequest.cancelledBy= cancelBy

            cancelModel.hitCancelBookingApi()


        })
    }

    fun setadapter() {
    }

    override fun setupObservers() {
    }

    override fun getLayout(): Int {
        return R.layout.fragment_reason_for_cancel_bottomsheet
    }

    private val onItemClick: (String, Int) -> Unit = { data, position ->
        when (position) {
            0 -> {
                reason = data
            }
        }
    }

    private fun hitCancelBookingApi() {
        cancelModel.cancelBookingResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()

                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            dialog?.dismiss()
                            Log.e("TAG", "hitCancelBookingApi: ${it.data.message}", )
                            findNavController().previousBackStackEntry?.savedStateHandle?.set("","")
                            dialog?.dismiss()


                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(it.data.message)
                            findNavController().previousBackStackEntry?.savedStateHandle?.set("","")
                            dialog?.dismiss()

                            Log.e("TAG", "hitCancelBookingApi: ${it.data.message}", )

                        }
                    }
                }

                Status.LOADING -> {
                    ProcessDialog.startDialog(requireContext())
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog()
                    it.message?.let {
                        showSnackBar(it)
                        Log.e("TAG", "hitCancelBookingApi: ${it}", )

                    }
                }

                else -> {}
            }
        }

    }

}