package com.eip.stopclopeip.Advice;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.eip.stopclopeip.R;

import java.util.ArrayList;
import java.util.List;

public class AdviceListFragment extends Fragment {
    RecyclerView recyclerView;
    AdviceAdapter adapter;
    List<AdviceSample> adviceList;

    public AdviceListFragment() {
    }

    public static AdviceListFragment newInstance(String param1, String param2) {
        AdviceListFragment fragment = new AdviceListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_advice_list, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        adviceList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.advice_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adviceList.add(new AdviceSample(1,
                "Fais des exercices de respiration : allongé ou assis toi, mets tes mains sur ton ventre, respires profondément et concentre toi sur le mouvement de ton ventre.",
                103,
                "Zen",
                false));
        /*adviceList.add(new Advice(2,
                "A la place d'une cigarette habituelle, essayé de changé celle-ci en prenant un café ou un verre d'eau.",
                35,
                "Autres",
                false));
        adviceList.add(new Advice(3,
                "Fait du sport.",
                24,
                "Sport",
                false));*/

        adapter = new AdviceAdapter(getActivity(), adviceList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_in_right);
                fragmentTransaction.replace(R.id.content_frame, new AdviceFragment()).addToBackStack(null).commit();
                return true;
        }

        return false;
    }
}
