package com.eip.stopclopeip;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeAdviceFragment extends Fragment {
    public HomeAdviceFragment() {
    }

    public static HomeAdviceFragment newInstance(String param1, String param2) {
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
