package com.example.servivet.ui.main.view_model

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.connection.connection_list.responnse.ConnectionListResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MyConnectionModelView : BaseViewModel() {

    val request = HashMap<String, String>()

    val connectionListData = SingleLiveEvent<Resource<ConnectionListResponse>>()

    fun getConnectionData(): LiveData<Resource<ConnectionListResponse>> {
        return connectionListData
    }

    inner class ClickAction {
        fun goConnectionrequest(view: View) {
            view.findNavController()
                .navigate(R.id.action_fragmentMyConnection_to_connectionsRequestFragment)
        }

        fun backbtn(view: View) {
            view.findNavController().popBackStack()
        }


    }

    fun getConnectionListRequest() {
        request["page"] = 1.toString()
        request["limit"] = 10.toString()
        hitConnectionListApi()
    }

     fun hitConnectionListApi() {
        val repository = MainRepository(RetrofitBuilder.apiService)
        connectionListData.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                connectionListData.postValue(Resource.success(repository.connectionListApi(request)))
            } catch (ex: IOException) {
                ex.printStackTrace()
                connectionListData.postValue(
                    Resource.error(
                        StatusCode.STATUS_CODE_INTERNET_VALIDATION,
                        null
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                if (exception is HttpException && exception.code() == StatusCode.HTTP_EXCEPTION) {
                    connectionListData.postValue(
                        Resource.error(
                            StatusCode.HTTP_EXCEPTION.toString(),
                            null
                        )
                    )
//                    if(!finishing)
//                        CommonUtils.logoutAlert(context, "Session Expired", "Your account has been blocked by Admin . Please contact to the Admin", requireActivity)
                } else
                    connectionListData.postValue(
                        Resource.error(
                            StatusCode.SERVER_ERROR_MESSAGE,
                            null
                        )
                    )


            }
        }
    }
}