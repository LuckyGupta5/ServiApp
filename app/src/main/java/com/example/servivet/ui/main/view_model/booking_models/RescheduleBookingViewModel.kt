package com.example.servivet.ui.main.view_model.booking_models

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.booking_detail.response.BookingDetail
import com.example.servivet.data.model.booking_module.booking_summary.response.ServiceDetail
import com.example.servivet.data.model.booking_module.reschedule_booking.request.RescheduleBookingRequest
import com.example.servivet.data.model.common.response.CommonResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.databinding.FragmentBookingSummaryBinding
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import com.example.servivet.utils.convertTo24HourFormat
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RescheduleBookingViewModel : BaseViewModel() {


    val request = RescheduleBookingRequest()
    private val rescheduleData = SingleLiveEvent<Resource<CommonResponse>>()

    fun getRescheduleData(): LiveData<Resource<CommonResponse>> {
        return rescheduleData
    }


    fun getRescheduleRequest(serviceDetail: ServiceDetail?, bookingDetails: BookingDetail) {
        request.apply {
            bookingTransactionId = bookingDetails._id
            slotId = serviceDetail?.slotId
            bookingDate = serviceDetail?.date
            startTime = serviceDetail?.date+" "+ convertTo24HourFormat(serviceDetail?.startTime!!)
            endTime =  serviceDetail?.date+" "+serviceDetail?.endTime
            day = serviceDetail?.day
        }
        Log.e("TAG", "getRescheduleRequest: ${Gson().toJson(request)}", )
        hitRescheduleBookingApi()


    }

    inner class ClickAction(var context: Context, var binding: FragmentBookingSummaryBinding) {
        fun backbtn(view: View) {
            view.findNavController().popBackStack()
        }


    }

    private fun hitRescheduleBookingApi() {
        val repository = MainRepository(RetrofitBuilder.apiService)
        rescheduleData.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                rescheduleData.postValue(Resource.success(repository.rescheduleBookingApi(request)))
            } catch (ex: IOException) {
                ex.printStackTrace()
                rescheduleData.postValue(
                    Resource.error(
                        StatusCode.STATUS_CODE_INTERNET_VALIDATION,
                        null
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                if (exception is HttpException && exception.code() == StatusCode.HTTP_EXCEPTION) {
                    rescheduleData.postValue(
                        Resource.error(
                            StatusCode.HTTP_EXCEPTION.toString(),
                            null
                        )
                    )
//                    if(!finishing)
//                        CommonUtils.logoutAlert(context, "Session Expired", "Your account has been blocked by Admin . Please contact to the Admin", requireActivity)
                } else
                    rescheduleData.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))


            }
        }
    }
}