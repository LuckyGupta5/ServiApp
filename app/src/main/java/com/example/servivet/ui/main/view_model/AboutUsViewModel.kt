package com.example.servivet.ui.main.view_model

import android.view.View
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.common.response.CommonResponse
import com.example.servivet.data.model.setting.cms.response.CmsResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import java.io.IOException

class AboutUsViewModel:BaseViewModel() {
    var cmsResponse = SingleLiveEvent<Resource<CmsResponse>>()

    inner class ClickAction{
        fun backBtn(view:View){
            view.findNavController().popBackStack()
        }
    }

    fun hitCmsApi(type:String) {
        val repository = MainRepository(RetrofitBuilder.apiService)

        cmsResponse.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                cmsResponse.postValue(Resource.success(repository.cmsApi(type)))
            }catch (ex: IOException) {
                ex.printStackTrace()
                cmsResponse.postValue(Resource.error(StatusCode.STATUS_CODE_INTERNET_VALIDATION, null))
            }catch (exception: Exception) {
                exception.printStackTrace()
                cmsResponse.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))
            }
        }
    }
}