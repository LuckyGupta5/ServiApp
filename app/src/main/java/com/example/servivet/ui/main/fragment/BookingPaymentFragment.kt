package com.example.servivet.ui.main.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.booking_module.booking_summary.request.ScheduleRequest
import com.example.servivet.databinding.FragmentBookingPaymentBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.bottom_sheet.MyWalletBottomsheet
import com.example.servivet.ui.main.view_model.BookingPaymentViewModel
import com.example.servivet.utils.Constants
import com.google.gson.Gson

class BookingPaymentFragment : BaseFragment<FragmentBookingPaymentBinding,BookingPaymentViewModel>(R.layout.fragment_booking_payment) {
    override val binding: FragmentBookingPaymentBinding by viewBinding(FragmentBookingPaymentBinding::bind)
    override val mViewModel: BookingPaymentViewModel by viewModels()

    private val timeSlotData: BookingPaymentFragmentArgs by navArgs()

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
            binding.applyCouponName.text=getText(R.string.code_sbi100_applied)
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
    }

    private fun getTimeSlot() {
        when(getString(timeSlotData.from)){
            getString(R.string.booking_summary)->{
                val data = Gson().fromJson(timeSlotData.data,ScheduleRequest::class.java)
                binding.slotData = data

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
        binding.Promocode.text="- "+"₹ "+getText(R.string._100)
    }

}