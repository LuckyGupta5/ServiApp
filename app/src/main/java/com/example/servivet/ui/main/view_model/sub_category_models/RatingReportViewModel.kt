package com.example.servivet.ui.main.view_model.sub_category_models

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.servivet.R
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.report_rating.request.ReportRatingRequest
import com.example.servivet.data.model.common.response.CommonResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RatingReportViewModel(): BaseViewModel(){
     val reportRequest = ReportRatingRequest()
     val ratingMData = SingleLiveEvent<Resource<CommonResponse>>()
    val report = MutableLiveData(false)
    var errorMessage = SingleLiveEvent<String>()

    fun getReportRatingData(): LiveData<Resource<CommonResponse>> {
        return ratingMData
    }


    fun getReportRatingRequest(id: String) {
        reportRequest.apply {
            reportRequest.contentId = id
            reportRequest.contentType = "rating"
        }



    }
   /* private fun validation(context: Context): Boolean {
        return if (reportRequest.userFeedBack!!.isEmpty()) {
            errorMessage.setValue("Please enter the feedback")
            false
        } else
            true

    }*/
     fun hitReportRatingApi(){
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
                ratingMData.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))
            }
        }
    }

    inner class ClickAction{
        fun onNameChange(text: CharSequence) {
            report.value = text.isNotEmpty()
            reportRequest.userFeedBack = text.toString().trim().replace("\\s+".toRegex(), " ")
        }

        fun submitBtn(view: View){

        }
    }
}