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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

import globalClass.GlobalUserClass;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int MILLISECONDS_PER_SECOND = 1000;

    public static final int UPDATE_INTERVAL_IN_SECONDS = 10;
    private static final long UPDATE_INTERVAL =  MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private static final int INITIAL_REQUEST=1337;
    private GoogleMap mMap;
    private String Event_id;
    private String User_id;
    GlobalUserClass globalUser;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation,mLastLocation;
    HashMap<String,Marker> ListMarkerByUser = new HashMap<String,Marker>();
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://totnghiep.herokuapp.com/");
//            mSocket = IO.socket("http://192.168.1.113:3000/");
            Log.e("TAG", "success ...............: ");
        } catch (URISyntaxException e) {
            Log.e("TAG", "erorsocket ...............: " + e.toString());
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        globalUser = (GlobalUserClass) getApplicationContext();

        if(savedInstanceState == null) {
            final Bundle extras = getIntent().getExtras();
            if (extras != null) {
                Event_id = extras.getString("event_id");
                User_id = extras.getString("currentUser_id");
            } else {

            }
        }
        mSocket.connect();
        mSocket.on("updatejoin",updatejoinListener);
        mSocket.on("updateUserPosition",updateUserPositionListener);
        mSocket.on("userout",useroutListener);
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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        addUsertoliveEvent();
        createLocationRequest();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSocket.connect();
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
        mSocket.close();
    }
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }
    protected void onStop() {
        mGoogleApiClient.disconnect();
        mSocket.close();
        mSocket.disconnect();
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
                dataInsert.put("lng",lng);
                dataInsert.put("lat",lat);
                mSocket.emit("sendUserPosition", dataInsert);
            } catch (JSONException e){
                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
            }
            LatLng myLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            Marker newmarker = ListMarkerByUser.get(globalUser.getCur_user().get_id());
            if(newmarker == null) {
                newmarker = mMap.addMarker(new MarkerOptions().position(myLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_marker)).title(globalUser.getCur_user().getName()));
            } else {
                newmarker.remove();
//                Toast.makeText(getApplicationContext(),lat +',' + lng,Toast.LENGTH_LONG).show();
                newmarker = mMap.addMarker(new MarkerOptions().position(myLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_marker)).title(globalUser.getCur_user().getName()));
            }
            ListMarkerByUser.put(globalUser.getCur_user().get_id(),newmarker);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,16));
        } else {
            Log.e("TAG", "location is null ...............");
            Toast.makeText(getApplicationContext(), "Location Null", Toast.LENGTH_LONG).show();
        }
    }

    private Emitter.Listener useroutListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message, userid, username;
                    Double Lastlng;
                    Double Lastlat;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                        userid = data.getString("userid");
                        Marker LastMarker = ListMarkerByUser.get(userid);
                        if(LastMarker == null) {
                           return;
                        } else {
                            Lastlng = LastMarker.getPosition().longitude;
                            Lastlat = LastMarker.getPosition().latitude;
                            LatLng LastPosition = new LatLng(Lastlat,Lastlng);
                            LastMarker.remove();
                            LastMarker = mMap.addMarker(new MarkerOptions().position(LastPosition)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_marker)).title(username));
                        }
                        ListMarkerByUser.put(userid,LastMarker);
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };
    private Emitter.Listener updateUserPositionListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String userid, username;
                    Double lng;
                    Double lat;
                    try {
                        userid = data.getString("userid");
                        username = data.getString("username");
                        lng = data.getDouble("lng");
                        lat = data.getDouble("lat");
                        LatLng myLocation = new LatLng(lat,lng);
                        Marker newmarker = ListMarkerByUser.get(userid);
                        if(newmarker == null) {
                            newmarker = mMap.addMarker(new MarkerOptions().position(myLocation).title(username));
                        } else {
                            newmarker.remove();
                            newmarker = mMap.addMarker(new MarkerOptions().position(myLocation).title(username));
                        }
                        ListMarkerByUser.put(userid,newmarker);
                        Toast.makeText(getApplicationContext(),username + " cập nhật vị trí",Toast.LENGTH_LONG).show();
                        Log.d("========>", "run: "+ListMarkerByUser.size());
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    private Emitter.Listener updatejoinListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message, userid, username;
                    Double lng;
                    Double lat;
                    try {
                        message = data.getString("message");
                        userid = data.getString("userid");
                        username = data.getString("username");
                        lng = data.getDouble("lng");
                        lat = data.getDouble("lat");
                        LatLng myLocation = new LatLng(lat,lng);
                        Marker newmarker = ListMarkerByUser.get(userid);
                        if(newmarker == null) {
                            newmarker = mMap.addMarker(new MarkerOptions().position(myLocation).title(username));
                        } else {
                            newmarker.remove();
                            newmarker = mMap.addMarker(new MarkerOptions().position(myLocation).title(username));
                        }
                        ListMarkerByUser.put(userid,newmarker);
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };


    private Emitter.Listener onUserJoinOrOut = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //
                }
            });
        }
    };
    private void addUsertoliveEvent(){

        String lat = String.valueOf(mLastLocation.getLatitude());
        String lng = String.valueOf(mLastLocation.getLongitude());
        try {
            JSONObject dataInsert = new JSONObject();
            //create JSONdata to send to server
            dataInsert.put("uname",globalUser.getCur_user().getName());
            dataInsert.put("uid",globalUser.getCur_user().get_id());
            dataInsert.put("eventid",Event_id);
            dataInsert.put("lng",lng);
            dataInsert.put("lat",lat);
            mSocket.emit("addUsertoliveEvent", dataInsert);
        } catch (JSONException e){
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }
    }
}
