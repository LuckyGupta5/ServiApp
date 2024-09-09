package com.example.servivet.ui.main.view_model.booking_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.booking_module.booking_summary.response.ServiceDetail
import com.example.servivet.data.model.booking_module.coupon.request.CouponAvalabilityRequest
import com.example.servivet.data.model.common.request.CommonRequest
import com.example.servivet.data.model.common.response.CommonResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
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

class BookingSlotAvailabilityViewModel: BaseViewModel(){
    val request = CouponAvalabilityRequest()
    private var newRequest = CommonRequest()

    private val couponAvailabilityMData =SingleLiveEvent<Resource<String>>()

    fun getCouponAvailabilityData(): LiveData<Resource<String>> {
        return couponAvailabilityMData
    }


    fun getCouponAvailabilityRequest(serviceData: ServiceDetail) {
        request.apply {
            serviceId =serviceData._id
            serviceMode =serviceData.serviceModeLocal
            slotId = serviceData.slotId
            bookingDate =serviceData.date
            SECURE_HEADER = "secure"

        }
        SECURE_HEADER = "secure"
        newRequest.servivet_user_req = AESHelper.encrypt(SECURITY_KEY, Gson().toJson(request))
      Log.e("TAG", "getCouponAvailabilityRequest: ${Gson().toJson(request)}",)
        hitCouponAvailabilityApi()

    }
    private fun hitCouponAvailabilityApi(){
        val repository = MainRepository(RetrofitBuilder.apiService)
        couponAvailabilityMData.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                couponAvailabilityMData.postValue(Resource.success(repository.bookingSlotAvailabilityApi(newRequest)))
            }catch (ex: IOException) {
                ex.printStackTrace()
                couponAvailabilityMData.postValue(Resource.error(StatusCode.STATUS_CODE_INTERNET_VALIDATION, null))
            }catch (exception: Exception) {
                exception.printStackTrace()
                if (exception is HttpException && exception.code() == StatusCode.HTTP_EXCEPTION) {
                    couponAvailabilityMData.postValue(Resource.error(StatusCode.HTTP_EXCEPTION.toString(), null))
//                    if(!finishing)
//                        CommonUtils.logoutAlert(context, "Session Expired", "Your account has been blocked by Admin . Please contact to the Admin", requireActivity)
                }else
                    couponAvailabilityMData.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))


            }
        }
    }
}