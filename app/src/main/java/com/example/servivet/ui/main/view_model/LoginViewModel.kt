package com.example.servivet.ui.main.view_model

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.send_otp.request.SendOtpRequest
import com.example.servivet.data.model.send_otp.response.SendOtpResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.databinding.FragmentLoginBinding
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.Resource
import com.example.servivet.utils.Session
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

@RequiresApi(Build.VERSION_CODES.O)
class LoginViewModel : BaseViewModel() {
    var errorMessage = SingleLiveEvent<String>()
    var customer = MutableLiveData(true)
    var business = MutableLiveData(false)
    var isCheck = MutableLiveData(false)
    var isClick = false
    var mobileNumber = ""
    var countryCode = ""
    var numberET = MutableLiveData(false)
    var sendOtpResponse = SingleLiveEvent<Resource<SendOtpResponse>>()
    var sendOtpRequest = SendOtpRequest()

    inner class ClickAction(
        var context: Context,
        var binding: FragmentLoginBinding,
        var requireActivity: Activity,
        var finishing: Boolean
    ) {

        fun consumerClick(view: View) {
            Session.saveType("1")
            customer.postValue(true)
            business.postValue(false)
        }

        fun businessClick(view: View) {
            Session.saveType("2")
            customer.postValue(false)
            business.postValue(true)
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

        fun onNumberChange(text: CharSequence) {
            numberET.value = text.isNotEmpty()
            mobileNumber = text.toString()
            sendOtpRequest.mobile = text.toString()
        }

        fun next(view: View) {
            if (validation()) {
                hitSendOtpApi(context, requireActivity, finishing)
            }
        }

        fun gotoOT(view: View) {

        }
    }

    private fun validation(): Boolean {
        return if (mobileNumber.startsWith("0")) {
            errorMessage.setValue("Mobile number should not start with zero")
            false
        } else if (mobileNumber.length != 10) {
            errorMessage.setValue("Mobile number must be of 10 digit")
            false
        } else true
    }


    fun hitSendOtpApi(context: Context, requireActivity: Activity, finishing: Boolean) {
        val mainRepository = MainRepository(RetrofitBuilder.apiService)
        viewModelScope.launch {
            sendOtpResponse.postValue(Resource.loading(null))
            try {
                sendOtpResponse.postValue(Resource.success(mainRepository.sendOtpApi(sendOtpRequest)))
            } catch (ex: IOException) {
                ex.printStackTrace()
                sendOtpResponse.postValue(
                    Resource.error(
                        StatusCode.STATUS_CODE_INTERNET_VALIDATION, null
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                if (exception is HttpException && exception.code() == 401) {
                    if (!finishing) CommonUtils.logoutAlert(
                        context,
                        "Session Expired",
                        "Your account has been blocked by Admin . Please contact to the Admin",
                        requireActivity
                    )
                } else sendOtpResponse.postValue(
                    Resource.error(
                        StatusCode.SERVER_ERROR_MESSAGE,
                        null
                    )
                )


            }


        }
    }


}




