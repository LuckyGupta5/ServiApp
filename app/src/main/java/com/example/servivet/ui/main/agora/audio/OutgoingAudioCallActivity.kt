package com.example.servivet.ui.main.agora.audio

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.*
import android.widget.Chronometer
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.navArgs
import com.example.servivet.R
import com.example.servivet.data.model.call_module.video_call.VideoCallResponse
import com.example.servivet.databinding.ActivityOutgoingAudioCallBinding
import com.example.servivet.ui.base.BaseActivity
import com.example.servivet.ui.main.agora.SoundPoolManager
import com.example.servivet.ui.main.agora.video.OutgoingVideoCallActivityArgs
import com.example.servivet.utils.AppStateLiveData
import com.example.servivet.utils.Constants.AGORA_APP_ID
import com.example.servivet.utils.Constants.AGORA_TOKEN
import com.example.servivet.utils.Constants.CALL_USER_IMAGE
import com.example.servivet.utils.Constants.CALL_USER_NAME
import com.example.servivet.utils.Constants.CHANNEL_NAME
import com.example.servivet.utils.Constants.CURRENT_USER_ID
import com.example.servivet.utils.Constants.END_CALL
import com.example.servivet.utils.Constants.MSG_ID
import com.example.servivet.utils.Constants.NO_ANSWER_CALL
import com.example.servivet.utils.ForegroundServiceUtils
import com.example.servivet.utils.Session
import com.example.servivet.utils.SocketManager
import com.example.servivet.utils.broadcast.CallEndBroadcast
import com.example.servivet.utils.isAppOnForeground
import com.example.servivet.utils.soundservices.OnClearFromRecentService
import com.google.gson.Gson

import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.socket.client.Ack
import io.socket.client.Socket
import org.json.JSONException
import org.json.JSONObject

class OutgoingAudioCallActivity : BaseActivity(), CallEndBroadcast.CallEndCallback {
    private lateinit var serviceIntent: Intent
    private lateinit var noAnswerTimmer: CountDownTimer
    private lateinit var mBinding: ActivityOutgoingAudioCallBinding
    private lateinit var agoraToken: String
    private lateinit var channelName: String
    private lateinit var callUserImage: String
    private lateinit var callUserName: String
    private var currentUserId = ""
    private val argumentData: OutgoingVideoCallActivityArgs by navArgs()
    private var msgId = ""
    private lateinit var mSocket: Socket
    private var handler: Handler? = null
    private var mRtcEngine: RtcEngine? = null
    private var isCallConnected = false
    private var isMute = true
    private var isCallEnd = false
    private var roomId = ""
    private var receiverId = ""
    private lateinit var callEndBroadcast: CallEndBroadcast
    private var isDestroy = false
    private val backgroundReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            isDestroy = true
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler(Looper.getMainLooper())
        // Preference.setPreference(this@OutgoingAudioCallActivity, PrefEntity.serviceFrom, "audio_o")
        callEndBroadcast = CallEndBroadcast(this)
        window.decorView.isVisible = false
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_outgoing_audio_call)
        mBinding.clickAction = ClickAction()
        SocketManager.connect()
        mSocket = SocketManager.getSocket()
        // mBinding.parent.setPadding(0, getPaddingAccordingToStatusBarHeight(), 0, 0)
        getArgumentData()


        initializeChannel()
        joinChannel()
    }


    private fun getArgumentData() {
        when (argumentData.from) {
            getString(R.string.outgoing_Audio) -> {
                val data = Gson().fromJson(argumentData.data, VideoCallResponse::class.java)

                agoraToken = data.result.callToken
                channelName = data.result.channelName
                callUserImage = data.result.senderId.image
                callUserName = data.result.senderId.name
                msgId = data.result.chatMessageId
                roomId = data.result.roomId
                receiverId = data.result.receiverId

                mBinding.userImage = callUserImage
                mBinding.userName = callUserName


            }
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
        startRinging()
        startTimer()
        mRtcEngine!!.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
        mRtcEngine!!.setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_BROADCASTER)
        mRtcEngine!!.setDefaultAudioRoutetoSpeakerphone(false)
        mRtcEngine!!.setAudioProfile(
            Constants.AUDIO_PROFILE_DEFAULT,
            Constants.AUDIO_SCENARIO_DEFAULT
        )

        val option = ChannelMediaOptions()
        option.autoSubscribeAudio = true
        mRtcEngine!!.joinChannel(agoraToken, channelName, 0, option)
    }

    private fun startTimer() {
        noAnswerTimmer = object : CountDownTimer(40000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                noAnswerCall()
            }
        }
        noAnswerTimmer.start()
    }

    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            Log.i(TAG, String.format("onJoinChannelSuccess channel %s uid %d", channel, uid))
            mRtcEngine!!.enableAudio()
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            super.onUserJoined(uid, elapsed)
            Log.i(TAG, "onUserJoined->$uid")
            stopRinging()
            noAnswerTimmer.cancel()
            isCallConnected = true
            mBinding.callStatus = getString(R.string.connected)
            startChronometer(mBinding.callTime)
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            Log.i(TAG, String.format("user %d offline! reason:%d", uid, reason))
            mBinding.callStatus = getString(R.string.callended)
            handler!!.post {
                disconnectRinging()
                finish()
            }
        }

        override fun onError(err: Int) {
            Log.e(
                TAG,
                String.format("onError code %d message %s", err, RtcEngine.getErrorDescription(err))
            )
        }

    }


    override fun onDestroy() {
        if (!isDestroy && !isAppOnForeground(this@OutgoingAudioCallActivity)) {
            if (!isCallEnd) {
                if (isCallConnected)
                    endCall()
                else
                    noAnswerCall()
            }
        } else if (!isDestroyed)
            finish()

        stopRinging()
        noAnswerTimmer.cancel()
        disconnectRinging()
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
        stopService(Intent(this@OutgoingAudioCallActivity, OnClearFromRecentService::class.java))
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
                        Toast.makeText(this@OutgoingAudioCallActivity, "endCall", Toast.LENGTH_SHORT).show()

                        finish()


                    } catch (ex: JSONException) {
                        ex.printStackTrace()
                    }
                }

            })

        } catch (ex: Exception) {

        }


//        Log.i(TAG, "endCall: outgoing")
//
//        val data = JSONObject()
//        try {
//            data.put("msgId", msgId)
//            data.put("endedByUserId", currentUserId)
//            if (mSocket.connected()) {
//                mSocket.emit(END_CALL, data, object : Ack {
//                    override fun call(vararg args: Any?) {
//                        runOnUiThread {
//                            val jsonObject = Gson().toJson(args[0])
//                            Log.i(TAG, "endedAudioCall: ${Gson().toJson(jsonObject)}")
//                            finish()
//                        }
//                    }
//                })
//            } /*else toast(getString(R.string.socket_not_connected))*/
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
    }

    fun noAnswerCall() {
        try {
            val data = JSONObject()
            data.put("chatMessageId", msgId)
            data.put("roomId", roomId)
            data.put("rejectedBy", Session.userDetails._id)
            data.put("receiverId", receiverId)
            mSocket.emit("rejectCall", data)
            mSocket.on("rejectCall", fun(args: Array<Any?>) {

                runOnUiThread {
                    val rejectCall = args[0] as JSONObject
                    try {
                        Log.e("TAG", "CallEnd:${rejectCall} ")
                        Toast.makeText(this@OutgoingAudioCallActivity, "noAnswer", Toast.LENGTH_SHORT).show()
                        finish()
                    } catch (ex: JSONException) {
                        ex.printStackTrace()
                    }
                }

            })

        } catch (ex: Exception) {

        }


//        val data = JSONObject()
//        try {
//            data.put("msgId", msgId)
//            data.put("endedByUserId", currentUserId)
//            if (mSocket.connected()) {
//
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

    private fun stopRinging() {
        SoundPoolManager.getInstance(this).stopRinging()
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
        open fun endCall(view: View) {
        //       endCall()
            if (isCallConnected)
                endCall()
            else
                noAnswerCall()
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        open fun muteUnMuteCall(view: View) {
            if (isMute) {
                isMute = false
                mBinding.audio.setImageDrawable(getDrawable(R.drawable.mute_mic_icon))
                mRtcEngine!!.disableAudio()
                mRtcEngine!!.muteLocalAudioStream(true)
            } else {
                isMute = true
                mBinding.audio.setImageDrawable(getDrawable(R.drawable.mic_icon))
                mRtcEngine!!.enableAudio()
                mRtcEngine!!.muteLocalAudioStream(false)

            }
        }
    }

    override fun onBackPressed() {}

    companion object {
        private const val TAG = "com.example.servivet.ui.main.agora.audio.OutgoingAudioCallActivity"
    }

    override fun onCallEnd() {
        if (!isDestroy) {
            isCallEnd = true
            stopRinging()
            noAnswerTimmer.cancel()
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

    override fun onStop() {
        super.onStop()
        try {
            unregisterReceiver(callEndBroadcast)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        if (!isCallEnd && !isAppOnForeground(this@OutgoingAudioCallActivity) && !ForegroundServiceUtils.isForegroundServiceRunning(
                this@OutgoingAudioCallActivity,
                OnClearFromRecentService::class.java
            )
        ) {
            serviceIntent = Intent(this, OnClearFromRecentService::class.java)
                .putExtra("name", callUserName)
                .putExtra("from", "Outgoing audio call...")
            ContextCompat.startForegroundService(this, serviceIntent)
        }
    }

    override fun onPause() {
        super.onPause()
        AppStateLiveData.instance.setForegroundState(false);
    }
}