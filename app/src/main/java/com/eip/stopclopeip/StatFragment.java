package com.eip.stopclopeip;

import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import static java.lang.Math.toIntExact;

public class StatFragment extends Fragment {
    String url = "http://romain-caldas.fr/api/rest.php?dev=69";

    public StatFragment() {
    }

    public static StatFragment newInstance(String param1, String param2) {
        StatFragment fragment = new StatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stat, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            BarChart barChart = view.findViewById(R.id.pression_chart);
                            LineChart lineChart = view.findViewById(R.id.money_chart);
                            TextView instMoney = view.findViewById(R.id.installation_value);
                            TextView weekMoney = view.findViewById(R.id.week_value);

                            JSONObject userData;
                            final String labels[] = new String[7];
                            int red[] = new int[7];
                            int blue[] = new int[7];
                            int black[] = new int[7];
                            int blueTotalInstallation = 0;
                            int redTotalInstallation = 0;
                            int blueTotalWeek = 0;
                            int redTotalWeek = 0;
                            Date currentDate = new Date();
                            Date date;
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                            String data = jsonResponse.getString("data");
                            JSONArray userArray = new JSONArray(data);

                            for (int i = 0; i < userArray.length(); i++) {
                                userData = userArray.getJSONObject(i);
                                if (userData.getString("button").equals("BLUE"))
                                    blueTotalInstallation++;
                                else if (userData.getString("button").equals("RED"))
                                    redTotalInstallation++;

                            }

                            for (int i = 0; i < 7; i++) {
                                Calendar cal = Calendar.getInstance();
                                cal.add(Calendar.DATE, i * -1);
                                date = cal.getTime();
                                labels[i] = (DateFormat.format("dd/MM", Math.abs(date.getTime()))).toString();
                            }

                            for (int i = 0; i < userArray.length(); i++) {
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
                            }

                            XAxis xvalButton = barChart.getXAxis();
                            xvalButton.setDrawLabels(true);
                            xvalButton.setValueFormatter(new IAxisValueFormatter() {
                                @Override
                                public String getFormattedValue(float value, AxisBase axis) {
                                    if (labels[(int)value] == null)
                                        return "";
                                    return labels[(int)value];
                                }
                            });

                            for (int i = 0; i < red.length; i++) {
                                blueTotalWeek += blue[i];
                                redTotalWeek += red[i];
                            }

                            List<BarEntry> entriesGroup1 = new ArrayList<>();
                            List<BarEntry> entriesGroup2 = new ArrayList<>();
                            List<BarEntry> entriesGroup3 = new ArrayList<>();

                            for (int i = 0; i < red.length; i++) {
                                entriesGroup1.add(new BarEntry(i, red[i]));
                                entriesGroup2.add(new BarEntry(i, blue[i]));
                                entriesGroup3.add(new BarEntry(i, black[i]));
                            }

                            BarDataSet set1 = new BarDataSet(entriesGroup1, "Habitudes évitée(s)");
                            BarDataSet set2 = new BarDataSet(entriesGroup2, "Cigarettes évitée(s)");
                            BarDataSet set3 = new BarDataSet(entriesGroup3, "Cigarettes fumée(s)");

                            set1.setColor(getResources().getColor(R.color.RedButton));
                            set2.setColor(getResources().getColor(R.color.BlueButton));
                            set3.setColor(getResources().getColor(R.color.BlackButton));

                            float groupSpace = 0.4f;
                            float barSpace = 0f;
                            float barWidth = 0.20f;

                            BarData barData = new BarData(set1, set2, set3);
                            barData.setBarWidth(barWidth);
                            barChart.setData(barData);
                            barChart.groupBars(-0.50f, groupSpace, barSpace);
                            barChart.invalidate();

                            List<Entry> lineEntries = new ArrayList<>();
                            for (int i = 0; i < red.length; i++)
                                lineEntries.add(new Entry(i, Float.parseFloat(String.valueOf((blue[i] + red[i]) * 0.425))));

                            XAxis xvalMoney = lineChart.getXAxis();
                            xvalMoney.setDrawLabels(true);
                            xvalMoney.setValueFormatter(new IAxisValueFormatter() {
                                @Override
                                public String getFormattedValue(float value, AxisBase axis) {
                                    if (labels[(int)value] == null)
                                        return "";
                                    return labels[(int)value];
                                }
                            });

                            weekMoney.setText("" + (blueTotalWeek + redTotalWeek) * 0.425);
                            instMoney.setText("" + (blueTotalInstallation + redTotalInstallation) * 0.425);
                            LineDataSet lineSet = new LineDataSet(lineEntries, "Argent économisé");
                            lineSet.setColor(getResources().getColor(R.color.DollarGreen));
                            LineData lineData = new LineData(lineSet);
                            lineChart.setData(lineData);
                            lineChart.invalidate();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
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

    public void Alert(String Msg) {
        Toast.makeText(this.getActivity(), Msg, Toast.LENGTH_SHORT).show();
    }
}

