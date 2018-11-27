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
    private CardView zenCard;

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
        zenCard = view.findViewById(R.id.fatigue_card);
        zenCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_in_left);
                fragmentTransaction.replace(R.id.content_frame, new AdviceListFragment()).addToBackStack(null).commit();
            }
        });
        /*adviceList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.advice_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adviceList.add(new Advice(1,
                "Fais des exercices de respiration : allongé ou assis toi, mets tes mains sur ton ventre, respires profondément et concentre toi sur le mouvement de ton ventre.",
                103,
                "Zen",
                false));
        adviceList.add(new Advice(2,
                "A la place d'une cigarette habituelle, essayé de changé celle-ci en prenant un café ou un verre d'eau.",
                35,
                "Autres",
                false));
        adviceList.add(new Advice(3,
                "Fait du sport.",
                24,
                "Sport",
                false));

        adapter = new AdviceAdapter(getActivity(), adviceList);
        recyclerView.setAdapter(adapter);*/
    }

    public void Alert(String Msg) {
        Toast.makeText(this.getActivity(), Msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}