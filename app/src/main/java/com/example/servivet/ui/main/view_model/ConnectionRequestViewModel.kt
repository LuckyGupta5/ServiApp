package com.example.servivet.ui.main.view_model

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.connection.connection_request.request.ConnectionRequest
import com.example.servivet.data.model.connection.connection_request.response.ConnnectionResponse
import com.example.servivet.data.model.user_profile.response.UserProfile
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.utils.Resource
import com.example.servivet.utils.Session
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ConnectionRequestViewModel : ViewModel() {
    private val request = ConnectionRequest()
    private val connectionRequestData = SingleLiveEvent<Resource<ConnnectionResponse>>()
    fun getConnectionRequestData(): LiveData<Resource<ConnnectionResponse>> {
        return connectionRequestData
    }


    inner class ClickAction {
        fun backbtn(view: View) {
            view.findNavController().popBackStack()
        }
    }


    fun getConnectionRequest(profileData: UserProfile, friendsId: String) {

        request.apply {
            connectionId = profileData.connectionId
            friendId = friendsId
            connectionType = if (profileData.isConnected == 0) {
                1
            } else {
                0
            }

        }
        Log.e("TAG", "getConnectionRequest: ${Gson().toJson(request)}")

        hitConnectionRequestApi()
    }

    private fun hitConnectionRequestApi() {
        val repository = MainRepository(RetrofitBuilder.apiService)
        connectionRequestData.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                connectionRequestData.postValue(
                    Resource.success(repository.connectionRequest(request))
                )
            } catch (ex: IOException) {
                ex.printStackTrace()
                connectionRequestData.postValue(
                    Resource.error(
                        StatusCode.STATUS_CODE_INTERNET_VALIDATION,
                        null
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                if (exception is HttpException && exception.code() == StatusCode.HTTP_EXCEPTION) {
                    connectionRequestData.postValue(
                        Resource.error(
                            StatusCode.HTTP_EXCEPTION.toString(),
                            null
                        )
                    )
//                    if(!finishing)
//                        CommonUtils.logoutAlert(context, "Session Expired", "Your account has been blocked by Admin . Please contact to the Admin", requireActivity)
                } else
                    connectionRequestData.postValue(
                        Resource.error(
                            StatusCode.SERVER_ERROR_MESSAGE,
                            null
                        )
                    )


            }
        }
    }
}