package com.example.servivet.ui.main.view_model.booking_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.booking_module.coupon.request.CouponAvalabilityRequest
import com.example.servivet.data.model.booking_module.coupon.response.CouponResponseMain
import com.example.servivet.data.model.report_rating.response.CommonResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class BookingSlotAvailabilityViewModel: BaseViewModel(){
    val request = CouponAvalabilityRequest()

    private val couponAvailabilityMData = SingleLiveEvent<Resource<CommonResponse>>()

    fun getCouponAvailabilityData(): LiveData<Resource<CommonResponse>> {
        return couponAvailabilityMData
    }


    fun getCouponAvailabilityRequest(){
        request.apply {
            serviceId ="65798f89b55d7af39650a617"
            serviceMode ="atCenter"
            slotId = "65798fb4b55d7af39650a63f"
            bookingDate ="2024-01-11"

        }
        hitCouponAvailabilityApi()

    }
    private fun hitCouponAvailabilityApi(){
        val repository = MainRepository(RetrofitBuilder.apiService)

        couponAvailabilityMData.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                couponAvailabilityMData.postValue(Resource.success(repository.bookingSlotAvailabilityApi(request)))
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