package com.eip.stopclopeip.Activities;

import com.eip.stopclopeip.R;
import com.eip.stopclopeip.Utils.SliderAdapter;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;

public class TutorialActivity extends Activity {
    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;

    private SliderAdapter sliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.BlackButton));
        }

        mSlideViewPager = findViewById(R.id.view_pager);
        mDotLayout = findViewById(R.id.view_dot_layout);

        sliderAdapter = new SliderAdapter(this);

        mSlideViewPager.setAdapter(sliderAdapter);
    }
}
