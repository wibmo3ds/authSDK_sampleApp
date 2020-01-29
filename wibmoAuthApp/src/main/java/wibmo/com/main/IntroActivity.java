package wibmo.com.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.wibmo.core.pojo.CustomInfoDataRequest;
import com.wibmo.core.pojo.PreConfigInfo;
import com.wibmo.sdk.SDKEnvironment;
import com.wibmo.sdk.WibmoSDK;

import wibmo.com.wibmoAuthApp.R;


/**
 * Created by nithyak on 18/08/17.
 */

public class IntroActivity extends AppCompatActivity {
    private static final String TAG = "IntroActivity";

    private Button nextBtn;
    private WibmoSDK wibmoSDK;
    private boolean isUserRegistered;
    private String domainToPost;
    private String bankName;
    private String apiKey;
    private int REQ_PRE_CONFIG = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        Log.v(TAG, "Inside onCreate");


        ApplicationSingleton.init(getApplicationContext());
        bankName = "Federal_Bank";
        apiKey = "federal_authapp_acs";

        CustomInfoDataRequest customInfoDataRequest = new CustomInfoDataRequest()
                .setRequesterCustData1("E-com");

        PreConfigInfo preConfigInfo = new PreConfigInfo(bankName, apiKey)
                .setCustomInfoDataRequest(customInfoDataRequest);

        WibmoSDK.initSDK(getApplicationContext(), preConfigInfo, SDKEnvironment.b);


        wibmoSDK = WibmoSDK.getInstance();

        isUserRegistered = wibmoSDK.checkForUserRegistration();


        Log.v(TAG, "domainToPost: " + domainToPost);

        updateUI();
    }

    private void updateUI() {

        //wibmoSDK.setPreferenceData(bankName, apiKey);
        nextBtn = findViewById(R.id.skipEduScreen);


        if (!isUserRegistered) {
            setContentView(R.layout.activity_intro);

            IntroFragment fragment = new IntroFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_placeholder, fragment);
            ft.commit();


            nextBtn = findViewById(R.id.skipEduScreen);
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startMainActivity();
                }
            });

            applyUICustomisation();

        } else if (TextUtils.isEmpty(wibmoSDK.getLoginMobile())) {
            setContentView(R.layout.activity_intro);

            IntroFragment fragment = new IntroFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_placeholder, fragment);
            ft.commit();


            nextBtn = findViewById(R.id.skipEduScreen);
            nextBtn.setText("LOGIN");
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startMainActivity();
                }
            });

            applyUICustomisation();

        } else {
            startMainActivity();
            return;
        }
    }


    private void startMainActivity() {

        if (!wibmoSDK.init(this)) {
            //No Root Detected
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            onBackPressed();
        } else {
            Toast.makeText(getApplicationContext(), "Root Device Detected", Toast.LENGTH_SHORT).show();
        }
    }


    private Activity getActivity() {
        return this;
    }

    private void applyUICustomisation() {
        String buttonFontName = getActivity().getString(R.string.sansFontName);

        nextBtn.setTypeface(Typeface.create(buttonFontName, Typeface.BOLD));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PRE_CONFIG) {
            updateUI();
        }
    }

}
