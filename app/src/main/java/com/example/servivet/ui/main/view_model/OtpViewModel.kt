package com.example.servivet.ui.main.view_model

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.send_otp.request.SendOtpRequest
import com.example.servivet.data.model.send_otp.response.SendOtpResponse
import com.example.servivet.data.model.verifyotp.request.VerifyOtpRequest
import com.example.servivet.data.model.verifyotp.response.VerifyOTPResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.databinding.FragmentOtpVarificationBinding
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.PreciseCountdown
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class OtpViewModel : BaseViewModel() {
    private lateinit var countDown: PreciseCountdown
    var mobilenumber = ""
    var otpNumber = ""
    var countrycode = ""
    private var otpText: String = ""
    var types: Int = 0
    var otppin = MutableLiveData(false)
    var timer = MutableLiveData<String>()
    var verifyOtpRequest = VerifyOtpRequest()
    var sendOtpRequest = SendOtpRequest()
    var sendOtpResponse = SingleLiveEvent<Resource<SendOtpResponse>>()
    var verifyOtpResponse = SingleLiveEvent<Resource<VerifyOTPResponse>>()

    inner class ClickAction(
        var context: Context,
        var binding: FragmentOtpVarificationBinding,
        var type: String,
        var requireActivity: Activity,
       var  finishing: Boolean,
    ) {
        fun editBtn(view: View) {
            view.findNavController().popBackStack()
        }

        fun backBtn(view: View) {
            view.findNavController().popBackStack()
        }

        fun onResendClick(view: View) {
            types = 1
            counterDownTimmer(context as Activity, binding)
            hitSendOtpApi(context,requireActivity)
        }

        fun onOTPChange(text: CharSequence) {
            otppin.value = text.length == 4
            otpText = otppin.toString()
           verifyOtpRequest.otp=text.toString()
        }

        fun gotoHomeActivity(view: View) {
            hitVerifyOtpApi(context,requireActivity,finishing)
        }


    }

    fun hitVerifyOtpApi(context: Context, requireActivity: Activity, finishing: Boolean) {
        val mainRepository = MainRepository(RetrofitBuilder.apiService)
        viewModelScope.launch {
            verifyOtpResponse.postValue(Resource.loading(null))
            try {
                verifyOtpResponse.postValue(
                    Resource.success(mainRepository.verifyOtpAPi(verifyOtpRequest)))
            } catch (ex: IOException) {
                ex.printStackTrace()
                verifyOtpResponse.postValue(
                    Resource.error(
                        StatusCode.STATUS_CODE_INTERNET_VALIDATION,
                        null
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                if (exception is HttpException && exception.code() == 401) {
                    if(!finishing)
                        CommonUtils.logoutAlert(context, "Session Expired", "Your account has been blocked by Admin . Please contact to the Admin", requireActivity)
                }else
                    verifyOtpResponse.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))


            }

        }
    }

    fun hitSendOtpApi(context: Context, requireActivity: Activity) {
        val mainRepository = MainRepository(RetrofitBuilder.apiService)
        viewModelScope.launch {
            sendOtpResponse.postValue(Resource.loading(null))
            try {
                sendOtpResponse.postValue(Resource.success(mainRepository.sendOtpApi(sendOtpRequest)))
            } catch (ex: IOException) {
                ex.printStackTrace()
                sendOtpResponse.postValue(
                    Resource.error(
                        StatusCode.STATUS_CODE_INTERNET_VALIDATION,
                        null
                    )
                )
            }  catch (exception: Exception) {
                exception.printStackTrace()
                if (exception is HttpException && exception.code() == 401) {
                    CommonUtils.logoutAlert(
                        context,
                        "Session Expired",
                        "Your account has been blocked by Admin . Please contact to the Admin",
                        requireActivity
                    )
                }else
                    sendOtpResponse.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))


            }


        }
    }


    fun counterDownTimmer(context: Activity, binding: FragmentOtpVarificationBinding) {
        /*var otpTimer = 0
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timer.value = "Resend OTP in " + millisUntilFinished / 1000 + " Seconds"

                binding.resendOtp.isEnabled = false
                otpTimer = millisUntilFinished.toInt()
                //here you can have your logic to set text to edittext
            }

            override fun onFinish() {
                timer.value = "Resend OTP"
                binding.resendOtp.isEnabled = true
            }

        }.start()*/


        countDown = object : PreciseCountdown(30000, 1000, 1000) {
            var otpTextTimer = 0
            override fun onTick(l: Long) {
                context.runOnUiThread {
                    timer.value = "Resend OTP in " + l / 1000 + " Seconds"
                    binding.resendOtp.setText("Resend OTP in " + l / 1000 + " Seconds")
                    otpTextTimer = l.toString().toInt()
                    binding.resendOtp.setEnabled(false)

                }
            }

            override fun onFinished() {
                context.runOnUiThread {
                    onTick(0) // when the timer finishes onTick isn't called
                    timer.value = "Resend OTP"
                    binding.resendOtp.isEnabled = true
                }
            }
        }

        countDown.start()


    }

    fun stopCountDown() {
        timer.value=""
        countDown.cancel()
        countDown.restart()
    }

}