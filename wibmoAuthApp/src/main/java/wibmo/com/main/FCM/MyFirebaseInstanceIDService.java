package wibmo.com.main.FCM;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.wibmo.sdk.WibmoSDK;

/**
 * Created by abhisheksingh on 1/30/18.
 */

public class MyFirebaseInstanceIDService /*extends FirebaseInstanceIdService*/ {

    /*private static final String TAG = "MyFirebaseIIDService";
    String refreshedToken;

    *//**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     *//*
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.


        //WibmoSDK.getInstance().setFcmToken(refreshedToken);

        //sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    *//**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     *//*
    private void sendRegistrationToServer(String token) {
        *//*if(!TextUtils.isEmpty(SharedPreferenceEditor.getPreferences(MyFirebaseInstanceIDService.this,AppConstants.PREFERENCES_USER,AppConstants.PREFERENCES_MOBILE))) {
            PostProfileCall postProfileCall = new PostProfileCall();
            postProfileCall.execute();
        }*//*
    }*/


}