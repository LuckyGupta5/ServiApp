package com.example.servivet.ui.main.view_model

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.add_service.request.AddServiceRequest
import com.example.servivet.data.model.add_service.request.AtHomeAvailability
import com.example.servivet.data.model.add_service.request.ServiceListSlot
import com.example.servivet.data.model.add_service.response.AddServiceResponse
import com.example.servivet.data.repository.MainRepository
import com.example.servivet.databinding.FragmentAddServiceBinding

import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.Resource
import com.example.servivet.utils.SingleLiveEvent
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class AddServiceViewModel() : BaseViewModel() {
    var errorMessage = SingleLiveEvent<String>()
    var subCatPostion: Int = 0
    var catPostion: Int = 0
    var isPhotoSelected: Boolean = false
    var isClick = false
    var isHomeClick = false
    var isCentreClick = false
    var isOnlineClick = false
    val name = MutableLiveData(false)
    val aboutService = MutableLiveData(false)
    var addServicesRequest = AddServiceRequest()
    var addServiceResponse = SingleLiveEvent<Resource<AddServiceResponse>>()


    inner class ClickAction(
        var context: Context,
        var binding: FragmentAddServiceBinding,
        var requireActivity: FragmentActivity,
        var finishing: Boolean
    ) {
        fun backBtn(view: View) {
            view.findNavController().popBackStack()
        }

        fun addService(view: View) {
            if (validation(context, requireActivity, finishing))
                hitAddServiceAPI(context, requireActivity, finishing)
        }

        fun onNameChange(text: CharSequence) {
            name.value = text.isNotEmpty()
            addServicesRequest.serviceName = text.toString().trim().replace("\\s+".toRegex(), " ")
        }

        fun aboutTextChange(text: CharSequence) {
            aboutService.value = text.isNotEmpty()
            addServicesRequest.aboutService = text.toString().trim().replace("\\s+".toRegex(), " ")
            binding.wordCount.text = "(" + text.toString().length.toString() + "/150)"

        }
    }

    private fun validation(
        context: Context,
        requireActivity: FragmentActivity,
        finishing: Boolean
    ): Boolean {
        return if (!isHomeClick && !isCentreClick) {
            errorMessage.setValue(context.getString(R.string.please_select_a_service_model))
            false
        } else if (catPostion == 0) {
            errorMessage.setValue(context.getString(R.string.please_select_the_category))
            false
        } else if (subCatPostion == 0) {
            errorMessage.setValue(context.getString(R.string.please_select_sub_category))
            false
        } else if (addServicesRequest.atCenterAvailability != null || addServicesRequest.atHomeAvailability != null) {
            var isErrorFound = false
            if (addServicesRequest.atCenterAvailability != null) {
                for (i in addServicesRequest.atCenterAvailability!!.indices) {
                    for (j in addServicesRequest.atCenterAvailability!![i].slot!!.indices) {
                        if (addServicesRequest.atCenterAvailability!![i].slot!![j].startTime!!.isEmpty() || addServicesRequest.atCenterAvailability!![i].slot!![j].endTime!!.isEmpty()) {
                            errorMessage.setValue("Select Session should not be empty for the Center Service Mode")
                            isErrorFound = true
                        } else if (!CommonUtils.checkDates(
                                addServicesRequest.atCenterAvailability!![i].slot!![j].startTime!!,
                                addServicesRequest.atCenterAvailability!![i].slot!![j].endTime!!
                            )
                        ) {
                            errorMessage.setValue(context.getString(R.string.start_time_should_not_be_greater_than_end_time_for_centre_mode))
                            isErrorFound = true
                        }

                    }
                }

            }
            if (addServicesRequest.atHomeAvailability != null) {
                for (i in addServicesRequest.atHomeAvailability!!.indices) {
                    for (j in addServicesRequest.atHomeAvailability!![i].slot!!.indices) {
                        if (addServicesRequest.atHomeAvailability!![i].slot!![j].startTime!!.isEmpty() || addServicesRequest.atHomeAvailability!![i].slot!![j].endTime!!.isEmpty()) {
                            errorMessage.setValue("Select Session should not be empty for the Home Service Mode")
                            isErrorFound = true
                        } else if (!CommonUtils.checkDates(
                                addServicesRequest.atHomeAvailability!![i].slot!![j].startTime!!,
                                addServicesRequest.atHomeAvailability!![i].slot!![j].endTime!!
                            )
                        ) {
                            errorMessage.setValue(context.getString(R.string.start_time_should_not_be_greater_than_end_time_for_home_mode))
                            isErrorFound = true
                        }

                    }
                }
            }

            if (isCentreClick && addServicesRequest.atCenterPrice == "0") {
                errorMessage.setValue(context.getString(R.string.please_enter_the_price_for_the_centre_service_mode))
                false
            } else if (isHomeClick && addServicesRequest.atHomePrice == "0") {
                errorMessage.setValue(context.getString(R.string.please_enter_the_price_for_the_home_service_mode))
                false
            } else if (addServicesRequest.image?.size!! == 0) {
                errorMessage.setValue(context.getString(R.string.please_select_at_one_image))
                false
            } else if (addServicesRequest.image != null && addServicesRequest.image?.size!! > 5) {
                errorMessage.setValue(context.getString(R.string.images_must_be_less_than_five))

            } else if (!isErrorFound)
                hitAddServiceAPI(context, requireActivity, finishing)
            false
        } /*else if (isCentreClick && addServicesRequest.atCenterPrice == "0") {
            errorMessage.setValue(context.getString(R.string.please_enter_the_price_for_the_centre_service_mode))
            false
        } else if (isHomeClick && addServicesRequest.atHomePrice == "0") {
            errorMessage.setValue(context.getString(R.string.please_enter_the_price_for_the_home_service_mode))
            false
        } else  if (isPhotoSelected==false) {
            errorMessage.setValue(context.getString(R.string.please_select_at_one_image))
            false
        }else if (addServicesRequest.image != null) {
            if (addServicesRequest.image?.size!! > 5) {
                errorMessage.setValue(context.getString(R.string.images_must_be_less_than_five))
                false
            } else
                true
        } */ else
            true
    }


    @SuppressLint("SuspiciousIndentation")
    fun hitAddServiceAPI(context: Context, requireActivity: FragmentActivity, finishing: Boolean) {
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        val repo = MainRepository(RetrofitBuilder.apiService)


        try {
            if (addServicesRequest.atCenter != null)
                builder.addFormDataPart("atCenter", addServicesRequest.atCenter.toString())
            else
                builder.addFormDataPart("atCenter", false.toString())

            if (addServicesRequest.atHome != null)
                builder.addFormDataPart("atHome", addServicesRequest.atHome.toString())
            else {
                addServicesRequest.atHome = false
                builder.addFormDataPart("atHome", addServicesRequest.atHome.toString())
            }

            builder.addFormDataPart("category", addServicesRequest.category!!)
            builder.addFormDataPart("serviceName", addServicesRequest.serviceName!!)
            builder.addFormDataPart("subCategory", addServicesRequest.subCategory!!)
            builder.addFormDataPart("aboutService", addServicesRequest.aboutService!!)
            if (addServicesRequest.atCenterPrice != null)
                builder.addFormDataPart("atCenterPrice", addServicesRequest.atCenterPrice!!)
            else
                builder.addFormDataPart("atCenterPrice", "0")

            if (addServicesRequest.atHomePrice != null)
                builder.addFormDataPart("atHomePrice", addServicesRequest.atHomePrice!!)
            else
                builder.addFormDataPart("atHomePrice", "0")


//            if (addServicesRequest.onlinePrice != null)
//                builder.addFormDataPart("onlinePrice", addServicesRequest.onlinePrice!!)
//            else
//                builder.addFormDataPart("onlinePrice", "0")

            if (isCentreClick) {
                builder.addFormDataPart("address", addServicesRequest.address!!)
                builder.addFormDataPart("latitute", addServicesRequest.latitute!!)
                builder.addFormDataPart("longitute", addServicesRequest.longitute!!)
            }


            if (addServicesRequest.atCenterAvailability != null && addServicesRequest.atCenterAvailability!!.isNotEmpty()) {
                builder.addFormDataPart(
                    "atCenterAvailability",
                    Gson().toJson(addServicesRequest.atCenterAvailability).replace("\\", "")
                )
            }



            if (addServicesRequest.atHomeAvailability != null && addServicesRequest.atHomeAvailability!!.isNotEmpty()) {

                builder.addFormDataPart(
                    "atHomeAvailability",
                    Gson().toJson(addServicesRequest.atHomeAvailability).replace("\\", "")
                )

            } else {
                val atHomeAvailabilityList: List<AtHomeAvailability> =
                    addServicesRequest.atHomeAvailability ?: emptyList()

                builder.addFormDataPart("atHomeAvailability", Gson().toJson(atHomeAvailabilityList))
            }





            if (isPhotoSelected) {
                for (i in addServicesRequest.image!!.indices) {
                    val file = File(addServicesRequest.image!![i])
                    builder.addFormDataPart(
                        "image",
                        file.name,
                        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                    )
                }

            }
            Log.e("TAG", "hitAddServiceAPI: " + Gson().toJson(addServicesRequest))

            addServiceResponse.postValue(Resource.loading(null))
            viewModelScope.launch {
                try {
                    addServiceResponse.postValue(Resource.success(repo.addServicesApi(builder.build())))
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    Log.d("exception", "" + ex)
                    addServiceResponse.postValue(
                        Resource.error(
                            StatusCode.STATUS_CODE_INTERNET_VALIDATION,
                            null
                        )
                    )
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    if (exception is HttpException && exception.code() == 401) {
                        if (!finishing)
                            CommonUtils.logoutAlert(
                                context,
                                "Session Expired",
                                "Your account has been blocked by Admin . Please contact to the Admin",
                                requireActivity
                            )
                    } else
                        addServiceResponse.postValue(
                            Resource.error(
                                StatusCode.SERVER_ERROR_MESSAGE,
                                null
                            )
                        )


                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }


    }
}