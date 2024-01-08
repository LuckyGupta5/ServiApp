package com.example.servivet.ui.main.view_model

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.databinding.ConnectionRequestDesignRecyclerviewBinding
import com.example.servivet.databinding.FragmentBookingPaymentBinding
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.ui.main.fragment.BookingPaymentFragment
import com.example.servivet.utils.Constants

class BookingPaymentViewModel:BaseViewModel() {
    inner class ClickAction(var context: Context,var binding: FragmentBookingPaymentBinding){
        fun backbtn(view:View){
            view.findNavController().popBackStack()

        }
        fun gotoCoupon(view: View){
            if(Constants.APPLIED_COUPON!="APPLIED_COUPON")
                view.findNavController().navigate(R.id.action_bookingPaymentFragment_to_couponsFragment)
        }
        fun cancelCoupon(view: View)
        {
            if(Constants.APPLIED_COUPON=="APPLIED_COUPON"){
                binding.applyCoupon.isVisible=true
                binding.applyCouponName.text=context.getText(R.string.apply_coupon)
                binding.appliedCoupon.isVisible=false
                binding.promoDiscountLayout.isVisible=false

            }
            else{
                binding.applyCoupon.isVisible=false
                binding.appliedCoupon.isVisible=true
                binding.applyCouponName.text=context.getText(R.string.code_sbi100_applied)
                binding.promoDiscountLayout.isVisible=true
            }

            Constants.APPLIED_COUPON=""

        }

    }
}