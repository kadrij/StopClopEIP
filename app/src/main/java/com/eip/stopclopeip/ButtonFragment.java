package com.eip.stopclopeip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.app.Fragment;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.invoke.ConstantCallSite;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ButtonFragment extends Fragment {
    String url = "http://romain-caldas.fr/api/rest.php?dev=69";
    private ProgressBar mProgress;
    private ConstraintLayout mButtonForm;
    private ConstraintLayout mErrorForm;
    private boolean firstGet = false;

    public ButtonFragment() {
    }

    public static ButtonFragment newInstance() {
        ButtonFragment fragment = new ButtonFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_button, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        Button red_button = view.findViewById(R.id.red_button);
        Button blue_button = view.findViewById(R.id.blue_button);
        Button black_button = view.findViewById(R.id.black_button);
        mProgress = view.findViewById(R.id.progressBar);
        mButtonForm = view.findViewById(R.id.button_form);
        mErrorForm = view.findViewById(R.id.error_form);

        showProgress(true);
        getCount(view);

        red_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPression(view, "RED");
            }
        });

        blue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPression(view, "BLUE");
            }
        });

        black_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPression(view, "BLACK");
            }
        });
    }

    private void sendPression(View view, String color){
        final RequestQueue queue = Volley.newRequestQueue(this.getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url
                + "&function=coordonnee.add&email="
                + getArguments().getString("email")
                + "&token="
                + getArguments().getString("token")
                + "&x=2&y=2&button="
                + color,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response);
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
        SystemClock.sleep(500);
        getCount(view);
    }

    private void getCount(final View view) {
        final RequestQueue queue = Volley.newRequestQueue(this.getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url
                + "&function=coordonnee.get&email="
                + getArguments().getString("email")
                + "&token="
                + getArguments().getString("token")
                + "&time=1j",
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
                            TextView red_count = view.findViewById(R.id.red_count);
                            TextView blue_count = view.findViewById(R.id.blue_count);
                            TextView black_count = view.findViewById(R.id.black_count);
                            Date currentDate = new Date();
                            Date date;
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                            String data = jsonResponse.getString("data");
                            JSONArray userArray = new JSONArray(data);

                            int red = 0;
                            int blue = 0;
                            int black = 0;

                            for (int i = 0; i < userArray.length(); i++) {
                                JSONObject userData = userArray.getJSONObject(i);
                                date = format.parse(userData.getString("date"));
                                long diff = Math.abs(currentDate.getTime() - date.getTime());
                                int day = Integer.parseInt(String.valueOf(TimeUnit.MILLISECONDS.toDays(diff)));
                                if (TimeUnit.MILLISECONDS.toDays(diff) == 0) {
                                    if (userData.getString("button").equals("BLUE"))
                                        blue++;
                                    else if (userData.getString("button").equals("RED"))
                                        red++;
                                    else
                                        black++;
                                }
                            }

                            red_count.setText("" + red);
                            blue_count.setText("" + blue);
                            black_count.setText("" + black);
                            showProgress(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
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
        queue.add(stringRequest);
        queue.start();
    }

    void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mButtonForm.setVisibility(show ? View.GONE : View.VISIBLE);
        mButtonForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mButtonForm.setVisibility(show ? View.GONE : View.VISIBLE);
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
    }

    void showError(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mButtonForm.setVisibility(show ? View.GONE : View.VISIBLE);
        mButtonForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mButtonForm.setVisibility(show ? View.GONE : View.VISIBLE);
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
