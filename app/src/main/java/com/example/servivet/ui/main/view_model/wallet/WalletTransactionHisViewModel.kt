package com.example.servivet.ui.main.view_model.wallet

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.booking_module.booking_summary.response.ServiceDetail
import com.example.servivet.data.model.booking_module.wallte_transaction.request.WalletTranctionnRequest
import com.example.servivet.data.model.common.request.CommonRequest
import com.example.servivet.data.model.payment.payment_amount.request.PaymentRequest
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.utils.AESHelper
import com.example.servivet.utils.Constants
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class WalletTransactionHisViewModel():ViewModel() {
    private var request = CommonRequest()
    private val historyRequest = WalletTranctionnRequest()

    private val walletHistoryData = SingleLiveEvent<Resource<String>>()
    fun getWalletData(): LiveData<Resource<String>> {
        return walletHistoryData
    }

    fun getPaymentAmountRequest(serviceData: ServiceDetail) {
        Constants.SECURE_HEADER = "secure"
        request.servivet_user_req = AESHelper.encrypt(Constants.SECURITY_KEY, Gson().toJson(historyRequest))

        hitWalletHistoryApi()

    }

    private fun hitWalletHistoryApi() {

        val repository = MainRepository(RetrofitBuilder.apiService)
        walletHistoryData.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                walletHistoryData.postValue(Resource.success(repository.walletTransactionApi(request)))
            } catch (ex: IOException) {
                ex.printStackTrace()
                walletHistoryData.postValue(Resource.error(StatusCode.STATUS_CODE_INTERNET_VALIDATION, null))
            } catch (exception: Exception) {
                exception.printStackTrace()
                if (exception is HttpException && exception.code() == StatusCode.HTTP_EXCEPTION) {
                    walletHistoryData.postValue(
                        Resource.error(
                            StatusCode.HTTP_EXCEPTION.toString(),
                            null
                        )
                    )
//                    if(!finishing)
//                        CommonUtils.logoutAlert(context, "Session Expired", "Your account has been blocked by Admin . Please contact to the Admin", requireActivity)
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
}