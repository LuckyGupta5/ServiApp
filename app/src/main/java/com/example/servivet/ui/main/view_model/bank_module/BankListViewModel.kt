package com.example.servivet.ui.main.view_model.bank_module

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.servivet.R
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.bank_module.bank_list_response.response.Bank
import com.example.servivet.data.model.bank_module.bank_list_response.response.BankListResposne
import com.example.servivet.data.model.bank_module.bank_list_response.response.BankListResult
import com.example.servivet.data.model.bank_module.create_bankAccount.request.CreateBankAccountRequest
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
import java.io.IOException

class BankListViewModel : BaseViewModel() {

    var bankListResult: BankListResult? = null
    var bankAccountRequest = CreateBankAccountRequest()

    var bankData: Bank? = null
    var errorMessage = SingleLiveEvent<Int>()
    private var request = HashMap<String, String>()
    private val bankListData = SingleLiveEvent<Resource<BankListResposne>>()

    /*validation Variables*/
    var accountNumber = MutableLiveData<String>("")
    var accountName = MutableLiveData<String>("")
    var documentNumber = MutableLiveData<String>("")
    var documentType = MutableLiveData<String>("")
    val accountType = MutableLiveData<Boolean>(true)

    val isSavingsAccountChecked: LiveData<Boolean>
        get() = accountType

    fun onSavingsAccountCheckedChanged(checked: Boolean) {
        accountType.value = checked
    }


    /* Click Events */
    fun saveData() {
        ///x Log.e("TAG", "chsjjsynd: ${accountType.value}")
        if (checkValidation()) {
            addRequestValue()
            Log.e("TAG", "checkBefore: ${Gson().toJson(bankAccountRequest)}")
            errorMessage.postValue(R.string.openbottomsheet)
        }
    }

    private fun addRequestValue() {
        bankAccountRequest.apply {
            bank_code = bankData?.code ?: ""
            country_code = "ZA"
            account_name = accountName.value ?: ""
            account_number = accountNumber.value ?: ""
            account_type = if (accountType.value!!) "personal" else "personal"
            document_type = documentType.value ?: ""
            document_number = documentNumber.value ?: ""
            type = bankData?.type ?: ""
            name = bankData?.name ?: ""
            currency = bankData?.currency ?: ""
        }
    }


    private fun checkValidation(): Boolean {
        if (accountNumber.value.isNullOrBlank()) {
            errorMessage.postValue(R.string.please_enter_account_number)
            return false
        } else if (accountName.value.isNullOrBlank()) {
            errorMessage.postValue(R.string.please_enter_account_name)
            return false
        } else if (documentNumber.value.isNullOrBlank()) {
            errorMessage.postValue(R.string.please_enter_document_number)
            return false
        } else if (documentType.value.isNullOrBlank()) {
            errorMessage.postValue(R.string.please_enter_document_type)
            return false
        }
        return true
    }


    fun getBalListData(): LiveData<Resource<BankListResposne>> {
        return bankListData
    }

    fun getListRequest() {
        request["currency"] = "ZAR"
        hitBankListApi()
    }


    /*bank list api*/
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
                        StatusCode.STATUS_CODE_INTERNET_VALIDATION, null
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                bankListData.postValue(
                    Resource.error(
                        StatusCode.SERVER_ERROR_MESSAGE, null
                    )
                )
            }
        }
    }

    /*create Bank APi*/


    private val createBankRequest = CommonRequest()

    private val createBankData = SingleLiveEvent<Resource<String>>()


     fun getLiveData(): LiveData<Resource<String>> {
        return createBankData
    }

    fun getCreateBankRequest() {
        Constants.SECURE_HEADER = "secure"
        createBankRequest.servivet_user_req =
            AESHelper.encrypt(Constants.SECURITY_KEY, Gson().toJson(bankAccountRequest))

        hitCreateBankApi()


    }


    private fun hitCreateBankApi() {

        val mainRepository = MainRepository(RetrofitBuilder.apiService)
        viewModelScope.launch {
            createBankData.postValue(Resource.loading(null))
            try {
                createBankData.postValue(
                    Resource.success(mainRepository.createBankApi(createBankRequest))
                )
            } catch (ex: IOException) {
                ex.printStackTrace()
                createBankData.postValue(
                    Resource.error(
                        StatusCode.STATUS_CODE_INTERNET_VALIDATION, null
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                createBankData.postValue(
                    Resource.error(
                        StatusCode.SERVER_ERROR_MESSAGE, null
                    )
                )
            }
        }
    }


}