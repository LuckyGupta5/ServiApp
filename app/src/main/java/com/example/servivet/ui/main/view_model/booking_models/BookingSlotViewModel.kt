package com.example.servivet.ui.main.view_model.booking_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.booking_module.booking_slot.BookingSlotResponseMain
import com.example.servivet.data.model.booking_module.booking_summary.response.BookingSummaryResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class BookingSlotViewModel: BaseViewModel(){
    val request = HashMap<String,String>()

    private val slotMData = SingleLiveEvent<Resource<BookingSlotResponseMain>>()

    fun getSlotData(): LiveData<Resource<BookingSlotResponseMain>> {
        return slotMData
    }


    fun getSlotRequest(){
        request["serviceId"] ="65798f89b55d7af39650a617"
        request["bookingDate"] ="2024-01-11"

        hitSlotApi()

    }
    private fun hitSlotApi(){
        val repository = MainRepository(RetrofitBuilder.apiService)

        slotMData.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                slotMData.postValue(Resource.success(repository.bookingSlotApi(request)))
            }catch (ex: IOException) {
                ex.printStackTrace()
                slotMData.postValue(Resource.error(StatusCode.STATUS_CODE_INTERNET_VALIDATION, null))
            }catch (exception: Exception) {
                exception.printStackTrace()
                if (exception is HttpException && exception.code() == StatusCode.HTTP_EXCEPTION) {
                    slotMData.postValue(Resource.error(StatusCode.HTTP_EXCEPTION.toString(), null))
//                    if(!finishing)
//                        CommonUtils.logoutAlert(context, "Session Expired", "Your account has been blocked by Admin . Please contact to the Admin", requireActivity)
                }else
                    slotMData.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))


            }
        }
    }
}