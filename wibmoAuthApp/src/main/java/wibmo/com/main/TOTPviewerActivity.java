package wibmo.com.main;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wibmo.authcallback.GenerateTOTP;
import com.wibmo.authcallback.OnTOTPChange;
import com.wibmo.sdk.OfflineOTP;


import java.util.Date;

import wibmo.com.wibmoAuthApp.R;

/**
 * Created by abhisheksingh on 6/8/18.
 */

public class TOTPviewerActivity extends AppCompatActivity implements OnTOTPChange {

    private static String TAG = TOTPviewerActivity.class.getSimpleName();
    TextView showTotpText;
    Date now;
    ProgressBar progressBar;
    TextView timerText;
    ImageView timerImage;
    RelativeLayout mainLayout;
    TextView dateTimeStamp;
    LinearLayout totpLayout;


    private GenerateTOTP generateTOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_totp_viewer);

        generateTOTP = new OfflineOTP(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }


        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Offline Otp");
        toolbar.setTitleTextColor(getResources().getColor(R.color.defaultwhite));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_24px);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultwhite), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        mainLayout = findViewById(R.id.mainLayout);
        totpLayout = findViewById(R.id.linearLayout);

        progressBar = findViewById(R.id.pbHeaderProgress);
        progressBar.setMax(60);
        timerText = findViewById(R.id.timerText);
        timerImage = findViewById(R.id.timer);
        dateTimeStamp = findViewById(R.id.dateTimeStamp);
        showTotpText = findViewById(R.id.showToptText);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (generateTOTP.isDeviceTimeSynced(getApplicationContext())) {

                generateTOTP.getTOTP();
            } else {

                // ask user to sync device time to generate valid TOTP
                showColoredSnackBar(mainLayout, "Auto Date Time Setting Not Enabled", false, "", 0, true, ContextCompat.getColor(TOTPviewerActivity.this, R.color.green), ContextCompat.getColor(TOTPviewerActivity.this, R.color.white), Snackbar.LENGTH_SHORT);
                showDateTimeSettingPopup();
            }

        }


        totpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Totp_Generated", showTotpText.getText().toString());
                clipboard.setPrimaryClip(clip);
                UIUtil.showSnackBar(mainLayout, showTotpText.getText().toString() + " copied to clipboard", false, "", 0, true, ContextCompat.getColor(TOTPviewerActivity.this, R.color.white), Snackbar.LENGTH_SHORT);

            }
        });


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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        generateTOTP.onDetach();
    }


    public void showDateTimeSettingPopup() {
        new AlertDialog.Builder(TOTPviewerActivity.this)
                .setIcon(R.drawable.ic_baseline_access_time_24px)
                .setTitle("Time Setting Not Automatic")
                .setMessage(R.string.auto_time_setting_message)
                .setPositiveButton(R.string.open_settings, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_DATE_SETTINGS));
                        finish();
                        dialog.dismiss();
                    }

                })
                .setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_wibmo_icon, menu);
        return true;
    }

    @Override
    public void onTimeChange(String time) {
        progressBar.setProgress(Integer.parseInt(time));
        timerText.setText(time);
    }

    @Override
    public void onTOTPChange(String otp) {

        progressBar.setProgress(0);
        showTotpText.setText(otp);
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


}
