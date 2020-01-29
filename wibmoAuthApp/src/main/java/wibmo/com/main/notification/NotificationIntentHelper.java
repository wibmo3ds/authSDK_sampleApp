package wibmo.com.main.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import wibmo.com.main.ApplicationSingleton;
import wibmo.com.main.Database.NotificationAlert;
import wibmo.com.main.OfferAlertsNotifications;
import wibmo.com.main.model.POJONotification;
import wibmo.com.wibmoAuthApp.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by abhisheksingh on 3/8/18.
 */

public class NotificationIntentHelper {

  static String channelTrident = ApplicationSingleton.getInstance().getApplicationContext().getResources().getString(R.string.default_notification_channel_name);

  public static void sendNotification(Context context, RemoteMessage remoteMessage) {

    NotificationCompat.Builder builder;
    NotificationManager manager;
    ArrayList<POJONotification> notificationData = new ArrayList();


    String title = "Wibmo Auth App", message = "", deeplink = "", imageUrl = "", userName = "";
    int invisible =0, silent = 0;
    String amount ="";
    String btn1Deeplink = "", notificationType = "authentication", showActionBtn = "", btn1Text = "", btn2Deeplink = "", btn2Text = "";
    String TxnID ="";
    int id = 0;
    int showImage = 0;
    String pushNotificationExpireTime= "";

    if (remoteMessage.getData().containsKey("productId")) {
      if (!TextUtils.isEmpty(remoteMessage.getData().get("productId"))) {
        title = remoteMessage.getData().get("productId");
        Log.d("FCM_Data", title);
      }
    }

    if (remoteMessage.getData().containsKey("amount")) {
      if (!TextUtils.isEmpty(remoteMessage.getData().get("amount"))) {
        amount = remoteMessage.getData().get("amount").replace("Rs","");
        Log.d("FCM_Data", amount);
      }
    }

    if(remoteMessage.getData().containsKey("pushNotificationExpireTime")){
      if(!TextUtils.isEmpty(remoteMessage.getData().get("pushNotificationExpireTime"))){
        pushNotificationExpireTime = remoteMessage.getData().get("pushNotificationExpireTime");
        Log.d("FCM_Data", pushNotificationExpireTime);

      }
    }

    if (remoteMessage.getData().containsKey("merchentName")) {
      if (!TextUtils.isEmpty(remoteMessage.getData().get("merchentName"))) {
        message = remoteMessage.getData().get("merchentName");
        Log.d("FCM_Data", message);
      }
    }

    if (remoteMessage.getData().containsKey("deeplink")) {
      if (!TextUtils.isEmpty(remoteMessage.getData().get("deeplink"))) {
        deeplink = remoteMessage.getData().get("deeplink");
        deeplink = deeplink.toLowerCase();
        Log.d("FCM_Data", deeplink);
      }
    }

    if (remoteMessage.getData().containsKey("id")) {
      if (!TextUtils.isEmpty(remoteMessage.getData().get("id"))) {
        id = (int) System.currentTimeMillis();
      }
    }

    if (remoteMessage.getData().containsKey("showImage")) {
      if (!TextUtils.isEmpty(remoteMessage.getData().get("showImage"))) {
        showImage = Integer.parseInt(remoteMessage.getData().get("showImage"));
        Log.d("FCM_Data", showImage + "");
      }
    }

    if (remoteMessage.getData().containsKey("imageUrl")) {
      if (!TextUtils.isEmpty(remoteMessage.getData().get("imageUrl"))) {
        imageUrl = remoteMessage.getData().get("imageUrl");
        Log.d("FCM_Data", imageUrl);
      }
    }
    //String id = remoteMessage.getData().get("notId");
    if (remoteMessage.getData().containsKey("userName")) {
      if (!TextUtils.isEmpty(remoteMessage.getData().get("userName"))) {
        userName = remoteMessage.getData().get("userName");
        Log.d("FCM_Data", userName);
      }
    }

    if (remoteMessage.getData().containsKey("notificationType")) {
      if (!TextUtils.isEmpty(remoteMessage.getData().get("notificationType"))) {
        notificationType = remoteMessage.getData().get("notificationType");
        Log.d("FCM_Data", notificationType + "");
      }
    }

    if (remoteMessage.getData().containsKey("showActionBtn")) {
      if (!TextUtils.isEmpty(remoteMessage.getData().get("showActionBtn"))) {
        showActionBtn = remoteMessage.getData().get("showActionBtn");
        Log.d("FCM_Data", showActionBtn + "");
      }
    }

    if (remoteMessage.getData().containsKey("btn1")) {
      if (!TextUtils.isEmpty(remoteMessage.getData().get("btn1"))) {
        btn1Text = remoteMessage.getData().get("btn1");
        Log.d("FCM_Data", btn1Text + "");
      }
    }

    if (remoteMessage.getData().containsKey("btn2")) {
      if (!TextUtils.isEmpty(remoteMessage.getData().get("btn2"))) {
        btn2Text = remoteMessage.getData().get("btn2");
        Log.d("FCM_Data", btn2Text + "");
      }
    }

    if (remoteMessage.getData().containsKey("btn1Deeplink")) {
      if (!TextUtils.isEmpty(remoteMessage.getData().get("btn1Deeplink"))) {
        btn1Deeplink = remoteMessage.getData().get("btn1Deeplink");
        Log.d("FCM_Data", btn1Deeplink);
      }
    }

    if (remoteMessage.getData().containsKey("btn2Deeplink")) {
      if (!TextUtils.isEmpty(remoteMessage.getData().get("btn2Deeplink"))) {
        btn2Deeplink = remoteMessage.getData().get("btn2Deeplink");
        Log.d("FCM_Data", btn2Deeplink);
      }
    }



    Intent localBrodcastIntent = new Intent("notificationBR");
    notificationData.clear();
    POJONotification pojoNotification = new POJONotification();
    pojoNotification.setId(id);
    pojoNotification.setTitle(title);
    pojoNotification.setMessage(message);
    pojoNotification.setAmount(amount);
    pojoNotification.setExpiry(pushNotificationExpireTime);
    pojoNotification.setExpiry(pushNotificationExpireTime);
    pojoNotification.setNotifyTime(String.valueOf(System.currentTimeMillis()));
    pojoNotification.setImageurl(imageUrl);
    pojoNotification.setType(notificationType);
    pojoNotification.setDeepLink(deeplink);
    pojoNotification.setTxnId(remoteMessage.getData().get("authAppTxnId"));
    pojoNotification.setRequestrTxnId(remoteMessage.getData().get("requesterTxnId"));
    notificationData.add(pojoNotification);
    Bundle args = new Bundle();
    args.putSerializable("ARRAYLIST",(Serializable) notificationData);
    localBrodcastIntent.putExtra("amount",amount);
    localBrodcastIntent.putExtra("Merchant_Name",remoteMessage.getData().get("merchant_name"));
    localBrodcastIntent.putExtra("BUNDLE",args);

    LocalBroadcastManager.getInstance(context).sendBroadcast(localBrodcastIntent);


    String channelId = context.getString(R.string.default_notification_channel_id);

    Intent notificationIntent = new Intent(context, OfferAlertsNotifications.class);
    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    notificationIntent.putExtra("deeplinkdata", deeplink);
    notificationIntent.putExtra("id", (int) System.currentTimeMillis());

    PendingIntent contentIntent = PendingIntent.getActivity(context,
        0, notificationIntent,
        PendingIntent.FLAG_ONE_SHOT);

    builder = new NotificationCompat.Builder(context, channelId);
    manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    int i = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);


    builder.setContentTitle(title)
        .setContentText(message)
        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
        .setTicker(title)
        .setColor(context.getResources().getColor(R.color.brand_color))
        .setSmallIcon(R.drawable.wibmo_icon)
        .setContentIntent(contentIntent)
        .setWhen(System.currentTimeMillis())
        .setChannelId(channelId)
        .setAutoCancel(true);

    if (!TextUtils.isEmpty(imageUrl)) {
      URL url = null;
      try {
        url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();
        Bitmap myBitmap = BitmapFactory.decodeStream(input);
        builder.setLargeIcon(myBitmap);
        builder.setStyle(
            new NotificationCompat.BigPictureStyle().setSummaryText(message).bigPicture(myBitmap))
            .setContentIntent(contentIntent);
      } catch (MalformedURLException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }

    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

      /* Create or update. */
      NotificationChannel channel = new NotificationChannel(channelId,
          channelTrident,
          NotificationManager.IMPORTANCE_HIGH);
      manager.createNotificationChannel(channel);
    }


    if(id == 0){
      id = (int) System.currentTimeMillis();
    }


    if (invisible == 0) {

   final NotificationAlert notificationAlert = new NotificationAlert();
   notificationAlert.setId(id);
   notificationAlert.setTitle(title);
   notificationAlert.setMessage(message);
   notificationAlert.setAmount(amount);
   notificationAlert.setExpiry(pushNotificationExpireTime);

   notificationAlert.setExpiry(pushNotificationExpireTime);
   notificationAlert.setNotifyTime(String.valueOf(System.currentTimeMillis()));
   notificationAlert.setImageurl(imageUrl);
   notificationAlert.setType(notificationType);
   notificationAlert.setDeepLink(deeplink);
   notificationAlert.setTxnId(remoteMessage.getData().get("authAppTxnId"));
   notificationAlert.setRequestrTxnId(remoteMessage.getData().get("requesterTxnId"));



      ApplicationSingleton.getInstance().getAppExecutors().diskIO().execute(new Runnable() {
        @Override
        public void run() {

          ApplicationSingleton.getInstance().getAppDatabase()
              .mNotificationAlertDao()
              .insert(notificationAlert);
        }
      });


      Notification notification = builder.build();
      notification.flags = Notification.FLAG_AUTO_CANCEL /*| Notification.FLAG_INSISTENT*/;
      Log.d("notification", String.valueOf(i));
      manager.notify(notificationType, i, notification);
    }
  }



  public static void clearNotification(Context context, RemoteMessage remoteMessage) {

    NotificationCompat.Builder builder;
    NotificationManager manager;
    String title = "Wibmo Auth App";
    String action ="",authAppTxnId="",requesterTxnId="";


    if (remoteMessage.getData().containsKey("action")) {
      if (!TextUtils.isEmpty(remoteMessage.getData().get("action"))) {
        action = remoteMessage.getData().get("action");
        Log.d("FCM_Data", action);
      }
    }

    if (remoteMessage.getData().containsKey("authAppTxnId")) {
      if (!TextUtils.isEmpty(remoteMessage.getData().get("authAppTxnId"))) {
        authAppTxnId = remoteMessage.getData().get("authAppTxnId");
        Log.d("FCM_Data", authAppTxnId);
      }
    }


    if (remoteMessage.getData().containsKey("requesterTxnId")) {
      if (!TextUtils.isEmpty(remoteMessage.getData().get("requesterTxnId"))) {
        requesterTxnId = remoteMessage.getData().get("requesterTxnId");
        Log.d("FCM_Data", requesterTxnId);
      }
    }

    Intent localBrodcastIntent = new Intent("notifyClearBR");
    localBrodcastIntent.putExtra("Txn_id",authAppTxnId);
    LocalBroadcastManager.getInstance(context).sendBroadcast(localBrodcastIntent);


  }

}
