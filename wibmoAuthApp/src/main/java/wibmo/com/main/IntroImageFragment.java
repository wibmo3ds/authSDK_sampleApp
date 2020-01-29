package wibmo.com.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wibmo.sdk.WibmoSDK;

import wibmo.com.wibmoAuthApp.R;

/**
 * Created by nithyak on 07/09/17.
 */

public class IntroImageFragment extends Fragment {
    private static final String TAG = "SliderImageFragment";

    private View view = null;
    private ImageView intoImageView;
    TextView bulletText1,bulletText2,footerTopText;

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

        view = inflater.inflate(R.layout.fragment_intro_image,
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
            footerTopText = view.findViewById(R.id.footerTopText);
            bulletText1 = view.findViewById(R.id.bulletText1);
            bulletText2 = view.findViewById(R.id.bulletText2);

            int resIntroDrawable = R.drawable.onboarding_img_1;

            if (instance <= 0) {
                resIntroDrawable = R.drawable.onboarding_img_1;
                footerTopText.setText(R.string.two_step_easy_registration);
                bulletText1.setText(R.string.choose_your_registered_mobile_number);
                bulletText2.setText(R.string.verify_device_set_pin);
            } else if (instance == 1) {
                resIntroDrawable = R.drawable.onboarding_img_2;
                footerTopText.setText(R.string.easy_payment_authentication);
                bulletText1.setText(R.string.accept_decline_via_push_notification);
                bulletText2.setText(R.string.generate_otp_on_your_device);
            }

            Glide.with(getActivity())
                    .load(resIntroDrawable)
                    .into(intoImageView);



        } catch (Exception e) {
            Log.e(TAG, "Error: "+e);
        }
    }

    /*private void applyUICustomisation() {
            String labelFontName = getActivity().getString(R.string.sansFontName);
            int labelTextSize = getActivity().getResources().getInteger(R.integer.headerTextSize);
            String labelFontColor = getActivity().getString(R.string.blueColor);

            if(wibmoSDK.getUiCustomization()!=null && wibmoSDK.getUiCustomization().getLabelCustomization()!=null) {
                LabelCustomization lblCustomization = wibmoSDK.getUiCustomization().getLabelCustomization();
                if(lblCustomization.getTextColor()!= null) {
                    labelFontColor = lblCustomization.getTextColor();
                }
                if(lblCustomization.getTextFontSize()!= -1) {
                    labelTextSize = lblCustomization.getTextFontSize();
                }
                if(lblCustomization.getTextFontName()!= null) {
                    labelFontName = lblCustomization.getTextFontName();
                }
            }
    }*/

    public static IntroImageFragment newInstance(int instance) {
        IntroImageFragment fragment = new IntroImageFragment();

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
