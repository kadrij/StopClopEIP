package com.eip.stopclopeip.Service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eip.stopclopeip.Activities.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ZoneService extends Service {
    private String url = "http://romain-caldas.fr/api/rest.php?dev=69";
    private String email, token;
    private Location[] zones_location;
    private double[] x, y, danger;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        email = intent.getStringExtra("email");
        token = intent.getStringExtra("token");
        zones_location = new Location[3];
        return START_NOT_STICKY;
    }

    private void getDangerousZones(double latitude, double longitude) {
        final RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url
                + "&function=algo&email="
                + email
                + "&token="
                + token
                + "&x="
                + longitude
                + "&y="
                + latitude,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response);
                            JSONArray data = jsonResponse.getJSONArray("data");
                            JSONArray zones = (JSONArray) data.get(1);
                            for (int i = 0; i < zones.length(); i++) {
                                Log.v("Zone" + i, "" + zones.get(i));
                                JSONObject zone = (JSONObject) zones.get(i);
                                JSONObject zone_data = zone.getJSONObject("0");
                                setDanger(zone_data.getDouble("x"), zone_data.getDouble("y"), zone.getDouble("dangereux"));
                            }
                            for (int i = 0; i < 3; i++)
                            Log.v("Zone 1", "X = " + x[0] + " | Y = " + y[0] + " | Danger = " + danger[0]);
                            Log.v("Zone 2", "X = " + x[1] + " | Y = " + y[1] + " | Danger = " + danger[1]);
                            Log.v("Zone 3", "X = " + x[2] + " | Y = " + y[2] + " | Danger = " + danger[2]);
                        } catch (JSONException e) {
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

    private void showPermissionDialog() {
        if (ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        } else {
            return;
        }
    }

    private void setDanger(Double zone_x, Double zone_y, Double zone_danger) {
        for (int i = 0; i < danger.length; i++) {
            if (danger[i] == 0) {
                danger[i] = zone_danger;
                x[i] = zone_x;
                y[i] = zone_y;
                return;
            }
        }
        for (int i = 0; i < danger.length; i++) {
            if (danger[i] < zone_danger) {
                danger[i] = zone_danger;
                x[i] = zone_x;
                y[i] = zone_y;
                return;
            }
        }
    }
}
