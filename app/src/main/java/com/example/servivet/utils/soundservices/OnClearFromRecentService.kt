package com.example.servivet.utils.soundservices
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.servivet.R


class OnClearFromRecentService : Service() {
    private val NOTIFICATION_ID: Int=1
    private val CHANNEL_ID = "conveyr"
    var mBinder: IBinder =LocalBinder()

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent?) : IBinder{
        return mBinder
    }

    inner class LocalBinder : Binder() {
        fun getService() = this@OnClearFromRecentService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val type = intent!!.getStringExtra("from")
        val notificationBuilder: Notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(intent.getStringExtra("name"))
            .setContentText(type)
            .setSmallIcon(R.drawable.apply_coupon_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        startForeground(NOTIFICATION_ID, notificationBuilder);

        return START_NOT_STICKY;
    }


    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        stopSelf()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        /*val from: String = Preference.getPreference(applicationContext, PrefEntity.serviceFrom).toString()

        if (from == "audio_o") {
            if (com.example.servivet.ui.main.agora.audio.OutgoingAudioCallActivity().isCallConnected)          //audio outgoing
                com.example.servivet.ui.main.agora.audio.OutgoingAudioCallActivity().endCall();
            else
                com.example.servivet.ui.main.agora.audio.OutgoingAudioCallActivity().noAnswerCall();

        } else if (from == "audio_i") {
            if (IncomingAudioCallActivity().isConnected)              //audio incoming
                IncomingAudioCallActivity().endCall();
            else {
                IncomingAudioCallActivity().rejectCall()
            }
        } else if (from == "video_i") {                                 //video incoming
            if (com.example.servivet.ui.main.agora.video.IncomingVideoCallActivity().isConnected)
                com.example.servivet.ui.main.agora.video.IncomingVideoCallActivity().endCall();
            else
                com.example.servivet.ui.main.agora.video.IncomingVideoCallActivity().rejectCall();
        } else if (from == "video_o") {                                //video outgoing
            if (com.example.servivet.ui.main.agora.video.OutgoingVideoCallActivity().isCallConnected)
                com.example.servivet.ui.main.agora.video.OutgoingVideoCallActivity().endCall();
            else
                com.example.servivet.ui.main.agora.video.OutgoingVideoCallActivity().noAnswerCall();
        }

        stopSelf()*/
    }
}
