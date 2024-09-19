package com.example.servivet.ui.main.view_model.bank_module

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.bank_module.create_bank_account_list.response.CreateBankListResponse
import com.example.servivet.data.model.bank_module.create_bank_account_list.response.CreateBankResult
import com.example.servivet.data.model.bank_module.create_bank_account_list.response.UserBank
import com.example.servivet.data.model.bank_module.remove_bank_accont.RemoveBankAccountRequest
import com.example.servivet.data.model.common.response.CommonResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import java.io.IOException

class MyBankViewModel : BaseViewModel() {

     val bankList = ArrayList<UserBank>()
     var createBankResult: CreateBankResult? = null;


    private val addBankListData = SingleLiveEvent<Resource<CreateBankListResponse>>()

    fun getAddBankListData(): LiveData<Resource<CreateBankListResponse>> {
        return addBankListData
    }

    fun getBankListRequest() {

        hitBankListApi()
    }

    private fun hitBankListApi() {
        val mainRepository = MainRepository(RetrofitBuilder.apiService)
        viewModelScope.launch {
            addBankListData.postValue(Resource.loading(null))
            try {
                addBankListData.postValue(
                    Resource.success(mainRepository.createBankListApi())
                )
            } catch (ex: IOException) {
                ex.printStackTrace()
                addBankListData.postValue(
                    Resource.error(
                        StatusCode.STATUS_CODE_INTERNET_VALIDATION,
                        null
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                addBankListData.postValue(
                    Resource.error(
                        StatusCode.SERVER_ERROR_MESSAGE,
                        null
                    )
                )
            }
        }
    }


    /*remove Bank account Api*/

    val request = RemoveBankAccountRequest()


    private val removeBankListData = SingleLiveEvent<Resource<CommonResponse>>()

    fun getRemoveBankData(): LiveData<Resource<CommonResponse>> {
        return removeBankListData
    }

    fun getRemoveBankRequest(bankId: String) {
        request.apply {
            userBankId = bankId
        }
        hitRemoveBankApi()
    }

    private fun hitRemoveBankApi() {
        val mainRepository = MainRepository(RetrofitBuilder.apiService)
        viewModelScope.launch {
            removeBankListData.postValue(Resource.loading(null))
            try {
                removeBankListData.postValue(
                    Resource.success(mainRepository.removeBankAccount(request))
                )
            } catch (ex: IOException) {
                ex.printStackTrace()
                removeBankListData.postValue(
                    Resource.error(
                        StatusCode.STATUS_CODE_INTERNET_VALIDATION,
                        null
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                removeBankListData.postValue(
                    Resource.error(
                        StatusCode.SERVER_ERROR_MESSAGE,
                        null
                    )
                )
            }
        }
    }


}