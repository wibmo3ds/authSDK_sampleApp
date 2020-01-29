package wibmo.com.main.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

import wibmo.com.wibmoAuthApp.R;
import wibmo.com.main.model.HomeScreenFeatureModel;

/**
 * Created by abhisheksingh on 4/16/18.
 */

public class AdapterHomeIcons extends RecyclerView.Adapter<AdapterHomeIcons.ViewHolder>{
    Context context;
    Activity activity;
    ArrayList<HomeScreenFeatureModel> cList;


    public AdapterHomeIcons(Context context, Activity activity, ArrayList<HomeScreenFeatureModel> arrayList){
        this.context =context;
        this.activity=activity;
        this.cList =arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_home_icons,parent,false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imageView.setImageResource(cList.get(position).getImageFile());
        holder.text.setText(cList.get(position).getTextView());
    }

    @Override
    public int getItemCount() {
        return cList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.scanDocumentIv);
            text = itemView.findViewById(R.id.scanDocumentTv);
        }
    }
}

