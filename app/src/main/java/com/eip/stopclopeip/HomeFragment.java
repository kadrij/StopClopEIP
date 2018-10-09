package com.eip.stopclopeip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {
    String url = "http://romain-caldas.fr/api/rest.php?dev=";
    RequestQueue queue;
    private ProgressBar mProgress;
    private ScrollView mHomeForm;
    private ConstraintLayout mErrorForm;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savecInstanceState) {
        final ImageView cactus = view.findViewById(R.id.cactos_image);
        Chronometer sevrageTime = view.findViewById(R.id.sevrage_value);
        TextView sevrageRecord = view.findViewById(R.id.record_value);
        mProgress = view.findViewById(R.id.progressBar);
        mHomeForm = view.findViewById(R.id.home_form);
        mErrorForm = view.findViewById(R.id.error_form);
        final Date[] mDateClop = {new Date()};

        showProgress(true);

        getDateClop(new CallBack() {
            @Override
            public void onSuccess(Date dateClop) {
                mDateClop[0] = dateClop;
            }

            @Override
            public void onFail(String msg) {
                showProgress(false);
                showError(true);
                Alert("Impossible de récupérer le temps de sevrage");
            }
        });

        sevrageTime.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                Date currentDate = new Date();
                long diff = Math.abs(currentDate.getTime() - mDateClop[0].getTime());
                if (TimeUnit.MILLISECONDS.toHours(diff) < 10)
                    cactus.setImageResource(R.drawable.cactos1);
                else if (TimeUnit.MILLISECONDS.toHours(diff) >= 10 && TimeUnit.MILLISECONDS.toHours(diff) < 48)
                    cactus.setImageResource(R.drawable.cactos2);
                else if (TimeUnit.MILLISECONDS.toHours(diff) >= 48 && TimeUnit.MILLISECONDS.toHours(diff) < 168)
                    cactus.setImageResource(R.drawable.cactos3);
                else if (TimeUnit.MILLISECONDS.toHours(diff) >= 168)
                    cactus.setImageResource(R.drawable.cactos4);
                chronometer.setText(String.format("%02d : %02d : %02d",
                        TimeUnit.MILLISECONDS.toDays(diff),
                        TimeUnit.MILLISECONDS.toHours(diff) % 24,
                        TimeUnit.MILLISECONDS.toMinutes(diff) % 60));
            }
        });
        sevrageTime.start();
        getRecord();

        tabLayout = view.findViewById(R.id.collection_tab);
        viewPager = view.findViewById(R.id.collection_view);
        ViewCollectionAdapter adapter = new ViewCollectionAdapter(getChildFragmentManager());
        adapter.AddFragment(new HomeAdviceFragment());
        adapter.AddFragment(new HomePressionFragment());
        adapter.AddFragment(new HomeAchievmentFragment());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void getRecord() {
        queue = Volley.newRequestQueue(this.getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url
                + "&function=sevrage.record&dev=69&email="
                + getArguments().getString("email")
                + "&token="
                + getArguments().getString("token"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonResponse = null;
                        try {
                            Log.v("Response", response);
                            jsonResponse = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONObject jsonData = new JSONObject(jsonResponse.getString("data"));
                            JSONObject jsonDate1 = new JSONObject(jsonData.getString("date1"));
                            JSONObject jsonDate2 = new JSONObject(jsonData.getString("date2"));
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
                            try {
                                TextView recordValue = null;
                                try {
                                    recordValue = getView().findViewById(R.id.record_value);
                                } catch (NullPointerException e) {
                                    System.out.println(e);
                                }

                                final Date date1 = format.parse(jsonDate1.getString("date"));
                                final Date date2 = format.parse(jsonDate2.getString("date"));
                                long diff = Math.abs(date2.getTime() - date1.getTime());

                                if (recordValue != null)
                                    recordValue.setText(String.format("%02d : %02d : %02d",
                                            TimeUnit.MILLISECONDS.toDays(diff),
                                            TimeUnit.MILLISECONDS.toHours(diff) % 24,
                                            TimeUnit.MILLISECONDS.toMinutes(diff) % 60));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        showProgress(false);
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
        queue.add(stringRequest);
        queue.start();
    }

    public void getDateClop(final CallBack onCallBack) {
        queue = Volley.newRequestQueue(this.getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url
                + "&function=sevrage.time&dev=69&email="
                + getArguments().getString("email")
                + "&token="
                + getArguments().getString("token")
                + "&button=BLACK",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONObject jsonData = new JSONObject(jsonResponse.getString("data"));
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                final Date dateClop = format.parse(jsonData.getString("dateClop"));
                                onCallBack.onSuccess(dateClop);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
        queue.add(stringRequest);
        queue.start();
    }

    public interface CallBack {
        void onSuccess(Date dateClop);
        void onFail(String msg);
    }

    void showProgress(final boolean show) {
        try {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mHomeForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mHomeForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mHomeForm.setVisibility(show ? View.GONE : View.VISIBLE);
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

        mHomeForm.setVisibility(show ? View.GONE : View.VISIBLE);
        mHomeForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mHomeForm.setVisibility(show ? View.GONE : View.VISIBLE);
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

