package wibmo.com.main;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wibmo.sdk.WibmoSDK;
import wibmo.com.wibmoAuthApp.R;

/**
 * Created by nithyak on 29/08/17.
 */

public class UIBaseActivity extends AppCompatActivity {
    private static final String TAG = "UIBaseActivity";

    protected Activity activity;
    protected View mCustomActionBar;
    protected TextView titleText;
    protected TextView subTitleText;
    protected ImageView rightIcon;

    private String tbarBGColor;
    private String fontColor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        tbarBGColor = "#019ed7";
        fontColor = activity.getString(R.string.whiteColor);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void setActionBarTitle(String title) {
        /*if (title != null) {
            titleText.setText(title);
        } else {
            titleText.setText(activity.getTitle());
            activity.setTitle("");
        }
        titleText.setTextColor(Color.parseColor(fontColor));*/
    }

    protected void initCustomActionBar() {
        /*setupCustomActionBar();
        setNavigateIcon();
        setActionBarTitle(null);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setCustomView(mCustomActionBar,
                    new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
            getSupportActionBar().setDisplayShowCustomEnabled(true);

            Toolbar parent = (Toolbar) mCustomActionBar.getParent();//first get parent toolbar of current action bar

            getSupportActionBar().setCustomView(mCustomActionBar);
            parent.setContentInsetsAbsolute(0, 0);
        } else {
            Log.w(TAG, "getSupportActionBar was null");
        }*/
    }


}
