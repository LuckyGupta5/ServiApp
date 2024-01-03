package com.example.servivet.ui.main.view_model

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.service_category_details.request.ServiceCategoryDetailsRequest
import com.example.servivet.data.model.service_category_details.response.ServiceCategoryDetailsResponse
import com.example.servivet.data.model.service_category_details.response.ServiceDetail
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.databinding.FragmentMyServiceDetailBinding
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.Constants
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MyServiceDetailViewModel:BaseViewModel() {
    val serviceCategoryDetailsResponse = SingleLiveEvent<Resource<ServiceCategoryDetailsResponse>>()
    var serviceCategoryDetailsRequest= ServiceCategoryDetailsRequest()
    var data:ServiceDetail?=null

    inner class ClickAction(
        requireActivity: FragmentActivity,
        binding: FragmentMyServiceDetailBinding,
        requireActivity1: FragmentActivity
    ) {
        fun backbtn(view:View){
            view.findNavController().popBackStack()
        }

        fun editBtn(view: View){
            var bundle=Bundle()
            bundle.putSerializable(Constants.DATA,data)
            view.findNavController().navigate(R.id.action_myServiceDetailFragment_to_editServiceFragment,bundle)
        }

    }
    fun hitServiceDetailsAPI(requireContext: Context, requireActivity: Activity, finishing: Boolean) {
        val mainRepository = MainRepository(RetrofitBuilder.apiService)
        viewModelScope.launch {
            serviceCategoryDetailsResponse.postValue(Resource.loading(null))
            try {
                serviceCategoryDetailsResponse.postValue(Resource.success(mainRepository.serviceDetailsApi(serviceCategoryDetailsRequest)))
            } catch (ex: IOException) {
                serviceCategoryDetailsResponse.postValue(Resource.error(StatusCode.STATUS_CODE_INTERNET_VALIDATION, null))
                Log.e("TAG", "hitAddAgencyAPI: "+ex.message )

            } catch (exception: Exception) {
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
                    serviceCategoryDetailsResponse.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))


            }


        }

    }


}