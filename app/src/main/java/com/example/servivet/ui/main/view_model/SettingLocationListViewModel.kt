package com.example.servivet.ui.main.view_model

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.save_address.request.SaveAddressRequest
import com.example.servivet.data.model.save_address.response.SaveAddressResponse
import com.example.servivet.data.model.setting.address_list.SettingAddressListResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import java.io.IOException


class SettingLocationListViewModel: BaseViewModel() {
    val addressListResponse = SingleLiveEvent<Resource<SettingAddressListResponse>>()
    var saveAddressResponse= SingleLiveEvent<Resource<SaveAddressResponse>>()
    var saveAddressRequest= SaveAddressRequest()
    inner class ClickAction{
        fun backBtn(view:View){
            view.findNavController().popBackStack()
        }

        fun addAddress(view:View){
            var bundle=Bundle()
            bundle.putString("action","add")
            view.findNavController().navigate(R.id.action_settingLocationListFragment_to_addLocationFragment,bundle)
        }
    }


    fun hitAddressListApi() {
        val mainRepository = MainRepository(RetrofitBuilder.apiService)
        viewModelScope.launch {
            addressListResponse.postValue(Resource.loading(null))
            try {
                addressListResponse.postValue(Resource.success(mainRepository.addressList()))
            } catch (ex: IOException) {
                addressListResponse.postValue(Resource.error(StatusCode.STATUS_CODE_INTERNET_VALIDATION, null))
                Log.e("TAG", "hitAddAgencyAPI: "+ex.message )

            } catch (exception: Exception) {
                addressListResponse.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))
                Log.e("TAG", "hitAddAgencyAPI: "+exception.message )
            }
        }
    }

    fun hitSaveAddressAPI() {
        val mainRepository = MainRepository(RetrofitBuilder.apiService)
        viewModelScope.launch {
            saveAddressResponse.postValue(Resource.loading(null))
            try {
                saveAddressResponse.postValue(Resource.success(mainRepository.saveAddressApi(saveAddressRequest)))
            } catch (ex: IOException) {
                ex.printStackTrace()
                saveAddressResponse.postValue(Resource.error(StatusCode.STATUS_CODE_INTERNET_VALIDATION, null))
            }catch (exception: Exception) {
                exception.printStackTrace()
                saveAddressResponse.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))
            }
        }
    }
}