package com.example.servivet.ui.main.view_model

import android.util.Log
import android.view.View
import android.widget.RatingBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.booking_module.booking_model.request.RatingRequest
import com.example.servivet.data.model.booking_module.booking_model.respnse.RatingResponseMain
import com.example.servivet.data.repository.M2Repository
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.databinding.FragmentRatingUsBottomSheetBinding
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RateUseBottomSheetViewModel(): BaseViewModel(){
    val about= MutableLiveData(false)
    private var abouttext=""

    val ratingRequest = RatingRequest()

    private val ratingMData = SingleLiveEvent<Resource<RatingResponseMain>>()

    fun getRatingData(): LiveData<Resource<RatingResponseMain>> {
        return ratingMData
    }

   /* fun onRatingChanged(ratingBar: RatingBar, rating: Float, fromUser: Boolean) {
        Log.e("TAG", "onRatingChanged: ${rating}", )
    }*/

    inner class ClickAction(var binding: FragmentRatingUsBottomSheetBinding){
        fun onAboutTextChanged(text: CharSequence) {
            binding.wordCount.text = "(" + text.toString().length.toString() + "/100 Words)"
            about.value=text.isNotEmpty()
            abouttext=text.toString().trim()
        }


        fun getRating(ratinng:Float){
            ratingRequest.apply {
                serviceId = "657fea25b55d7af39650d84e"
                rating = ratinng.toInt()
                comment ="Hii"
            }
            Log.e("TAG", "getRating: ", )
            hitRatingApi()
        }
    }

    fun getRatingRequest(){

    }
    fun hitRatingApi(){
        val repository = MainRepository(RetrofitBuilder.apiService)

        ratingMData.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                ratingMData.postValue(Resource.success(repository.ratingApi(ratingRequest)))
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