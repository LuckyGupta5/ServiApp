package com.example.servivet.ui.main.view_model.booking_models

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.booking_module.booking_summary.request.ScheduleRequest
import com.example.servivet.data.model.booking_module.booking_summary.response.BookingSummaryResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.databinding.FragmentBookingSummaryBinding
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.ui.main.fragment.BookingSummaryFragmentDirections
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import com.example.servivet.data.model.booking_module.booking_summary.response.Result
import kotlin.math.log

class BookingSummaryViewModel : BaseViewModel() {
    var atCenter = MutableLiveData(true)
    var atHome = MutableLiveData(false)
    val request = HashMap<String, String>()
    var result = Result()
    private val summaryMData = SingleLiveEvent<Resource<BookingSummaryResponse>>()

    fun getSummaryData(): LiveData<Resource<BookingSummaryResponse>> {
        return summaryMData
    }


    fun getReportRatingRequest(id: String) {
        request["serviceId"] = id  //"657fea25b55d7af39650d84e"
        hitSummaryApi()

    }

    inner class ClickAction(var context: Context, var binding: FragmentBookingSummaryBinding) {
        fun backbtn(view: View) {
            view.findNavController().popBackStack()
        }

        fun atCenter(view: View) {
            atCenter.postValue(true)
            atHome.postValue(false)
            binding.addAddressLayout.isVisible = false

        }

        fun atHome(view: View) {
            atHome.postValue(true)
            binding.addAddressLayout.isVisible = true
            atCenter.postValue(false)

        }


        fun gotopayment(view: View) {
            Log.e("TAG", "gotopayment: ${Gson().toJson(result.serviceDetail)}")
            view.findNavController().navigate(
                BookingSummaryFragmentDirections.actionBookingSummaryFragmentToBookingPaymentFragment(
                    Gson().toJson(result.serviceDetail),
                    R.string.booking_summary
                )
            )
        }

        fun gotoaddlocation(view: View) {
            view.findNavController()
                .navigate(R.id.action_bookingSummaryFragment_to_addLocationFragment)
        }


    }

    private fun hitSummaryApi() {
        val repository = MainRepository(RetrofitBuilder.apiService)
        summaryMData.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                summaryMData.postValue(Resource.success(repository.bookingSummaryApi(request)))
            } catch (ex: IOException) {
                ex.printStackTrace()
                summaryMData.postValue(
                    Resource.error(
                        StatusCode.STATUS_CODE_INTERNET_VALIDATION,
                        null
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                if (exception is HttpException && exception.code() == StatusCode.HTTP_EXCEPTION) {
                    summaryMData.postValue(
                        Resource.error(
                            StatusCode.HTTP_EXCEPTION.toString(),
                            null
                        )
                    )
//                    if(!finishing)
//                        CommonUtils.logoutAlert(context, "Session Expired", "Your account has been blocked by Admin . Please contact to the Admin", requireActivity)
                } else
                    summaryMData.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))


            }
        }
    }

}