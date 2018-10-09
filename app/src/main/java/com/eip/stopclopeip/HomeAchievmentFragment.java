package com.eip.stopclopeip;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeAchievmentFragment extends Fragment {
    public HomeAchievmentFragment() {
    }

    public static HomeAchievmentFragment newInstance(String param1, String param2) {
        HomeAchievmentFragment fragment = new HomeAchievmentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_achievment, container, false);
    }
}
