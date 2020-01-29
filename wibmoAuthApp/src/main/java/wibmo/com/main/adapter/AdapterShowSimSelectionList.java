package wibmo.com.main.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wibmo.Common.SIMInfo;



import java.util.ArrayList;
import java.util.Arrays;

import wibmo.com.wibmoAuthApp.R;
import wibmo.com.main.callback.OnSimAdapterItemClick;

public class AdapterShowSimSelectionList extends RecyclerView.Adapter<AdapterShowSimSelectionList.simSelectionHolder> {
    private static final String TAG = "AdapterShowSimSelection";
    ArrayList<SIMInfo> simInfoArrayList;
    Boolean[] booleans;

    private OnSimAdapterItemClick onSimAdapterItemClick;

    public AdapterShowSimSelectionList(OnSimAdapterItemClick callBack, ArrayList<SIMInfo> simInfoArrayList) {
        this.simInfoArrayList = simInfoArrayList;
        this.onSimAdapterItemClick = callBack;
        booleans = new Boolean[simInfoArrayList.size()];
        Arrays.fill(booleans, false);
    }

    @Override
    public simSelectionHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_sim_info, viewGroup, false);
        AdapterShowSimSelectionList.simSelectionHolder simSelectionHolder = new AdapterShowSimSelectionList.simSelectionHolder(itemView);
        return simSelectionHolder;
    }

    @Override
    public void onBindViewHolder(final simSelectionHolder holder, int position) {
        final int pos = position;

        holder.sim1Text.setText(simInfoArrayList.get(pos).getCarrierName().toUpperCase());

        holder.simLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Arrays.fill(booleans, false);
                booleans[holder.getAdapterPosition()] = true;


                onSimAdapterItemClick.onSimClick(simInfoArrayList.get(holder.getAdapterPosition()));
                notifyDataSetChanged();
            }
        });

        if (booleans[pos]) {
            holder.simLayout1.setAlpha(1.0f);
            holder.sim1Tick.setVisibility(View.VISIBLE);
        } else {
            holder.simLayout1.setAlpha(0.5f);
            holder.sim1Tick.setVisibility(View.GONE);
        }

        String url = "https://d1cysmyps5mkdu.cloudfront.net/network_operators/airtel.svg";
        holder.operatorImageView1.setImageResource(getOperatorImage(simInfoArrayList.get(pos).getCarrierName().trim()));




    }

    @Override
    public int getItemCount() {
        return simInfoArrayList.size();
    }

    public SIMInfo getItem(int i) {
        return simInfoArrayList.get(i);
    }

    public class simSelectionHolder extends RecyclerView.ViewHolder {
        RelativeLayout simLayout1, sim1Innerlayout;
        ImageView sim1ImageView, simCardImageView1, operatorImageView1;
        TextView sim1Text;
        ImageView sim1Tick;

        public simSelectionHolder(View itemView) {
            super(itemView);
            simLayout1 = itemView.findViewById(R.id.simLayout1);
            sim1Text = itemView.findViewById(R.id.sim1Text);
            sim1ImageView = itemView.findViewById(R.id.sim1ImageView);
            simCardImageView1 = itemView.findViewById(R.id.simCardImageView1);
            operatorImageView1 = itemView.findViewById(R.id.operatorImageView1);
            //sim1Innerlayout = itemView.findViewById(R.id.sim1Innerlayout);
            sim1Tick = itemView.findViewById(R.id.sim1Tick);
        }
    }


    private int getOperatorImage(String operatorName){

        operatorName = operatorName.trim().toString();
        int imageDrawable = R.drawable.ic_airtel;

        if(operatorName.equalsIgnoreCase("airtel") || operatorName.contains("airtel")){
            imageDrawable = R.drawable.ic_airtel;
        } else if(operatorName.equalsIgnoreCase("vodafone in") || operatorName.contains("vodafone")){
            imageDrawable = R.drawable.ic_vodafone;
        } else if(operatorName.equalsIgnoreCase("aircel") || operatorName.contains("aircel")){
            imageDrawable = R.drawable.ic_aircel;
        } else if (operatorName.equalsIgnoreCase("bsnl") || operatorName.contains("bsnl")){
            imageDrawable = R.drawable.ic_bsnl;
        } else if(operatorName.equalsIgnoreCase("docomo") || operatorName.contains("docomo")){
            imageDrawable = R.drawable.ic_docomo;
        } else if(operatorName.equalsIgnoreCase("idea") || operatorName.contains("idea")){
            imageDrawable = R.drawable.ic_idea;
        } else if(operatorName.equalsIgnoreCase("jio") || operatorName.contains("jio")){
            imageDrawable = R.drawable.ic_jio;
        } else if(operatorName.equalsIgnoreCase("mtnl") || operatorName.contains("mtnl")){
            imageDrawable = R.drawable.ic_mtnl;
        } else if (operatorName.equalsIgnoreCase("mts") || operatorName.contains("mts")){
            imageDrawable = R.drawable.ic_mts;
        } else if(operatorName.equalsIgnoreCase("reliance") || operatorName.contains("reliance")){
            imageDrawable = R.drawable.ic_reliance;
        } else if(operatorName.equalsIgnoreCase("indicom") || operatorName.contains("indicom") || operatorName.contains("tata")){
            imageDrawable = R.drawable.ic_tata_indicom;
        } else if(operatorName.equalsIgnoreCase("videocon") || operatorName.contains("videocon")){
            imageDrawable = R.drawable.ic_videocon;
        }
        return imageDrawable;
    }
}

