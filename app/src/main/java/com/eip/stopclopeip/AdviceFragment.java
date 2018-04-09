package com.eip.stopclopeip;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AdviceFragment extends Fragment {
    String url = "http://romain-caldas.fr/api/rest.php?dev=69";
    RecyclerView recyclerView;
    AdviceAdapter adapter;
    List<Advice> adviceList;
    SearchView searchView;

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
        adviceList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.advice_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adviceList.add(new Advice(1, "Fais des exercices de respiration : allongé ou assis toi, mets tes mains sur ton ventre, respires profondément et concentre toi sur le mouvement de ton ventre.", 103, "Zen"));
        adviceList.add(new Advice(2, "A la place d'une cigarette habituelle, essayé de changé celle-ci en prenant un café ou un verre d'eau.", 35, "Autres"));
        adviceList.add(new Advice(3, "Manges des fruits secs.", 24, "Cuisine"));

        adapter = new AdviceAdapter(getActivity(), adviceList);
        recyclerView.setAdapter(adapter);
    }

    public void Alert(String Msg) {
        Toast.makeText(this.getActivity(), Msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        /*final MenuItem search = menu.findItem(R.id.action_search);
        searchView = (SearchView) search.getActionView();
        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setHintTextColor(getResources().getColor(R.color.white));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!searchView.isIconified())
                    searchView.setIconified(true);
                search.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                final List<Advice> filtermodelist = filter(adviceList, s);
                adapter.setFilter(filtermodelist);
                    Log.v("Recherche", s);
                return true;
            }
        });*/
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<Advice> filter(List<Advice> pl, String query) {
        query = query.toLowerCase();

        final List<Advice> filteredModelList = new ArrayList<>();
        for (Advice model:pl) {
            final String text = model.getTitle().toLowerCase();
            if (text.contains(query))
                filteredModelList.add(model);
        }
        return filteredModelList;
    }*/
}