package com.eip.stopclopeip.Contact;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.android.gms.maps.model.LatLng;

public class ContactFragment extends Fragment implements OnMapReadyCallback {
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    public static final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 20000;
    private GoogleMap mMap;
    double latitude;
    double longitude;
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
}
