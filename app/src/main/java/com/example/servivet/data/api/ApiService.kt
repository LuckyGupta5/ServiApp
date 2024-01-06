package com.example.servivet.data.api

import com.example.servivet.data.model.add_service.response.AddServiceResponse
import com.example.servivet.data.model.booking_module.booking_model.request.RatingRequest
import com.example.servivet.data.model.booking_module.booking_model.respnse.RatingResponseMain
import com.example.servivet.data.model.booking_module.booking_slot.BookingSlotResponseMain
import com.example.servivet.data.model.booking_module.booking_summary.response.BookingSummaryResponse
import com.example.servivet.data.model.booking_module.coupon.request.CouponAvalabilityRequest
import com.example.servivet.data.model.booking_module.coupon.response.CouponResponseMain
import com.example.servivet.data.model.business_verification_api.request.BusinessVerificationRequest
import com.example.servivet.data.model.business_verification_api.response.BusinessVerificationResponse
import com.example.servivet.data.model.current_api.response.CurrentResponse
import com.example.servivet.data.model.edit_profile.response.EditProfileResponse
import com.example.servivet.data.model.home.response.HomeResponse
import com.example.servivet.data.model.report_rating.request.ReportRatingRequest
import com.example.servivet.data.model.report_rating.response.CommonResponse
import com.example.servivet.data.model.review_ratinng.ReviewRatingResponse
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
    suspend fun ratingApi(@Body rating: RatingRequest): RatingResponseMain
    @POST("report")
    suspend fun reportRating(@Body ratingReport:ReportRatingRequest):CommonResponse
    @GET("rating/list")
    suspend fun reviewList(@QueryMap request:HashMap<String,String> ): ReviewRatingResponse

    @GET("booking/summary")
    suspend fun bookingSummaryApi(@QueryMap request:HashMap<String,String> ): BookingSummaryResponse

    @GET("booking/slot/list")
    suspend fun bookingSlotApi(@QueryMap request:HashMap<String,String> ): BookingSlotResponseMain
    @GET("coupon/list")
    suspend fun bookingCouponApi(@QueryMap request:HashMap<String,String> ): CouponResponseMain

    @POST("booking/slot/availability")
    suspend fun bookingSlotAvailabilityApi(@Body request:CouponAvalabilityRequest ): CommonResponse





}