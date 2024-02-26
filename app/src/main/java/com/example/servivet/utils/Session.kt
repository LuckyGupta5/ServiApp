package com.example.servivet.utils


import com.example.servivet.data.model.current_api.response.CurrentUser
import com.example.servivet.data.model.current_api.response.MasterData
import com.example.servivet.data.model.home.response.HomeServiceCategory
import com.example.servivet.data.model.location.LocationInfo
import com.example.servivet.data.model.save_address.request.SaveAddressRequest
import com.example.servivet.data.model.user_profile.response.UserProfile
import com.example.servivet.data.model.verifyotp.response.VerifyOTPResult
import com.example.servivet.utils.PreferenceEntity.CATEGORY
import com.example.servivet.utils.PreferenceEntity.DEVICE_TOKEN
import com.example.servivet.utils.PreferenceEntity.IS_LOGIN
import com.example.servivet.utils.PreferenceEntity.LOCATION
import com.example.servivet.utils.PreferenceEntity.LOCATION_ADDRESS_INFO
import com.example.servivet.utils.PreferenceEntity.LOCATION_INFO
import com.example.servivet.utils.PreferenceEntity.MASTER_DATA
import com.example.servivet.utils.PreferenceEntity.NOTIFICATION_DATA
import com.example.servivet.utils.PreferenceEntity.NOTIFY_STATUS
import com.example.servivet.utils.PreferenceEntity.SAVE_ADDRESS
import com.example.servivet.utils.PreferenceEntity.TOKEN
import com.example.servivet.utils.PreferenceEntity.TYPE
import com.example.servivet.utils.PreferenceEntity.USER_DETAILS
import com.example.servivet.utils.PreferenceEntity.USER_PROFILE
import com.example.servivet.utils.PreferenceEntity.VERIFY_DATA
import com.orhanobut.hawk.Hawk

object Session {
    var token = Hawk.get<String>(TOKEN, null)
    var isLogin = Hawk.get<Boolean>(IS_LOGIN, null)
    var notificationStatus = Hawk.get<Boolean>(NOTIFY_STATUS, null)
    var location = Hawk.get<String>(LOCATION, null)
    var type = Hawk.get<String>(TYPE, null)
    var locationInfo = Hawk.get<LocationInfo>(LOCATION_INFO, null)
    var verifiedData = Hawk.get<VerifyOTPResult>(VERIFY_DATA, null)
    var userProfile = Hawk.get<UserProfile>(USER_PROFILE, null)
    var category = Hawk.get<ArrayList<HomeServiceCategory>>(CATEGORY, null)
    var userDetails = Hawk.get<CurrentUser>(USER_DETAILS, null)
    var masterData = Hawk.get<MasterData>(MASTER_DATA, null)
    var saveAddress = Hawk.get<SaveAddressRequest>(SAVE_ADDRESS, null)
    var fcmToken = Hawk.get<String>(DEVICE_TOKEN, null)
    var notificationData = Hawk.get<String>(NOTIFICATION_DATA, null)
    var saveLocationInfo = Hawk.get<LocationInfo>(LOCATION_ADDRESS_INFO, null)
    fun saveToken(token: String) {
        Hawk.put(TOKEN, token)
        Session.token = token
    }

    fun saveDeviceToken(deviceToken: String) {
        Hawk.put(DEVICE_TOKEN, deviceToken)
        fcmToken = deviceToken
    }

    fun saveNotificationData(notificationData: String) {
        Hawk.put(NOTIFICATION_DATA, notificationData)
        this.notificationData = notificationData
    }

    fun saveLocation(location: String) {
        Hawk.put(LOCATION, location)
        Session.location = location
    }

    fun saveType(type: String) {
        Hawk.put(TYPE, type)
        Session.type = type
    }

    fun saveIsLogin(isLogin: Boolean) {
        Hawk.put(IS_LOGIN, isLogin)
        Session.isLogin = isLogin
    }

    fun saveNotificationStatus(notificationStatus: Boolean) {
        Hawk.put(NOTIFY_STATUS, notificationStatus)
        Session.notificationStatus = notificationStatus
    }

    fun saveLocationInfo(locationInfo: LocationInfo) {
        Hawk.put(LOCATION_ADDRESS_INFO, locationInfo)
        saveLocationInfo = locationInfo
    }


    fun saveVerifyUserData(userData: VerifyOTPResult) {
        Hawk.put(VERIFY_DATA, userData)
        this.verifiedData = userData
    }

    fun saveUserProfile(userProfile: UserProfile) {
        Hawk.put(USER_PROFILE, userProfile)
        this.userProfile = userProfile
    }

    fun saveAddress(saveAddress: SaveAddressRequest) {
        Hawk.put(SAVE_ADDRESS, saveAddress)
        this.saveAddress = saveAddress
    }

    fun saveCategory(category: ArrayList<HomeServiceCategory>) {
        Hawk.put(CATEGORY, category)
        this.category = category
    }

    fun saveUserDetails(user: CurrentUser) {
        Hawk.put(USER_DETAILS, user)
        this.userDetails = user
    }

    fun saveMasterData(user: MasterData) {
        Hawk.put(MASTER_DATA, user)
        this.masterData = user
    }


    fun logout() {
        verifiedData = null
        token = null
        isLogin = false
        type = null
        userProfile = null
        category = null
        userDetails = null
        notificationStatus = null
        val deviceToken = fcmToken
        Hawk.deleteAll()
        saveDeviceToken(deviceToken)

    }


}