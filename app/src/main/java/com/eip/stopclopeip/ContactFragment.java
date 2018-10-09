package com.eip.stopclopeip;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = view.findViewById(R.id.contact_map);

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
                ActivityCompat.requestPermissions(getActivity().getParent(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            else
                ActivityCompat.requestPermissions(getActivity().getParent(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            return false;
        } else
            return true;
    }
}
