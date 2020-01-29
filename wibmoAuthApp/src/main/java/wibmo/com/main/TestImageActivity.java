package wibmo.com.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import wibmo.com.wibmoAuthApp.R;

public class TestImageActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);


        AppCompatImageView imageView = (AppCompatImageView)findViewById(R.id.keyur);


        Glide.with(this).load("https://www.goxtrafit.com/goxtrafit/uploads/Service18179.png")
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                .into(imageView);

    }
}
