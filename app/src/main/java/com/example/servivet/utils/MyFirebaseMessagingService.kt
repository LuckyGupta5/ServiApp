package com.example.servivet.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.servivet.R
import com.example.servivet.ui.main.activity.MainActivity
import com.example.servivet.utils.LogUtil.objectLog
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private lateinit var message:String
    private lateinit var title: String
    private lateinit var image: String
    private var count = 0
    // private lateinit var bitmap: Bitmap

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("TAG", "onNewToken: $token")
       // Preferences.setStringPreference(this, FCM_TOKEN,token)
    }

    override fun onMessageReceived(rMessage: RemoteMessage) {
        super.onMessageReceived(rMessage)
  //      rMessage.notification?.let { objectLog(it) }

        /*  if(!rMessage.data.isNullOrEmpty())
          {
              message = rMessage.data["message"]!!.trim()
              title = rMessage.data["title"]!!.trim()
          }
          else{
              title = rMessage.notification?.title.toString()
              message = rMessage.notification?.body.toString()
          }*/
        title = rMessage.notification?.title.toString()
        message = rMessage.notification?.body.toString()
//        image = rMessage.data["image"]!!  // new
        //bitmap = getBitmapfromUrl(image)!!

        sendNotification(message,title,count)
    }

    private fun sendNotification(message: String, title: String, count: Int) {
         val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
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
        /*val notiStyle = NotificationCompat.BigPictureStyle()
        notiStyle.setSummaryText(message)
        notiStyle.bigPicture(picture)*/

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        notificationBuilder.setSmallIcon(R.mipmap.app_icon_round)
        notificationBuilder.setContentTitle(title)
        // notificationBuilder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(image))
        notificationBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(message))
        notificationBuilder.setContentText(message)
        notificationBuilder.setAutoCancel(true)
        notificationBuilder.setSound(defaultSoundUri)
        notificationBuilder.setChannelId(channelId)
        notificationBuilder.setContentIntent(pendingIntent)
        notificationBuilder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
        notificationBuilder.setNumber(count)




    }

    override fun handleIntent(intent: Intent?) {
        super.handleIntent(intent)

    }




}