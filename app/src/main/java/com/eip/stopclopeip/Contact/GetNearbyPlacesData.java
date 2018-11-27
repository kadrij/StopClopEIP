package com.eip.stopclopeip.Contact;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;

import com.eip.stopclopeip.Contact.DataParser;
import com.eip.stopclopeip.Contact.DownloadUrl;
import com.eip.stopclopeip.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {
    String googlePlacesData;
    GoogleMap mMap;
    String url;

    private Context context;

    public GetNearbyPlacesData(Context current){
        this.context = current;
    }

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap)objects[0];
        url = (String)objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlacesData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String, String>> nearbyPlaceList = null;
        DataParser parser = new DataParser();
        nearbyPlaceList = parser.parse(s);
        showNearbyPlaces(nearbyPlaceList);
    }

    private void showNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        BitmapDrawable openDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.ic_pharmacy);
        BitmapDrawable closedDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.ic_pharmacy_closed);
        Bitmap openMarker = Bitmap.createScaledBitmap(openDrawable.getBitmap(), 48, 48, false);
        Bitmap closedMarker = Bitmap.createScaledBitmap(closedDrawable.getBitmap(), 48, 48, false);
        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);

            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            LatLng latLng = new LatLng(lat, lng);

            markerOptions.position(latLng);
            markerOptions.title(placeName);
            markerOptions.snippet(vicinity);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(openMarker));

            mMap.addMarker(markerOptions);
        }
    }
}
