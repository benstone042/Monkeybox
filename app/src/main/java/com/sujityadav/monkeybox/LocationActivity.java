package com.sujityadav.monkeybox;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LocationActivity extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    double latitude;
    double longitude;
    private GoogleApiClient mGoogleApiClient;
    private static final int MY_PERMISSIONS_REQUEST_LOC = 30;
    private RecyclerView recyclerView;
    private Location mLastLocation;
    private RequestQueue queue;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        recyclerView =(RecyclerView) findViewById(R.id.merchant_lv);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            GPSTracker  gps = new GPSTracker(getApplicationContext(),LocationActivity.this);

            // Check if GPS enabled
            if(gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                getNearbyPlace();
                // \n is for new line

            } else {
                // Can't get location.
                // GPS or network is not enabled.
                // Ask user to enable GPS/network in settings.
                gps.showSettingsAlert();
            }
        } else {

            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOC);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            } else {
                GPSTracker  gps = new GPSTracker(getApplicationContext(),LocationActivity.this);

                // Check if GPS enabled
                if(gps.canGetLocation()) {

                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    getNearbyPlace();
                    // \n is for new line

                } else {
                    // Can't get location.
                    // GPS or network is not enabled.
                    // Ask user to enable GPS/network in settings.
                    gps.showSettingsAlert();
                }
            }
        }





    }

    public void getNearbyPlace(){
        showProgressDialog();
     String getnearbyplace = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s&radius=500&types=restaurant" +
                "|department_store" +
                "|cafe" +
                "|convenience_store" +
                "|shopping_mall" +
                "|home_goods_store" +
                "|gas_station" +
                "|store" +
                "|shoe_store" +
                "|electronics_store" +
                "|clothing_store" +
                "|pharmacy" +
                "|night_club" +
                "|movie_theater" +
                "|bowling_alley" +
                "|book_store" +
                "|meal_delivery" +
                "|meal_takeaway" +
                "|lodging" +
                "|bar&sensor=true&key=AIzaSyCT6Z0Tu0Bfy2BoAonXehHsnDr3I6KU3lo";
        String url = String.format(getnearbyplace,latitude+","+longitude);
        queue = Volley.newRequestQueue(LocationActivity.this);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();
                try {
                ArrayList<Place>placelist = new ArrayList<>();
                    JSONArray jsonArray= response.getJSONArray("results");
                    for(int i=0;i<jsonArray.length();++i){
                        JSONObject jsonObject= jsonArray.getJSONObject(i);

                        Place place= new Place();
                        place.setName(jsonObject.getString("name"));
                        place.setAddress(jsonObject.getString("vicinity"));
                        place.setImagelink(jsonObject.getString("icon"));
                        placelist.add(place);
                        NearbyPlaceAdapter  mAdapter = new NearbyPlaceAdapter(placelist,LocationActivity.this);
                        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext());
                        mAdapter.notifyDataSetChanged();
                        recyclerView.setLayoutManager(mLayoutManager1);
                        RecyclerView.ItemDecoration itemDecoration =
                                new DividerItemDecoration(LocationActivity.this, LinearLayoutManager.VERTICAL);
                        recyclerView.addItemDecoration(itemDecoration);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(mAdapter);

                    }


                } catch (JSONException e) {
                    hideProgressDialog();
                    e.printStackTrace();
                    Toast.makeText(LocationActivity.this,"error occured in volley GetNearbyPlace",Toast.LENGTH_LONG).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();

            }
        });
        queue.add(jsObjRequest);

    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void fetchLocation(){
        //Build google API client to use fused location
        buildGoogleApiClient();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (mLastLocation != null) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
                getNearbyPlace();
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOC: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission granted!
                    fetchLocation();

                } else {
                    // permission denied!

                    Toast.makeText(this, "Please grant permission for using this app!", Toast.LENGTH_LONG).show();
                }
                return;
            }


        }
    }
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Fetching Nearby Location..");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }
    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}
