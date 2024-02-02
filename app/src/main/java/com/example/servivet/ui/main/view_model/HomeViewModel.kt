package com.example.servivet.ui.main.view_model

import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.current_api.response.CurrentResponse
import com.example.servivet.data.model.home.response.HomeResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class HomeViewModel:BaseViewModel() {
    var currentResponse= SingleLiveEvent<Resource<CurrentResponse>>()
    var homeResponse = SingleLiveEvent<Resource<HomeResponse>>()

    inner class ClickAction{}

    fun hitCurrentApi(requireContext: Context, requireActivity: Activity, finishing: Boolean) {
        val mainRepository = MainRepository(RetrofitBuilder.apiService)
        viewModelScope.launch {
            currentResponse.postValue(Resource.loading(null))
            try {
                currentResponse.postValue(Resource.success(mainRepository.currentApi()))
            } catch (ex: IOException) {
                ex.printStackTrace()
                currentResponse.postValue(Resource.error(StatusCode.STATUS_CODE_INTERNET_VALIDATION, null))
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
                    homeResponse.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))


            }

        }
    }

    fun hitHomeApi(requireContext: Context, requireActivity: FragmentActivity, finishing: Boolean) {
        val mainRepository = MainRepository(RetrofitBuilder.apiService)
        viewModelScope.launch {
            homeResponse.postValue(Resource.loading(null))
            try {
                homeResponse.postValue(Resource.success(mainRepository.homeApi()))
            } catch (ex: IOException) {
                ex.printStackTrace()
                homeResponse.postValue(Resource.error(StatusCode.STATUS_CODE_INTERNET_VALIDATION, null))
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
                    homeResponse.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))


            }

        }
    }
}