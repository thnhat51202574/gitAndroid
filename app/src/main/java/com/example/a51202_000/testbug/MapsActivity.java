package com.example.a51202_000.testbug;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int MILLISECONDS_PER_SECOND = 1000;

    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    private static final long UPDATE_INTERVAL =  MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private static final int INITIAL_REQUEST=1337;
    private GoogleMap mMap;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation,mLastLocation;
    String UName;
    HashMap<String,Marker> ListMarkerByUser = new HashMap<String,Marker>();
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://totnghiep.herokuapp.com/");
            Log.e("TAG", "success ...............: ");
        } catch (URISyntaxException e) {
            Log.e("TAG", "erorsocket ...............: " + e.toString());
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        final Bundle extras = getIntent().getExtras();
        if(extras!= null) {
            UName = extras.getString("username");
            Log.e("TAG", "NAME ...............: " +UName);
        }
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.e("TAG", "onCreatea");
        mSocket.on("newmessageadd",onNewmessage);
        mSocket.on("disMess",onUserdisconnect);
        mSocket.connect();
        //listening
        //end listening socket

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.e("TAG", "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        Log.e("TAG", "haspermission ...............: " +canAccessLocation() );
        if(!canAccessLocation()){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    INITIAL_REQUEST);
        }



    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        Log.e("TAG", "requestcode - ...............: " + grantResults[0]);
        Log.e("TAG", "requestcode - ...............: " + PackageManager.PERMISSION_GRANTED);
        switch (requestCode) {
            case INITIAL_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                            mGoogleApiClient);
//                    if (mLastLocation != null) {
//                        String lat = String.valueOf(mLastLocation.getLatitude());
//                        String lng = String.valueOf(mLastLocation.getLongitude());
//                        Log.e("TAG", "Latitude - ...............: " + lat);
//                        Log.e("TAG", "Longitude - ...............: " + lng);
//                        LatLng myLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//                        mMap.addMarker(new MarkerOptions().position(myLocation).title("Marker in My location"));
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
//                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,16));
//
//                    }
                    createLocationRequest();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        startLocationUpdates();
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("TAG", "Connection failed: " + connectionResult.toString());
    }
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.e("TAG", "Location update started ..............: ");
    }
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
//        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    private void updateUI() {
        Log.e("TAG", "UI update initiated .............");
        if (null != mCurrentLocation) {
            String lat = String.valueOf(mCurrentLocation.getLatitude());
            String lng = String.valueOf(mCurrentLocation.getLongitude());

//            Toast.makeText(getApplicationContext(), "Longitude: " + lng + "\nLatitude: "
//                    + lat, Toast.LENGTH_LONG).show();
            try {
                JSONObject dataInsert = new JSONObject();
                //create JSONdata to send to server
                dataInsert.put("uname",UName);
                dataInsert.put("lng",lng);
                dataInsert.put("lat",lat);
                mSocket.emit("newmessage", dataInsert);
            } catch (JSONException e){
                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
            }

            LatLng myLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            Marker newmarker = ListMarkerByUser.get(UName);
            if(newmarker == null) {
                Toast.makeText(getApplicationContext(),"NOCHANGE",Toast.LENGTH_LONG).show();
                newmarker = mMap.addMarker(new MarkerOptions().position(myLocation).title(UName));
            } else {
                newmarker.remove();
                Toast.makeText(getApplicationContext(),lat +',' + lng,Toast.LENGTH_LONG).show();
                newmarker = mMap.addMarker(new MarkerOptions().position(myLocation).title(UName));
            }
            ListMarkerByUser.put(UName,newmarker);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,16));
        } else {
            Log.e("TAG", "location is null ...............");
            Toast.makeText(getApplicationContext(), "Location Null", Toast.LENGTH_LONG).show();
        }
    }
    private boolean canAccessLocation() {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }
    private boolean hasPermission(String perm) {
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_CALENDAR);
        return (permissionCheck == PackageManager.PERMISSION_GRANTED);
    }
    private Emitter.Listener onNewmessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    ListUser = (ListView) findViewById(R.id.listUser);
                    JSONObject data = (JSONObject) args[0];
                    String name;
                    Double lng;
                    Double lat;
                    try {
                        name = data.getString("name");
                        lng = data.getDouble("lng");
                        lat = data.getDouble("lat");
                        LatLng myLocation = new LatLng(lat,lng);
                        Marker newmarker = ListMarkerByUser.get(name);
                        if(newmarker == null) {
                            newmarker = mMap.addMarker(new MarkerOptions().position(myLocation).title(name));
                        } else {
                            newmarker.remove();
                            newmarker = mMap.addMarker(new MarkerOptions().position(myLocation).title(name));
                        }
                        ListMarkerByUser.put(name,newmarker);
//                        Toast.makeText(getApplicationContext(),name,Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };
    private Emitter.Listener onUserdisconnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String name;
                    try {
                        name = data.getString("name");
                        Marker newmarker = ListMarkerByUser.get(name);
                        if(newmarker != null) {
                            newmarker.remove();
                        }
                        ListMarkerByUser.remove(name);
                        Toast.makeText(getApplicationContext(),name + " disconect",Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };
}
