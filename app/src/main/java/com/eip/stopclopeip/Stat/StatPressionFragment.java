package com.eip.stopclopeip.Stat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eip.stopclopeip.Utils.MyMarkerView;
import com.eip.stopclopeip.Utils.MyValueFormatter;
import com.eip.stopclopeip.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StatPressionFragment extends Fragment {
    String url = "http://romain-caldas.fr/api/rest.php?dev=69";
    private ProgressBar mProgress;
    private ConstraintLayout mErrorForm;
    private ConstraintLayout mStatForm;
    private int red[] = new int[7];;
    private int blue[] = new int[7];;
    private int black[] = new int[7];;

    public static StatPressionFragment newInstance() {
        StatPressionFragment fragment = new StatPressionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pression, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        drawPressionGraph(view);
    }

    private void drawPressionGraph(final View view) {
        final RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        mProgress = view.findViewById(R.id.progressBar);
        mStatForm = view.findViewById(R.id.stat_form);
        mErrorForm = view.findViewById(R.id.error_form);
        showProgress(true);

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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            final String labels[] = getLabels();;
                            String data = jsonResponse.getString("data");
                            JSONArray userArray = new JSONArray(data);
                            getPressions(userArray);
                            setPressionChart(view, red, blue, black, labels);
                            showProgress(false);
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

    private void setPressionChart(View view, int red[], int blue[], int black[], final String labels[]) {
        LineChart lineChart = view.findViewById(R.id.pression_chart);
        try {
            MyMarkerView mv = new MyMarkerView(getActivity().getBaseContext(), R.layout.marker_chart);
            mv.setChartView(lineChart);
            LineDataSet set1 = null;
            LineDataSet set2 = null;
            LineDataSet set3 = null;

            lineChart.getDescription().setEnabled(false);
            lineChart.getXAxis().setDrawGridLines(false);

            XAxis xvalButton = lineChart.getXAxis();
            xvalButton.setDrawLabels(true);
            xvalButton.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return labels[6 - (int)value];
                }
            });

            List<Entry> entriesGroup1 = new ArrayList<>();
            List<Entry> entriesGroup2 = new ArrayList<>();
            List<Entry> entriesGroup3 = new ArrayList<>();

            for (int i = 0; i < red.length; i++) {
                entriesGroup1.add(new BarEntry(i, red[6 - i]));
                entriesGroup2.add(new BarEntry(i, blue[6 - i]));
                entriesGroup3.add(new BarEntry(i, black[6 - i]));
            }

            try {
                set1 = createSet(entriesGroup1, "Habitudes évitée(s)", getResources().getColor(R.color.RedButton));
                set2 = createSet(entriesGroup2, "Cigarettes évitée(s)", getResources().getColor(R.color.BlueButton));
                set3 = createSet(entriesGroup3, "Cigarettes fumée(s)", getResources().getColor(R.color.BlackButton));
            } catch (NullPointerException e) {}

            LineData lineData = new LineData(set1, set2, set3);

            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);

            YAxis yAxis1 = lineChart.getAxisRight();
            yAxis1.setEnabled(false);

            lineData.setDrawValues(false);
            lineData.setValueFormatter(new MyValueFormatter());
            lineChart.setData(lineData);
            lineChart.setDrawMarkers(true);
            lineChart.setMarker(mv);
            lineChart.setClickable(false);
            lineChart.invalidate();
        } catch (NullPointerException e) {}
    }

    private LineDataSet createSet(List<Entry> entriesGroup, String title, int color) {
        LineDataSet set = new LineDataSet(entriesGroup, title);

        set.setColor(color);
        set.setCircleColor(color);
        set.setLineWidth(1f);
        set.setCircleRadius(3f);
        set.setDrawCircleHole(false);
        set.setValueTextSize(9f);
        set.setDrawFilled(true);
        set.setFillColor(color);
        set.setDrawHighlightIndicators(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        return set;
    }

    private String[] getLabels() {
        String[] labels = new String[7];
        Date date;

        for (int i = 0; i < 7; i++) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, i * -1);
            date = cal.getTime();
            labels[i] = (DateFormat.format("dd/MM", Math.abs(date.getTime()))).toString();
        }
        return labels;
    }

    private void getPressions(JSONArray userArray) {
        JSONObject userData;
        Date currentDate = new Date();
        Date date;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < userArray.length(); i++) {
            try {
                userData = userArray.getJSONObject(i);
                date = format.parse(userData.getString("date"));
                long diff = Math.abs(currentDate.getTime() - date.getTime());
                int day = Integer.parseInt(String.valueOf(TimeUnit.MILLISECONDS.toDays(diff)));
                if (TimeUnit.MILLISECONDS.toDays(diff) < 7) {
                    if (userData.getString("button").equals("BLUE"))
                        blue[day]++;
                    else if (userData.getString("button").equals("RED"))
                        red[day]++;
                    else
                        black[day]++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
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

    private void Alert(String Msg) {
        Toast.makeText(this.getActivity(), Msg, Toast.LENGTH_SHORT).show();
    }
}

    /*private void setMoneyChart(View view, int red[], int blue[], int redTotalWeek, int redTotalInstallation,
                       int blueTotalWeek, int blueTotalInstallation, final String labels[]) {
        LineChart lineChart = view.findViewById(R.id.money_chart);
        TextView instMoney = view.findViewById(R.id.installation_value);
        TextView weekMoney = view.findViewById(R.id.week_value);
        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setDrawGridLines(false);
        List<Entry> lineEntries = new ArrayList<>();

        for (int i = 0; i < red.length; i++)
            lineEntries.add(new Entry(i, Float.parseFloat(String.valueOf((blue[6 - i] + red[6 - i]) * 0.425))));

        XAxis xvalMoney = lineChart.getXAxis();
        xvalMoney.setDrawLabels(true);
        xvalMoney.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return labels[6 - (int)value];
            }
        });

        weekMoney.setText(new DecimalFormat("#.##").format((blueTotalWeek + redTotalWeek) * 0.425));
        instMoney.setText(new DecimalFormat("#.##").format((blueTotalInstallation + redTotalInstallation) * 0.425));
        LineDataSet lineSet = new LineDataSet(lineEntries, "Argent économisé");

        try {
            lineSet.setColor(getResources().getColor(R.color.DollarGreen));
        } catch (IllegalStateException e) {
            System.out.println(e);
        }

        LineData lineData = new LineData(lineSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }*/