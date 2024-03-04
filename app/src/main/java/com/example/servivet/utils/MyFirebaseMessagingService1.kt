package com.example.servivet.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.servivet.R
import com.example.servivet.data.model.call_module.notification_call.CallBody
import com.example.servivet.data.model.notification_data.NotificationData
import com.example.servivet.ui.main.activity.HomeActivity
import com.example.servivet.ui.main.agora.audio.IncomingAudioCallActivity
import com.example.servivet.ui.main.agora.video.IncomingVideoCallActivity
import com.example.servivet.utils.Constants.AGORA_TOKEN
import com.example.servivet.utils.Constants.CALL_USER_IMAGE
import com.example.servivet.utils.Constants.CALL_USER_NAME
import com.example.servivet.utils.Constants.CHANNEL_NAME
import com.example.servivet.utils.Constants.MSG_ID
import com.example.servivet.utils.Constants.NOTIFICATION
import com.example.servivet.utils.Constants.RECEIVER_ID
import com.example.servivet.utils.Constants.ROOM_ID
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Random

open class MyFirebaseMessagingService : FirebaseMessagingService() {
    private lateinit var message: String
    private lateinit var title: String
    private lateinit var image: String
    private var count = 0
    private lateinit var notification: NotificationData

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("TAG", "onNewToken: $token")
        Session.saveDeviceToken(token)
    }

    override fun onMessageReceived(rMessage: RemoteMessage) {
        super.onMessageReceived(rMessage)
        rMessage.notification?.let { Log.e("TAG", "onMessageReceived: ${Gson().toJson(it)}") }
        val callBody = Gson().fromJson(rMessage.data["customData"], CallBody::class.java)

        title = rMessage.notification?.title.toString()
        message = rMessage.notification?.body.toString()


        val intentData = Intent(NOTIFICATION)
        intentData.putExtra(NOTIFICATION, rMessage.notification?.body)
        sendBroadcast(intentData)

        Log.e("TAG", "checkCallBody: ${Gson().toJson(rMessage)}")


        Log.e("TAG", "onMessageReceived: $message")
        sendNotification(message, title, count, callBody)

        when (callBody.messageType) {

            6 -> {
                inviteForAudioCall(callBody)
            }

            7 -> {
                inviteForVideoCall(callBody)
            }
        }
    }

    private fun sendNotification(message: String, title: String, count: Int, callBody: CallBody) {
        val pendingIntent: PendingIntent?

        val intent1 = Intent(this, HomeActivity::class.java)
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent1 = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_IMMUTABLE)

        val notifyID = 1
        val channelId = "channel-0888888888"
        val channelName = "Janamarines"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val mChannel = NotificationChannel(
                channelId, channelName, importance
            )
            mChannel.setShowBadge(true)
            notificationManager.createNotificationChannel(mChannel)
            notificationManager.areNotificationsEnabled()
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        notificationBuilder.setSmallIcon(R.mipmap.app_icon_round)
        notificationBuilder.setContentTitle(title)
        //notificationBuilder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(image))
        notificationBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(message))
        notificationBuilder.setContentText(message)
        notificationBuilder.setAutoCancel(true)
        notificationBuilder.setSound(defaultSoundUri)
        notificationBuilder.setChannelId(channelId)
         notificationBuilder.setContentIntent(pendingIntent1)
        notificationBuilder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
        notificationBuilder.setNumber(count)

        try {
            notificationManager.notify(
                getRequestCode(),
                notificationBuilder.build()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }


        val intent: Intent = when (callBody.messageType) {
            6-> Intent(baseContext, IncomingAudioCallActivity::class.java)
            7 -> Intent(baseContext, IncomingVideoCallActivity::class.java)
            else -> Intent()
        }.also {

            val bundle = Bundle()
            bundle.putString(AGORA_TOKEN, callBody.callToken)
            bundle.putString(CHANNEL_NAME, callBody.channelName)
            //  bundle.putString(CALL_USER_IMAGE, callBody.senderId.image)
            // bundle.putString(CALL_USER_NAME, callBody.senderId.name)
            bundle.putString(MSG_ID, callBody.chatMessageId)

            it.putExtra("bundle", bundle)
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            it.flags = Intent.FLAG_FROM_BACKGROUND
            it.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {


            pendingIntent = PendingIntent.getActivity(
                this,
                0, intent,
                PendingIntent.FLAG_IMMUTABLE
            );
        } else {
            pendingIntent = PendingIntent.getActivity(
                this,
                0, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            );
        }

        notificationBuilder.setFullScreenIntent(pendingIntent, true)


    }


    private fun getRequestCode(): Int {
        val rnd = Random()
        return 100 + rnd.nextInt(900000)
    }

    open fun getBitmapfromUrl(imageUrl: String?): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: java.lang.Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            null
        }
    }

    override fun handleIntent(intent: Intent?) {
        super.handleIntent(intent)
        val notificationData = intent!!.getStringExtra("customData")
        Log.e("TAG", "handleIntent: ${notificationData}")
        if(notificationData!=null)
        Session.saveNotificationData(notificationData!!)
    }


    private fun inviteForVideoCall(callBody: CallBody) {
        val bundle = Bundle()
        bundle.putString(AGORA_TOKEN, callBody.callToken)
        bundle.putString(CHANNEL_NAME, callBody.channelName)
        bundle.putString(CALL_USER_IMAGE, callBody.senderId.image)
        bundle.putString(CALL_USER_NAME, callBody.senderId.name)
        bundle.putString(MSG_ID, callBody.chatMessageId)
        bundle.putString(ROOM_ID, callBody.roomId)
        bundle.putString(RECEIVER_ID, callBody.receiverId)
        launchActivityWithBundle(IncomingVideoCallActivity::class.java, bundle)
    }

    private fun inviteForAudioCall(callBody: CallBody) {
        val bundle = Bundle()
        bundle.putString(AGORA_TOKEN, callBody.callToken)
        bundle.putString(CHANNEL_NAME, callBody.channelName)
        bundle.putString(CALL_USER_IMAGE, callBody.senderId.image)
        bundle.putString(CALL_USER_NAME, callBody.senderId.name)
        bundle.putString(MSG_ID, callBody.chatMessageId)
        bundle.putString(ROOM_ID, callBody.roomId)
        bundle.putString(RECEIVER_ID, callBody.receiverId)
        launchActivityWithBundle(IncomingAudioCallActivity::class.java, bundle)
    }

    private fun sendDm() {
        val newDm = Intent("newDmMsg")
        newDm.`package` = "com.example.servivet"
        applicationContext.sendBroadcast(newDm)
    }

    private fun callEnd() {
        val callEnd = Intent("callEnd")
        callEnd.`package` = "com.ripenapps.conveyr"
        applicationContext.sendBroadcast(callEnd)
    }

}



