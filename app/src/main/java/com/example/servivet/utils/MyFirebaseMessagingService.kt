package com.example.servivet.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.text.Html
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.servivet.R
import com.example.servivet.data.model.notification_data.NotificationData
import com.example.servivet.ui.main.activity.HomeActivity
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
    private lateinit var notification:NotificationData

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("TAG", "onNewToken: $token")
        Session.saveDeviceToken(token)
    }

    override fun onMessageReceived(rMessage: RemoteMessage) {
        super.onMessageReceived(rMessage)
        rMessage.notification?.let { Log.e("TAG", "onMessageReceived: $it") }

        title = rMessage.notification?.title.toString()
        message = rMessage.notification?.body.toString()

        Log.e("TAG", "onMessageReceived: $message")
        sendNotification(message, title, count)
    }

    private fun sendNotification(message: String, title: String, count: Int) {

        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

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
        notificationBuilder.setContentIntent(pendingIntent)
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
        Log.e("TAG", "handleIntent: ${notificationData}", )
        Session.saveNotificationData(notificationData!!)
    }
}



