package com.example.servivet.data.api

import com.example.servivet.data.model.accept_booking.request.AcceptBookingRequest
import com.example.servivet.data.model.accept_booking.response.AcceptBookingResponse
import com.example.servivet.data.model.add_service.response.AddServiceResponse
import com.example.servivet.data.model.bank_module.bank_list_response.response.BankListResposne
import com.example.servivet.data.model.bank_module.create_bank_account_list.response.CreateBankListResponse
import com.example.servivet.data.model.bank_module.remove_bank_accont.RemoveBankAccountRequest
import com.example.servivet.data.model.booking_detail.request.BookingDetailRequest
import com.example.servivet.data.model.booking_detail.response.BookingDetailResponse
import com.example.servivet.data.model.booking_list.response.BookingListResponse
import com.example.servivet.data.model.booking_module.booking_model.request.RatingRequest
import com.example.servivet.data.model.booking_module.booking_model.respnse.RatingResponseMain
import com.example.servivet.data.model.booking_module.booking_slot.BookingSlotResponseMain
import com.example.servivet.data.model.booking_module.booking_summary.response.BookingSummaryResponse
import com.example.servivet.data.model.booking_module.coupon.response.CouponResponseMain
import com.example.servivet.data.model.booking_module.mark_as_complete.MarkAsCompleteRequest
import com.example.servivet.data.model.booking_module.provider_leave.ProviderLeaveRequest
import com.example.servivet.data.model.booking_module.reschedule_booking.request.RescheduleBookingRequest
import com.example.servivet.data.model.business_verification_api.request.BusinessVerificationRequest
import com.example.servivet.data.model.business_verification_api.response.BusinessVerificationResponse
import com.example.servivet.data.model.cancel_booking.request.CancelBookingRequest
import com.example.servivet.data.model.cancel_booking.response.CancelBookingResponse
import com.example.servivet.data.model.chat_models.chat_media.ChatMediaResponse
import com.example.servivet.data.model.common.request.CommonRequest
import com.example.servivet.data.model.common.response.CommonResponse
import com.example.servivet.data.model.connection.accept_reject.request.AcceptRejectRequest
import com.example.servivet.data.model.connection.connection_list.responnse.ConnectionListResponse
import com.example.servivet.data.model.connection.connection_request.request.ConnectionRequest
import com.example.servivet.data.model.connection.connection_request.response.ConnnectionResponse
import com.example.servivet.data.model.current_api.response.CurrentResponse
import com.example.servivet.data.model.edit_profile.response.EditProfileResponse
import com.example.servivet.data.model.home.response.HomeResponse
import com.example.servivet.data.model.notification_list.response.NotificationListResponse
import com.example.servivet.data.model.report_rating.request.ReportRatingRequest
import com.example.servivet.data.model.review_ratinng.ReviewRatingResponse
import com.example.servivet.data.model.save_address.request.SaveAddressRequest
import com.example.servivet.data.model.save_address.response.SaveAddressResponse
import com.example.servivet.data.model.send_otp.request.SendOtpRequest
import com.example.servivet.data.model.send_otp.response.SendOtpResponse
import com.example.servivet.data.model.service_category_details.request.ServiceCategoryDetailsRequest
import com.example.servivet.data.model.service_category_details.response.ServiceCategoryDetailsResponse
import com.example.servivet.data.model.service_list.ServiceListResponse
import com.example.servivet.data.model.service_list.request.ServiceListRequest
import com.example.servivet.data.model.setting.address_list.SettingAddressListResponse
import com.example.servivet.data.model.setting.cms.response.CmsResponse
import com.example.servivet.data.model.setting.contact.request.ContactUsRequest
import com.example.servivet.data.model.setting.faq_list.response.FaqListResponse
import com.example.servivet.data.model.setting.faq_type_list.response.FaqTypeListResponse
import com.example.servivet.data.model.setting.notification.request.NotificationRequest
import com.example.servivet.data.model.setting_module.request.ChangeRoleRequest
import com.example.servivet.data.model.user_profile.response.UserProfileResponse
import com.example.servivet.data.model.verifyotp.request.VerifyOtpRequest
import com.example.servivet.data.model.verifyotp.response.VerifyOTPResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
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

    @POST("uploadChatFile")
    suspend fun uploadChatFileApi(@Body addServicesRequest: MultipartBody): ChatMediaResponse

    @POST("editService")
    suspend fun editServices(@Body addServicesRequest: MultipartBody): AddServiceResponse

    @GET("profile")
    suspend fun userProfile(@QueryMap request: HashMap<String, String>): UserProfileResponse

    @POST("editProfile")
    suspend fun editProfile(@Body editProfileRequest: MultipartBody): EditProfileResponse

    @POST("listService")
    suspend fun serviceList(@Body serviceListRequest: ServiceListRequest): ServiceListResponse

    @POST("serviceDetail")
    suspend fun servicedetailsapi(@Body serviceCategoryDetailsRequest: ServiceCategoryDetailsRequest): ServiceCategoryDetailsResponse

    @POST("profileVerification")
    suspend fun businessVerificationApi(@Body businessVerificationRequest: BusinessVerificationRequest): BusinessVerificationResponse

    // booking module
    @PUT("add/rating")
    suspend fun ratingApi(@Body rating: RatingRequest): RatingResponseMain

    @POST("report")
    suspend fun reportRating(@Body ratingReport: ReportRatingRequest): CommonResponse

    @GET("rating/list")
    suspend fun reviewList(@QueryMap request: HashMap<String, String>): ReviewRatingResponse

    @GET("booking/summary")
    suspend fun bookingSummaryApi(@QueryMap request: HashMap<String, String>): BookingSummaryResponse

    @GET("booking/slot/list")
    suspend fun bookingSlotApi(@QueryMap request: HashMap<String, String>): BookingSlotResponseMain

    @GET("coupon/list")
    suspend fun bookingCouponApi(@QueryMap request: HashMap<String, String>): CouponResponseMain


    @POST("buy/payment/amount")
    suspend fun paymentAmountApi(@Body request: CommonRequest): String

    @POST("booking/slot/availability")
    suspend fun bookingSlotAvailabilityApi(@Body request: CommonRequest): String

    @POST("saveAddress")
    suspend fun saveAddress(@Body saveAddressRequest: SaveAddressRequest): SaveAddressResponse

    @GET("mybooking")
    suspend fun bookingList(@QueryMap request: HashMap<String, String>): BookingListResponse

    @GET("mysoldbooking")
    suspend fun mysoldbooking(@QueryMap request: HashMap<String, String>): BookingListResponse

    @POST("cancel/booking")
    suspend fun cancelBooking(@Body cancelBookingRequest: CancelBookingRequest): CancelBookingResponse

    @POST("accept/booking")
    suspend fun acceptBooking(@Body acceptBookingRequest: AcceptBookingRequest): AcceptBookingResponse

    @POST("booking/detail")
    suspend fun bookingDetail(@Body bookingDetailRequest: BookingDetailRequest): BookingDetailResponse

    @GET("my/wallet")
    suspend fun myWalletApi(): String

    @POST("wallet/transaction/histroy")
    suspend fun walletTransactionApi(@Body request: CommonRequest): String

    @POST("order/create")
    suspend fun createOrderApi(@Body request: CommonRequest): String

    @POST("reschedule/booking")
    suspend fun rescheduleBookingApi(@Body request: RescheduleBookingRequest): CommonResponse

    @POST("markascomplete/booking")
    suspend fun markAsCompleteApi(@Body request: MarkAsCompleteRequest): CommonResponse

    @POST("provider/leave")
    suspend fun providerLeaveApi(@Body request: ProviderLeaveRequest): CommonResponse

    @POST("logout")
    suspend fun logoutUser(): CommonResponse

    @GET("my/connection")
    suspend fun connectionListApi(@QueryMap request: HashMap<String, String>): ConnectionListResponse

    @GET("connection/request/list")
    suspend fun connectionRequestListApi(@QueryMap request: HashMap<String, Int>): ConnectionListResponse

    @POST("accept/reject/connection")
    suspend fun acceptRejectApi(@Body request: AcceptRejectRequest): CommonResponse

    @POST("connection/request")
    suspend fun connectionRequest(@Body request: ConnectionRequest): ConnnectionResponse

    //setting
    @GET("setting/cmsData")
    suspend fun cmsData(@Query("type") type: String): CmsResponse

    @POST("setting/notificationStatus")
    suspend fun notificationStatus(@Body request: NotificationRequest): CommonResponse

    @POST("addContactUs")
    suspend fun addContactUs(@Body request: ContactUsRequest): CommonResponse

    @GET("address/list")
    suspend fun addressList(): SettingAddressListResponse

    @GET("faq/faqList")
    suspend fun faqList(@Query("faqTypeId") faqTypeId: String): FaqListResponse

    @GET("faq/faqTypeList")
    suspend fun faqTypeList(): FaqTypeListResponse

    @POST("setting/deleteAccount")
    suspend fun deleteAccount(): CommonResponse

    @POST("setting/changeRole")
    suspend fun changeRoleApi(@Body request: ChangeRoleRequest): CommonResponse

    @GET("notification/notificationListing")
    suspend fun notificationListing(@QueryMap request: HashMap<String, Int>): NotificationListResponse

    @GET("provider/bank/list")
    suspend fun bankListApi(@QueryMap request: HashMap<String, String>): BankListResposne

    @GET("provider/saved/account/list")
    suspend fun createBankListApi(): CreateBankListResponse

    @POST("provider/bank/createAccount")
    suspend fun createBankApi(@Body request: CommonRequest): String

    @POST("provider/bank/remove")
    suspend fun removeBankAccount(@Body request: RemoveBankAccountRequest): String
}