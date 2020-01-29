package wibmo.com.main.Fragments;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewpagerindicator.CirclePageIndicator;

import com.wibmo.sdk.WibmoSDK;

import java.util.Timer;
import java.util.TimerTask;

import wibmo.com.wibmoAuthApp.R;

/**
 * Created by nithyak on 07/09/17.
 */

public class SliderFragment extends Fragment {
    private static final String TAG = "SliderFragment";

    private View view = null;

    private IntroFragmentAdapter mAdapter;
    private ViewPager mPager;
    private CirclePageIndicator mIndicator;

    private int currentPage = 0;
    private int NUM_PAGES = 3;
    private Timer swipeTimer;

    private boolean pauseAnimaion;
    private WibmoSDK wibmoSDK;

    @Override
    public void onStop() {
        super.onStop();
        pauseAnimaion = true;
    }

    @Override
    public void onStart() {
        super.onStart();
        pauseAnimaion = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(swipeTimer!=null) {
            swipeTimer.cancel();
            swipeTimer = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        final Activity activity = getActivity();

        view = inflater.inflate(R.layout.slider_fragment,
                container, false);


        mAdapter = new IntroFragmentAdapter(getActivity().getSupportFragmentManager());


        mPager = view.findViewById(R.id.viewpager);
        mPager.setAdapter(mAdapter);

        mIndicator = view.findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);

        wibmoSDK = WibmoSDK.getInstance();

        applyUICustomisation();

        mPager.setCurrentItem(0, false);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position + 1;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if(NUM_PAGES > 1) {
            final Handler handler = new Handler();
            final Runnable update = new Runnable() {
                public void run() {
                    if(pauseAnimaion) {
                        return;
                    }

                    if (currentPage == NUM_PAGES) {
                        currentPage = 0;
                    }
                    mPager.setCurrentItem(currentPage++, true);
                }
            };

            swipeTimer = new Timer();
            swipeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(update);
                }
            }, activity.getResources().getInteger(R.integer.animation_start_time), activity.getResources().getInteger(R.integer.animation_delay_time));
        }

        return view;
    }

    private void applyUICustomisation() {
        String tbarBGColor = getActivity().getString(R.string.blueColor);

        mIndicator.setFillColor(Color.parseColor(tbarBGColor));
        mIndicator.setStrokeColor(Color.parseColor("#FFFFFF"));
        mIndicator.setStrokeWidth(8);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v(TAG, "onActivityCreated");
    }

    class IntroFragmentAdapter extends FragmentPagerAdapter {
        private int mCount = NUM_PAGES;

        public IntroFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return SliderImageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return mCount;
        }
    }
}
