package wibmo.com.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.wibmo.Common.SIMInfo;
import com.wibmo.authcallback.AuthRegistration;
import com.wibmo.authcallback.AuthRegistrationCallBack;
import com.wibmo.core.util.PermissionUtil;
import com.wibmo.device.register.AuthDeviceRegisterImpl;
import com.wibmo.sdk.WibmoSDK;

import wibmo.com.wibmoAuthApp.R;


/**
 * Created by nithyak on 22/08/17.
 */

public class MainActivity extends UIBaseActivity implements AuthRegistrationCallBack {
    private static final String TAG = "MainActivity";

    public static final String ALREADY_REG = "AR";

    public static final String KEY_REG = "regStatus";
    private TextView tvStatusFor;
    //private ImageView statusIcon;
    private Button cancelBtn;
    // private ImageView headerImg;
    private TextView plWaitTextView;
    RelativeLayout mainLayout;

    private boolean isSimValid;


    AuthRegistration authRegistration;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        initCustomActionBar();
        setActionBarTitle(getString(R.string.title_registration));

        setContentView(R.layout.activity_status_wait);

        authRegistration = new AuthDeviceRegisterImpl(this);

        tvStatusFor = findViewById(R.id.statusFor);
        //statusIcon = (ImageView) findViewById(R.id.statusIcon);
        cancelBtn = findViewById(R.id.cancelBtn);
        //headerImg = (ImageView) findViewById(R.id.headerImg);
        plWaitTextView = findViewById(R.id.plWait);
        mainLayout = findViewById(R.id.mainLayout);


        // sdk config check

        if (!authRegistration.checkSDKConfiguration(getApplicationContext())) {

            // client id and key not available
        } else {
            applyUICustomisation();

            askPermissionForBasicInfo();

        }
    }

    private void applyUICustomisation() {

        cancelBtn.setVisibility(View.GONE);
        tvStatusFor.setText(getString(R.string.label_reg_status_check));

        String tbarBGColor = activity.getString(R.string.blueColor);

        String headerFontName = activity.getString(R.string.sansFontName);
        int headerTextSize = activity.getResources().getInteger(R.integer.headerTextSize);
        String headerFontColor = activity.getString(R.string.blueColor);


        tvStatusFor.setTypeface(Typeface.create(headerFontName, Typeface.NORMAL));
        tvStatusFor.setTextSize(headerTextSize);
        tvStatusFor.setTextColor(Color.parseColor(headerFontColor));

        String lblFontName = activity.getString(R.string.sansFontName);
        int lblTextSize = activity.getResources().getInteger(R.integer.infoTextSize);
        String lblFontColor = activity.getString(R.string.blueColor);


        plWaitTextView.setTypeface(Typeface.create(lblFontName, Typeface.NORMAL));
        plWaitTextView.setTextSize(lblTextSize);
        plWaitTextView.setTextColor(Color.parseColor(lblFontColor));
    }

    private void askPermissionForBasicInfo() {

        if (PermissionUtil.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            Log.w(TAG, "Permission not granted! READ_PHONE_STATE");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,

                    Manifest.permission.READ_PHONE_STATE)) {

                PermissionUtil.showRequestPermissionRationalel(this,

                        getString(R.string.phone_state_permission_rationale),

                        new Runnable() {

                            @Override
                            public void run() {

                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{

                                                Manifest.permission.READ_PHONE_STATE
                                        }
                                        ,

                                        PermissionUtil.REQUEST_CODE_ASK_PERMISSION_PHONE_STATE
                                );

                            }

                        }, false);

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        PermissionUtil.REQUEST_CODE_ASK_PERMISSION_PHONE_STATE);


            }
        } else {

            doRegister();
        }
    }

    private void doRegister() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {

                            String token = task.getResult().getToken();

                            WibmoSDK.getInstance().setFcmToken(null);

                        }

                        TelephonyManager mngr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                        //String phone_imeir = mngr.getDeviceId();
                        SIMInfo simInfo = new SIMInfo();
                        simInfo.setCarrierName("JIO");
                        simInfo.setCountryCode("91");
                        simInfo.setPhoneNumber("8050689796");
                        simInfo.setSimId(mngr.getDeviceId());
                        simInfo.setDeviceId(mngr.getSubscriberId());

                        authRegistration.onPermissionGranted(simInfo);

                    }
                });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionUtil.REQUEST_CODE_ASK_PERMISSION_PHONE_STATE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Got permission READ_PHONE_STATE");
                // success!
                doRegister();
            } else {
                Log.w(TAG, "Permission not got! READ_PHONE_STATE");
                PermissionUtil.showPermissionMissingUI(this, getString(R.string.phone_state_permission_missing_msg));
                finish();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onDestroy() {
        authRegistration.onActivityDestroy();
        super.onDestroy();
    }

    @Override
    public void onDeviceSuccess(boolean status) {

        // true : means device is already register with server

        if (status) {
            Intent intent_reg = new Intent(this, LockScreenActivity.class);
            intent_reg.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent_reg.putExtra(KEY_REG, ALREADY_REG);
            startActivity(intent_reg);
            finish();
        } else {
            Log.v(TAG, "User hasn't registered for any product. lets Re-register in this case.");
            Intent in_regProd = new Intent(this, ActivitySimSelectionScreen.class);
            finish();
            startActivity(in_regProd);
        }

    }

    @Override
    public void onDeviceFailed(String reason) {

        // handle fail case
        Toast.makeText(getApplicationContext(), reason, Toast.LENGTH_SHORT).show();

    }
}