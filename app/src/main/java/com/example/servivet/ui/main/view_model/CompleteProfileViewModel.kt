package com.example.servivet.ui.main.view_model

import android.content.Context
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.edit_profile.request.EditProfileRequest
import com.example.servivet.data.model.edit_profile.response.EditProfileResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.databinding.FragmentCompleteProfileBinding
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class CompleteProfileViewModel:BaseViewModel() {
    var mobilenumber = ""
    var countrycode = ""
    private var email = ""
    private var abouttext=""
    val about=MutableLiveData(false)
    var errorMessage = SingleLiveEvent<String>()
    var editProfileRequest = EditProfileRequest()
    var editProfileResponse = SingleLiveEvent<Resource<EditProfileResponse>>()
    val enailET = MutableLiveData(false)
    val name = MutableLiveData(false)

    inner class ClickAction(
        var binding: FragmentCompleteProfileBinding,
        var context: Context,
        var requireActivity: FragmentActivity,
        var finishing: Boolean
    ){
        fun backbtn(view:View){
            view.findNavController().popBackStack()
        }
        fun onNameChange(text: CharSequence) {
            name.value = text.isNotEmpty()
            editProfileRequest.name = text.toString().trim().replace("\\s+".toRegex(), " ")
        }

        fun onEmailChange(text: CharSequence) {
            enailET.value = text.isNotEmpty()
            email = text.toString().trim()
            editProfileRequest.email = email
        }


        fun updateBtn(view: View) {
            if (validation(context)) {
                hitEditProfileApi(context,requireActivity,finishing)
            }
        }


        private fun validation(context: Context): Boolean {
            return if (email!=null && !CommonUtils.emailValidator(email)) {
                errorMessage.setValue(context.getString(R.string.please_enter_a_valid_e_mail))
                false
            } else
                true

        }
        fun onAboutTextChanged(text: CharSequence) {
            binding.wordCount.text = "(" + text.toString().length.toString() + "/150)"
            about.value=text.isNotEmpty()
            abouttext=text.toString().trim()
            editProfileRequest.aboutus = text.toString().trim().replace("\\s+".toRegex(), " ")
        }




    }
    fun hitEditProfileApi(context: Context, requireActivity: FragmentActivity, finishing: Boolean) {

        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        val repo = MainRepository(RetrofitBuilder.apiService)
        try {
            editProfileRequest.name?.let { builder.addFormDataPart("name", it) }
            editProfileRequest.countryCode?.let { builder.addFormDataPart("countryCode", it) }
            if (editProfileRequest.email != null && editProfileRequest.email != "")
                editProfileRequest.email?.let { builder.addFormDataPart("email", it) }
            editProfileRequest.mobile?.let { builder.addFormDataPart("mobile", it) }
            editProfileRequest.aboutus?.let { builder.addFormDataPart("aboutus", it) }


            if (EditProfileVewModel.isPhotoSelected) {
                val file = File(editProfileRequest.image!!)
                builder.addFormDataPart(
                    "image",
                    file.name,
                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                )

            }


            editProfileResponse.postValue(Resource.loading(null))
            viewModelScope.launch {
                try {
                    editProfileResponse.postValue(Resource.success(repo.editProfile(builder.build())))
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    Log.d("exception", "" + ex.localizedMessage)
                    editProfileResponse.postValue(
                        Resource.error(
                            StatusCode.STATUS_CODE_INTERNET_VALIDATION,
                            null
                        )
                    )
                }catch (exception: Exception) {
                    exception.printStackTrace()
                    if (exception is HttpException && exception.code() == 401) {
                        if ((finishing))
                            CommonUtils.logoutAlert(context, "Session Expired", "Your account has been blocked by Admin . Please contact to the Admin", requireActivity)
                    }else
                        editProfileResponse.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))


                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

    }

}