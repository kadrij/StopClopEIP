package com.eip.stopclopeip.Home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eip.stopclopeip.R;

public class HomeAdviceFragment extends Fragment {
    public HomeAdviceFragment() {
    }

    public static HomeAdviceFragment newInstance() {
        HomeAdviceFragment fragment = new HomeAdviceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_advice, container, false);
    }
}