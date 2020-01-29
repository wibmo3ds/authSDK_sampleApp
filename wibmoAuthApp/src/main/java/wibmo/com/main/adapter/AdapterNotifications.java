package wibmo.com.main.adapter;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.wibmo.authcallback.NotificationInterface;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import wibmo.com.main.ApplicationSingleton;
import wibmo.com.main.Database.NotificationAlert;
import wibmo.com.wibmoAuthApp.R;


//import com.app.mypoolin.Pool.PoolViewActivity;
//import com.app.mypoolin.Pool.PoolViewDecision;

public class AdapterNotifications extends
        RecyclerView.Adapter<AdapterNotifications.NotifyViewHolder> {

    private static final String TAG = AdapterNotifications.class.getSimpleName();
    private List<NotificationAlert> mNotificationAlerts;
    Context activity;
    int positionPassed;
    private ProgressDialog progressDialog;
    private NotificationInterface callBack;
    private int pos;

    public AdapterNotifications(Context activity, NotificationInterface notificationInterface) {
        this.activity = activity;
        callBack = notificationInterface;
        progressDialog = new
                ProgressDialog(activity);

        progressDialog.setMessage("Processing transaction");
        progressDialog.setCancelable(false);
    }


    @Override
    public NotifyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_notification, parent, false);
        return new NotifyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final NotifyViewHolder holder, final int position) {
        Log.v("Binding", "position: " + position);
        positionPassed = position;

        NotificationAlert alert = mNotificationAlerts.get(positionPassed);
        holder.Title.setText(alert.getMessage());
        holder.Message.setText(alert.getTitle());
        holder.amount.setText("\u20B9" + alert.getAmount());
        holder.textNotificationTime.setText(convertTimeToDate(alert.getNotifyTime()));
        holder.ID.setText(String.valueOf(alert.getId()));
        holder.notifiPhotoSmall.setVisibility(View.VISIBLE);
        holder.notifiPhotoSmall.setImageDrawable(getDrawableFromText(alert.getMessage(), activity));
        holder.notifiPhoto.setVisibility(View.GONE);
        holder.notificationTag.setText(alert.getType());

        if (alert.getType().equalsIgnoreCase("authentication")) {
            holder.notitificationTypeImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_push_notification));
            holder.notificationTypeTextView.setText("Push Notification");
        } else {
            holder.notitificationTypeImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.offline_otp_gray));
            holder.notificationTypeTextView.setText("Offline Otp");
        }


        viewHolder = holder;
        enableButton(true);


        if (!TextUtils.isEmpty(mNotificationAlerts.get(positionPassed).getExpiry())) {
            if (Long.parseLong(mNotificationAlerts.get(positionPassed).getExpiry()) <= System.currentTimeMillis()) {
                holder.cv.setAlpha((float) 0.5);
                holder.expiryButton.setVisibility(View.VISIBLE);
                holder.payButton.setVisibility(View.GONE);
                holder.declineButton.setVisibility(View.GONE);
                pos = positionPassed;
                deleteNotificationLog();
            } else {
                holder.cv.setAlpha(1);
                holder.expiryButton.setVisibility(View.GONE);
                holder.payButton.setVisibility(View.VISIBLE);
                holder.declineButton.setVisibility(View.VISIBLE);
            }
        } else {
            holder.cv.setAlpha(1);
            holder.expiryButton.setVisibility(View.GONE);
            holder.payButton.setVisibility(View.VISIBLE);
            holder.declineButton.setVisibility(View.VISIBLE);
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos = positionPassed;
                deleteNotificationLog();
            }
        });

        holder.declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewHolder = holder;
                enableButton(false);
                NotificationAlert alert = mNotificationAlerts.get(holder.getAdapterPosition());

                pos = holder.getAdapterPosition();

                progressDialog.show();
                callBack.updateTransactionStatus("D", alert.getTxnId(), alert.getRequestrTxnId(),alert.getTitle());
                NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
            }
        });

        holder.payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.show();
                viewHolder = holder;
                enableButton(false);
                pos = holder.getAdapterPosition();

                NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
                NotificationAlert alert = mNotificationAlerts.get(holder.getAdapterPosition());

                callBack.updateTransactionStatus("Y", alert.getTxnId(), alert.getRequestrTxnId(),alert.getTitle());

            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotificationAlerts != null ? mNotificationAlerts.size() : 0;
    }

    public void swapData(List<NotificationAlert> notificationAlerts) {
        this.mNotificationAlerts = notificationAlerts;
        this.notifyDataSetChanged();
    }

    public class NotifyViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
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
        RelativeLayout mainLayout;

        NotifyViewHolder(final View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.view);
            Title = itemView.findViewById(R.id.textViewAlertNotifyTitle);
            ID = itemView.findViewById(R.id.textViewNotifyID);
            Message = itemView.findViewById(R.id.textViewMessage);
            amount = itemView.findViewById(R.id.textViewAmount);
            textNotificationTime = itemView.findViewById(R.id.tvNotificationTime);
            delete = itemView.findViewById(R.id.imageButtonDelete);
            notifiPhoto = itemView.findViewById(R.id.imageViewAlertnotify);
            notifiPhotoSmall = itemView.findViewById(R.id.imageViewAlertnotifysmall);
            //linearLayout = itemView.findViewById(R.id.expiry);
            notificationTag = itemView.findViewById(R.id.notificationTag);
            declineButton = itemView.findViewById(R.id.declineButton);
            payButton = itemView.findViewById(R.id.payButton);
            expiryButton = itemView.findViewById(R.id.expiryButton);
            notitificationTypeImageView = itemView.findViewById(R.id.notificationTypeImageView);
            notificationTypeTextView = itemView.findViewById(R.id.notificationTypeTextView);
            mainLayout = itemView.findViewById(R.id.mainLayoutNotification);
        }
    }


    public void deleteNotificationLog() {
        try {

            if (mNotificationAlerts.size() > 0) {
                final NotificationAlert alert = mNotificationAlerts.get(pos);
                ApplicationSingleton.getInstance().getAppExecutors()
                        .diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        ApplicationSingleton.getInstance().getAppDatabase()
                                .mNotificationAlertDao()
                                .delete(alert);
                    }
                });
                notifyDataSetChanged();
            } else {
                return;
            }
        } catch (Exception e) {
            Log.d("abhi_exception", TAG + " " + e);
        }
    }
    NotifyViewHolder viewHolder;

    public void enableButton(boolean flag) {
        if (flag) {
            viewHolder.declineButton.setEnabled(true);
            viewHolder.payButton.setEnabled(true);
            viewHolder.declineButton.setAlpha(1);
            viewHolder.payButton.setAlpha(1);

            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }

        } else {
            viewHolder.declineButton.setEnabled(false);
            viewHolder.payButton.setEnabled(false);
            viewHolder.declineButton.setAlpha((float) 0.5);
            viewHolder.payButton.setAlpha((float) 0.5);

        }
    }

    public  Drawable getDrawableFromText(String data, Context context) {
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