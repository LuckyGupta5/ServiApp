package com.example.servivet.ui.main.view_model

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.service_list.ServiceListResponse
import com.example.servivet.data.model.service_list.request.ServiceListRequest
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.databinding.FragmentServicesTypeListingBinding
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ServicesTypeViewModel :BaseViewModel(){
    val serviceListResponse = SingleLiveEvent<Resource<ServiceListResponse>>()
    var serviceListRequest= ServiceListRequest()
    inner class ClickAction(var context: Context,var binding: FragmentServicesTypeListingBinding)
    {
        fun backBtnhai(view: View){
            view.findNavController().navigateUp()
        }
    }


    fun hitServiceListAPI(requireContext: Context, requireActivity: Activity, finishing: Boolean) {
        val mainRepository = MainRepository(RetrofitBuilder.apiService)
        viewModelScope.launch {
            serviceListResponse.postValue(Resource.loading(null))
            try {
                serviceListResponse.postValue(Resource.success(mainRepository.serviceListApi(serviceListRequest)))
            } catch (ex: IOException) {
                serviceListResponse.postValue(Resource.error(StatusCode.STATUS_CODE_INTERNET_VALIDATION, null))
                Log.e("TAG", "hitAddAgencyAPI: "+ex.message )

            }  catch (exception: Exception) {
                exception.printStackTrace()
                if (exception is HttpException && exception.code() == 401) {
                    if(!finishing)
                        CommonUtils.logoutAlert(
                        requireContext,
                        "Session Expired",
                        "Your account has been blocked by Admin . Please contact to the Admin",
                        requireActivity
                    )
                }else
                    serviceListResponse.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))


            }


        }

    }

}