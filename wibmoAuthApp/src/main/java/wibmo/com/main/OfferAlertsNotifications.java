package wibmo.com.main;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.google.firebase.messaging.RemoteMessage;
import com.wibmo.authcallback.NotificationCallBack;
import com.wibmo.authcallback.NotificationInterface;
import com.wibmo.device.NotificationImpl;

import java.util.HashMap;
import java.util.List;

import wibmo.com.main.Database.NotificationAlert;
import wibmo.com.main.adapter.AdapterNotifications;

import static com.clevertap.android.sdk.CleverTapAPI.getInstance;

import wibmo.com.main.notification.NotificationIntentHelper;
import wibmo.com.wibmoAuthApp.R;

public class OfferAlertsNotifications extends AppCompatActivity implements NotificationCallBack {

    public static String TAG = OfferAlertsNotifications.class.getSimpleName();

    CleverTapAPI cleverTap;
    RelativeLayout emptyLayout;
    private List<NotificationAlert> mNotificationAlerts = ApplicationSingleton.getInstance().getNotitifcationList();

    RecyclerView rvAlerts;
    private AdapterNotifications rvAdapter;

    private NotificationInterface notificationInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_alerts_notifications);

        notificationInterface = new NotificationImpl(this, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.defaultwhite));
        toolbar.setTitle("Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_24px);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultwhite), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(getIntent().getIntExtra("id", 0));

        emptyLayout = findViewById(R.id.emptyLayout);

        rvAdapter = new AdapterNotifications(this, notificationInterface);
        rvAlerts = findViewById(R.id.rvAlerts);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        rvAlerts.setLayoutManager(mLayoutManager);
        rvAlerts.setAdapter(rvAdapter);


        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.brand_color_dark));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.brand_color_dark));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.drawer_side_notification, menu);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();

        if (i == R.id.action_clear_notification) {
            final Context context = OfferAlertsNotifications.this;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle(R.string.clear_all_notification);
            builder.setCancelable(true);

            builder
                    .setMessage(R.string.clear_all_notification_message)
                    .setCancelable(false)
                    .setPositiveButton("Yes", new OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {


                            ApplicationSingleton.getInstance().getAppExecutors()
                                    .diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    ApplicationSingleton.getInstance().getAppDatabase()
                                            .mNotificationAlertDao()
                                            .deleteAll();
                                }
                            });

                            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancelAll();
                        }
                    })
                    .setNegativeButton("No", new OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {


                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        } else if (i == android.R.id.home) {
            finish();

        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        observeNotificationLiveData();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void observeNotificationLiveData() {
        ApplicationSingleton.getInstance().getAppDatabase()
                .mNotificationAlertDao()
                .getAll().observe(this, new android.arch.lifecycle.Observer<List<NotificationAlert>>() {
            @Override
            public void onChanged(@Nullable List<NotificationAlert> notificationAlerts) {
                swapData(notificationAlerts);
            }
        });
    }


    public void swapData(List<NotificationAlert> notificationAlerts) {

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();


        this.mNotificationAlerts = notificationAlerts;
        if (mNotificationAlerts != null) {
            if (mNotificationAlerts.size() > 0) {
                emptyLayout.setVisibility(View.GONE);
            } else {
                emptyLayout.setVisibility(View.VISIBLE);
            }
        } else {
            emptyLayout.setVisibility(View.VISIBLE);
        }

        rvAdapter.swapData(mNotificationAlerts);
    }

    @Override
    public void onPaymentSuccess(String result) {


        Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_SHORT).show();
        rvAdapter.enableButton(true);
        rvAdapter.deleteNotificationLog();


    }

    @Override
    public void onPaymentFailed(String reason) {
        Toast.makeText(getApplicationContext(), reason, Toast.LENGTH_SHORT).show();
        rvAdapter.enableButton(true);

    }
}
