package com.eip.stopclopeip.Home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HomeAchievmentFragment extends Fragment {
    private String url = "http://romain-caldas.fr/api/rest.php?dev=69";
    private ConstraintLayout mHomeAchievment;
    private ConstraintLayout mProgress;
    private ImageView logo;
    private TextView desc;
    private TextView no_unlocked;

    public int[] achievment_logos = {
            R.drawable.blue_1,
            R.drawable.blue_2,
            R.drawable.blue_3,
            R.drawable.blue_4,

            R.drawable.red_1,
            R.drawable.red_2,
            R.drawable.red_3,
            R.drawable.red_4,

            R.drawable.total_1,
            R.drawable.total_2,
            R.drawable.total_3,
            R.drawable.total_4,

            R.drawable.conso_1,
            R.drawable.conso_2,
            R.drawable.conso_3,
            R.drawable.conso_4,

            R.drawable.eco_1,
            R.drawable.eco_2,
            R.drawable.eco_3,
            R.drawable.eco_4,
    };

    public String[] achievment_descs = {
            "Résister à 10 cigarettes réflexes",
            "Résister à 50 cigarettes réflexes",
            "Résister à 100 cigarettes réflexes",
            "Résister à 500 cigarettes réflexes",
            "Résister à 10 cigarettes",
            "Résister à 50 cigarettes",
            "Résister à 100 cigarettes",
            "Résister à 500 cigarettes",
            "Résister à 10 cigarettes au total",
            "Résister à 50 cigarettes au total",
            "Résister à 100 cigarettes au total",
            "Résister à 500 cigarettes au total",
            "Ne pas fumer pendant 1 jour",
            "Ne pas fumer pendant 10 jours",
            "Ne pas fumer pendant 100 jours",
            "Ne pas fumer pendant 365 jours",
            "Economiser 10€ au total",
            "Economiser 50€ au total",
            "Economiser 100€ au total",
            "Economiser 500€ au total",
    };

    public static HomeAchievmentFragment newInstance() {
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgress = view.findViewById(R.id.progress_form);
        mHomeAchievment = view.findViewById(R.id.home_achievment_form);
        logo = view.findViewById(R.id.home_achievment_logo);
        desc = view.findViewById(R.id.home_achievment_desc);
        no_unlocked = view.findViewById(R.id.no_unlocked);
        getAchievments(view);
    }

    void getAchievments(final View view) {
        showProgress(true);

        final RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url
                + "&function=user.achie&email="
                + getArguments().getString("email")
                + "&token="
                + getArguments().getString("token"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response);
                            String data = jsonResponse.getString("data");
                            JSONArray userArray = new JSONArray(data);
                            try {
                                JSONObject achievment_check_json = userArray.getJSONObject(0);
                                String achievment_check = achievment_check_json.getString("achie");
                                Random random = new Random();
                                int i = 0;

                                Log.v("STRING", achievment_check);
                                if (achievment_check.equals("00000000000000000000"))
                                    showAchievment(true);
                                else {
                                    i = random.nextInt(20);
                                    while (achievment_check.charAt(i) == '0')
                                        i = random.nextInt(20);
                                    logo.setImageDrawable(ContextCompat.getDrawable(getActivity(), achievment_logos[i]));
                                    desc.setText(achievment_descs[i]);
                                }
                                /*for (int i = 0; i < achievment_images.length; i++) {
                                    final int j = i;
                                    if (achievment_check.charAt(i) == '0') {
                                        achievment_images[i].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.locked));
                                        achievment_logos[i] = R.drawable.locked;
                                        achievment_descs[i] = "?";
                                    }
                                    achievment_images[i].setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            openDialog(achievment_descs[j], achievment_logos[j]);
                                        }
                                    });
                                }*/
                                showProgress(false);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                } catch (NullPointerException e) {}
                showProgress(false);
                showAchievment(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("myData", "{}");
                return params;
            }
        };
        queue.add(stringRequest);
        queue.start();
    }

    void showProgress(final boolean show) {
        try {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mHomeAchievment.setVisibility(show ? View.GONE : View.VISIBLE);
            mHomeAchievment.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mHomeAchievment.setVisibility(show ? View.GONE : View.VISIBLE);
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

    void showAchievment(final boolean show) {
        try {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            logo.setVisibility(show ? View.GONE : View.VISIBLE);
            logo.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    logo.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            desc.setVisibility(show ? View.GONE : View.VISIBLE);
            desc.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    desc.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            no_unlocked.setVisibility(show ? View.VISIBLE : View.GONE);
            no_unlocked.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    no_unlocked.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } catch (IllegalStateException e) {
            System.out.println(e);
        }
    }
}
