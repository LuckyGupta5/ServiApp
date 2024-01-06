package com.example.servivet.ui.main.view_model.booking_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.booking_module.booking_slot.BookingSlotResponseMain
import com.example.servivet.data.model.booking_module.coupon.response.CouponResponseMain
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class BookingCouponViewModel : BaseViewModel(){
    val request = HashMap<String,String>()

    private val couponMData = SingleLiveEvent<Resource<CouponResponseMain>>()

    fun getCouponData(): LiveData<Resource<CouponResponseMain>> {
        return couponMData
    }


    fun getCouponRequest(){
        request["providerId"] ="65798d6ab55d7af39650a4ce"
        hitSlotApi()

    }
    private fun hitSlotApi(){
        val repository = MainRepository(RetrofitBuilder.apiService)

        couponMData.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                couponMData.postValue(Resource.success(repository.bookingCouponApi(request)))
            }catch (ex: IOException) {
                ex.printStackTrace()
                couponMData.postValue(Resource.error(StatusCode.STATUS_CODE_INTERNET_VALIDATION, null))
            }catch (exception: Exception) {
                exception.printStackTrace()
                if (exception is HttpException && exception.code() == StatusCode.HTTP_EXCEPTION) {
                    couponMData.postValue(Resource.error(StatusCode.HTTP_EXCEPTION.toString(), null))
//                    if(!finishing)
//                        CommonUtils.logoutAlert(context, "Session Expired", "Your account has been blocked by Admin . Please contact to the Admin", requireActivity)
                }else
                    couponMData.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))


            }
        }
    }
}