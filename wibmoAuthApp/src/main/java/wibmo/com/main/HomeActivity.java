package wibmo.com.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.wibmo.Common.AppConstants;
import com.wibmo.authcallback.HomeCallBack;
import com.wibmo.authcallback.HomeInterface;
import com.wibmo.device.HomeScreenImpl;

import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import wibmo.com.main.Database.NotificationAlert;
import wibmo.com.main.Fragments.SliderFragment;
import wibmo.com.main.adapter.AdapterHomeIcons;
import wibmo.com.main.callback.RecyclerItemClickListener;
import wibmo.com.main.model.HomeScreenFeatureModel;
import wibmo.com.main.model.POJONotification;
import wibmo.com.wibmoAuthApp.R;


/**
 * Created by abhisheksingh on 7/30/18.
 */

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, HomeCallBack {

    private static String TAG = HomeActivity.class.getSimpleName();
    public DrawerLayout Drawer;
    ImageView imageViewUserProfileImage;
    LinearLayout headerLayoutNavigationView;
    RecyclerView recyclerViewMain;
    ArrayList<HomeScreenFeatureModel> homeIconsList;
    AlertDialog alertDialogPayment, alertDialogSMS;
    KeyStore keyStore;
    RelativeLayout mainLayout, deregisteredUserLayout;
    Button exitButton;
    NavigationView navigationView;
    private ProgressDialog progressDialog;
    Activity activity;

    boolean clearNotificationFlag = false;
    FrameLayout fragmentPlaceholder;
    public static final String homeTextOfIcons[] = {
            "Offline\nOtp",
            "Transaction\nHistory",
            //"Fraud News\nAlerts",
            //"Promotional\nOffers"
    };


    public static final int homeIconImages[] = {
            R.drawable.offline_otp,
            R.drawable.txn_history,
            // R.drawable.news,
            // R.drawable.ic_offer,
    };
    private List<NotificationAlert> mNotificationAlerts = ApplicationSingleton.getInstance().getNotitifcationList();
    private ArrayList<POJONotification> notificationIntentData = new ArrayList<>();

    BroadcastReceiver notificationBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive Notification");
            NotificationManager nMgr = (NotificationManager) getSystemService(HomeActivity.this.NOTIFICATION_SERVICE);
            nMgr.cancelAll();

            Bundle args = intent.getBundleExtra("BUNDLE");
            notificationIntentData = (ArrayList<POJONotification>) args.getSerializable("ARRAYLIST");
            Log.d(TAG, notificationIntentData.toString());
            observeNotificationLiveData();
            transactionSuccessPopup(notificationIntentData);
        }
    };


    BroadcastReceiver notificationClearBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive Notification");
            clearNotificationFlag = true;
            observeClearNotifyLiveData(intent);
        }
    };


    private void observeNotificationLiveData() {
        ApplicationSingleton.getInstance().getAppDatabase()
                .mNotificationAlertDao()
                .getAll().observe(this, new android.arch.lifecycle.Observer<List<NotificationAlert>>() {
            @Override
            public void onChanged(@Nullable List<NotificationAlert> notificationAlerts) {
                //if(updateNotificationData) {
                swapData(notificationAlerts);
                //}
            }
        });
    }

    private void observeClearNotifyLiveData(final Intent intent) {
        ApplicationSingleton.getInstance().getAppDatabase()
                .mNotificationAlertDao()
                .getAll().observe(this, new android.arch.lifecycle.Observer<List<NotificationAlert>>() {
            @Override
            public void onChanged(@Nullable List<NotificationAlert> notificationAlerts) {
                if (clearNotificationFlag) {
                    clearNotificationData(notificationAlerts, intent);
                }
            }
        });
    }

    public void clearNotificationData(List<NotificationAlert> notificationAlerts, Intent intent) {
        try {
            this.mNotificationAlerts = notificationAlerts;
            String txnIdToClear;


            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if (alertDialogSMS != null) {
                alertDialogSMS.dismiss();
            }

            if (alertDialogPayment != null) {
                alertDialogPayment.dismiss();
            }

            NotificationManager nMgr = (NotificationManager) getSystemService(HomeActivity.this.NOTIFICATION_SERVICE);
            nMgr.cancelAll();

            if (intent != null) {
                txnIdToClear = intent.getStringExtra("Txn_id");
            } else {
                txnIdToClear = "";
            }

            if (mNotificationAlerts != null) {
                if (mNotificationAlerts.size() > 0) {

                    for (int i = 0; i < mNotificationAlerts.size(); i++) {
                        if (mNotificationAlerts.get(i).getTxnId().equalsIgnoreCase(txnIdToClear)) {
                            clearNotificationFlag = false;
                            deleteNotificationLog();
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.d("abhi_exception", TAG + " " + e);
        }
    }


    private void deleteNotificationLog() {
        try {
            if (mNotificationAlerts.size() < 1) {
                return;
            }
            final NotificationAlert alert;
            //alert = mNotificationAlerts.get(mNotificationAlerts.size()-1);
            alert = mNotificationAlerts.get(mNotificationAlerts.size() - 1);
            ApplicationSingleton.getInstance().getAppExecutors()
                    .diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    ApplicationSingleton.getInstance().getAppDatabase()
                            .mNotificationAlertDao()
                            .delete(alert);
                }
            });
        } catch (Exception e) {
            Log.d("abhi_exception", TAG + " " + e);
        }
    }

    public void swapData(List<NotificationAlert> notificationAlerts) {
        this.mNotificationAlerts = notificationAlerts;
    }


    public void transactionSuccessPopup(ArrayList<POJONotification> arrayList) {
        if (alertDialogPayment != null) {
            alertDialogPayment.dismiss();
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        //alertDialogBuilder.setTitle("Send Instant Money");
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.adapter_notification, null, false);
        alertDialogBuilder.setView(dialoglayout);
        double i;
        double rounded;

        TextView ID;
        TextView Message;
        TextView amount;
        TextView Title;
        TextView textNotificationTime;
        ImageView notifiPhoto;
        ImageView notifiPhotoSmall;
        ImageButton delete;
        LinearLayout linearLayout;
        TextView notificationTag;
        Button declineButton, payButton, expiryButton;
        ImageView notitificationTypeImageView;
        TextView notificationTypeTextView;

        Title = dialoglayout.findViewById(R.id.textViewAlertNotifyTitle);
        ID = dialoglayout.findViewById(R.id.textViewNotifyID);
        Message = dialoglayout.findViewById(R.id.textViewMessage);
        amount = dialoglayout.findViewById(R.id.textViewAmount);
        textNotificationTime = dialoglayout.findViewById(R.id.tvNotificationTime);
        delete = dialoglayout.findViewById(R.id.imageButtonDelete);
        notifiPhoto = dialoglayout.findViewById(R.id.imageViewAlertnotify);
        notifiPhotoSmall = dialoglayout.findViewById(R.id.imageViewAlertnotifysmall);
        //linearLayout = dialoglayout.findViewById(R.id.expiry);
        notificationTag = dialoglayout.findViewById(R.id.notificationTag);
        declineButton = dialoglayout.findViewById(R.id.declineButton);
        payButton = dialoglayout.findViewById(R.id.payButton);
        expiryButton = dialoglayout.findViewById(R.id.expiryButton);
        notitificationTypeImageView = dialoglayout.findViewById(R.id.notificationTypeImageView);
        notificationTypeTextView = dialoglayout.findViewById(R.id.notificationTypeTextView);
        CardView cv = dialoglayout.findViewById(R.id.view);

        delete.setVisibility(View.GONE);

        //Title.setText(mNotificationAlerts.get());
        //swapData(mNotificationAlerts)


        POJONotification alert = arrayList.get(arrayList.size() - 1);
        Title.setText(alert.getMessage());
        Message.setText(alert.getTitle());
        amount.setText("\u20B9" + alert.getAmount());
        textNotificationTime.setText(convertTimeToDate(alert.getNotifyTime()));
        ID.setText(String.valueOf(alert.getId()));
        notifiPhotoSmall.setVisibility(View.VISIBLE);
        notifiPhotoSmall.setImageDrawable(getDrawableFromText(alert.getMessage(), this));
        notifiPhoto.setVisibility(View.GONE);
        notificationTag.setText(alert.getType());

        if (alert.getType().equalsIgnoreCase("authentication")) {
            notitificationTypeImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_push_notification));
            notificationTypeTextView.setText("Push Notification");
        } else {
            notitificationTypeImageView.setImageDrawable(getResources().getDrawable(R.drawable.offline_otp_gray));
            notificationTypeTextView.setText("Offline Otp");
        }

        if (!TextUtils.isEmpty(alert.getExpiry()) && alert.getExpiry() != null) {
            if (Long.parseLong(alert.getExpiry()) <= System.currentTimeMillis()) {
                cv.setAlpha((float) 0.5);
                expiryButton.setVisibility(View.VISIBLE);
                payButton.setVisibility(View.GONE);
                declineButton.setVisibility(View.GONE);
            } else {
                cv.setAlpha(1);
                expiryButton.setVisibility(View.GONE);
                payButton.setVisibility(View.VISIBLE);
                declineButton.setVisibility(View.VISIBLE);
            }
        } else {
            cv.setAlpha(1);
            expiryButton.setVisibility(View.GONE);
            payButton.setVisibility(View.VISIBLE);
            declineButton.setVisibility(View.VISIBLE);
        }


        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (alertDialogPayment != null) {
                    alertDialogPayment.dismiss();
                }
                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();

                //PayDeclineCall payDeclineCall = new PayDeclineCall();
                // payDeclineCall.execute(mNotificationAlerts.get(mNotificationAlerts.size()-1).getTxnId(),"declined");
                progressDialog = new ProgressDialog(HomeActivity.this);
                progressDialog.setMessage("Transaction in progress\n\n Please wait ...");
                progressDialog.show();
                progressDialog.setCancelable(false);

                updateTxnStatus(HomeActivity.this, "D", mNotificationAlerts.size() - 1);
            }
        });

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    /*AppUtil.showColoredSnackBar(mainLayout, "Payment Successful", false, "", 0, true,ContextCompat.getColor(HomeActivity.this, R.color.green), ContextCompat.getColor(HomeActivity.this, R.color.white), Snackbar.LENGTH_SHORT);
                    //Toast.makeText(HomeActivity.this, "Payment Successful", Toast.LENGTH_SHORT).show();
                    deleteNotificationLog();*/
                if (alertDialogPayment != null) {
                    alertDialogPayment.dismiss();
                }

                NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();

                progressDialog = new ProgressDialog(HomeActivity.this);
                progressDialog.setMessage("Transaction in progress\n\n Please wait ...");
                progressDialog.show();
                updateTxnStatus(HomeActivity.this, "A", mNotificationAlerts.size() - 1);
            }
        });


        // create alert dialog
        alertDialogPayment = alertDialogBuilder.create();
        alertDialogPayment.setCanceledOnTouchOutside(true);
        alertDialogPayment.setCancelable(true);
        // show it
        alertDialogPayment.show();
    }


    HomeInterface homeInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        activity = HomeActivity.this;


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {

                            homeInterface = new HomeScreenImpl(HomeActivity.this, null);

                        } else {

                            // Get new Instance ID token
                            String token = task.getResult().getToken();

                            homeInterface = new HomeScreenImpl(HomeActivity.this, token);

                            //TODO register token to your server.
                        }

                        boolean isRegistered = homeInterface.isRegisterCheck();

                        if (isRegistered) {
                            showColoredSnackBar(mainLayout, "Registered Successfully ", false, "", 0, true, ContextCompat.getColor(HomeActivity.this, R.color.green), ContextCompat.getColor(HomeActivity.this, R.color.white), Snackbar.LENGTH_SHORT);
                        } else {
                            showColoredSnackBar(mainLayout, "Verified Successfully ", false, "", 0, true, ContextCompat.getColor(HomeActivity.this, R.color.green), ContextCompat.getColor(HomeActivity.this, R.color.white), Snackbar.LENGTH_SHORT);
                        }


                    }
                });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }


        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        toolbar.setTitleTextColor(getResources().getColor(R.color.defaultwhite));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.bank_side);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultwhite), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        recyclerViewMain = findViewById(R.id.recyclerViewMain);
        mainLayout = findViewById(R.id.mainRegisteredUserLayout);
        deregisteredUserLayout = findViewById(R.id.deregisteredUserLayout);
        mainLayout.setVisibility(View.VISIBLE);
        deregisteredUserLayout.setVisibility(View.GONE);
        exitButton = findViewById(R.id.exitButton);
        fragmentPlaceholder = findViewById(R.id.fragment_placeholder);

        addSlider();


        initializeUI();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }


    @Override
    public void onPause() {
        super.onPause();

        if (notificationBR != null) {
            LocalBroadcastManager.getInstance(HomeActivity.this).unregisterReceiver(notificationBR);
        }
        if (notificationClearBR != null) {
            LocalBroadcastManager.getInstance(HomeActivity.this).unregisterReceiver(notificationClearBR);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(HomeActivity.this).registerReceiver(notificationBR, new IntentFilter("notificationBR"));
        LocalBroadcastManager.getInstance(HomeActivity.this).registerReceiver(notificationClearBR, new IntentFilter("notifyClearBR"));

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            Drawer.openDrawer(GravityCompat.START);

        } else if (i == R.id.notification) {
            Intent offerIntent = new Intent(getApplicationContext(), OfferAlertsNotifications.class);
            startActivity(offerIntent);
        }
        return super.onOptionsItemSelected(item);

    }

    private void initializeUI() {


        homeIconsList = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(HomeActivity.this, 2);
        recyclerViewMain.setLayoutManager(gridLayoutManager);
        recyclerViewMain.setItemAnimator(new DefaultItemAnimator());
        recyclerViewMain.setHasFixedSize(true);

        // add number of tabs you want in home screen with proper icon
        for (int i = 0; i < homeTextOfIcons.length; i++) {
            HomeScreenFeatureModel pojoScanEkyc = new HomeScreenFeatureModel();
            pojoScanEkyc.setTextView(homeTextOfIcons[i]);
            pojoScanEkyc.setImageFile(homeIconImages[i]);
            homeIconsList.add(pojoScanEkyc);
        }


        AdapterHomeIcons adapterScanEkyc = new AdapterHomeIcons(HomeActivity.this, HomeActivity.this, homeIconsList);
        recyclerViewMain.setAdapter(adapterScanEkyc);
        adapterScanEkyc.notifyDataSetChanged();


        recyclerViewMain.addOnItemTouchListener(new RecyclerItemClickListener(HomeActivity.this, recyclerViewMain, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0) { // offline otp


                    Intent intent = new Intent(HomeActivity.this, TOTPviewerActivity.class);
                    startActivity(intent);

                }
                if (position == 1) { // transaction history

                    Intent intent = new Intent(HomeActivity.this, TransactionHistory.class);
                    startActivity(intent);


                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        setUpDrawer();

    }

    public void setUpDrawer() {
        Drawer = findViewById(R.id.DrawerLayout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(HomeActivity.this);
        View navHeader = navigationView.getHeaderView(0);
        TextView textViewUserProfileName = navHeader.findViewById(R.id.name);
        TextView versionTextView = findViewById(R.id.versionTextView);

        //versionTextView.setText("v" + BuildConfig.VERSION_NAME);
        imageViewUserProfileImage = navHeader.findViewById(R.id.circleView);
        TextView lastLoginValue = navHeader.findViewById(R.id.last_login);



        headerLayoutNavigationView = navHeader.findViewById(R.id.headerLayoutNavigationView);
        ImageView logoutButton = navHeader.findViewById(R.id.logoutButton);

        headerLayoutNavigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add profile view if needed
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(HomeActivity.this)
                        .setIcon(R.drawable.ic_baseline_logout_black)
                        .setTitle("LOGOUT")
                        .setMessage(R.string.really_quit)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                showColoredSnackBar(mainLayout, "Logout Successfully",
                                        false, "", 0, true,
                                        ContextCompat.getColor(HomeActivity.this, R.color.green),
                                        ContextCompat.getColor(HomeActivity.this, R.color.white),
                                        Snackbar.LENGTH_SHORT);

                                homeInterface.logout();
                                finish();

                            }

                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Log.e(getClass().getName(), "id = " + id);
        if (id == R.id.nav_home) {


            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else if (id == R.id.nav_Deregister) {
          /*  Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);*/

            progressDialog = new ProgressDialog(HomeActivity.this);


            new AlertDialog.Builder(HomeActivity.this)
                    .setIcon(R.drawable.deregister)
                    .setTitle("DE-REGISTER")
                    .setMessage(R.string.really_degister)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            progressDialog.setMessage("Deregistering your account, please wait...");
                            progressDialog.show();
                            homeInterface.deregisterRequest();
                        }

                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        } else if (id == R.id.nav_email) {


            sendEmail("Query/Feedback on  Authentication Android App");
        } else if (id == R.id.nav_call) {


            String number = "080-25318423";
            String uri = "tel:" + number.trim();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(uri));
            startActivity(intent);
        } else {


            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.DrawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void sendEmail(String sub) {
        //Log.i("Send email", "");
        String build_version = "";
        String app_version = "";
        String sdk_version = String.valueOf(Build.VERSION.SDK_INT);
        String phone_manufacturer = Build.MANUFACTURER;
        String phone_model = Build.MODEL;
        TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //String phone_imeir = mngr.getDeviceId();

        try {
            PackageInfo pInfo = null;
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            build_version = String.valueOf(pInfo.versionCode);
            app_version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }

        String subject = sub;//"Feedback on Android app - Mypoolin";
       /* String body = "               Device Info           " + "\n"
                + "Registered Mobile Number: " + SharedPreferenceEditor.getUserMobile(HomeActivity.this) + "\n"
                + "Registered User Name: " + MyPoolinUtil.getUserName(HomeActivity.this) + "\n"
                + "Registered User Email: " + MyPoolinUtil.getUserEmail(HomeActivity.this) + "\n"
                + "Build Version: " + build_version + "\n"
                + "App Version: " + app_version + "\n"
                + "SDK Version: " + sdk_version + "\n"
                + "Phone Manufacturer: " + phone_manufacturer.toUpperCase() + "\n"
                + "Phone Model: " + phone_model + "\n"
                + "This is important for us to serve you better :) " + "\n\n\n\n"
                + "Please Enter Your Query Here - " + "\n";*/

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"hocancard@asd.com"});
        final PackageManager pm = HomeActivity.this.getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        if (best != null)
            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        HomeActivity.this.startActivity(emailIntent);

    }


    public void showColoredSnackBar(View view, String message, boolean isHasAction, String actionText, int actionColor,
                                    boolean isHasMessageColor, int backgroundColor, int messageColor, int duration) {
        final Snackbar snackbar = Snackbar.make(view, message, duration);
        if (isHasAction) {
            snackbar.setAction(actionText, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.setActionTextColor(actionColor);
        }
        if (isHasMessageColor) {
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(backgroundColor);
            TextView textView = sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(messageColor);
        }
        snackbar.show();
    }


    private void updateTxnStatus(final Context mContext, final String pushStatus, final int positionPassed) {


        homeInterface.updateTransactionStatus(pushStatus, mNotificationAlerts.get(positionPassed).getTxnId(),
                mNotificationAlerts.get(positionPassed).getRequestrTxnId(), mNotificationAlerts.get(positionPassed).getTitle());
    }

    @Override
    public void onTransactionStatus(String result) {

        progressDialog.dismiss();
        UIUtil.showColoredSnackBar(mainLayout, result, false, "", 0, true, ContextCompat.getColor(activity, R.color.green), ContextCompat.getColor(activity, R.color.white), Snackbar.LENGTH_LONG);
        deleteNotificationLog();
    }


    private void addSlider() {
        SliderFragment fragment = new SliderFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, fragment);
        ft.commit();
    }

    @Override
    public void onDeregisterSuccess() {
        progressDialog.dismiss();

        mainLayout.setVisibility(View.GONE);
        deregisteredUserLayout.setVisibility(View.VISIBLE);
        navigationView.setVisibility(View.GONE);
        deregisteredUserLayout.setVisibility(View.VISIBLE);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @Override
    public void onDeregisterFailed(String reason) {
        progressDialog.dismiss();
        UIUtil.showColoredSnackBar(mainLayout, reason, false, "", 0, true, ContextCompat.getColor(activity, R.color.green), ContextCompat.getColor(activity, R.color.white), Snackbar.LENGTH_LONG);


        // show error message and take appropriate action
    }


    public Drawable getDrawableFromText(String data, Context context) {
        String text = "";
        StringBuilder sb = new StringBuilder();
        if (data != null) {
            if (!data.isEmpty()) {
                if (data.contains(" ")) {
                    String[] str = data.split("[\\s]+");
                    if (str != null && str.length != 0) {
                        if (str.length > 0) {
                       /* for (int i = 0; i <str.length; i++) {
                            if (str[i].trim().length() != 0) {
                                sb.append(str[i].trim().substring(0, 1).toUpperCase());
                            }
                        }*/

                            if (str[0].trim().length() != 0) {
                                sb.append(str[0].trim().substring(0, 1).toUpperCase());
                            }
                            if (str.length > 1) {
                                if (str[1].trim().length() != 0) {
                                    sb.append(str[1].trim().substring(0, 1).toUpperCase());
                                }
                            }
                            text = sb.toString();
                        } else {
                            text = data.substring(0, 1).toUpperCase();
                        }

                    } else {
                        text = data.substring(0, 1).toUpperCase();
                    }
                } else {
                    text = data.substring(0, 1).toUpperCase();
                }

            } else {
                text = "@";
            }
        } else {
            text = "*";
        }
        int color = context.getResources().getColor(com.wibmo.sdk.R.color.brand_color);

       /* if (position % 4 == 1)
            color = context.getResources().getColor(R.color.brand_color);
        if (position % 4 == 2)
            color = context.getResources().getColor(R.color.brand_color_dark);
        if (position % 4 == 3)
            color = context.getResources().getColor(R.color.amount_gray);*/
        return TextDrawable.builder().buildRound(text, color);
    }
    /* This function works for time in milliseconds */
    public static String convertTimeToDate(String time) {
        String dateFormat = "dd MMM yyyy hh:mm:ss a";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(time));
        return simpleDateFormat.format(calendar.getTime());
    }

}
