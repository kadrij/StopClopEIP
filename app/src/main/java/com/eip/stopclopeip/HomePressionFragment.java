package com.eip.stopclopeip;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HomePressionFragment extends Fragment {
    private String url = "http://romain-caldas.fr/api/rest.php?dev=69";

    public HomePressionFragment() {
    }

    public static HomePressionFragment newInstance(String param1, String param2) {
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
                                    if (userData.getString("button").equals("BLUE") && blue <= 99999)
                                        blue++;
                                    else if (userData.getString("button").equals("RED") && red <= 99999)
                                        red++;
                                    else if (userData.getString("button").equals("BLACK") && black <= 99999)
                                        black++;
                                }
                            }

                            red_count.setText("" + red);
                            blue_count.setText("" + blue);
                            black_count.setText("" + black);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
}
