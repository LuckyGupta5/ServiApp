package com.example.servivet.ui.main.view_model

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.save_address.request.SaveAddressRequest
import com.example.servivet.data.model.save_address.response.SaveAddressResponse
import com.example.servivet.data.model.send_otp.request.SendOtpRequest
import com.example.servivet.data.model.send_otp.response.SendOtpResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class SettingAddLocationViewModel : BaseViewModel() {
    var isCheck = MutableLiveData(false)
    var isClick = false

    var action: String = ""
    val name = MutableLiveData(false)
    val number = MutableLiveData(false)
    var saveAddressResponse = SingleLiveEvent<Resource<SaveAddressResponse>>()
    var saveAddressRequest = SaveAddressRequest()

    inner class ClickAction() {
        fun backbtn(view: View) {
            view.findNavController().popBackStack()
        }

        fun onNameChange(text: CharSequence) {
            name.value = text.isNotEmpty()
            saveAddressRequest.name = text.toString().trim().replace("\\s+".toRegex(), " ")
        }

        fun onNumberChange(text: CharSequence) {
            number.value = text.isNotEmpty()
            saveAddressRequest.mobileNumber = text.toString().trim().replace("\\s+".toRegex(), " ")
        }

        fun agree(view: View) {
            isClick = if (!isClick) {
                isCheck.postValue(true)
                true
            } else {
                isCheck.postValue(false)
                false
            }
        }

        fun saveAddressBtn(view: View) {
            if (action == "add") {
                saveAddressRequest.addressActionType = "add"
            } else if (action == "update") {
                saveAddressRequest.addressActionType = "update"
            }
            hitSaveAddressAPI()
        }

    }

    fun hitSaveAddressAPI() {
        val mainRepository = MainRepository(RetrofitBuilder.apiService)
        viewModelScope.launch {
            saveAddressResponse.postValue(Resource.loading(null))
            try {
                saveAddressResponse.postValue(
                    Resource.success(
                        mainRepository.saveAddressApi(
                            saveAddressRequest
                        )
                    )
                )
            } catch (ex: IOException) {
                ex.printStackTrace()
                saveAddressResponse.postValue(
                    Resource.error(
                        StatusCode.STATUS_CODE_INTERNET_VALIDATION,
                        null
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                saveAddressResponse.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))
            }
        }
    }
}