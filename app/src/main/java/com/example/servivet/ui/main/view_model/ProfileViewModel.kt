package com.example.servivet.ui.main.view_model

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.user_profile.response.UserProfileResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.ui.main.activity.MainActivity
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.Resource
import com.example.servivet.utils.Session
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


class ProfileViewModel:BaseViewModel() {
    var errorMessage = SingleLiveEvent<String>()
    var dialog:Dialog?=null

    val userProfileResponse = SingleLiveEvent<Resource<UserProfileResponse>>()
    var userProfileRequest=HashMap<String,String>()

    inner class ClickAction(var requireActivity: Activity,var  context: Context) {
        fun goEditProfile(view: View){
            if(Session.type=="1")
                view.findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment2)
            else{
                view.findNavController().navigate(R.id.action_profileFragment_to_myServiceFragment)
            }
        }
        fun goEditProfile2(view: View){
                view.findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment2)

        }

        fun logOut(view: View){
            CommonUtils.alert(context,"Are you sure you want to logout ?",requireActivity)
//            logoutAlert(context,requireActivity)
        }
    }
    fun logoutAlert(context: Context, requireActivity: Activity) {
        val alert: AlertDialog = AlertDialog.Builder(context).create()
        alert.setTitle("Are you sure you want to logout ?")
        alert.setMessage("  ")
        alert.setCanceledOnTouchOutside(false)
        alert.setCancelable(false)
        alert.setButton(DialogInterface.BUTTON_NEGATIVE, "No") { dialog, _ ->
           alert.dismiss()
        }

        alert.setButton(DialogInterface.BUTTON_POSITIVE, "Yes") { dialog, _ ->
            Session.logout()
            SplashViewModel.isLogout = true
            requireActivity.startActivity(Intent(requireActivity, MainActivity::class.java))
            requireActivity.finish()
            Toast.makeText(requireActivity,"Log out successfully !", Toast.LENGTH_SHORT).show()
        }

        alert.show()
    }


    fun hitUserProfileApi(userId: String, isMyProfile: Int, requireContext: Context, requireActivity: Activity, finishing: Boolean) {
        userProfileRequest["userId"] = userId
        userProfileRequest["isMyProfile"]= isMyProfile.toString()
        val mainRepository = MainRepository(RetrofitBuilder.apiService)
        viewModelScope.launch {
            userProfileResponse.postValue(Resource.loading(null))
            try {
                userProfileResponse.postValue(Resource.success(mainRepository.userProfile(userProfileRequest)))
            } catch (ex: IOException) {
                userProfileResponse.postValue(Resource.error(StatusCode.STATUS_CODE_INTERNET_VALIDATION, null))
                Log.e("TAG", "hitAddAgencyAPI: "+ex.message )

            } catch (exception: Exception) {
                exception.printStackTrace()
                if (exception is HttpException && exception.code() == 401) {
                    if(!finishing)
                        CommonUtils.logoutAlert(requireContext, "Session Expired", "Your account has been blocked by Admin . Please contact to the Admin", requireActivity)
                }else
                    userProfileResponse.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))


            }


        }

    }
}