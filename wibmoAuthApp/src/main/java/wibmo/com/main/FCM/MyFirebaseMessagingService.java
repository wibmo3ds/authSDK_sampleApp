package wibmo.com.main.FCM;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Map;

import wibmo.com.main.ApplicationSingleton;
import wibmo.com.main.Database.NotificationAlert;
import wibmo.com.main.notification.NotificationIntentHelper;

/**
 * Created by abhisheksingh on 1/30/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//{action=TxnAuth, amount=Rs. 1.00, detail=for Rs. 1.00 at eBay.co.in, productId=ACS,
// pushNotificationExpireTime=1563946735405, requesterTxnId=201907241103491157kE3dO6uS,
// merchentName=eBay.co.in, authAppTxnId=201907241103518582eB4wJ7oQ}

        if(ApplicationSingleton.getInstance() == null){
            ApplicationSingleton.init(getApplicationContext());
        }
        Log.d(TAG, "Message data payload: " + remoteMessage.toString());

        String from = remoteMessage.getFrom();
        Map<String, String> data = remoteMessage.getData();
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Data: " + data);


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData().toString());
            //observeNotificationLiveData();
            if (remoteMessage.getData().containsKey("action")) {

                 if(!TextUtils.isEmpty(remoteMessage.getData().get("action"))) {
                     if (remoteMessage.getData().get("action").equalsIgnoreCase("TxnAuth")) {
                         NotificationIntentHelper.sendNotification(getApplicationContext(), remoteMessage);
                     } else if (remoteMessage.getData().get("action").equalsIgnoreCase("NotifyClear")) {
                        NotificationIntentHelper.clearNotification(getApplicationContext(), remoteMessage);
                     }
                 }
            }
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }
}



