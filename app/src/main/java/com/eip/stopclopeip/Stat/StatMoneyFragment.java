package com.eip.stopclopeip.Stat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eip.stopclopeip.R;

public class StatMoneyFragment extends Fragment {
    public StatMoneyFragment() {
    }

    public static StatMoneyFragment newInstance() {
        StatMoneyFragment fragment = new StatMoneyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_money, container, false);
    }
}
