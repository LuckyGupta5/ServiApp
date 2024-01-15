package com.example.servivet.ui.main.view_model.booking_models

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.booking_module.booking_summary.response.ServiceDetail
import com.example.servivet.data.model.common.request.CommonRequest
import com.example.servivet.data.model.payment.payment_amount.request.PaymentRequest
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.databinding.FragmentBookingPaymentBinding
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.ui.main.fragment.BookingPaymentFragmentDirections
import com.example.servivet.utils.AESHelper
import com.example.servivet.utils.Constants
import com.example.servivet.utils.Constants.SECURE_HEADER
import com.example.servivet.utils.Constants.SECURITY_KEY
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class BookingPaymentViewModel : BaseViewModel() {

    var bookingData = ServiceDetail()
    private var request = CommonRequest()
    var cCode = ""
    val amountRequest = PaymentRequest()
    private val paymentAmountData = SingleLiveEvent<Resource<String>>()


    inner class ClickAction(var context: Context, var binding: FragmentBookingPaymentBinding) {
        fun backbtn(view: View) {
            view.findNavController().popBackStack()
        }

        fun gotoCoupon(view: View) {
            if (Constants.APPLIED_COUPON != "APPLIED_COUPON")
                view.findNavController().navigate(
                    BookingPaymentFragmentDirections.actionBookingPaymentFragmentToCouponsFragment(
                        Gson().toJson(bookingData),
                        R.string.payment
                    )
                )
        }

        fun cancelCoupon(view: View) {
            if (Constants.APPLIED_COUPON == "APPLIED_COUPON") {
                binding.applyCoupon.isVisible = true
                binding.applyCouponName.text = context.getText(R.string.apply_coupon)
                binding.appliedCoupon.isVisible = false
                binding.promoDiscountLayout.isVisible = false

            } else {
                binding.applyCoupon.isVisible = false
                binding.appliedCoupon.isVisible = true
                binding.applyCouponName.text = cCode
                binding.promoDiscountLayout.isVisible = true
            }

            Constants.APPLIED_COUPON = ""

        }

    }

    fun getPaymentAmountData(): LiveData<Resource<String>> {
        return paymentAmountData
    }

    fun getPaymentAmountRequest(serviceData: ServiceDetail) {
        amountRequest.apply {
            serviceId = serviceData._id
            serviceMode = serviceData.serviceModeLocal
            slotId = serviceData.slotId
            isCouponApply = cCode.isNotEmpty()
            couponCode = cCode

        }
        SECURE_HEADER = "secure"
        request.servivet_user_req = AESHelper.encrypt(SECURITY_KEY, Gson().toJson(amountRequest))

        hitPaymentAmountApi()

    }


    private fun hitPaymentAmountApi() {
        val repository = MainRepository(RetrofitBuilder.apiService)
        paymentAmountData.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                paymentAmountData.postValue(Resource.success(repository.paymentAmountApi(request)))
            } catch (ex: IOException) {
                ex.printStackTrace()
                paymentAmountData.postValue(
                    Resource.error(
                        StatusCode.STATUS_CODE_INTERNET_VALIDATION,
                        null
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                if (exception is HttpException && exception.code() == StatusCode.HTTP_EXCEPTION) {
                    paymentAmountData.postValue(
                        Resource.error(
                            StatusCode.HTTP_EXCEPTION.toString(),
                            null
                        )
                    )
//                    if(!finishing)
//                        CommonUtils.logoutAlert(context, "Session Expired", "Your account has been blocked by Admin . Please contact to the Admin", requireActivity)
                } else
                    paymentAmountData.postValue(
                        Resource.error(
                            StatusCode.SERVER_ERROR_MESSAGE,
                            null
                        )
                    )


            }
        }
    }
}