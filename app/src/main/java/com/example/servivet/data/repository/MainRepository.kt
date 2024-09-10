package com.example.servivet.data.repository

import com.example.servivet.data.api.ApiService
import com.example.servivet.data.model.accept_booking.request.AcceptBookingRequest
import com.example.servivet.data.model.bank_module.remove_bank_accont.RemoveBankAccountRequest
import com.example.servivet.data.model.booking_detail.request.BookingDetailRequest
import com.example.servivet.data.model.booking_module.booking_model.request.RatingRequest
import com.example.servivet.data.model.booking_module.coupon.request.CouponAvalabilityRequest
import com.example.servivet.data.model.booking_module.mark_as_complete.MarkAsCompleteRequest
import com.example.servivet.data.model.booking_module.provider_leave.ProviderLeaveRequest
import com.example.servivet.data.model.booking_module.reschedule_booking.request.RescheduleBookingRequest
import com.example.servivet.data.model.business_verification_api.request.BusinessVerificationRequest
import com.example.servivet.data.model.cancel_booking.request.CancelBookingRequest

import com.example.servivet.data.model.common.request.CommonRequest
import com.example.servivet.data.model.connection.accept_reject.request.AcceptRejectRequest
import com.example.servivet.data.model.connection.connection_request.request.ConnectionRequest

import com.example.servivet.data.model.report_rating.request.ReportRatingRequest
import com.example.servivet.data.model.save_address.request.SaveAddressRequest
import com.example.servivet.data.model.send_otp.request.SendOtpRequest
import com.example.servivet.data.model.service_category_details.request.ServiceCategoryDetailsRequest
import com.example.servivet.data.model.service_list.request.ServiceListRequest
import com.example.servivet.data.model.setting.contact.request.ContactUsRequest
import com.example.servivet.data.model.setting.notification.request.NotificationRequest
import com.example.servivet.data.model.setting_module.request.ChangeRoleRequest
import com.example.servivet.data.model.verifyotp.request.VerifyOtpRequest
import okhttp3.MultipartBody

class MainRepository(private val apiService: ApiService) {
    suspend fun sendOtpApi(sendOtpRequest: SendOtpRequest) = apiService.sendOtp(sendOtpRequest)
    suspend fun verifyOtpAPi(verifyOtpRequest: VerifyOtpRequest) =
        apiService.verifyOtp(verifyOtpRequest)

    suspend fun homeApi() = apiService.home()
    suspend fun currentApi() = apiService.current()
    suspend fun addServicesApi(addServicesRequest: MultipartBody) =
        apiService.addServices(addServicesRequest)
    suspend fun uploadChatFileApi(request: MultipartBody) = apiService.uploadChatFileApi(request)
    suspend fun editServicesApi(addServicesRequest: MultipartBody) =
        apiService.editServices(addServicesRequest)
    suspend fun userProfile(request: HashMap<String, String>) = apiService.userProfile(request)

    suspend fun editProfile(editProfileRequest: MultipartBody) =
        apiService.editProfile(editProfileRequest)


    suspend fun serviceListApi(serviceListRequest: ServiceListRequest) =
        apiService.serviceList(serviceListRequest)

    suspend fun serviceDetailsApi(serviceCategoryDetailsRequest: ServiceCategoryDetailsRequest) =
        apiService.servicedetailsapi(serviceCategoryDetailsRequest)

    suspend fun businessVerificationApi(businessVerificationRequest: BusinessVerificationRequest) =
        apiService.businessVerificationApi(businessVerificationRequest)

    suspend fun ratingApi(request: RatingRequest) = apiService.ratingApi(request)
    suspend fun reportRating(request: ReportRatingRequest) = apiService.reportRating(request)
    suspend fun reviewList(request: HashMap<String, String>) = apiService.reviewList(request)
    suspend fun bookingSummaryApi(request: HashMap<String, String>) =
        apiService.bookingSummaryApi(request)

    suspend fun bookingSlotApi(request: HashMap<String, String>) =
        apiService.bookingSlotApi(request)

    suspend fun bookingCouponApi(request: HashMap<String, String>) =
        apiService.bookingCouponApi(request)

    suspend fun bookingSlotAvailabilityApi(request: CommonRequest) =
        apiService.bookingSlotAvailabilityApi(request)

    suspend fun paymentAmountApi(request: CommonRequest) = apiService.paymentAmountApi(request)
    suspend fun walletTransactionApi(request: CommonRequest) =
        apiService.walletTransactionApi(request)

    suspend fun saveAddressApi(saveAddressRequest: SaveAddressRequest) =
        apiService.saveAddress(saveAddressRequest)

    suspend fun bookingListApi(request: HashMap<String, String>) = apiService.bookingList(request)
    suspend fun mysoldbookingListApi(request: HashMap<String, String>) =
        apiService.mysoldbooking(request)

    suspend fun cancelBooking(cancelBookingRequest: CancelBookingRequest) =
        apiService.cancelBooking(cancelBookingRequest)

    suspend fun acceptBookingApi(acceptBookingRequest: AcceptBookingRequest) =
        apiService.acceptBooking(acceptBookingRequest)

    suspend fun bookingDetailApi(bookingDetailRequest: BookingDetailRequest) =
        apiService.bookingDetail(bookingDetailRequest)

    suspend fun myWalletApi() = apiService.myWalletApi()
    suspend fun createOrderApi(request: CommonRequest) = apiService.createOrderApi(request)
    suspend fun rescheduleBookingApi(request: RescheduleBookingRequest) =
        apiService.rescheduleBookingApi(request)

    suspend fun markAsCompleteApi(request: MarkAsCompleteRequest) =
        apiService.markAsCompleteApi(request)

    suspend fun providerLeaveApi(request: ProviderLeaveRequest) =
        apiService.providerLeaveApi(request)

    suspend fun logoutUser() = apiService.logoutUser()
    suspend fun connectionListApi(request: HashMap<String, String>) =
        apiService.connectionListApi(request)

    suspend fun connectionRequestListApi(request: HashMap<String, Int>) =
        apiService.connectionRequestListApi(request)

    suspend fun acceptRejectApi(request: AcceptRejectRequest) = apiService.acceptRejectApi(request)
    suspend fun connectionRequest(request: ConnectionRequest) =
        apiService.connectionRequest(request)


    //setting
    suspend fun cmsApi(type: String) = apiService.cmsData(type)
    suspend fun notificationStatusApi(request: NotificationRequest) =
        apiService.notificationStatus(request)

    suspend fun contactUsApi(request: ContactUsRequest) = apiService.addContactUs(request)
    suspend fun addressList() = apiService.addressList()
    suspend fun faqListApi(faqTypeId: String) = apiService.faqList(faqTypeId)
    suspend fun faqTypeListApi() = apiService.faqTypeList()
    suspend fun deleteAccountApi() = apiService.deleteAccount()
    suspend fun changeRoleApi(request: ChangeRoleRequest) = apiService.changeRoleApi(request)
    suspend fun notificationListing(request: HashMap<String, Int>) =
        apiService.notificationListing(request)

    suspend fun bankListApi(request: HashMap<String, String>) = apiService.bankListApi(request)
    suspend fun createBankListApi() = apiService.createBankListApi()
    suspend fun createBankApi(request: CommonRequest) = apiService.createBankApi(request)
    suspend fun removeBankAccount(request: RemoveBankAccountRequest) =
        apiService.removeBankAccount(request)
}
