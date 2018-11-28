package com.eip.stopclopeip.Advice;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eip.stopclopeip.R;

public class AdviceFragment extends Fragment {
    String url = "http://romain-caldas.fr/api/rest.php?dev=69";
    private CardView fatigueCard, insomnieCard, touxCard, addictionCard, faimCard, irritabiliteCard;

    public AdviceFragment() {}

    public static AdviceFragment newInstance(String param1, String param2) {
        AdviceFragment fragment = new AdviceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_advice, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        fatigueCard = view.findViewById(R.id.fatigue_card);
        insomnieCard = view.findViewById(R.id.insomnie_card);
        touxCard = view.findViewById(R.id.toux_card);
        addictionCard = view.findViewById(R.id.addiction_card);
        faimCard = view.findViewById(R.id.faim_card);
        irritabiliteCard = view.findViewById(R.id.irritabilite_card);

        fatigueCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAdviceList("fatigue");
            }
        });

        insomnieCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAdviceList("insomnie");
            }
        });

        touxCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAdviceList("toux");
            }
        });

        addictionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAdviceList("addiction");
            }
        });

        faimCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAdviceList("faim");
            }
        });

        irritabiliteCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAdviceList("irritabilite");
            }
        });
    }

    public void goToAdviceList(String category) {
        Fragment adviceListFragment = new AdviceListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("category", category);
        adviceListFragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_in_left);
        fragmentTransaction.replace(R.id.content_frame, adviceListFragment).addToBackStack(null).commit();
    }

    public void Alert(String Msg) {
        Toast.makeText(this.getActivity(), Msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}