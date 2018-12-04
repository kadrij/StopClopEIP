package com.eip.stopclopeip.Achievment;

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
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eip.stopclopeip.R;
import com.eip.stopclopeip.Utils.AchievmentDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AchievmentFragment extends Fragment {
    private String url = "http://romain-caldas.fr/api/rest.php?dev=69";
    private ImageView[] achievment_images;
    private ProgressBar mProgress;
    private ConstraintLayout mErrorForm;
    private ScrollView mAchievmentForm;

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

    public static AchievmentFragment newInstance() {
        AchievmentFragment fragment = new AchievmentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_achievment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        achievment_images = new ImageView[20];
        mProgress = view.findViewById(R.id.progressBar);
        mAchievmentForm = view.findViewById(R.id.achievment_form);
        mErrorForm = view.findViewById(R.id.error_form);
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
                                getAchievmentImages(view);
                                JSONObject achievment_check_json = userArray.getJSONObject(0);
                                String achievment_check = achievment_check_json.getString("achie");
                                Log.v("STRING", achievment_check);
                                for (int i = 0; i < achievment_images.length; i++) {
                                    final int j = i;
                                    if (achievment_check.charAt(i) == '0') {
                                        achievment_images[i].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.locked));
                                        achievment_logos[i] = R.drawable.locked;
                                        achievment_descs[i] = "???";
                                    }
                                    achievment_images[i].setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            openDialog(achievment_descs[j], achievment_logos[j]);
                                        }
                                    });
                                }
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
                    Alert("Impossible de se connecter au serveur");
                } catch (NullPointerException e) {}
                showProgress(false);
                showError(true);
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

    void getAchievmentImages(View view) {
        achievment_images[0] = view.findViewById(R.id.blue_1);
        achievment_images[1] = view.findViewById(R.id.blue_2);
        achievment_images[2] = view.findViewById(R.id.blue_3);
        achievment_images[3] = view.findViewById(R.id.blue_4);

        achievment_images[4] = view.findViewById(R.id.red_1);
        achievment_images[5] = view.findViewById(R.id.red_2);
        achievment_images[6] = view.findViewById(R.id.red_3);
        achievment_images[7] = view.findViewById(R.id.red_4);

        achievment_images[8] = view.findViewById(R.id.total_1);
        achievment_images[9] = view.findViewById(R.id.total_2);
        achievment_images[10] = view.findViewById(R.id.total_3);
        achievment_images[11] = view.findViewById(R.id.total_4);

        achievment_images[12] = view.findViewById(R.id.conso_1);
        achievment_images[13] = view.findViewById(R.id.conso_2);
        achievment_images[14] = view.findViewById(R.id.conso_3);
        achievment_images[15] = view.findViewById(R.id.conso_4);

        achievment_images[16] = view.findViewById(R.id.eco_1);
        achievment_images[17] = view.findViewById(R.id.eco_2);
        achievment_images[18] = view.findViewById(R.id.eco_3);
        achievment_images[19] = view.findViewById(R.id.eco_4);
    }

    void openDialog(String desc, int image) {
        Bundle bundle = new Bundle();
        bundle.putString("desc", desc);
        bundle.putInt("image", image);
        AchievmentDialog achievmentDialog = new AchievmentDialog();
        achievmentDialog.setArguments(bundle);
        achievmentDialog.show(getFragmentManager(), "Achievment dialog");
    }

    void showProgress(final boolean show) {
        try {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mAchievmentForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mAchievmentForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAchievmentForm.setVisibility(show ? View.GONE : View.VISIBLE);
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

    private void showError(final boolean show) {
        try {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mAchievmentForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mAchievmentForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAchievmentForm.setVisibility(show ? View.GONE : View.VISIBLE);
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
        } catch (IllegalStateException e) {
            System.out.println(e);
        }
    }

    private void Alert(String Msg) {
        Toast.makeText(this.getActivity(), Msg, Toast.LENGTH_SHORT).show();
    }
}
