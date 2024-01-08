package com.example.servivet.ui.main.view_model.sub_category_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.review_ratinng.ReviewRatingResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RatingReviewViewModel(): BaseViewModel(){
    private val request = HashMap<String,String>()

    private val reviewMData = SingleLiveEvent<Resource<ReviewRatingResponse>>()

    fun getReviewData(): LiveData<Resource<ReviewRatingResponse>> {
        return reviewMData
    }


    fun getReviewRequest(id: String) {
        request["page"] = "1"
        request["limit"] = "10"
        request["serviceId"] = "657fea25b55d7af39650d84e"    // testing ID 657fea25b55d7af39650d84e
        hitReviewApi()

    }
    private fun hitReviewApi(){
        val repository = MainRepository(RetrofitBuilder.apiService)

        reviewMData.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                reviewMData.postValue(Resource.success(repository.reviewList(request)))
            }catch (ex: IOException) {
                ex.printStackTrace()
                reviewMData.postValue(Resource.error(StatusCode.STATUS_CODE_INTERNET_VALIDATION, null))
            }catch (exception: Exception) {
                exception.printStackTrace()
                if (exception is HttpException && exception.code() == StatusCode.HTTP_EXCEPTION) {
                    reviewMData.postValue(Resource.error(StatusCode.HTTP_EXCEPTION.toString(), null))
//                    if(!finishing)
//                        CommonUtils.logoutAlert(context, "Session Expired", "Your account has been blocked by Admin . Please contact to the Admin", requireActivity)
                }else
                    reviewMData.postValue(Resource.error(StatusCode.SERVER_ERROR_MESSAGE, null))


            }
        }
    }
}