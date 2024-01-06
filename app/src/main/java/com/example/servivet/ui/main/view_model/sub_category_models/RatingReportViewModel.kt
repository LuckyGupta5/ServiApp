package com.example.servivet.ui.main.view_model.sub_category_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.report_rating.request.ReportRatingRequest
import com.example.servivet.data.model.report_rating.response.CommonResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RatingReportViewModel(): BaseViewModel(){
    private val reportRequest = ReportRatingRequest()

    private val ratingMData = SingleLiveEvent<Resource<CommonResponse>>()

    fun getReportRatingData(): LiveData<Resource<CommonResponse>> {
        return ratingMData
    }


    fun getReportRatingRequest(){
        reportRequest.apply {
            reportRequest.contentId = "658d2320d0303603d1c69a40"
            reportRequest.contentType = "rating"
            reportRequest.userFeedBack = "fraud"
        }
        hitReportRatingApi()

    }
    private fun hitReportRatingApi(){
        val repository = MainRepository(RetrofitBuilder.apiService)

        ratingMData.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                ratingMData.postValue(Resource.success(repository.reportRating(reportRequest)))
            }catch (ex: IOException) {
                ex.printStackTrace()
                ratingMData.postValue(Resource.error(StatusCode.STATUS_CODE_INTERNET_VALIDATION, null))
            }catch (exception: Exception) {
                exception.printStackTrace()
                if (exception is HttpException && exception.code() == StatusCode.HTTP_EXCEPTION) {
                    ratingMData.postValue(Resource.error(StatusCode.HTTP_EXCEPTION.toString(), null))
//                    if(!finishing)
//                        CommonUtils.logoutAlert(context, "Session Expired", "Your account has been blocked by Admin . Please contact to the Admin", requireActivity)
                }else
                    ratingMData.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))


            }
        }
    }
}