package wibmo.com.main.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.wibmo.core.TransactionHistoryModel;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import wibmo.com.wibmoAuthApp.R;


//import com.app.mypoolin.Pool.PoolViewActivity;
//import com.app.mypoolin.Pool.PoolViewDecision;

public class AdapterTransactionHistory extends
    RecyclerView.Adapter<AdapterTransactionHistory.ViewHolder> {

  ArrayList<TransactionHistoryModel> cList;


  public AdapterTransactionHistory( ArrayList<TransactionHistoryModel> arrayList){

    this.cList =arrayList;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_transaction_history,parent,false);
    ViewHolder vh = new ViewHolder(view);
    return vh;
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    holder.merchantName.setText(cList.get(position).getMerchantName());
    holder.productName.setText(capitalize(cList.get(position).getProductName().toLowerCase(),null));
    holder.dateTimeStamp.setText(convertToFormattedTimeStamp(cList.get(position).getTimeStamp()));
    holder.amount.setText("\u20B9" + " "+ cList.get(position).getTransactionAmount());
    if(cList.get(position).getValidationMethod().contains("offline") || cList.get(position).getValidationMethod().contains("otp") || cList.get(position).getValidationMethod().contains("OFFLINE_OTP")){
      holder.type.setText("Offline\nOTP");
      holder.notitificationTypeIv.setImageDrawable(holder.notitificationTypeIv.getContext().getResources().getDrawable(R.drawable.offline_otp_gray));
    }else{
      holder.type.setText("Push\nNotification");
      holder.notitificationTypeIv.setImageDrawable(holder.notitificationTypeIv.getContext().getResources().getDrawable(R.drawable.ic_push_notification));
    }
    holder.circleImageView.setImageDrawable(getDrawableFromText(cList.get(position).getMerchantName(), holder.circleImageView.getContext()));
  }


  @Override
  public int getItemCount() {
    return cList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    TextView merchantName,productName,dateTimeStamp;
    TextView amount,type;
    ImageView circleImageView;
    ImageView notitificationTypeIv;

    public ViewHolder(View itemView) {
      super(itemView);
      merchantName = itemView.findViewById(R.id.merchantName);
      productName = itemView.findViewById(R.id.productName);
      dateTimeStamp = itemView.findViewById(R.id.dateTimeStamp);
      amount = itemView.findViewById(R.id.amount);
      type = itemView.findViewById(R.id.type);
      circleImageView = itemView.findViewById(R.id.circleView);
      notitificationTypeIv = itemView.findViewById(R.id.notificationTypeImageView);
    }
  }

  private String capitalize(String str, char[] delimiters) {
    int delimLen = (delimiters == null ? -1 : delimiters.length);
    if (str == null || str.length() == 0 || delimLen == 0) {
      return str;
    }
    int strLen = str.length();
    StringBuffer buffer = new StringBuffer(strLen);
    boolean capitalizeNext = true;
    for (int i = 0; i < strLen; i++) {
      char ch = str.charAt(i);

      if (isDelimiter(ch, delimiters)) {
        buffer.append(ch);
        capitalizeNext = true;
      } else if (capitalizeNext) {
        buffer.append(Character.toTitleCase(ch));
        capitalizeNext = false;
      } else {
        buffer.append(ch);
      }
    }
    return buffer.toString();
  }

  private  boolean isDelimiter(char ch, char[] delimiters) {
    if (delimiters == null) {
      return Character.isWhitespace(ch);
    }
    return true;
  }
    // Format of date come here like this 2018-09-03T15:25:38.953+0530-   To Handle this Type of Date use this function
    public static String convertToFormattedTimeStamp(String actualDate){
        Date date = null;
        String month_name ="";
        String[] actualDateTime;
        //2018-09-03T15:25:38.953+0530

        try {
            SimpleDateFormat month_date = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            actualDateTime = actualDate.split("\\.");
            date = sdf.parse(actualDateTime[0].replace("T"," "));
            month_name = month_date.format(date);//.replace("AM", "am").replace("PM","pm");;
            //month_name = month_name.replace("AM", "am").replace("PM","pm");
            Long timeinMillis = date.getTime();
            System.out.println("Month :" + month_name);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return month_name;
    }

    private Drawable getDrawableFromText(String data, Context context) {
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
        int color = context.getResources().getColor(R.color.brand_color);

       /* if (position % 4 == 1)
            color = context.getResources().getColor(R.color.brand_color);
        if (position % 4 == 2)
            color = context.getResources().getColor(R.color.brand_color_dark);
        if (position % 4 == 3)
            color = context.getResources().getColor(R.color.amount_gray);*/
        return TextDrawable.builder().buildRound(text, color);
    }

}