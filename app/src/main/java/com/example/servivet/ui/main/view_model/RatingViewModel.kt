package com.example.servivet.ui.main.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.servivet.data.model.booking_module.booking_model.request.RatingRequest
import com.example.servivet.data.model.booking_module.booking_model.respnse.RatingResponseMain
import com.example.servivet.data.repository.M2Repository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import com.example.servivet.utils.StatusCode.HTTP_EXCEPTION
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RatingViewModel(private val repository: M2Repository):BaseViewModel() {

    val ratingRequest = RatingRequest()

    private val ratingMData = SingleLiveEvent<Resource<RatingResponseMain>>()

    fun getRatingData():LiveData<Resource<RatingResponseMain>>{
        return ratingMData
    }

    fun hitRatingApi(){
        ratingMData.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                ratingMData.postValue(Resource.success(repository.ratingApi(ratingRequest)))
            }catch (ex: IOException) {
                ex.printStackTrace()
                ratingMData.postValue(Resource.error(StatusCode.STATUS_CODE_INTERNET_VALIDATION, null))
            }catch (exception: Exception) {
                exception.printStackTrace()
                if (exception is HttpException && exception.code() == HTTP_EXCEPTION) {
                    ratingMData.postValue(Resource.error(HTTP_EXCEPTION.toString(), null))

//                    if(!finishing)
//                        CommonUtils.logoutAlert(context, "Session Expired", "Your account has been blocked by Admin . Please contact to the Admin", requireActivity)
                }else
                    ratingMData.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))


            }
        }
    }




}