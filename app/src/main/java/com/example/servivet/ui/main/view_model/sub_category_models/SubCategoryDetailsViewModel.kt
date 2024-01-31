package com.example.servivet.ui.main.view_model.sub_category_models

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.service_category_details.request.ServiceCategoryDetailsRequest
import com.example.servivet.data.model.service_category_details.response.ServiceCategoryDetailsResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.databinding.FragmentSubCategoryDetailsBinding
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.ui.main.fragment.SubCategoryDetailsFragmentDirections
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.Resource
import com.example.servivet.utils.Session
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class SubCategoryDetailsViewModel:BaseViewModel() {
    val serviceCategoryDetailsResponse = SingleLiveEvent<Resource<ServiceCategoryDetailsResponse>>()
    var serviceCategoryDetailsRequest= ServiceCategoryDetailsRequest()


    inner class ClickAction(var context: FragmentActivity, binding: FragmentSubCategoryDetailsBinding,var  serviceId: String) {
        fun backbtn(view:View){
            view.findNavController().popBackStack()
        }

        fun callSummaryFragment(view: View){
          //  Session.saveAddress=null
            view.findNavController().navigate(SubCategoryDetailsFragmentDirections.actionSubCategoryDetailsFragmentToBookingSummaryFragment(serviceId, context.getString(R.string.sub_category)))
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
                    serviceCategoryDetailsResponse.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))


            }


        }

    }


}