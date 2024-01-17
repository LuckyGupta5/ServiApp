package com.example.servivet.ui.main.view_model

import android.view.View
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.accept_booking.request.AcceptBookingRequest
import com.example.servivet.data.model.accept_booking.response.AcceptBookingResponse
import com.example.servivet.data.model.booking_detail.request.BookingDetailRequest
import com.example.servivet.data.model.booking_detail.response.BookingDetailResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.ui.main.fragment.BookingDetailsFragmentDirections
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import java.io.IOException

class BookingDetailsViewModel :BaseViewModel() {
    val bookingDetailResponse = SingleLiveEvent<Resource<BookingDetailResponse>>()
    var bookingDetailRequest= BookingDetailRequest()
    inner class ClickAction(){
        fun backbtn(view:View){
            view.findNavController().popBackStack()
        }
        fun gotoBookingSummary(view: View){
            view.findNavController().navigate(BookingDetailsFragmentDirections.actionBookingDetailsFragmentToBookingSummaryFragment("",""))
        }

    }

    fun hitBookingDetailApi(){
        val repository = MainRepository(RetrofitBuilder.apiService)

        bookingDetailResponse.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                bookingDetailResponse.postValue(Resource.success(repository.bookingDetailApi(bookingDetailRequest)))
            }catch (ex: IOException) {
                ex.printStackTrace()
                bookingDetailResponse.postValue(Resource.error(StatusCode.STATUS_CODE_INTERNET_VALIDATION, null))
            }catch (exception: Exception) {
                exception.printStackTrace()
                bookingDetailResponse.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))
            }
        }
    }

}