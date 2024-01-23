package com.example.servivet.data.repository

import com.example.servivet.data.api.ApiService
import com.example.servivet.data.model.accept_booking.request.AcceptBookingRequest
import com.example.servivet.data.model.booking_detail.request.BookingDetailRequest
import com.example.servivet.data.model.booking_module.booking_model.request.RatingRequest
import com.example.servivet.data.model.booking_module.coupon.request.CouponAvalabilityRequest
import com.example.servivet.data.model.booking_module.mark_as_complete.MarkAsCompleteRequest
import com.example.servivet.data.model.booking_module.reschedule_booking.request.RescheduleBookingRequest
import com.example.servivet.data.model.business_verification_api.request.BusinessVerificationRequest
import com.example.servivet.data.model.cancel_booking.request.CancelBookingRequest

import com.example.servivet.data.model.common.request.CommonRequest

import com.example.servivet.data.model.report_rating.request.ReportRatingRequest
import com.example.servivet.data.model.save_address.request.SaveAddressRequest
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
    suspend fun reportRating(request: ReportRatingRequest) = apiService.reportRating(request)
    suspend fun reviewList(request:HashMap<String,String>) = apiService.reviewList(request)
    suspend fun bookingSummaryApi(request:HashMap<String,String>) = apiService.bookingSummaryApi(request)
    suspend fun bookingSlotApi(request:HashMap<String,String>) = apiService.bookingSlotApi(request)
    suspend fun bookingCouponApi(request:HashMap<String,String>) = apiService.bookingCouponApi(request)
    suspend fun bookingSlotAvailabilityApi(request: CouponAvalabilityRequest) = apiService.bookingSlotAvailabilityApi(request)
    suspend fun paymentAmountApi(request: CommonRequest) = apiService.paymentAmountApi(request)
    suspend fun walletTransactionApi(request: CommonRequest) = apiService.walletTransactionApi(request)
    suspend fun saveAddressApi(saveAddressRequest: SaveAddressRequest) = apiService.saveAddress(saveAddressRequest)
    suspend fun bookingListApi(request:HashMap<String,String>) = apiService.bookingList(request)
    suspend fun mysoldbookingListApi(request:HashMap<String,String>) = apiService.mysoldbooking(request)
    suspend fun cancelBooking(cancelBookingRequest: CancelBookingRequest) = apiService.cancelBooking(cancelBookingRequest)
    suspend fun acceptBookingApi(acceptBookingRequest: AcceptBookingRequest) = apiService.acceptBooking(acceptBookingRequest)
    suspend fun bookingDetailApi(bookingDetailRequest: BookingDetailRequest) = apiService.bookingDetail(bookingDetailRequest)
    suspend fun myWalletApi() = apiService.myWalletApi()
    suspend fun createOrderApi(request: CommonRequest) = apiService.createOrderApi(request)
    suspend fun rescheduleBookingApi(request: RescheduleBookingRequest) = apiService.rescheduleBookingApi(request)
    suspend fun markAsCompleteApi(request: MarkAsCompleteRequest) = apiService.markAsCompleteApi(request)



}
