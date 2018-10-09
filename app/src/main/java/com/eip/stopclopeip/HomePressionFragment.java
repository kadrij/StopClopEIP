package com.eip.stopclopeip;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomePressionFragment extends Fragment {
    public HomePressionFragment() {
    }

    public static HomePressionFragment newInstance(String param1, String param2) {
        HomePressionFragment fragment = new HomePressionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_pression, container, false);
    }
}
