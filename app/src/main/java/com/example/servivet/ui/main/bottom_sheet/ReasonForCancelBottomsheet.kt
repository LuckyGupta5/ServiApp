package com.example.servivet.ui.main.bottom_sheet

import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.servivet.R
import com.example.servivet.databinding.FragmentReasonForCancelBottomsheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.adapter.ReasonForCancelAdapterbottomsheet
import com.example.servivet.ui.main.view_model.BookingViewModel
import com.example.servivet.ui.main.view_model.ReasonForCancellationViewModel
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode

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
            "reason1", "reason2", "reason3", "reason4"
        )
    }


    fun dismissbottomsheet() {
        binding.submitbtn.setOnClickListener(View.OnClickListener {
//            val fragment=BookingCancelledBottomSheet()
//            fragment.show(childFragmentManager,"jhgfds")

            if(Session.type =="1"){
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
        binding.recyclerviewcancel.adapter = ReasonForCancelAdapterbottomsheet(
            requireContext(),
            ArrayList(),
            onItemClick,
            reasonList
        )
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
                            showSnackBar(it.data.message)
                            Log.e("TAG", "hitCancelBookingApi: ${it.data.message}", )

                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(it.data.message)
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
                    }
                }

                else -> {}
            }
        }

    }

}