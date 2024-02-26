package com.example.servivet.ui.main.view_model

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.common.response.CommonResponse
import com.example.servivet.data.model.edit_profile.response.EditProfileResponse
import com.example.servivet.data.model.setting.contact.request.ContactUsRequest
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.IOException

class ContactUsViewModel : BaseViewModel() {
    var titleET = MutableLiveData(false)
    var messageET = MutableLiveData(false)
    var contactUsRequest = ContactUsRequest()
    var contactUsResponse = SingleLiveEvent<Resource<CommonResponse>>()

    inner class ClickAction {
        fun backBtn(view: View) {
            view.findNavController().popBackStack()
        }

        fun title(text: CharSequence) {
            titleET.value = text.isNotEmpty()
            contactUsRequest.title = text.toString()
        }

        fun message(text: CharSequence) {
            messageET.value = text.isNotEmpty()
            contactUsRequest.message = text.toString()
        }

        fun getData(view:View) {
            hitContactUsApi()
        }


    }

    fun hitContactUsApi() {
        val repository = MainRepository(RetrofitBuilder.apiService)
        Log.e("TAG", "hitContactUsApi: ${Gson().toJson(contactUsRequest)}")

        contactUsResponse.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                contactUsResponse.postValue(
                    Resource.success(
                        repository.contactUsApi(
                            contactUsRequest
                        )
                    )
                )
            } catch (ex: IOException) {
                ex.printStackTrace()
                contactUsResponse.postValue(
                    Resource.error(
                        StatusCode.STATUS_CODE_INTERNET_VALIDATION,
                        null
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                contactUsResponse.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))
            }
        }
    }
}