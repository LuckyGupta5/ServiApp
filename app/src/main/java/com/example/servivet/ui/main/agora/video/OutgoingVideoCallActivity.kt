package com.example.servivet.ui.main.agora.video

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Chronometer
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.navArgs
import com.example.servivet.R
import com.example.servivet.data.model.call_module.video_call.VideoCallResponse
import com.example.servivet.databinding.ActivityOutgoingVideoCallBinding
import com.example.servivet.ui.base.BaseActivity
import com.example.servivet.ui.main.agora.SoundPoolManager
import com.example.servivet.utils.AppStateLiveData
import com.example.servivet.utils.Constants.AGORA_APP_ID
import com.example.servivet.utils.ForegroundServiceUtils
import com.example.servivet.utils.Session
import com.example.servivet.utils.SocketManager
import com.example.servivet.utils.broadcast.CallEndBroadcast
import com.example.servivet.utils.isAppOnForeground
import com.example.servivet.utils.isBluetoothConnectd
import com.example.servivet.utils.soundservices.OnClearFromRecentService
import com.google.gson.Gson


import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration
import io.socket.client.Socket
import org.json.JSONException
import org.json.JSONObject

class OutgoingVideoCallActivity : BaseActivity(), CallEndBroadcast.CallEndCallback {

    private lateinit var serviceIntent: Intent

    //Do not move the code structure
    private lateinit var noAnswerTimmer: CountDownTimer
    private lateinit var mBinding: ActivityOutgoingVideoCallBinding
    private lateinit var agoraToken: String
    private lateinit var channelName: String
    private lateinit var callUserImage: String
    private lateinit var callUserName: String
    private var currentUserId = ""
    private var handler: Handler? = null
    private var msgId = ""
    private var mRtcEngine: RtcEngine? = null
    private lateinit var mSocket: Socket
    private val argumentData: OutgoingVideoCallActivityArgs by navArgs()
    public var isCallConnected = false
    private var isAudioMute = true
    private var isVideoMute = true
    private var roomId = ""
    private lateinit var callEndBroadcast: CallEndBroadcast
    private var isCallEnd = false
    private var isDestroy = false
    private val backgroundReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            isDestroy = true
            finish()
        }
    }

    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {

        override fun onError(err: Int) {
            Log.i(
                TAG,
                String.format("onError code %d message %s", err, RtcEngine.getErrorDescription(err))
            )
        }

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            Log.i(TAG, String.format("onJoinChannelSuccess channel %s uid %d", channel, uid))
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            super.onUserJoined(uid, elapsed)
            Log.i(TAG, "onUserJoined->$uid")
            isCallConnected = true
            mBinding.callStatus = getString(R.string.connected)
            startChronometer(mBinding.callTime)
            stopRinging()
            showRemoteVideo(uid)
            noAnswerTimmer.cancel()
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            Log.i(TAG, String.format("user %d offline! reason:%d", uid, reason))
            handler!!.post {
                disconnectRinging()
                if (mRtcEngine != null) {
                    mRtcEngine!!.setupRemoteVideo(
                        VideoCanvas(
                            null,
                            VideoCanvas.RENDER_MODE_HIDDEN,
                            uid
                        )
                    )
                }
                finish()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        //  Preference.setPreference(this@OutgoingVideoCallActivity, PrefEntity.serviceFrom, "video_o")
        handler = Handler(Looper.getMainLooper())

        window.decorView.isVisible = false
        super.onCreate(savedInstanceState)
        currentUserId = Session.userDetails._id
        callEndBroadcast = CallEndBroadcast(this)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_outgoing_video_call)
        mBinding.clickAction = ClickAction()
        SocketManager.connect()
        mSocket = SocketManager.getSocket()
        // mBinding.parent.setPadding(0, getPaddingAccordingToStatusBarHeight(), 0, 0)
        // window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        getArgumentData()

//        val bundle = intent.getBundleExtra("bundle")
//        if (bundle != null) {
//            agoraToken = bundle.getString(AGORA_TOKEN)!!
//            channelName = bundle.getString(CHANNEL_NAME)!!
//            callUserImage = bundle.getString(CALL_USER_IMAGE)!!
//            callUserName = bundle.getString(CALL_USER_NAME)!!
//            msgId = bundle.getString(MSG_ID)!!
//
//            mBinding.callUserImage = callUserImage
//            mBinding.userName = callUserName
//        }

        initializeChannel()
        joinChannel()
//        startService(Intent())
        mBinding.idSwitcCamera.setOnClickListener {
            onSwitchCameraClicked(it)
        }
    }

    private fun getArgumentData() {
        when (argumentData.from) {
            getString(R.string.outgoing_video) -> {
                val data = Gson().fromJson(argumentData.data, VideoCallResponse::class.java)
                agoraToken = data.result.callToken
                channelName = data.result.channelName
                callUserImage = data.result.senderId.image
                callUserName = data.result.senderId.name
                msgId = data.result.chatMessageId
                roomId = data.result.roomId


                mBinding.callUserImage = callUserImage
                mBinding.userName = callUserName


            }
        }
    }

    private fun initializeChannel() {
        try {
            startRinging()
            startTimer()
            mRtcEngine = RtcEngine.create(this, AGORA_APP_ID, mRtcEventHandler)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i(TAG, "initializeChannel: ${e.message}")
            finish()
        }
    }

    private fun joinChannel() {
        // stopRinging()
        showLocalVideo()

        val option = ChannelMediaOptions()
        option.autoSubscribeAudio = true
        option.autoSubscribeVideo = true
        // mRtcEngine!!.joinChannel(agoraToken, channelName, "Extra Optional Data", 0, option)
        mRtcEngine!!.joinChannel(agoraToken, channelName, 0, option)

    }

    private fun showLocalVideo() {
        val localSurfaceView = RtcEngine.CreateRendererView(baseContext)
        if (mBinding.localFrame.childCount > 0) mBinding.localFrame.removeAllViews()
        mBinding.localFrame.addView(
            localSurfaceView,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        mRtcEngine!!.setupLocalVideo(
            VideoCanvas(
                localSurfaceView,
                VideoCanvas.RENDER_MODE_HIDDEN,
                0
            )
        )
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED && isBluetoothConnectd()
        )
            mRtcEngine!!.setDefaultAudioRoutetoSpeakerphone(false)
        else
            mRtcEngine!!.setDefaultAudioRoutetoSpeakerphone(true)

        mRtcEngine!!.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
        mRtcEngine!!.setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_BROADCASTER)
        mRtcEngine!!.enableVideo()
        mRtcEngine!!.setEnableSpeakerphone(true)
        mRtcEngine!!.setVideoEncoderConfiguration(
            VideoEncoderConfiguration(
                getGlobalSettings()!!.videoEncodingDimensionObject,
                VideoEncoderConfiguration.FRAME_RATE.valueOf(getGlobalSettings()!!.videoEncodingFrameRate),
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.valueOf(getGlobalSettings()!!.videoEncodingOrientation)
            )
        )
        localSurfaceView.setZOrderMediaOverlay(true)
//        mRtcEngine!!.setAudioProfile(Constants.AUDIO_PROFILE_MUSIC_STANDARD, Constants.AUDIO_SCENARIO_CHATROOM_ENTERTAINMENT);
    }

    private fun showRemoteVideo(uid: Int) {
        handler!!.post {
            val remoteSurfaceView = RtcEngine.CreateRendererView(baseContext)
            mBinding.remoteFrame.addView(
                remoteSurfaceView,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            mRtcEngine!!.setupRemoteVideo(
                VideoCanvas(
                    remoteSurfaceView,
                    VideoCanvas.RENDER_MODE_HIDDEN,
                    uid
                )
            )
            remoteSurfaceView!!.setZOrderMediaOverlay(true)
        }
    }

    public fun endCall() {

        try {
            val data = JSONObject()
            data.put("chatMessageId", msgId)
            data.put("receiverId", currentUserId)
            data.put("roomId", roomId)
            mSocket.emit("noAnswerCall", data)
            mSocket.on("receiveMessages", fun(args: Array<Any?>) {

                runOnUiThread {
                    val CallEnd = args[0] as JSONObject
                    try {
                        Log.e("TAG", "CallEnd:${CallEnd} ")
                        finish()


                    } catch (ex: JSONException) {
                        ex.printStackTrace()
                    }
                }

            })

        } catch (ex: Exception) {

        }

        /*    val data = JSONObject()
            try {
                data.put("chatMessageId", msgId)
                data.put("receiverId", currentUserId)
                data.put("roomId", roomId)
                if (mSocket.connected()) {
                    mSocket.emit(CALL_END, data, object : Ack {
                        override fun call(vararg args: Any?) {
                            runOnUiThread {
                                val jsonObject = Gson().toJson(args[0])
                                Log.i(TAG, "endedAudioCall: ${Gson().toJson(jsonObject)}")
                                finish()
                            }
                        }
                    })
                }*//* else toast(getString(R.string.socket_not_connected))*//*
        } catch (e: JSONException) {
            e.printStackTrace()
        }*/
    }

    public fun noAnswerCall() {
        val data = JSONObject()

        try {
            val data = JSONObject()
            data.put("chatMessageId", msgId)
            data.put("roomId", roomId)
            mSocket.emit("noAnswerCall", data)
            mSocket.on("receiveMessages", fun(args: Array<Any?>) {

                runOnUiThread {
                    val noAnswers = args[0] as JSONObject
                    try {
                        Log.e("TAG", "CallEnd:${noAnswers} ")
                        finish()


                    } catch (ex: JSONException) {
                        ex.printStackTrace()
                    }
                }

            })

        } catch (ex: Exception) {

        }


//        try {
//            data.put("chatMessageId", msgId)
//            data.put("roomId", roomId)
//            if (mSocket.connected()) {
//                mSocket.emit(NO_ANSWER_CALL, data, object : Ack {
//                    override fun call(vararg args: Any?) {
//                        runOnUiThread {
//                            val jsonObject = Gson().toJson(args[0])
//                            Log.i(TAG, "noAnswerAudioCall: ${Gson().toJson(jsonObject)}")
//                            finish()
//                        }
//                    }
//                })
//            } /*else toast(getString(R.string.socket_not_connected))*/
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
    }

    private fun startRinging() = SoundPoolManager.getInstance(this).playRinging()

    private fun stopRinging() = SoundPoolManager.getInstance(this).stopRinging()

    private fun disconnectRinging() = SoundPoolManager.getInstance(this).playDisconnect()

    @SuppressLint("UseCompatLoadingForDrawables")
    open inner class ClickAction {
        open fun endCall(view: View) {
            if (isCallConnected)
                endCall()
            else
                noAnswerCall()
        }

        open fun muteUnMuteVideo(view: View) {
            if (isVideoMute) {
                isVideoMute = false
                //mBinding.video.setImageDrawable(getDrawable(R.drawable.mute_video))
                mRtcEngine!!.disableVideo()
            } else {
                isVideoMute = true
                // mBinding.video.setImageDrawable(getDrawable(R.drawable.video))
                mRtcEngine!!.enableVideo()
            }
        }

        open fun muteUnMuteAudio(view: View) {
            if (isAudioMute) {
                isAudioMute = false
                //  mBinding.audio.setImageDrawable(getDrawable(R.drawable.mute_mic))
//                mRtcEngine!!.disableAudio()
                mRtcEngine!!.muteLocalAudioStream(true)
            } else {
                isAudioMute = true
                //  mBinding.audio.setImageDrawable(getDrawable(R.drawable.mic))
//                mRtcEngine!!.enableAudio()
                mRtcEngine!!.muteLocalAudioStream(false)
            }
        }
    }

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


    override fun onDestroy() {
        stopRinging()
        disconnectRinging()
        if (!isDestroy && !isAppOnForeground(this@OutgoingVideoCallActivity)) {
            if (!isCallEnd) {
                if (isCallConnected)
                    endCall()
                else
                    noAnswerCall()
            }
        } else if (!isDestroyed)
            finish()
        noAnswerTimmer.cancel()
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
//        if (!isCallEnd && !isAppOnForeground(this@com.example.servivet.ui.main.agora.video.OutgoingVideoCallActivity))
        stopService(Intent(this@OutgoingVideoCallActivity, OnClearFromRecentService::class.java))
    }


    override fun onBackPressed() {}

    companion object {
        private const val TAG = "com.example.servivet.ui.main.agora.video.OutgoingVideoCallActivity"
    }

    override fun onCallEnd() {
//        if (!isDestroy) {
        isCallEnd = true
        stopRinging()
        finish()
//        }
    }

    override fun onPause() {
        super.onPause()
        AppStateLiveData.instance.setForegroundState(false);
    }

    override fun onResume() {
        super.onResume()
        AppStateLiveData.instance.setForegroundState(true);
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
        if (!isCallEnd && !isAppOnForeground(this@OutgoingVideoCallActivity) && !ForegroundServiceUtils.isForegroundServiceRunning(
                this@OutgoingVideoCallActivity,
                OnClearFromRecentService::class.java
            )
        ) {
            serviceIntent = Intent(this, OnClearFromRecentService::class.java)
                .putExtra("name", callUserName)
                .putExtra("from", "Outgoing video call...")
            ContextCompat.startForegroundService(this, serviceIntent)
        }
    }

    fun onSwitchCameraClicked(view: View?) {
        mRtcEngine!!.switchCamera()
    }

    private fun startTimer() {
        noAnswerTimmer = object : CountDownTimer(40000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                noAnswerCall()
            }
        }
        noAnswerTimmer.start()
    }
}