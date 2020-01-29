package wibmo.com.main.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wibmo.Common.AppConstants;

import com.wibmo.sdk.WibmoSDK;

import wibmo.com.wibmoAuthApp.R;

/**
 * Created by nithyak on 07/09/17.
 */

public class SliderImageFragment extends Fragment {
    private static final String TAG = "SliderImageFragment";

    private View view = null;
    private ImageView intoImageView;

    private int instance;
    private WibmoSDK wibmoSDK;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = getArguments() != null ? getArguments().getInt("instance") : -1;
        Log.v(TAG, "onCreate "+instance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView: "+instance);
        super.onCreateView(inflater, container, savedInstanceState);

        wibmoSDK = WibmoSDK.getInstance();

        view = inflater.inflate(R.layout.slider_image_fragment,
                container, false);
        return view;
    }

    public void update() {
        if(view==null) {
            Log.w(TAG, "in update instance: "+instance+"; view is null will return");
            return;
        } else {
            Log.v(TAG, "in update instance: "+instance);
        }

        try {
            intoImageView = view.findViewById(R.id.main_bg);

            String resIntroDrawable = AppConstants.S3_BASE_URL + "/pic_1.png";

            if (instance <= 0) {
                resIntroDrawable = AppConstants.S3_BASE_URL + "/pic_1.png";
            } else if (instance == 1) {
                resIntroDrawable =  AppConstants.S3_BASE_URL + "/pic_2.png";
            }else if(instance == 2){
                resIntroDrawable =  AppConstants.S3_BASE_URL + "/pic_3.png";
            }

            Glide.with(getActivity())
                    .load(resIntroDrawable)
                    .into(intoImageView);

        } catch (Exception e) {
            Log.e(TAG, "Error: "+e);
        }
    }

    public static SliderImageFragment newInstance(int instance) {
        SliderImageFragment fragment = new SliderImageFragment();

        Bundle args = new Bundle();
        args.putInt("instance", instance);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.v(TAG, "onActivityCreated "+instance);
        update();
    }


}
