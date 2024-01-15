package com.example.servivet.ui.main.fragment

import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.booking_module.booking_summary.response.ServiceDetail
import com.example.servivet.data.model.payment.payment_amount.response.PaymentResponseMain
import com.example.servivet.databinding.FragmentBookingPaymentBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.bottom_sheet.MyWalletBottomsheet
import com.example.servivet.ui.main.view_model.SharedViewModel
import com.example.servivet.ui.main.view_model.booking_models.BookingPaymentViewModel
import com.example.servivet.utils.AESHelper
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.Constants
import com.example.servivet.utils.Constants.SECURITY_KEY
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson

class BookingPaymentFragment : BaseFragment<FragmentBookingPaymentBinding, BookingPaymentViewModel>(R.layout.fragment_booking_payment) {
    override val binding: FragmentBookingPaymentBinding by viewBinding(FragmentBookingPaymentBinding::bind)
    override val mViewModel: BookingPaymentViewModel by viewModels()
    private val timeSlotData: BookingPaymentFragmentArgs by navArgs()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var serviceData:ServiceDetail
    private var couponCode = ""


    override fun isNetworkAvailable(boolean: Boolean) {
    }
    override fun setupViews() {

        binding.apply {
            lifecycleOwner=viewLifecycleOwner
            viewModel=mViewModel
            click=mViewModel.ClickAction(requireContext(),binding)
        }

        if(Constants.APPLIED_COUPON=="APPLIED_COUPON"){
            binding.promoDiscountLayout.isVisible=true
            binding.appliedCoupon.isVisible=true
            binding.applyCouponName.text=getString(R.string.code)+" "+mViewModel.cCode+" "+getString(R.string.applied)
            binding.applyCoupon.isVisible=false
           binding.applyCouponName.isClickable=false
        }
        else{
            binding.applyCouponName.isClickable=false
            binding.applyCouponName.text=getText(R.string.apply_coupon)
            binding.promoDiscountLayout.isVisible=false
            binding.appliedCoupon.isVisible=false
            binding.applyCoupon.isVisible=true

        }

        openWelletBottomsheet()
        getTimeSlot()
        getCouponCode()

    }

    private fun getCouponCode() {
        sharedViewModel.getData().observe(viewLifecycleOwner) { couponCode ->
            mViewModel.cCode = couponCode
            setupObservers()
        }
    }

    private fun getTimeSlot() {
        when(getString(timeSlotData.from)){
            getString(R.string.booking_summary)->{
                serviceData = Gson().fromJson(timeSlotData.data, ServiceDetail::class.java)
                mViewModel.bookingData = serviceData
                binding.slotData = serviceData

            }
        }
    }

    override fun setupViewModel() {

    }

    fun openWelletBottomsheet(){
        binding.paynow.setOnClickListener(View.OnClickListener {
            var fragment=MyWalletBottomsheet()
            fragment.show(childFragmentManager,"MyWalletBottomsSheet")
        })
    }

    override fun setupObservers() {
        mViewModel.getPaymentAmountRequest(serviceData)
        mViewModel.getPaymentAmountData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    val data = Gson().fromJson(
                        AESHelper.decrypt(SECURITY_KEY, it.data), PaymentResponseMain::class.java
                    )
                    when (data.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            Log.e("TAG", "setupObservers: ${data.result}", )
                           binding.paymentData = data.result
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(data.message)
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