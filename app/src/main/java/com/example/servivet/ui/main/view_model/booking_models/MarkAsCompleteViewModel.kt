package com.example.servivet.ui.main.view_model.booking_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.booking_module.mark_as_complete.MarkAsCompleteRequest
import com.example.servivet.data.model.common.response.CommonResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MarkAsCompleteViewModel : BaseViewModel() {
    val request = MarkAsCompleteRequest()
    private var completed = ""

    private val markAsCompleteData = SingleLiveEvent<Resource<CommonResponse>>()

    fun getMarkAsCompleteData(): LiveData<Resource<CommonResponse>> {
        return markAsCompleteData
    }


    fun getMarkAsCompleteRequest(_id: String, typeOfUser: String) {

        if (typeOfUser == "bought") {
            completed = "userBought"
        } else {
            completed = "providerSold"
        }

        request.apply {
            bookingId = _id
            completedBy = completed

        }
        Log.e("TAG", "getMarkAsCompleteRequest: ${Gson().toJson(request)}", )

        hitMarkAsCompleteApi()

    }

    private fun hitMarkAsCompleteApi() {
        val repository = MainRepository(RetrofitBuilder.apiService)

        markAsCompleteData.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                markAsCompleteData.postValue(Resource.success(repository.markAsCompleteApi(request)))
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