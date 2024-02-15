package com.example.servivet.ui.main.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.common.response.CommonResponse
import com.example.servivet.data.model.connection.accept_reject.request.AcceptRejectRequest
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class AcceptRejectViewModel : BaseViewModel() {
    private val request = AcceptRejectRequest()
    private val acceptRejectListData = SingleLiveEvent<Resource<CommonResponse>>()
    fun getAcceptRejectData(): LiveData<Resource<CommonResponse>> {
        return acceptRejectListData
    }

    fun getAcceptRejectRequest(identifier: Int, data: String) {
        request.apply {
            connectionId = data
            isAccept = identifier
        }
        hitAcceptRejectApi()
    }


    private fun hitAcceptRejectApi() {
        val repository = MainRepository(RetrofitBuilder.apiService)
        acceptRejectListData.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                acceptRejectListData.postValue(
                    Resource.success(
                        repository.acceptRejectApi(
                            request
                        )
                    )
                )
            } catch (ex: IOException) {
                ex.printStackTrace()
                acceptRejectListData.postValue(
                    Resource.error(
                        StatusCode.STATUS_CODE_INTERNET_VALIDATION,
                        null
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                if (exception is HttpException && exception.code() == StatusCode.HTTP_EXCEPTION) {
                    acceptRejectListData.postValue(
                        Resource.error(
                            StatusCode.HTTP_EXCEPTION.toString(),
                            null
                        )
                    )
//                    if(!finishing)
//                        CommonUtils.logoutAlert(context, "Session Expired", "Your account has been blocked by Admin . Please contact to the Admin", requireActivity)
                } else
                    acceptRejectListData.postValue(
                        Resource.error(
                            StatusCode.SERVER_ERROR_MESSAGE,
                            null
                        )
                    )


            }
        }
    }


}