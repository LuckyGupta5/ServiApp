package com.example.servivet.ui.main.view_model.bank_module

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.bank_module.bank_list_response.response.BankListResposne
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import java.io.IOException

class MyBankAccountViewModel : BaseViewModel() {

    private var request = HashMap<String, String>()

    private val bankListData = SingleLiveEvent<Resource<BankListResposne>>()

    fun getBalListData(): LiveData<Resource<BankListResposne>> {
        return bankListData
    }

    fun getListRequest() {
        request["currency"] = "ZAR"
        hitBankListApi()
    }


    private fun hitBankListApi() {

        val mainRepository = MainRepository(RetrofitBuilder.apiService)
        viewModelScope.launch {
            bankListData.postValue(Resource.loading(null))
            try {
                bankListData.postValue(
                    Resource.success(mainRepository.bankListApi(request))
                )
            } catch (ex: IOException) {
                ex.printStackTrace()
                bankListData.postValue(
                    Resource.error(
                        StatusCode.STATUS_CODE_INTERNET_VALIDATION,
                        null
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                bankListData.postValue(
                    Resource.error(
                        StatusCode.SERVER_ERROR_MESSAGE,
                        null
                    )
                )
            }
        }
    }


}