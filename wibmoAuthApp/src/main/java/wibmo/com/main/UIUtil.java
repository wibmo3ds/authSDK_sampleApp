package wibmo.com.main;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import wibmo.com.wibmoAuthApp.R;
class UIUtil {

    private static final String TAG = "UIUtil";


    public static final String RES_SUCCESS = "Success";
    public static final String RES_FAILED = "Failed";
    public static final String KEY_REG = "regStatus";

    public static final String RES_SUCCESS_CODE = "000";
    public static void showErrorToast(final Context context, final String msg) {
        Log.i(TAG, "Show Toast: " + msg);
        if (msg == null || context == null) return;

                Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);

                toast.show();

    }

    public static void showSnackBar(View view, String message, boolean isHasAction, String actionText, int actionColor, boolean isHasMessageColor, int messageColor, int duration) {
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
            TextView textView = sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(messageColor);
        }
        snackbar.show();
    }

    public static void showColoredSnackBar(View view, String message, boolean isHasAction, String actionText, int actionColor, boolean isHasMessageColor,int backgroundColor, int messageColor, int duration) {
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
