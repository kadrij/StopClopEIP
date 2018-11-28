package com.eip.stopclopeip.Advice;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eip.stopclopeip.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdviceListFragment extends Fragment {
    private Bundle bundle;
    private ConstraintLayout mProgress;
    private ConstraintLayout mErrorForm;
    private RecyclerView recyclerView;
    private AdviceAdapter adapter;
    private List<AdviceSample> adviceList;

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
        bundle = this.getArguments();
        mProgress = view.findViewById(R.id.progress_form);
        mErrorForm = view.findViewById(R.id.error_form);
        recyclerView = view.findViewById(R.id.advice_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        showProgress(true);
        createAdviceList();
    }

    public void createAdviceList() {
        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "https://romain-caldas.fr/api/rest.php?dev=69&function=conseil.getAll&fbclid=IwAR1cGjI0W6brskBVpiQU_QcmxQJ8fhKsInyt4fD-tBHGnrt6o_70bGTQS_M",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response);
                            String data = jsonResponse.getString("data");
                            String category = bundle.getString("category");
                            JSONArray adviceArray = new JSONArray(data);
                            for (int i = 0; i < adviceArray.length(); i++) {
                                JSONObject adviceData = adviceArray.getJSONObject(i);
                                if (category.equals(adviceData.getString("categorie"))) {
                                    adviceList.add(new AdviceSample(adviceData.getInt("id"),
                                            adviceData.getString("title"),
                                            adviceData.getString("comment"),
                                            adviceData.getString("date"),
                                            adviceData.getString("categorie")));
                                }
                            }
                            adapter = new AdviceAdapter(getActivity(), adviceList);
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                showError(true);
                Alert("Impossible de se connecter au serveur");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("myData", "{}");
                return params;
            }
        };
        showProgress(false);
        queue.add(stringRequest);
        queue.start();
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

    void showProgress(final boolean show) {
        try {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
            recyclerView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } catch (IllegalStateException e) {
            System.out.println(e);
        }
    }

    void showError(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        recyclerView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mErrorForm.setVisibility(show ? View.VISIBLE : View.GONE);
        mErrorForm.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mErrorForm.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public void Alert(String Msg) {
        Toast.makeText(this.getActivity(), Msg, Toast.LENGTH_SHORT).show();
    }
}
