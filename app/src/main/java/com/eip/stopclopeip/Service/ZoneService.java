package com.eip.stopclopeip.Service;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ZoneService extends Service {
    private String url = "http://romain-caldas.fr/api/rest.php?dev=69";
    private String email, token;
    private Location[] zones_location;
    private double[] x;
    private double[] y;
    private double[] danger;
    private float[] distance;
    private LocationManager locationManager;
    private boolean in_zone = false;
    private RequestQueue queue;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationManager = (LocationManager) getBaseContext().getSystemService(getBaseContext().LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        } else {
            Log.e("ZONE SERVICE", "Can't obtain permissions");
        }
        Log.v("ZONE SERVICE", "Started");
        email = intent.getStringExtra("email");
        token = intent.getStringExtra("token");
        zones_location = new Location[3];
        return START_NOT_STICKY;
    }

    private void getDangerousZones(final Location userLocation) {
        if (queue == null)
            queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url
                + "&function=algo&email="
                + email
                + "&token="
                + token
                + "&x="
                + userLocation.getLongitude()
                + "&y="
                + userLocation.getLatitude(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonResponse = null;
                        try {
                            x = new double[3];
                            y = new double[3];
                            danger = new double[3];
                            distance = new float[3];
                            zones_location = new Location[3];
                            Log.v("ZONE SERVICE", "Getting zones");
                            jsonResponse = new JSONObject(response);
                            JSONArray data = jsonResponse.getJSONArray("data");
                            JSONArray zones = (JSONArray) data.get(1);
                            for (int i = 0; i < zones.length(); i++) {
                                JSONObject zone = (JSONObject) zones.get(i);
                                JSONObject zone_data = zone.getJSONObject("0");
                                setDanger(zone_data.getDouble("x"), zone_data.getDouble("y"), zone.getDouble("dangereux"));
                            }
                            for (int i = 0; i < 3; i++) {
                                zones_location[i] = new Location("");
                                zones_location[i].setLongitude(x[i]);
                                zones_location[i].setLatitude(y[i]);
                                distance[i] = userLocation.distanceTo(zones_location[i]);
                            }
                            checkInZone(distance);
                            Log.v("Zone 1", "X = " + x[0] + " | Y = " + y[0] + " | Danger = " + danger[0] + " | Distance = " + distance[0]);
                            Log.v("Zone 2", "X = " + x[1] + " | Y = " + y[1] + " | Danger = " + danger[1] + " | Distance = " + distance[1]);
                            Log.v("Zone 3", "X = " + x[2] + " | Y = " + y[2] + " | Danger = " + danger[2] + " | Distance = " + distance[2]);
                        } catch (JSONException e) {
                            Log.v("ZONE SERVICE", "Problem with json");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ZONE SERVICE", "Cannot connect to server");
                Log.e("ZONE SERVICE", error.toString());
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

    private void checkInZone(float[] distance) {
        boolean check = false;

        for (int i = 0; i < distance.length; i++) {
            if (distance[i] <= 250) {
                check = true;
            }
        }

        if (check == true && in_zone == false) {
            showNotification("Attention !",  "Vous êtes dans une zone à risque !");
            in_zone = true;
            check = true;
        }

        if (check == false)
            in_zone = false;
    }

    private void showNotification(String bigMsg, String Msg) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(com.eip.stopclopeip.R.drawable.stopclop_logo)
                .setContentTitle(bigMsg)
                .setContentInfo(Msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(Msg);

        Intent resultIntent = new Intent(this, getBaseContext().getClass());
        mBuilder.setStyle(inboxStyle);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(final Location location) {
            if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
                Log.v("ZONE SERVICE", "Beginning getting zones");
                getDangerousZones(location);
            } else {
                Log.e("ZONE SERVICE", "Can't obtain permissions");
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };
}
