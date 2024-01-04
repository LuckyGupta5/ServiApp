package com.example.servivet.data.api

import com.example.servivet.data.model.add_service.response.AddServiceResponse
import com.example.servivet.data.model.booking_model.request.RatingRequest
import com.example.servivet.data.model.booking_model.respnse.RatingResponseMain
import com.example.servivet.data.model.business_verification_api.request.BusinessVerificationRequest
import com.example.servivet.data.model.business_verification_api.response.BusinessVerificationResponse
import com.example.servivet.data.model.current_api.response.CurrentResponse
import com.example.servivet.data.model.edit_profile.response.EditProfileResponse
import com.example.servivet.data.model.home.response.HomeResponse
import com.example.servivet.data.model.send_otp.request.SendOtpRequest
import com.example.servivet.data.model.send_otp.response.SendOtpResponse
import com.example.servivet.data.model.service_category_details.request.ServiceCategoryDetailsRequest
import com.example.servivet.data.model.service_category_details.response.ServiceCategoryDetailsResponse
import com.example.servivet.data.model.service_list.ServiceListResponse
import com.example.servivet.data.model.service_list.request.ServiceListRequest
import com.example.servivet.data.model.user_profile.response.UserProfileResponse
import com.example.servivet.data.model.verifyotp.request.VerifyOtpRequest
import com.example.servivet.data.model.verifyotp.response.VerifyOTPResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.QueryMap


interface ApiService {

    @POST("sendOtp")
    suspend fun sendOtp(@Body sendOtpRequest: SendOtpRequest): SendOtpResponse

    @POST("verifyOTP")
    suspend fun verifyOtp(@Body verifyOtpRequest: VerifyOtpRequest): VerifyOTPResponse

    @POST("home")
    suspend fun home(): HomeResponse

    @GET("current")
    suspend fun current(): CurrentResponse


    @POST("addService")
    suspend fun addServices(@Body addServicesRequest: MultipartBody): AddServiceResponse

    @POST("editService")
    suspend fun editServices(@Body addServicesRequest: MultipartBody): AddServiceResponse

    @GET("profile")
    suspend fun userProfile(@QueryMap request: HashMap<String, String>):UserProfileResponse

    @POST("editProfile")
    suspend fun editProfile(@Body editProfileRequest: MultipartBody): EditProfileResponse

   @POST("listService")
    suspend fun serviceList(@Body serviceListRequest: ServiceListRequest): ServiceListResponse

   @POST("serviceDetail")
    suspend fun servicedetailsapi(@Body serviceCategoryDetailsRequest: ServiceCategoryDetailsRequest): ServiceCategoryDetailsResponse

   @POST("profileVerification")
    suspend fun  businessVerificationApi(@Body businessVerificationRequest: BusinessVerificationRequest): BusinessVerificationResponse

    @PUT("add/rating")
    suspend fun ratingApi(@Body rating:RatingRequest):RatingResponseMain



}