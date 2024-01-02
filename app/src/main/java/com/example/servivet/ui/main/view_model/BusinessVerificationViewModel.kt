package com.example.servivet.ui.main.view_model

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.servivet.R
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.business_verification_api.request.BusinessVerificationRequest
import com.example.servivet.data.model.business_verification_api.response.BusinessVerificationResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.databinding.FragmentBusinessVerificationBinding
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class BusinessVerificationViewModel : BaseViewModel() {
    var email = ""
    var name = ""
    var isPhotoSelected: Boolean = false
    var errorMessage = SingleLiveEvent<String>()
    val enailET = MutableLiveData(false)
    val nameuser = MutableLiveData(false)
    var indivisualRole = MutableLiveData(true)
    var instituaionLRole = MutableLiveData(false)
    var businessVerificationRequest = BusinessVerificationRequest()
    var businessVerificationResponse = SingleLiveEvent<Resource<BusinessVerificationResponse>>()

    inner class ClickAction(
        var context: Context,
        var binding: FragmentBusinessVerificationBinding,
        var requireActivity: FragmentActivity,
        var finishing: Boolean,
    ) {
        fun individual(view: View) {
            businessVerificationRequest.businessType = "3"   // 3-> individual

            indivisualRole.postValue(true)
            instituaionLRole.postValue(false)

        }

        fun institution(view: View) {
            businessVerificationRequest.businessType = "4"   // 3-> institutional
            instituaionLRole.postValue(true)
            indivisualRole.postValue(false)

        }

        fun homePage(view: View) {
            if (validation(context,finishing)) {
                hitBusinessVerificationAPI(context,requireActivity,finishing)
            }


        }

        fun onNameChange(text: CharSequence) {
            nameuser.value = text.isNotEmpty()
            businessVerificationRequest.name=text.toString()

        }

        fun onEmailChange(text: CharSequence) {
            enailET.value = text.isNotEmpty()
            email=text.toString().trim()
            businessVerificationRequest.email=text.toString()
        }


        private fun validation(context: Context, finishing: Any?): Boolean {
            return if (email!="" && !CommonUtils.emailValidator(email)) {
                errorMessage.setValue(context.getString(R.string.please_enter_a_valid_e_mail))
                false
            }  else
                true
        }
    }
    fun hitBusinessVerificationAPI(
        context: Context,
        requireActivity: FragmentActivity,
        finishing: Boolean
    ) {
        val mainRepository = MainRepository(RetrofitBuilder.apiService)
        viewModelScope.launch {
            businessVerificationResponse.postValue(Resource.loading(null))
            try {
                businessVerificationResponse.postValue(
                    Resource.success(
                        mainRepository.businessVerificationApi(
                            businessVerificationRequest
                        )
                    )
                )
            } catch (ex: IOException) {
                ex.printStackTrace()
                businessVerificationResponse.postValue(Resource.error(StatusCode.STATUS_CODE_INTERNET_VALIDATION, null))
            }  catch (exception: Exception) {
                exception.printStackTrace()
                if (exception is HttpException && exception.code() == 401) {
                    if(!finishing)
                        CommonUtils.logoutAlert(context, "Session Expired", "Your account has been blocked by Admin . Please contact to the Admin", requireActivity)
                }else
                    businessVerificationResponse.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))


            }


        }
    }


}