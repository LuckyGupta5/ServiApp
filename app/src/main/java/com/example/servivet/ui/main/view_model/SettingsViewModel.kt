package com.example.servivet.ui.main.view_model

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.common.response.CommonResponse
import com.example.servivet.data.model.home.response.HomeResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.ui.main.fragment.SettingsFragmentDirections
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class SettingsViewModel : BaseViewModel() {
     var logoutResponse = SingleLiveEvent<Resource<CommonResponse>>()
    fun getLogoutData(): LiveData<Resource<CommonResponse>> {
        return logoutResponse
    }

    inner class ClickAction(var frgmentActivity: Activity) {
        fun backbtn(view: View) {
            view.findNavController().popBackStack()
        }

        fun gotoMyWallet(view: View) {
            view.findNavController().navigate(R.id.action_settingsFragment_to_myWalletFragment)
        }

        fun goLogout(view: View) {
            view.findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToCloseServiceAlert("",

                    "logOut"
                ))
           // CommonUtils.customalertdialog(frgmentActivity, frgmentActivity.getString(R.string.are_you_sure_you_want_to_logout), 1)
        }


    }


    fun hitLogoutApi() {
        val repository = MainRepository(RetrofitBuilder.apiService)

        logoutResponse.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                logoutResponse.postValue(Resource.success(repository.logoutUser()))
            }catch (ex: IOException) {
                ex.printStackTrace()
                logoutResponse.postValue(Resource.error(StatusCode.STATUS_CODE_INTERNET_VALIDATION, null))
            }catch (exception: Exception) {
                exception.printStackTrace()
                logoutResponse.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))
            }
        }
    }
}