package com.example.servivet.ui.main.view_model

import android.view.View
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.setting.faq_list.response.FaqListResponse
import com.example.servivet.data.model.setting.faq_type_list.response.FaqTypeListResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import java.io.IOException

class FaqViewModel:BaseViewModel() {
    var faqTypeResponse = SingleLiveEvent<Resource<FaqTypeListResponse>>()
    var faqListResponse = SingleLiveEvent<Resource<FaqListResponse>>()
    inner class ClickAction{
        fun backBtn(view:View){
            view.findNavController().popBackStack()
        }
    }

    fun hitFaqTypeListApi() {
        val repository = MainRepository(RetrofitBuilder.apiService)

        faqTypeResponse.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                faqTypeResponse.postValue(Resource.success(repository.faqTypeListApi()))
            }catch (ex: IOException) {
                ex.printStackTrace()
                faqTypeResponse.postValue(Resource.error(StatusCode.STATUS_CODE_INTERNET_VALIDATION, null))
            }catch (exception: Exception) {
                exception.printStackTrace()
                faqTypeResponse.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))
            }
        }
    }

    fun hitFaqTypeListApi(faqTypeId:String) {
        val repository = MainRepository(RetrofitBuilder.apiService)

        faqListResponse.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                faqListResponse.postValue(Resource.success(repository.faqListApi(faqTypeId)))
            }catch (ex: IOException) {
                ex.printStackTrace()
                faqListResponse.postValue(Resource.error(StatusCode.STATUS_CODE_INTERNET_VALIDATION, null))
            }catch (exception: Exception) {
                exception.printStackTrace()
                faqListResponse.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))
            }
        }
    }
}