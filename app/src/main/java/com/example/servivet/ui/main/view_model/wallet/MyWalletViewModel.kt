package com.example.servivet.ui.main.view_model.wallet

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.booking_module.wallte_transaction.request.WalletTranctionnRequest
import com.example.servivet.data.model.common.request.CommonRequest
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.AESHelper
import com.example.servivet.utils.Constants
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MyWalletViewModel : BaseViewModel() {
    var request = CommonRequest()
    val historyRequest = WalletTranctionnRequest()
    val walletHistoryData = SingleLiveEvent<Resource<String>>()
    private val walletData = SingleLiveEvent<Resource<String>>()
    fun getWalletData(): LiveData<Resource<String>> {
        return walletData
    }

    inner class ClickAction {
        fun backbtn(view: View) {
            view.findNavController().popBackStack()
        }
    }

    fun getWalletHistoryData(): LiveData<Resource<String>> {
        return walletHistoryData
    }

    fun getHistoryRequest(isBought: Boolean) {

        historyRequest.apply {
            this.isBought = isBought
            page = 1
            limit = 10
        }

        request.servivet_user_req = AESHelper.encrypt(Constants.SECURITY_KEY, Gson().toJson(historyRequest))

        hitWalletHistoryApi()
    }


    fun hitWalletHistoryApi() {
        Constants.SECURE_HEADER = "secure"
        val repository = MainRepository(RetrofitBuilder.apiService)
        walletHistoryData.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                walletHistoryData.postValue(Resource.success(repository.walletTransactionApi(request)))
            } catch (ex: IOException) {
                ex.printStackTrace()
                walletHistoryData.postValue(
                    Resource.error(
                        StatusCode.STATUS_CODE_INTERNET_VALIDATION,
                        null
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                if (exception is HttpException && exception.code() == StatusCode.HTTP_EXCEPTION) {
                    walletHistoryData.postValue(
                        Resource.error(
                            StatusCode.HTTP_EXCEPTION.toString(),
                            null
                        )
                    )
                } else
                    walletHistoryData.postValue(
                        Resource.error(
                            StatusCode.SERVER_ERROR_MESSAGE,
                            null
                        )
                    )


            }
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