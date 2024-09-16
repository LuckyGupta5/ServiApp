package com.example.servivet.ui.main.agora.video

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
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
import androidx.databinding.DataBindingUtil
import com.example.servivet.R
import com.example.servivet.databinding.ActivityIncomingVideoCallBinding
import com.example.servivet.ui.base.BaseActivity
import com.example.servivet.ui.main.agora.SoundPoolManager
import com.example.servivet.utils.AppStateLiveData
import com.example.servivet.utils.Constants.ACCEPT_CALL
import com.example.servivet.utils.Constants.AGORA_APP_ID
import com.example.servivet.utils.Constants.AGORA_TOKEN
import com.example.servivet.utils.Constants.CALL_USER_IMAGE
import com.example.servivet.utils.Constants.CALL_USER_NAME
import com.example.servivet.utils.Constants.CHANNEL_NAME
import com.example.servivet.utils.Constants.MSG_ID
import com.example.servivet.utils.Constants.RECEIVER_ID
import com.example.servivet.utils.Constants.REJECT_CALL
import com.example.servivet.utils.Constants.ROOM_ID
import com.example.servivet.utils.ForegroundServiceUtils
import com.example.servivet.utils.Session
import com.example.servivet.utils.SocketManager
import com.example.servivet.utils.broadcast.CallEndBroadcast
import com.example.servivet.utils.isAppOnForeground
import com.example.servivet.utils.isBluetoothConnectd
import com.example.servivet.utils.soundservices.OnClearFromRecentService
import com.example.servivet.utils.startBackgroundMusicService
import com.example.servivet.utils.stopBackgroundMusicService
import com.google.gson.Gson

import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration
import io.socket.client.Ack
import io.socket.client.Socket
import org.json.JSONException
import org.json.JSONObject

class IncomingVideoCallActivity : BaseActivity(), CallEndBroadcast.CallEndCallback {
    //Do not move the code structure
    private lateinit var serviceIntent: Intent
    private lateinit var mBinding: ActivityIncomingVideoCallBinding
    private lateinit var agoraToken: String
    private lateinit var channelName: String
    private lateinit var callUserImage: String
    private lateinit var callUserName: String
    private var msgId = ""
    private lateinit var mSocket: Socket
    private var mRtcEngine: RtcEngine? = null
    private lateinit var callEndBroadcast: CallEndBroadcast
    private var handler: Handler? = null
    private var isAudioMute = true
    private var isVideoMute = true
    public var isConnected = false
    private var isCallEnd = false
    private var isDestroy = false
    private var isAutoEnd = true
    private var roomId = ""
    private var receiverId = ""

    private val backgroundReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            isDestroy = true
            isAutoEnd = false
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
            mBinding.isCallConnected = true
            isConnected = true
            mBinding.callStatus = getString(R.string.connected)

            /*  stopRinging()*/
            stopBackgroundMusicService(this@IncomingVideoCallActivity)

            startChronometer(mBinding.callTime)

            showRemoteVideo(uid)
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            Log.i(TAG, String.format("user %d offline! reason:%d", uid, reason))
            mBinding.callStatus = getString(R.string.callended)
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
        super.onCreate(savedInstanceState)
        handler = Handler(Looper.getMainLooper())
        //Preference.setPreference(this@IncomingVideoCallActivity, PrefEntity.serviceFrom, "video_i")
        callEndBroadcast = CallEndBroadcast(this)
        stopBackgroundMusicService(this)
        /* startRinging()*/
        startBackgroundMusicService(this)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_incoming_video_call)
        //  mBinding.parent.setPadding(0, getPaddingAccordingToStatusBarHeight(), 0, 0)
        mBinding.clickAction = ClickAction()
        SocketManager.connect()
        mSocket = SocketManager.getSocket()
        mBinding.isCallConnected = false
        isConnected = false
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
        //  window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val bundle = intent.getBundleExtra("bundle")
        if (bundle != null) {
            agoraToken = bundle.getString(AGORA_TOKEN)!!
            channelName = bundle.getString(CHANNEL_NAME)!!
            callUserImage = bundle.getString(CALL_USER_IMAGE)!!
            callUserName = bundle.getString(CALL_USER_NAME)!!
            msgId = bundle.getString(MSG_ID)!!
            roomId = bundle.getString(ROOM_ID)!!
            receiverId = bundle.getString(RECEIVER_ID)!!



            mBinding.callUserImage = callUserImage
            mBinding.userName = callUserName
        }

        initializeChannel()
        showLocalVideo()
        mBinding.idSwitcCamera.setOnClickListener {
            onSwitchCameraClicked(it)
        }

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
        val option = ChannelMediaOptions()
        option.autoSubscribeAudio = true
        option.autoSubscribeVideo = true
        if (mRtcEngine != null && agoraToken != null && channelName != null && option != null)
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

    private fun acceptCall() {
        try {
            val data = JSONObject()
            data.put("chatMessageId", msgId)
            data.put("roomId", roomId)
            mSocket.emit("acceptedCall", data)
            mSocket.on("acceptedCall", fun(args: Array<Any?>) {

                runOnUiThread {
                    val CallEnd = args[0] as JSONObject
                    try {
                        Log.e("TAG", "CallEnd:${CallEnd} ")
                        joinChannel()
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
            mSocket.emit("rejectCall", data)
            mSocket.on("acceptedCall", fun(args: Array<Any?>) {

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

    fun endCall() {
        try {
            val data = JSONObject()
            data.put("chatMessageId", msgId)
            data.put("receiverId", receiverId)

            data.put("roomId", roomId)
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
    private fun startRinging() = SoundPoolManager.getInstance(this).playRinging()
    private fun stopRinging() = SoundPoolManager.getInstance(this).stopRinging()
    private fun disconnectRinging() = SoundPoolManager.getInstance(this).playDisconnect()
    @SuppressLint("UseCompatLoadingForDrawables")
    open inner class ClickAction {
        open fun muteUnMuteVideo(view: View) {
            if (isVideoMute) {
                isVideoMute = false
                mBinding.video.setImageDrawable(getDrawable(R.drawable.mute_video_icon))
                mRtcEngine!!.enableLocalVideo(false)
            } else {
                isVideoMute = true
                mBinding.video.setImageDrawable(getDrawable(R.drawable.video_icon))
                mRtcEngine!!.enableLocalVideo(true)
            }
        }
        open fun muteUnMuteAudio(view: View) {
            if (isAudioMute) {
                isAudioMute = false
                mBinding.audio.setImageDrawable(getDrawable(R.drawable.mute_mic_icon))
//                mRtcEngine!!.disableAudio()
                mRtcEngine!!.muteLocalAudioStream(true)
            } else {
                isAudioMute = true
                mBinding.audio.setImageDrawable(getDrawable(R.drawable.mic_icon))
//                mRtcEngine!!.enableAudio()
                mRtcEngine!!.muteLocalAudioStream(false)
            }
        }

        open fun endCall(view: View) = endCall()

        open fun acceptCall(view: View) = acceptCall()

        open fun rejectCall(view: View) = rejectCall()

    }

    private fun startChronometer(mChronometer: Chronometer) {
        // Make sure we are on the UI thread when updating the Chronometer
        runOnUiThread {
            val stoppedMilliseconds: Int
            mChronometer.base = SystemClock.elapsedRealtime()

            val chronoText = mChronometer.text.toString()
            val array = chronoText.split(":".toRegex()).toTypedArray()

            stoppedMilliseconds = if (array.size == 2) {
                // Format is MM:SS
                (array[0].toInt() * 60 * 1000) + (array[1].toInt() * 1000)
            } else {
                // Format is HH:MM:SS
                (array[0].toInt() * 60 * 60 * 1000) +
                        (array[1].toInt() * 60 * 1000) +
                        (array[2].toInt() * 1000)
            }

            mChronometer.base = SystemClock.elapsedRealtime() - stoppedMilliseconds
            mChronometer.start()
        }
    }


    override fun onDestroy() {
        stopBackgroundMusicService(this)
        if (!isAutoEnd) {
            if (!isDestroy && !isAppOnForeground(this@IncomingVideoCallActivity)) {
                if (!isCallEnd) {
                    if (isConnected)
                        endCall()
                    else
                        rejectCall()
                }
            } else if (!isDestroyed)
                finish()
        }
        mBinding.callTime.stop()
        if (mRtcEngine != null) mRtcEngine!!.leaveChannel()
        handler!!.post { RtcEngine.destroy() }
        mRtcEngine = null
        try {
            unregisterReceiver(backgroundReceiver)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
//        if (!isCallEnd && !isAppOnForeground(this@com.example.servivet.ui.main.agora.video.IncomingVideoCallActivity))
        stopService(Intent(this@IncomingVideoCallActivity, OnClearFromRecentService::class.java));
    }

    /*override fun onPause() {
        super.onPause()
        stopBackgroundMusicService(this)

        if(isConnected)
            endCall()
        else
            rejectCall()

        mBinding.callTime.stop()
        if (mRtcEngine != null) mRtcEngine!!.leaveChannel()
        handler!!.post { RtcEngine.destroy() }
        mRtcEngine = null
    }*/

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

        val intentFilter = IntentFilter()
        intentFilter.addAction("callEnd")
        registerReceiver(callEndBroadcast, intentFilter)

        val intentFilterBackground = IntentFilter("app_background_action")
        registerReceiver(backgroundReceiver, intentFilterBackground)
    }

    override fun onPause() {
        super.onPause()
        AppStateLiveData.instance.setForegroundState(false);
    }

    override fun onStop() {
        super.onStop()
        try {
            unregisterReceiver(callEndBroadcast)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        if (!isCallEnd && !isAppOnForeground(this@IncomingVideoCallActivity) && !ForegroundServiceUtils.isForegroundServiceRunning(
                this@IncomingVideoCallActivity,
                OnClearFromRecentService::class.java
            )
        ) {
            serviceIntent = Intent(this, OnClearFromRecentService::class.java)
                .putExtra("name", callUserName)
                .putExtra("from", "Incoming video call...")
            ContextCompat.startForegroundService(this, serviceIntent)
        }
    }

    override fun onBackPressed() {}

    companion object {
        private const val TAG = "com.example.servivet.ui.main.agora.video.IncomingVideoCallActivity"
    }

    fun onSwitchCameraClicked(view: View?) {
        mRtcEngine!!.switchCamera()
    }
}