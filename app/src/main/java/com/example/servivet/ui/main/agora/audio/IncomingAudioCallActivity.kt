package com.example.servivet.ui.main.agora.audio


import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Chronometer
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.servivet.R
import com.example.servivet.databinding.ActivityIncomingAudioCallBinding
import com.example.servivet.ui.base.BaseActivity
import com.example.servivet.ui.main.agora.SoundPoolManager
import com.example.servivet.utils.AppStateLiveData
import com.example.servivet.utils.Constants.AGORA_APP_ID
import com.example.servivet.utils.Constants.AGORA_TOKEN
import com.example.servivet.utils.Constants.CALL_USER_IMAGE
import com.example.servivet.utils.Constants.CALL_USER_NAME
import com.example.servivet.utils.Constants.CHANNEL_NAME
import com.example.servivet.utils.Constants.MSG_ID
import com.example.servivet.utils.Constants.NO_ANSWER_CALL
import com.example.servivet.utils.Constants.RECEIVER_ID
import com.example.servivet.utils.Constants.ROOM_ID
import com.example.servivet.utils.Constants.SENDER_ID
import com.example.servivet.utils.ForegroundServiceUtils
import com.example.servivet.utils.Session
import com.example.servivet.utils.SocketManager
import com.example.servivet.utils.SocketManager.getSocket
import com.example.servivet.utils.broadcast.CallEndBroadcast
import com.example.servivet.utils.isAppOnForeground
import com.example.servivet.utils.soundservices.OnClearFromRecentService
import com.example.servivet.utils.startBackgroundMusicService
import com.example.servivet.utils.stopBackgroundMusicService
import com.google.gson.Gson
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.socket.client.Ack
import io.socket.client.Socket
import org.json.JSONException
import org.json.JSONObject


@Suppress("DEPRECATION")
class IncomingAudioCallActivity : BaseActivity(), CallEndBroadcast.CallEndCallback {
    private lateinit var serviceIntent: Intent
    private lateinit var mBinding: ActivityIncomingAudioCallBinding
    private lateinit var agoraToken: String
    private lateinit var channelName: String
    private lateinit var callUserImage: String
    private lateinit var callUserName: String
    private lateinit var mSocket: Socket
    private var msgId = ""
    private var currentUserId = ""
    private var mRtcEngine: RtcEngine? = null
    private var isMute = true
    private lateinit var callEndBroadcast: CallEndBroadcast
    var isConnected = false
    var isDisconnected = false
    private lateinit var audioManager: AudioManager
    private var isCallEnd = false
    private var isDestroy = false
    private var handler: Handler? = null
    private var isAutoEnd = true
    private var roomId = ""
    private var receiverId = ""
    private var senderId = ""

    private val backgroundReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            isDestroy = true
            isAutoEnd = false
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
             AppStateLiveData.instance.setForegroundState(false);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Preference.setPreference(this@IncomingAudioCallActivity, PrefEntity.serviceFrom, "audio_i")
        handler = Handler(Looper.getMainLooper())
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        window.decorView.isVisible = false
        callEndBroadcast = CallEndBroadcast(this)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_incoming_audio_call)
        stopBackgroundMusicService(this)
        //mBinding.parent.setPadding(0, getPaddingAccordingToStatusBarHeight(), 0, 0)
        mBinding.clickAction = ClickAction()
        mBinding.isCallConnected = false
        SocketManager.connect()
        mSocket = getSocket()

        startRinging()
        val bundle = intent.getBundleExtra("bundle")
        if (bundle != null) {
            agoraToken = bundle.getString(AGORA_TOKEN)?:""
            channelName = bundle.getString(CHANNEL_NAME)?:""
            callUserImage = bundle.getString(CALL_USER_IMAGE)?:""
            callUserName = bundle.getString(CALL_USER_NAME)?:""
            msgId = bundle.getString(MSG_ID)?:""
            roomId = bundle.getString(ROOM_ID)?:""
            receiverId = bundle.getString(RECEIVER_ID)?:""
            senderId = bundle.getString(SENDER_ID)?:""

            //Preference.setPreference(this@IncomingAudioCallActivity, PrefEntity.messageId, msgId)
            mBinding.userImage = callUserImage
            mBinding.userName = callUserName
        }

        initializeChannel()
    }


    private fun initializeChannel() {
        try {
            mRtcEngine = RtcEngine.create(this, AGORA_APP_ID, mRtcEventHandler)

        } catch (e: Exception) {
            e.printStackTrace()
            Log.i(TAG, "initializeChannel: ${e.message}")
            finish()
        }
    }


    private fun joinChannel() {
        mRtcEngine?.let {
            it.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
            it.setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_BROADCASTER)
            it.setDefaultAudioRoutetoSpeakerphone(false)
            it.setAudioProfile(Constants.AUDIO_PROFILE_DEFAULT, Constants.AUDIO_SCENARIO_DEFAULT)
        }

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.isSpeakerphoneOn = false


        val option = ChannelMediaOptions()
        option.autoSubscribeAudio = true
        // mRtcEngine!!.joinChannel(agoraToken, channelName, "Extra Optional Data", 0,option)
        if (mRtcEngine != null && agoraToken != null && channelName != null && option != null) mRtcEngine!!.joinChannel(
            agoraToken,
            channelName,
            0,
            option
        )
    }

    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            Log.i(TAG, String.format("onJoinChannelSuccess channel %s uid %d", channel, uid))
            mBinding.isCallConnected = true
            mBinding.callStatus = "connected"
            startChronometer(mBinding.callTime)
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            super.onUserJoined(uid, elapsed)
            Log.i(TAG, "onUserJoined->$uid")
            mRtcEngine!!.monitorBluetoothHeadsetEvent(true)
            mRtcEngine!!.enableAudio()
            stopRinging()
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            mBinding.callStatus = "call ended"
            handler!!.post {
                finish()
            }
            disconnectRinging()
        }

        override fun onError(err: Int) {
            Log.e(
                TAG,
                String.format("onError code %d message %s", err, RtcEngine.getErrorDescription(err))
            )
        }
    }

    override fun onDestroy() {
        if (!isAutoEnd) {
            if (!isDestroy && !isAppOnForeground(this@IncomingAudioCallActivity)) {
                if (!isCallEnd) {
                    if (isConnected)
                        endCall()
                    else {
                        rejectCall()
                    }
                }
            } else if (!isDestroyed)
                finish()
        }
        stopRinging()
        mBinding.callTime.stop()
        if (mRtcEngine != null) {
            mRtcEngine!!.leaveChannel()
        }
        handler!!.post { RtcEngine.destroy() }
        mRtcEngine = null
        try {
            unregisterReceiver(backgroundReceiver)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
        stopService(Intent(this@IncomingAudioCallActivity, OnClearFromRecentService::class.java))
    }

    public fun noAnswerCall() {
        val data = JSONObject()
        try {
            data.put("msgId", msgId)
            data.put("endedByUserId", currentUserId)
            if (mSocket.connected()) {
                mSocket.emit(NO_ANSWER_CALL, data, object : Ack {
                    override fun call(vararg args: Any?) {
                        runOnUiThread {
                            val jsonObject = Gson().toJson(args[0])
                            Log.i(TAG, "noAnswerAudioCall: ${Gson().toJson(jsonObject)}")
                            isDisconnected = true
                            isConnected = false
                            finish()
                        }
                    }
                })
            } /*else toast(getString(R.string.socket_not_connected))*/
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun acceptCall() {
        try {
            val data = JSONObject()
            data.put("chatMessageId", msgId)
            data.put("roomId", roomId)
            mSocket.emit("acceptedCall", data)
            mSocket.on("acceptedCall", fun(args: Array<Any?>) {

                runOnUiThread {
                    val data = args[0] as JSONObject
                    try {
                        Log.e("TAG", "CallEnd:${data} ")
                        joinChannel()
                        stopRinging()
                        isConnected = true


                    } catch (ex: JSONException) {
                        ex.printStackTrace()
                    }
                }

            })

        } catch (ex: Exception) {

        }
    }

    fun rejectCall() {
        try {
            val data = JSONObject()
            data.put("chatMessageId", msgId)
            data.put("roomId", roomId)
            data.put("rejectedBy", Session.userDetails._id)
            data.put("receiverId", senderId)
            mSocket.emit("rejectCall", data)
            mSocket.on("rejectCall", fun(args: Array<Any?>) {

                runOnUiThread {
                    val rejectCall = args[0] as JSONObject
                    try {
                        Log.e("TAG", "CallEnd:${rejectCall} ")
                        isConnected = false
                        isDisconnected = true
                        finish()
                    } catch (ex: JSONException) {
                        ex.printStackTrace()
                    }
                }

            })

        } catch (ex: Exception) {

        }

    }


    fun endCall() {
        try {
            val data = JSONObject()
            data.put("chatMessageId", msgId)
            data.put("roomId", roomId)
            data.put("receiverId", receiverId)
            mSocket.emit("endedCall", data)
            mSocket.on("endedCall", fun(args: Array<Any?>) {

                runOnUiThread {
                    val rejectCall = args[0] as JSONObject
                    try {
                        Log.e("TAG", "CallEnd:${rejectCall} ")
                        finish()


                    } catch (ex: JSONException) {
                        ex.printStackTrace()
                    }
                }

            })

        } catch (ex: Exception) {

        }
    }

    private fun startRinging() {
        startBackgroundMusicService(this)
    }

    private fun stopRinging() {
        stopBackgroundMusicService(this)
        /* SoundPoolManager.getInstance(this).stopRinging()*/
    }

    private fun disconnectRinging() = SoundPoolManager.getInstance(this).playDisconnect()

    private fun startChronometer(mChronometer: Chronometer) {
        val stoppedMilliseconds: Int
        mChronometer.base = SystemClock.elapsedRealtime()

        val chronoText = mChronometer.text.toString()
        val array = chronoText.split(":".toRegex()).toTypedArray()

        stoppedMilliseconds = if (array.size == 2) {
            Integer.parseInt((array[0].toInt() * 60 * 1000).toString()) + Integer.parseInt((array[1].toInt() * 1000).toString())
        } else {
            Integer.parseInt((array[0].toInt() * 60 * 60 * 1000).toString()) + Integer.parseInt(
                (array[1].toInt() * 60 * 1000).toString() + Integer.parseInt(
                    (array[2].toInt() * 1000).toString()
                )
            )
        }
        mChronometer.base = SystemClock.elapsedRealtime() - stoppedMilliseconds
        mChronometer.start()

    }

    open inner class ClickAction {
        open fun acceptCall(view: View) {
            acceptCall()
        }

        open fun rejectCall(view: View) {
            if (isConnected)
                endCall()
            else
                rejectCall()
        }

        open fun endCall(view: View) {
            endCall()
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        open fun muteUnMuteCall(view: View) {
            if (isMute) {
                isMute = false
                mBinding.audio.setImageDrawable(getDrawable(R.drawable.mute_mic_icon))
//                mRtcEngine!!.disableAudio()
                mRtcEngine!!.muteLocalAudioStream(true)
            } else {
                isMute = true
                mBinding.audio.setImageDrawable(getDrawable(R.drawable.mic_icon))
//                mRtcEngine!!.enableAudio()
                mRtcEngine!!.muteLocalAudioStream(false)
            }
        }
    }

    override fun onCallEnd() {
        isAutoEnd = false
        if (!isDestroy) {
            isCallEnd = true
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        AppStateLiveData.instance.setForegroundState(true);
        currentUserId = Session.userDetails._id

        val intentFilter = IntentFilter()
        intentFilter.addAction("callEnd")
        registerReceiver(callEndBroadcast, intentFilter)

        val intentFilterBackground = IntentFilter("app_background_action")
        registerReceiver(backgroundReceiver, intentFilterBackground)
    }

    override fun onStop() {
        super.onStop()
        try {
            unregisterReceiver(callEndBroadcast)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        if (!isCallEnd && !isAppOnForeground(this@IncomingAudioCallActivity) && !ForegroundServiceUtils.isForegroundServiceRunning(
                this@IncomingAudioCallActivity,
                OnClearFromRecentService::class.java
            )
        ) {
            serviceIntent = Intent(this, OnClearFromRecentService::class.java)
                .putExtra("name", callUserName)
                .putExtra("from", "Incoming audio call...")
            ContextCompat.startForegroundService(this, serviceIntent)
        }
    }

    override fun onBackPressed() {}

    companion object {
        private const val TAG = "IncomingAudioCallActivity"
    }
}
