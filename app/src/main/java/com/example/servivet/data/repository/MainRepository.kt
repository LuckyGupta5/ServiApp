package com.example.servivet.data.repository

import com.example.servivet.data.api.ApiService
import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.booking_model.request.RatingRequest
import com.example.servivet.data.model.business_verification_api.request.BusinessVerificationRequest
import com.example.servivet.data.model.send_otp.request.SendOtpRequest
import com.example.servivet.data.model.service_category_details.request.ServiceCategoryDetailsRequest
import com.example.servivet.data.model.service_list.request.ServiceListRequest
import com.example.servivet.data.model.verifyotp.request.VerifyOtpRequest
import okhttp3.MultipartBody

class MainRepository(private val apiService: ApiService) {
    suspend fun sendOtpApi(sendOtpRequest: SendOtpRequest) = apiService.sendOtp(sendOtpRequest)
    suspend fun verifyOtpAPi(verifyOtpRequest: VerifyOtpRequest)=apiService.verifyOtp(verifyOtpRequest)
    suspend fun homeApi() = apiService.home()
    suspend fun currentApi()=apiService.current()

    suspend fun addServicesApi(addServicesRequest: MultipartBody) =
        apiService.addServices(addServicesRequest)
    suspend fun editServicesApi(addServicesRequest: MultipartBody) =
        apiService.editServices(addServicesRequest)
    suspend fun userProfile(request: HashMap<String, String>) =
        apiService.userProfile(request)

    suspend fun editProfile(editProfileRequest: MultipartBody) =
        apiService.editProfile(editProfileRequest)

    suspend fun serviceListApi(serviceListRequest: ServiceListRequest) =
        apiService.serviceList(serviceListRequest)

    suspend fun serviceDetailsApi(serviceCategoryDetailsRequest: ServiceCategoryDetailsRequest)=
        apiService.servicedetailsapi(serviceCategoryDetailsRequest)

    suspend fun businessVerificationApi(businessVerificationRequest: BusinessVerificationRequest)=
        apiService.businessVerificationApi(businessVerificationRequest)

    suspend fun ratingApi(request: RatingRequest) = apiService.ratingApi(request)



}
