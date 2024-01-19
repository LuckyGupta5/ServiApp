package com.example.servivet.ui.main.view_model.wallet

import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.booking_module.booking_summary.response.ServiceDetail
import com.example.servivet.data.model.payment.payment_amount.response.PayAmountResult
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.ui.main.bottom_sheet.BookingCancelledBottomSheet
import com.example.servivet.utils.AESHelper
import com.example.servivet.utils.Constants
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


class MyWalletBottomsheetViewModel : BaseViewModel() {


    private val walletData = SingleLiveEvent<Resource<String>>()
    fun getWalletData(): LiveData<Resource<String>> {
        return walletData
    }


    inner class ClickAction(var context: Context) {
        fun dismiss(view: View) {
//            val fragment = BookingCancelledBottomSheet()
//            fragment.show((context as AppCompatActivity).supportFragmentManager, "childfragment")
            // (context as AppCompatActivity).supportFragmentManager
        }


    }

    fun hitWalletApi() {
        Constants.SECURE_HEADER = "secure"
        val repository = MainRepository(RetrofitBuilder.apiService)
        walletData.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                walletData.postValue(Resource.success(repository.myWalletApi()))
            } catch (ex: IOException) {
                ex.printStackTrace()
                walletData.postValue(
                    Resource.error(
                        StatusCode.STATUS_CODE_INTERNET_VALIDATION, null
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                if (exception is HttpException && exception.code() == StatusCode.HTTP_EXCEPTION) {
                    walletData.postValue(
                        Resource.error(
                            StatusCode.HTTP_EXCEPTION.toString(),
                            null
                        )
                    )
//                    if(!finishing)
//                        CommonUtils.logoutAlert(context, "Session Expired", "Your account has been blocked by Admin . Please contact to the Admin", requireActivity)
                } else
                    walletData.postValue(
                        Resource.error(
                            StatusCode.SERVER_ERROR_MESSAGE,
                            null
                        )
                    )


            }
        }
    }


}