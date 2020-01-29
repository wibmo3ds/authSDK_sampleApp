package wibmo.com.main;

import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import wibmo.com.wibmoAuthApp.R;



/**
 * Created by abhisheksingh on 6/8/18.
 */

public class LockScreenActivity extends AppCompatActivity {
    public static String TAG = LockScreenActivity.class.getSimpleName();

    private static int CODE_AUTHENTICATION_VERIFICATION = 241;
    private static int CODE_AUTHENTICATION_CREATION = 242;
    RelativeLayout mainLayout;
    Button enableTouchIdButton, patternLockButton;
    LinearLayout fingerPrintDetectedLayout;
    TextView securityText;
    FingerprintManager fingerprintManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enable_device_security);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        mainLayout = findViewById(R.id.lockScreenMainLayout);
        enableTouchIdButton = findViewById(R.id.enableTouchIdButton);
        patternLockButton = findViewById(R.id.patternLockButton);
        fingerPrintDetectedLayout = findViewById(R.id.fingerPrintDetectedLayout);
        securityText = findViewById(R.id.securityText);
        mainLayout.setVisibility(View.GONE);

        // Initializing both Android Keyguard Manager and Fingerprint Manager
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        fingerprintManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        }


        enableTouchIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(LockScreenActivity.this, "Please setup security to proceed(pattern or password or pin or fingerprint", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
                //startActivity(intent);


                startActivityForResult(new Intent(Settings.ACTION_SETTINGS), CODE_AUTHENTICATION_CREATION);
            }
        });

        patternLockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
                startActivityForResult(intent, CODE_AUTHENTICATION_CREATION);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (km.isKeyguardSecure()) {

                Intent i = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    i = km.createConfirmDeviceCredentialIntent("Authentication required", "password");
                }
                startActivityForResult(i, CODE_AUTHENTICATION_VERIFICATION);
            } else {
                pushtoEnableFingerprint();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                if (requestCode == CODE_AUTHENTICATION_VERIFICATION) {
                    //Toast.makeText(this, "Successfully Verified", Toast.LENGTH_SHORT).show();
                    Intent homeIntent = new Intent(LockScreenActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                    finish();
                }
                if (requestCode == CODE_AUTHENTICATION_CREATION) {
                    KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        if (km.isKeyguardSecure()) {

                            Intent i = null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                i = km.createConfirmDeviceCredentialIntent("Authentication required", "password");
                            }
                            startActivityForResult(i, CODE_AUTHENTICATION_VERIFICATION);
                        }
                    }
                } else {
                    Snackbar snackbar = Snackbar
                            .make(mainLayout, "Failure: Unable to verify user's identity", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }
    }


    private void pushtoEnableFingerprint() {
        mainLayout.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (fingerprintManager != null) {
                if (!fingerprintManager.isHardwareDetected()) {
                    /**
                     * An error message will be displayed if the device does not contain the fingerprint hardware.
                     * However if you plan to implement a default authentication method,
                     * you can redirect the user to a default authentication activity from here.
                     * Example:
                     * Intent intent = new Intent(this, DefaultAuthenticationActivity.class);
                     * startActivity(intent);
                     */
                    // textView.setText("Your Device does not have a Fingerprint Sensor");
                    fingerPrintDetectedLayout.setVisibility(View.GONE);
                    enableTouchIdButton.setVisibility(View.GONE);
                    securityText.setText(R.string.enable_security);
                } else {
                    fingerPrintDetectedLayout.setVisibility(View.VISIBLE);
                    enableTouchIdButton.setVisibility(View.VISIBLE);
                    securityText.setText(R.string.security_at_your_fingertip);
                }
            } else { // as we are not sure for Android below M whether it has Fingerprint sensor or not, So take safe case that it doesn't exist and if exists it'll be used by default if Set up by user
                fingerPrintDetectedLayout.setVisibility(View.GONE);
                enableTouchIdButton.setVisibility(View.GONE);
                securityText.setText(R.string.enable_security);
            }
        } else { // as we are not sure for Android below M whether it has Fingerprint sensor or not, So take safe case that it doesn't exist and if exists it'll be used by default if Set up by user
            fingerPrintDetectedLayout.setVisibility(View.GONE);
            enableTouchIdButton.setVisibility(View.GONE);
            securityText.setText(R.string.enable_security);
        }
    }
}
