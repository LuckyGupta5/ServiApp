package com.example.servivet.ui.main.view_model

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.viewModelScope
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.accept_booking.request.AcceptBookingRequest
import com.example.servivet.data.model.accept_booking.response.AcceptBookingResponse
import com.example.servivet.data.model.booking_list.response.BookingListResponse
import com.example.servivet.data.model.cancel_booking.request.CancelBookingRequest
import com.example.servivet.data.model.cancel_booking.response.CancelBookingResponse
import com.example.servivet.data.model.sold_booking_list.response.SoldBookingListResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.databinding.FragmentBusinessBookingBinding
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import java.io.IOException

class BusinessBookingViewModel:BaseViewModel() {
    val request = HashMap<String,String>()
    val soldBookingListResponse = SingleLiveEvent<Resource<BookingListResponse>>()
    val cancelBookingResponse = SingleLiveEvent<Resource<CancelBookingResponse>>()
    var cancelBookingRequest=CancelBookingRequest()

    val acceptBookingResponse = SingleLiveEvent<Resource<AcceptBookingResponse>>()
    var acceptBookingRequest=AcceptBookingRequest()

    inner class ClickAction(var context: Context, var binding: FragmentBusinessBookingBinding){

        fun sold(view: View){
            binding.soldOut.isVisible=true
            binding.bought.isVisible=false

        }
        fun bought(view: View){
            binding.soldOut.isVisible=false
            binding.bought.isVisible=true

        }
    }

    fun hitBookingListAPI(myBookingStatus:Int,page:Int,limit:Int){
        request["myBookingStatus"]=myBookingStatus.toString()
        request["page"]=page.toString()
        request["limit"]=limit.toString()
        val repository = MainRepository(RetrofitBuilder.apiService)

        soldBookingListResponse.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                soldBookingListResponse.postValue(Resource.success(repository.mysoldbookingListApi(request)))
            }catch (ex: IOException) {
                ex.printStackTrace()
                soldBookingListResponse.postValue(Resource.error(StatusCode.STATUS_CODE_INTERNET_VALIDATION, null))
            }catch (exception: Exception) {
                exception.printStackTrace()
                soldBookingListResponse.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))
            }
        }
    }


    fun hitCancelBookingApi(){
        val repository = MainRepository(RetrofitBuilder.apiService)

        cancelBookingResponse.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                cancelBookingResponse.postValue(Resource.success(repository.cancelBooking(cancelBookingRequest)))
            }catch (ex: IOException) {
                ex.printStackTrace()
                cancelBookingResponse.postValue(Resource.error(StatusCode.STATUS_CODE_INTERNET_VALIDATION, null))
            }catch (exception: Exception) {
                exception.printStackTrace()
                cancelBookingResponse.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))
            }
        }
    }

    fun hitAcceptBookingApi(){
        val repository = MainRepository(RetrofitBuilder.apiService)

        acceptBookingResponse.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                acceptBookingResponse.postValue(Resource.success(repository.acceptBookingApi(acceptBookingRequest)))
            }catch (ex: IOException) {
                ex.printStackTrace()
                acceptBookingResponse.postValue(Resource.error(StatusCode.STATUS_CODE_INTERNET_VALIDATION, null))
            }catch (exception: Exception) {
                exception.printStackTrace()
                acceptBookingResponse.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))
            }
        }
    }
}