package com.eip.stopclopeip.Stat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StatMoneyFragment extends Fragment {
    String url = "http://romain-caldas.fr/api/rest.php?dev=69";
    private ConstraintLayout mStatForm, mProgress, mErrorForm;
    private TextView install_euro, install_cents, month_euro, month_cents, week_euro, week_cents, day_euro, day_cents;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mStatForm = view.findViewById(R.id.stat_form);
        mProgress = view.findViewById(R.id.progress_form);
        mErrorForm = view.findViewById(R.id.error_form);
        install_euro = view.findViewById(R.id.install_euro);
        install_cents = view.findViewById(R.id.install_cents);
        month_euro = view.findViewById(R.id.month_euro);
        month_cents = view.findViewById(R.id.month_cents);
        week_euro = view.findViewById(R.id.week_euro);
        week_cents = view.findViewById(R.id.week_cents);
        day_euro = view.findViewById(R.id.day_euro);
        day_cents = view.findViewById(R.id.day_cents);
        showProgress(true);
        getCount(view);
    }

    private void getCount(final View view) {
        final RequestQueue queue = Volley.newRequestQueue(this.getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url
                + "&function=coordonnee.get&email="
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
                            getInstallEconomies(userArray);
                            getMonthEconomies(userArray);
                            getWeekEconomies(userArray);
                            getDayEconomies(userArray);
                            showProgress(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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

    void getInstallEconomies(JSONArray pressions) {
        int red_install = 0;
        int blue_install = 0;
        JSONObject pression;
        Calendar currentCal = Calendar.getInstance();
        Calendar pressionCal = Calendar.getInstance();
        Date pressionDate;
        Date currentDay = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String str;
        String[] money;

        currentCal.setTime(currentDay);
        try {
            try {
                for (int i = 0; i < pressions.length(); i++) {
                    pression = pressions.getJSONObject(i);
                    pressionDate = format.parse(pression.getString("date"));
                    pressionCal.setTime(pressionDate);
                    if ((currentCal.get(Calendar.YEAR) != pressionCal.get(Calendar.YEAR) && currentCal.get(Calendar.MONTH) != pressionCal.get(Calendar.MONTH))
                            || (currentCal.get(Calendar.YEAR) == pressionCal.get(Calendar.YEAR) && currentCal.get(Calendar.MONTH) != pressionCal.get(Calendar.MONTH))) {
                        if (pression.getString("button").equals("BLUE"))
                            blue_install++;
                        else if (pression.getString("button").equals("RED"))
                            red_install++;
                    }
                }
                str = new DecimalFormat("#.##").format((blue_install + red_install) * 0.425);
                money = str.split("\\.");
                install_euro.setText(money[0]);
                if (money.length > 1 && money[1].length() == 1)
                    install_cents.setText(money[1] + "0");
                else if (money.length > 1 && money[1].length() > 1)
                    install_cents.setText(money[1]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void getMonthEconomies(JSONArray pressions) {
        int red_month = 0;
        int blue_month = 0;
        JSONObject pression;
        Calendar currentCal = Calendar.getInstance();
        Calendar pressionCal = Calendar.getInstance();
        Date pressionDate;
        Date currentDay = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String str;
        String[] money;

        currentCal.setTime(currentDay);
        try {
            try {
                for (int i = 0; i < pressions.length(); i++) {
                    pression = pressions.getJSONObject(i);
                    pressionDate = format.parse(pression.getString("date"));
                    pressionCal.setTime(pressionDate);
                    if (currentCal.get(Calendar.YEAR) == pressionCal.get(Calendar.YEAR) &&
                            currentCal.get(Calendar.MONTH) == pressionCal.get(Calendar.MONTH)) {
                        if (pression.getString("button").equals("BLUE"))
                            blue_month++;
                        else if (pression.getString("button").equals("RED"))
                            red_month++;
                    }
                }
                str = new DecimalFormat("#.##").format((blue_month + red_month) * 0.425);
                money = str.split("\\.");
                month_euro.setText(money[0]);
                if (money.length > 1 && money[1].length() == 1)
                    month_cents.setText(money[1] + "0");
                else if (money.length > 1 && money[1].length() > 1)
                    month_cents.setText(money[1]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void getWeekEconomies(JSONArray pressions) {
        int red_week = 0;
        int blue_week = 0;
        JSONObject pression;
        Date pressionDate;
        Date weekDate;
        String[] week = getCurrentWeek();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String str;
        String[] money;

        try {
            try {
                for (int i = 0; i < pressions.length(); i++) {
                    pression = pressions.getJSONObject(i);
                    pressionDate = format.parse(pression.getString("date"));
                    for (int j = 0; j < week.length; j++) {
                        weekDate = format.parse(week[j]);
                        long diff = Math.abs(weekDate.getDate() - pressionDate.getDate());
                        if (TimeUnit.DAYS.toMillis(diff) == 0) {
                            if (pression.getString("button").equals("BLUE"))
                                blue_week++;
                            else if (pression.getString("button").equals("RED"))
                                red_week++;
                        }
                    }
                }
                str = new DecimalFormat("#.##").format((blue_week + red_week) * 0.425);
                money = str.split("\\.");
                week_euro.setText(money[0]);
                if (money.length > 1 && money[1].length() == 1)
                    week_cents.setText(money[1] + "0");
                else if (money.length > 1 && money[1].length() > 1)
                    week_cents.setText(money[1]);
            } catch (ParseException e) {}
        } catch (JSONException e) {}
    }

    void getDayEconomies(JSONArray pressions) {
        int red_day = 0;
        int blue_day = 0;
        JSONObject pression;
        Date pressionDate;
        Date currentDay = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String str;
        String[] money;

        try {
            try {
                for (int i = 0; i < pressions.length(); i++) {
                    pression = pressions.getJSONObject(i);
                    pressionDate = format.parse(pression.getString("date"));
                    int diff = Math.abs(currentDay.getDate() - pressionDate.getDate());
                    if (TimeUnit.DAYS.toMillis(diff) == 0) {
                        if (pression.getString("button").equals("BLUE"))
                            blue_day++;
                        else if (pression.getString("button").equals("RED"))
                            red_day++;
                    }
                }
                str = new DecimalFormat("#.##").format((blue_day + red_day) * 0.425);
                money = str.split("\\.");
                day_euro.setText(money[0]);
                if (money.length > 1 && money[1].length() == 1)
                    day_cents.setText(money[1] + "0");
                else if (money.length > 1 && money[1].length() > 1)
                    day_cents.setText(money[1]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    String[] getCurrentWeek() {
        Calendar now = Calendar.getInstance();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        String[] days = new String[7];
        int delta = -now.get(GregorianCalendar.DAY_OF_WEEK) + 2;
        now.add(Calendar.DAY_OF_MONTH, delta);
        for (int i = 0; i < 7; i++) {
            days[i] = format.format(now.getTime());
            now.add(Calendar.DAY_OF_MONTH, 1);
        }
        System.out.println(Arrays.toString(days));
        return days;
    }

    void showProgress(final boolean show) {
        try {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mStatForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mStatForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mStatForm.setVisibility(show ? View.GONE : View.VISIBLE);
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

            mStatForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mStatForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mStatForm.setVisibility(show ? View.GONE : View.VISIBLE);
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
}
