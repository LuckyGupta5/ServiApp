package com.example.servivet.ui.main.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.chat_models.chat_media.ChatMediaResponse
import com.example.servivet.data.model.common.response.CommonResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

class ChattingViewModel : BaseViewModel() {

    val messageText = MutableLiveData("")
    private val uploadDocumentLiveData = SingleLiveEvent<Resource<ChatMediaResponse>>()
    fun getDocumentData(): LiveData<Resource<ChatMediaResponse>> {
        return uploadDocumentLiveData
    }
    fun getRequest(file: String) {
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        val path = File(file)
        builder.addFormDataPart("chatImage", path.name, path.asRequestBody("multipart/form-data".toMediaTypeOrNull()))
        val requestBody = builder.build()
        hitUploadDocumentApi(requestBody)
    }
    private fun hitUploadDocumentApi(requestBody: MultipartBody) {
        val mainRepository = MainRepository(RetrofitBuilder.apiService)
        viewModelScope.launch {
            uploadDocumentLiveData.postValue(Resource.loading(null))
            try {
                uploadDocumentLiveData.postValue(
                    Resource.success(mainRepository.uploadChatFileApi(requestBody))
                )
            } catch (ex: IOException) {
                ex.printStackTrace()
                uploadDocumentLiveData.postValue(
                    Resource.error(
                        StatusCode.STATUS_CODE_INTERNET_VALIDATION,
                        null
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                uploadDocumentLiveData.postValue(
                    Resource.error(
                        StatusCode.SERVER_ERROR_MESSAGE,
                        null
                    )
                )
            }
        }
    }
}