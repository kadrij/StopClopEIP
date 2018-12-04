package com.eip.stopclopeip.Contact;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eip.stopclopeip.R;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ContactFragment extends Fragment implements OnMapReadyCallback {
    private String url = "http://romain-caldas.fr/api/rest.php?dev=69";
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    private Circle[] circle;
    public static final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 20000;
    private GoogleMap mMap;
    double latitude;
    double longitude;
    double[] x, y, danger;
    MapView mMapView;
    View mView;

    public ContactFragment() {
    }

    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_contact, container, false);
        return mView;
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Votre GPS semble désactivé, pour le bon fonctionnement de l'application, il est préférable de l'activer.")
                .setCancelable(false)
                .setPositiveButton("Activer", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = view.findViewById(R.id.contact_map);

        statusCheck();
        x = new double[]{0, 0, 0};
        y = new double[]{0, 0, 0};
        danger = new double[]{0, 0, 0};
        circle = new Circle[3];

        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        mGeoDataClient = Places.getGeoDataClient(getActivity().getBaseContext(), null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity().getBaseContext(), null);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        final Boolean[] init = {false};
        final GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData(getActivity());
        checkLocationPermission();
        MapsInitializer.initialize(getActivity().getBaseContext());
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location arg0) {
                latitude = arg0.getLatitude();
                longitude = arg0.getLongitude();
                if (init[0] == false) {
                    CameraPosition pos = CameraPosition.builder().target(new LatLng(arg0.getLatitude(), arg0.getLongitude())).zoom(12).bearing(0).tilt(0).build();
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
                    getPharmacies(getNearbyPlacesData);
                    getDangerousZones(latitude, longitude);
                    init[0] = true;
                }
            }
        });

        MapsInitializer.initialize(getActivity().getBaseContext());
    }

    private void getPharmacies(GetNearbyPlacesData getNearbyPlacesData) {
        String url = getUrl(latitude, longitude);
        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;
        getNearbyPlacesData.execute(dataTransfer);
    }

    private void getDangerousZones(double latitude, double longitude) {
        final RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url
                + "&function=algo&email="
                + getArguments().getString("email")
                + "&token="
                + getArguments().getString("token")
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
                                circle[i] = drawDangerZone(x[i], y[i]);
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

    private Circle drawDangerZone(double zone_x, double zone_y) {
        Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(zone_y, zone_x))
                .radius(250)
                .strokeWidth(2)
                .strokeColor(Color.RED)
                .fillColor(Color.argb(60, 89, 0, 0))
                .clickable(true));
        return circle;
    }

    private String getUrl(double latitude, double longitude) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type=pharmacy");
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key=AIzaSyAMMlC-L-DD2n08cSuNnoukKLVNzqJzn_U");
        return googlePlaceUrl.toString();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity().getParent(), Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(getActivity().getParent(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_CODE);
            else
                ActivityCompat.requestPermissions(getActivity().getParent(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_CODE);
            return false;
        } else
            return true;
    }

    public void Alert(String Msg) {
        Toast.makeText(this.getActivity(), Msg, Toast.LENGTH_SHORT).show();
    }
}
