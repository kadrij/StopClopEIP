package com.eip.stopclopeip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HomePressionFragment extends Fragment {
    private String url = "http://romain-caldas.fr/api/rest.php?dev=69";
    private ConstraintLayout mDailyStatForm;
    private ProgressBar mProgress;
    private PieChart mChart;

    public HomePressionFragment() {
    }

    public static HomePressionFragment newInstance() {
        HomePressionFragment fragment = new HomePressionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_pression, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        mDailyStatForm = view.findViewById(R.id.daily_stat_content);
        mProgress = view.findViewById(R.id.progressBar);
        showProgress(true);
        getCount(view);
    }

    private void makeChart(View view, int red, int blue, int black) {
        Description description = new Description();
        mChart = view.findViewById(R.id.pression_pie_chart);
        description.setText("");
        mChart.setUsePercentValues(true);
        mChart.setDescription(description);

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleRadius(70);
        mChart.setTransparentCircleRadius(0);

        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        mChart.setTouchEnabled(false);
        addData(red, blue, black);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);
    }

    private void addData(int red, int blue, int black) {
        ArrayList<PieEntry> yVals1 = new ArrayList<PieEntry>();
        float[] yData = { red, blue, black };
        String[] xData = { "Sony", "Huawei", "LG" };

        for (int i = 0; i < yData.length; i++)
            yVals1.add(new PieEntry(yData[i], i));

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < xData.length; i++)
            xVals.add(xData[i]);

        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        ArrayList<Integer> mColors = new ArrayList<Integer>();

        try {
            mColors.add(getContext().getResources().getColor(R.color.RedButton));
            mColors.add(getContext().getResources().getColor(R.color.BlueButton));
            mColors.add(getContext().getResources().getColor(R.color.BlackButton));
        } catch (NullPointerException e) {
            System.out.println(e);
        }
        dataSet.setColors(mColors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.GRAY);
        data.setDrawValues(false);
        mChart.setData(data);
        mChart.setDrawSliceText(false);
        mChart.highlightValues(null);
        mChart.getLegend().setEnabled(false);
        mChart.invalidate();
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
                                    if (userData.getString("button").equals("BLUE") && blue <= 99999)
                                        blue++;
                                    else if (userData.getString("button").equals("RED") && red <= 99999)
                                        red++;
                                    else if (userData.getString("button").equals("BLACK") && black <= 99999)
                                        black++;
                                }
                            }

                            makeChart(view, red, blue, black);

                            red_count.setText("" + red);
                            blue_count.setText("" + blue);
                            black_count.setText("" + black);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        showProgress(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
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

            mDailyStatForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mDailyStatForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mDailyStatForm.setVisibility(show ? View.GONE : View.VISIBLE);
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
}
