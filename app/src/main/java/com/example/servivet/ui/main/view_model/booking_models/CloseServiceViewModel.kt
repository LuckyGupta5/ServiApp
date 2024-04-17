package com.example.servivet.ui.main.view_model.booking_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.booking_module.mark_as_complete.MarkAsCompleteRequest
import com.example.servivet.data.model.booking_module.provider_leave.ProviderLeaveRequest
import com.example.servivet.data.model.common.response.CommonResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.Resource
import com.example.servivet.utils.Session
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class CloseServiceViewModel : BaseViewModel() {

    inner class ClickAction {

    }

    val request = ProviderLeaveRequest()


    private val markAsCompleteData = SingleLiveEvent<Resource<CommonResponse>>()


    fun getMarkAsCompleteData(): LiveData<Resource<CommonResponse>> {
        return markAsCompleteData
    }


    fun getMarkAsCompleteRequest(startDate: String, endDate: String, check: Boolean) {

        request.apply {
            leaveStartDate = startDate
            leaveEndDate = endDate
            isStartService = check
        }
        Log.e("TAG", "getMarkAsCompleteRequest: $request", )
        hitMarkAsCompleteApi()

    }

    private fun hitMarkAsCompleteApi() {
        val repository = MainRepository(RetrofitBuilder.apiService)

        markAsCompleteData.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                markAsCompleteData.postValue(Resource.success(repository.providerLeaveApi(request)))
            } catch (ex: IOException) {
                ex.printStackTrace()
                markAsCompleteData.postValue(
                    Resource.error(
                        StatusCode.STATUS_CODE_INTERNET_VALIDATION,
                        null
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                if (exception is HttpException && exception.code() == StatusCode.HTTP_EXCEPTION) {
                    markAsCompleteData.postValue(
                        Resource.error(
                            StatusCode.HTTP_EXCEPTION.toString(),
                            null
                        )
                    )
//                    if(!finishing)
//                        CommonUtils.logoutAlert(context, "Session Expired", "Your account has been blocked by Admin . Please contact to the Admin", requireActivity)
                } else
                    markAsCompleteData.postValue(
                        Resource.error(
                            StatusCode.SERVER_ERROR_MESSAGE,
                            null
                        )
                    )


            }
        }
    }
}