package com.example.servivet.ui.main.view_model.wallet

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.booking_module.booking_summary.response.ServiceDetail
import com.example.servivet.data.model.booking_module.create_order.request.CreateOrderRequest
import com.example.servivet.data.model.common.request.CommonRequest
import com.example.servivet.data.model.payment.payment_amount.response.PayAmountResult
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.utils.AESHelper
import com.example.servivet.utils.Constants
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class CreateOrderViewModel() : ViewModel() {

    private var request = CommonRequest()
    val orderRequest = CreateOrderRequest()

    private val createOrderData = SingleLiveEvent<Resource<String>>()
    fun getOrderData(): LiveData<Resource<String>> {
        return createOrderData
    }

    fun getPaymentAmountRequest(
        paymentAmountData: PayAmountResult,
        serviceData: ServiceDetail,
        walletAmounts: Double,
        payWith: String
    ) {
        Constants.SECURE_HEADER = "secure"
        orderRequest.apply {
            serviceId = serviceData._id
            serviceMode = serviceData.serviceModeLocal
            bookingDate = serviceData.date
            startTime = serviceData.date+" "+serviceData.startTime
            endTime = serviceData.date+" "+serviceData.endTime
            slotId = serviceData.slotId
            day = serviceData.day
            isCouponApply = serviceData.couponCode?.isNotEmpty()
            serviceAmount = paymentAmountData.serviceAmount
            couponDiscount = paymentAmountData.couponDiscount
            taxAmount = paymentAmountData.taxAmount
            payableAmount = paymentAmountData.payableAmount
            couponCode = serviceData.couponCode
            walletAmount = walletAmounts
            paymentMode = payWith
            saveApplyCouponId = paymentAmountData.saveApplyCouponId
            addressId = ""
        }

        Log.e("TAG", "getPaymentAmountRequest: ${Gson().toJson(orderRequest)}", )
        request.servivet_user_req = AESHelper.encrypt(Constants.SECURITY_KEY, Gson().toJson(orderRequest))

        hitOrderRequestApi()

    }

    private fun hitOrderRequestApi() {
        val repository = MainRepository(RetrofitBuilder.apiService)
        createOrderData.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                createOrderData.postValue(Resource.success(repository.createOrderApi(request)))
            } catch (ex: IOException) {
                ex.printStackTrace()
                createOrderData.postValue(
                    Resource.error(
                        StatusCode.STATUS_CODE_INTERNET_VALIDATION,
                        null
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                if (exception is HttpException && exception.code() == StatusCode.HTTP_EXCEPTION) {
                    createOrderData.postValue(
                        Resource.error(
                            StatusCode.HTTP_EXCEPTION.toString(),
                            null
                        )
                    )
//                    if(!finishing)
//                        CommonUtils.logoutAlert(context, "Session Expired", "Your account has been blocked by Admin . Please contact to the Admin", requireActivity)
                } else
                    createOrderData.postValue(
                        Resource.error(
                            StatusCode.SERVER_ERROR_MESSAGE,
                            null
                        )
                    )


            }
        }
    }
}
