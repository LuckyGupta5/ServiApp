package com.example.servivet.ui.main.view_model

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.common.response.CommonResponse
import com.example.servivet.data.model.connection.connection_list.responnse.ConnectionListResponse
import com.example.servivet.data.model.notification_list.response.NotificationListResponse
import com.example.servivet.data.model.setting.contact.request.ContactUsRequest
import com.example.servivet.data.model.setting.notification.request.NotificationRequest
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class NotificationListViewModel:BaseViewModel() {
    private val request = HashMap<String, Int>()
    val connectionRequestListData = SingleLiveEvent<Resource<NotificationListResponse>>()
    fun getConnectionRequestData(): LiveData<Resource<NotificationListResponse>> {
        return connectionRequestListData
    }


    inner class ClickAction {
        fun backbtn(view: View) {
            view.findNavController().popBackStack()
        }
    }


    fun getConnectionListRequest() {
        request["page"] = 1
        request["limit"] = 10
        hitConnectionRequestListApi()
    }

    private fun hitConnectionRequestListApi() {
        val repository = MainRepository(RetrofitBuilder.apiService)
        connectionRequestListData.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                connectionRequestListData.postValue(
                    Resource.success(
                        repository.notificationListing(
                            request
                        )
                    )
                )
            } catch (ex: IOException) {
                ex.printStackTrace()
                connectionRequestListData.postValue(
                    Resource.error(
                        StatusCode.STATUS_CODE_INTERNET_VALIDATION,
                        null
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                if (exception is HttpException && exception.code() == StatusCode.HTTP_EXCEPTION) {
                    connectionRequestListData.postValue(
                        Resource.error(
                            StatusCode.HTTP_EXCEPTION.toString(),
                            null
                        )
                    )
//                    if(!finishing)
//                        CommonUtils.logoutAlert(context, "Session Expired", "Your account has been blocked by Admin . Please contact to the Admin", requireActivity)
                } else
                    connectionRequestListData.postValue(
                        Resource.error(
                            StatusCode.SERVER_ERROR_MESSAGE,
                            null
                        )
                    )


            }
        }
    }
}