package wibmo.com.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wibmo.Common.SIMInfo;
import com.wibmo.authcallback.AuthSimRegisterCallBack;
import com.wibmo.authcallback.SimRegistration;
import com.wibmo.core.util.PermissionUtil;
import com.wibmo.device.SimRegisterImpl;


import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import wibmo.com.main.adapter.AdapterShowSimSelectionList;
import wibmo.com.main.callback.OnSimAdapterItemClick;
import wibmo.com.wibmoAuthApp.R;
public class ActivitySimSelectionScreen extends UIBaseActivity implements AuthSimRegisterCallBack, OnSimAdapterItemClick {

    public static final String TAG = ActivitySimSelectionScreen.class.getSimpleName();
    //ArrayList<SIMInfo> simInfoArrayList;
    //AdapterShowSimSelectionList adapterShowSimSelectionList;

    RecyclerView recyclerView;
    Activity activity;
    LinearLayoutManager llm;
    Context context;
    TextView termsConditionTextView, privacyTextView;
    Button simVerifyButton, simSelectionConfirmBtn;
    BottomSheetDialog dialog;

    boolean showSIMselection, simChanged;
    RelativeLayout mainLayout;


    LinearLayout mainView;

    private TextView tvStatusFor;
    //private ImageView statusIcon;
    private Button cancelBtn;
    //private ImageView headerImg;
    private TextView plWaitTextView;

    private boolean hasUsrRegForProduct;
    private Bundle bundle;
    private String regStatus;
    private AtomicInteger waitCount = new AtomicInteger();
    private int selSimSlotIndex = 0;
    Boolean[] booleans;


    SimRegistration simRegistration;


    // failed re registration

    private TextView tvRegStatus;
    private TextView tvMobNumber;
    private TextView tvActionView;
    //private ImageView statusIcon;
    //private ImageView headerImg;
    private TextView tvLabelFor;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sim_selection);

        simRegistration = new SimRegisterImpl(this);
        //simInfoArrayList = simRegistration.getSimList();
        activity = ActivitySimSelectionScreen.this;
        context = this;

        mainView = (LinearLayout) findViewById(R.id.activity_view);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SIGN UP");
        toolbar.setTitleTextColor(getResources().getColor(R.color.defaultwhite));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_24px);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultwhite), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);


        bundle = getIntent().getExtras();
        if (bundle != null) {
            regStatus = bundle.getString(UIUtil.KEY_REG, null);
        }



        termsConditionTextView = findViewById(R.id.textViewTermsAndConditions);
        privacyTextView = findViewById(R.id.textViewPrivacyPolicy);
        simVerifyButton = findViewById(R.id.simSelectionVerifyButton);
        recyclerView = findViewById(R.id.simDetails);
        mainLayout = findViewById(R.id.simMainLayout);
        llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(llm);

        //adapterShowSimSelectionList = new AdapterShowSimSelectionList(this, simInfoArrayList);
        //recyclerView.setAdapter(adapterShowSimSelectionList);
        recyclerView.setHasFixedSize(true);


        initializeUi();
    }




    @Override
    public void onSimClick(SIMInfo simInfo) {

        //simRegistration.onSimChecked(simInfo);


    }


   /* @Override
    public void onSimCheckValidationSuccess() {

        bottomSheetDialogLayout();

    }*/

    @Override
    public void onSimRegisterSuccess() {

        Intent newHomeIntent = new Intent(ActivitySimSelectionScreen.this, LockScreenActivity.class);
        startActivity(newHomeIntent);

        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        simRegistration.onDetach();
        simRegistration = null;
    }

    @Override
    public void onSimRegistrationFailed(final String reason) {

        // load new layout and re register again
        mainView.removeAllViews();

        View progressView = getLayoutInflater().inflate(R.layout.activity_reg_status, mainView, false);
        tvRegStatus = progressView.findViewById(R.id.regStatus);
        tvMobNumber = progressView.findViewById(R.id.mobNum);
        tvActionView = progressView.findViewById(R.id.actionView);
        //statusIcon = (ImageView) findViewById(R.id.statusIcon);
        //headerImg = (ImageView) findViewById(R.id.headerImg);
        tvLabelFor = progressView.findViewById(R.id.lblFor);

        // set use phone number
        //tvMobNumber.setText("(" + UserData.getMobileNumber(activity.getApplicationContext()) + ")");

        if (regStatus != null) {
            UIUtil.showErrorToast(activity, regStatus);
        }
        tvRegStatus.setText(activity.getResources().getString(R.string.reg_failed));
        tvActionView.setText(activity.getResources().getString(R.string.link_to_register));
        tvActionView.setVisibility(View.VISIBLE);
        mainView.addView(progressView);


        tvActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (reason.equals(UIUtil.RES_FAILED)) {

                    simRegistration.onStartSimRegistrationProcess();

                } else {


                    mainView.removeAllViews();

                    View progressView = getLayoutInflater().inflate(R.layout.activity_status_wait, mainView, false);


                    tvStatusFor = progressView.findViewById(R.id.statusFor);
                    //statusIcon = (ImageView) findViewById(R.id.statusIcon);
                    cancelBtn = progressView.findViewById(R.id.cancelBtn);
                    //headerImg = (ImageView) findViewById(R.id.headerImg);
                    plWaitTextView = progressView.findViewById(R.id.plWait);
                    // implement cancel letter on

                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                           // simRegistration.onCancelSimRegistration();

                        }
                    });

                    mainView.addView(progressView);


                    simRegistration.onReTryRegistration();

                }

            }
        });

        UIUtil.showSnackBar(mainLayout, reason, false, reason, 0, true, ContextCompat.getColor(ActivitySimSelectionScreen.this, R.color.white), Snackbar.LENGTH_SHORT);

    }





    public void initializeUi() {

        simVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialogLayout();
                //simRegistration.onSimVerificationButtonClicked();
            }
        });
    }


    public void bottomSheetDialogLayout() {


        Log.d(TAG, "bottomSheetDialogLayout");
        dialog = new BottomSheetDialog(ActivitySimSelectionScreen.this, R.style.BottomSheetDialog);
        dialog.setContentView(R.layout.confirm_your_number);
        simSelectionConfirmBtn = dialog.findViewById(R.id.sim_selection_confirmBtn);
        simSelectionConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                dialog.dismiss();



                mainView.removeAllViews();

                View progressView = getLayoutInflater().inflate(R.layout.activity_status_wait, mainView, false);


                tvStatusFor = progressView.findViewById(R.id.statusFor);
                //statusIcon = (ImageView) findViewById(R.id.statusIcon);
                cancelBtn = progressView.findViewById(R.id.cancelBtn);
                //headerImg = (ImageView) findViewById(R.id.headerImg);
                plWaitTextView = progressView.findViewById(R.id.plWait);
                // implement cancel letter on
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        //simRegistration.onCancelSimRegistration();

                    }
                });
                mainView.addView(progressView);




                Log.d(TAG, "simChanged:" + simChanged);
                //sendOutwardSMS(ApplicationSingleton.getInstance().getUpihrSimSelectedBuilder().build().getICCID(), ApplicationSingleton.getInstance().getUpihrSimSelectedBuilder().build().getSimSlotNumber(), simChanged);
                doValidateAndTrust();

            }
        });
        dialog.show();
    }

    @Override
    public void onProcessUpdate(String msg) {

        if (tvStatusFor != null) {
            tvStatusFor.setText(msg);
        }

    }


    private void resultCancel() {




        Intent intent = new Intent();
        Bundle returnBundle = new Bundle();
        returnBundle.putString("status", "MC07");
        returnBundle.putString("statusDesc", "Cancelled By User");
        intent.putExtras(returnBundle);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        resultCancel();
    }


    private void doValidateAndTrust() {

        simRegistration.onStartSimRegistrationProcess();
    }


    @Override
    protected void onResume() {
        super.onResume();

        //simRegistration.onActivityResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_wibmo_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionUtil.REQUEST_CODE_ASK_PERMISSION_SMS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Got permission SEND_SMS");
                // success!
                simRegistration.onStartSimRegistrationProcess();
            } else {
                Log.w(TAG, "Permission not got! SEND_SMS");
                PermissionUtil.showPermissionMissingUI(activity, getString(R.string.sms_send_permission_missing_msg));
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
