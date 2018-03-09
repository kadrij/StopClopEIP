package com.eip.stopclopeip;

import android.os.Bundle;
import android.app.Fragment;
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
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

                            String data = jsonResponse.getString("data");
                            JSONArray userArray = new JSONArray(data);

                            int blue = 0;
                            int red = 0;
                            int black = 0;

                            for (int i = 0; i < userArray.length(); i++) {
                                JSONObject userData = userArray.getJSONObject(i);
                                if (userData.getString("button").equals("BLUE"))
                                    blue++;
                                else if (userData.getString("button").equals("RED"))
                                    red++;
                                else
                                    black++;
                            }

                            List<BarEntry> entriesGroup1 = new ArrayList<>();
                            List<BarEntry> entriesGroup2 = new ArrayList<>();
                            List<BarEntry> entriesGroup3 = new ArrayList<>();

                            entriesGroup1.add(new BarEntry(0f, red));
                            entriesGroup2.add(new BarEntry(0f, blue));
                            entriesGroup3.add(new BarEntry(0f, black));

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
                            lineEntries.add(new Entry(10f, (blue + red) * 7));
                            weekMoney.setText("" + (blue + red) * 7);
                            instMoney.setText("" + (blue + red) * 7);
                            LineDataSet lineSet = new LineDataSet(lineEntries, "Argent économisé");
                            lineSet.setColor(getResources().getColor(R.color.DollarGreen));
                            LineData lineData = new LineData(lineSet);
                            lineChart.setData(lineData);
                            lineChart.invalidate();
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

    public void Alert(String Msg) {
        Toast.makeText(this.getActivity(), Msg, Toast.LENGTH_SHORT).show();
    }
}
