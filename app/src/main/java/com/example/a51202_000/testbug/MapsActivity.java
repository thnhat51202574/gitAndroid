package com.example.a51202_000.testbug;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import AsyncTask.getNearestLocation;
import Model.Address;
import Model.Event;
import Model.User;
import Modules.MDistance;
import at.markushi.ui.CircleButton;
import globalClass.GlobalUserClass;

public class MapsActivity extends FragmentActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,GoogleMap.OnMarkerClickListener {

    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 21212;
    public static final int UPDATE_INTERVAL_IN_SECONDS = 10;
    public static final int MIN_DISTANCE_TO_ALERT = 24;
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
    MapView mMapView;
    GlobalUserClass globalUser;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation,mLastLocation;
    Event cur_event;
    private SlidingUpPanelLayout slidingLayout;
    HashMap<String,Marker> ListMarkerByUser = new HashMap<String,Marker>();
    static private HashMap<Marker,Address> ListMarkerChoose;
    static private HashMap<String, User> ListCurUser;
    static private HashMap<String, Bitmap> listBitmap;
    static private ArrayList<User> arUserOutofRoad;
    static private ArrayList<Marker> nearestLocation;
    ImageView address_picture;
    private View image_progressbar;
    SwitchCompat watchlocation;
    TextView address_name, address_rate, address_position, address_phone, address_type, address_detail,idaddresshidde;
    TextView info_detail, info_warning, nummember, totalmember;
    Boolean watchlocation_flag;
    CircleButton addStopLocation;
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
        globalUser = (GlobalUserClass) getApplicationContext();
        image_progressbar = (View) findViewById(R.id.image_progressbar);
        address_picture = (ImageView) findViewById(R.id.picture);
        address_name = (TextView) findViewById(R.id.detailname);
        address_rate = (TextView) findViewById(R.id.detailRate);
        address_position = (TextView) findViewById(R.id.nameAddress);
        address_phone = (TextView) findViewById(R.id.detailphone);
        address_type = (TextView) findViewById(R.id.detailtype);
        address_detail = (TextView) findViewById(R.id.detailContent);
        idaddresshidde = (TextView) findViewById(R.id.idaddresshidde);
        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        info_detail = (TextView) findViewById(R.id.detailinfo);
        info_warning = (TextView) findViewById(R.id.detailinfo_warning);
        nummember = (TextView) findViewById(R.id.detainum);
        totalmember = (TextView) findViewById(R.id.detaitotalnum);
        watchlocation = (SwitchCompat) findViewById(R.id.watchmylocation);
        addStopLocation = (CircleButton) findViewById(R.id.OkBtnAdd) ;
        watchlocation_flag = true;
        watchlocation.setChecked(watchlocation_flag);
        watchlocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    watchlocation_flag = true;
                    Log.d("CHECK","CHECKED");
                } else {
                    watchlocation_flag = false;
                    Log.d("CHECK","UNCHECKED");
                }
            }
        });
        //some "demo" event
//        slidingLayout.setPanelSlideListener(onSlideListener());
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        slidingLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        ListMarkerChoose = new HashMap<>();
        ListCurUser = new HashMap<>();
        listBitmap = new HashMap<>();
        arUserOutofRoad = new ArrayList<>();
        nearestLocation = new ArrayList<>();
        if(savedInstanceState == null) {
            final Bundle extras = getIntent().getExtras();
            if (extras != null) {
                String cur_event_string = extras.getString("eventString");
                Event_id = extras.getString("event_id");
                User_id = extras.getString("currentUser_id");
                try {
                    JSONObject eventobj = new JSONObject(cur_event_string);
                    cur_event = new Event(eventobj,cur_event_string,false);
                    ArrayList<User> listMember = cur_event.getList_member();
                    User CreateUser = cur_event.getEvent_owner();
                    ListCurUser.put(CreateUser.get_id(),CreateUser);
                    for (int idx = 0; idx < listMember.size(); idx++) {
                        ListCurUser.put(listMember.get(idx).get_id(),listMember.get(idx));
                        info_detail.setText("Kết nối chuyến đi thành công");
                    }
                    totalmember.setText("/" + String.valueOf(cur_event.getCountUser()));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } else {

            }
        }
        mSocket.connect();
        mSocket.on("updatejoin",updatejoinListener);
        mSocket.on("updateUserPosition",updateUserPositionListener);
        mSocket.on("userout",useroutListener);
        mSocket.on("receivenotifyNearStopLocation",receivenotifyNearStopLocationListener);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
        mMapView = (MapView) findViewById(R.id.mapView);

        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                ArrayList<Address> arAddress = new ArrayList<>();
                LatLng startLocs = cur_event.getStartLatlng();
                LatLng endLocs = cur_event.getEndLatLng();
                mMap.addMarker(new MarkerOptions()
                        .title(cur_event.getStartAddress())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_marker1))
                        .position(startLocs));
                mMap.addMarker(new MarkerOptions()
                        .title(cur_event.getEndAddress())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_marker1))
                        .position(endLocs));


                arAddress = cur_event.getList_address();
                for (int idx = 0; idx < arAddress.size();idx++) {
                    Address cur_address = arAddress.get(idx);
                    Marker cur_marker = mMap.addMarker(new MarkerOptions().position(cur_address.getLocs())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_marker)).title(cur_address.getName()));
                    ListMarkerChoose.put(cur_marker,cur_address);
                }
                PolylineOptions polylineOptions = new PolylineOptions().
                        geodesic(true).
                        color(ContextCompat.getColor(MapsActivity.this,R.color.colorAccent)).
                        width(5);

                for (int i = 0; i < cur_event.getArLocs().size(); i++) {
                    LatLng cur_point = cur_event.getArLocs().get(i);
                    polylineOptions.add(cur_point);
                }
                mMap.addPolyline(polylineOptions);
                mMap.setOnMarkerClickListener(MapsActivity.this);
            }
        });
    }

    @Override
    public boolean onMarkerClick(final Marker marker){
            final Object obj = this.ListMarkerChoose.get(marker);
            Address choose_address = null;
            if (obj instanceof Address) {
                choose_address = (Address) obj;
            }
            if(!(choose_address == null)) {
                image_progressbar.setVisibility(View.VISIBLE);
                address_name.setText(choose_address.getName());
                address_rate.setText(String.valueOf(choose_address.getRate()));
                address_position.setText(choose_address.getAddress());
                address_phone.setText(choose_address.getPhone());
                address_detail.setText(choose_address.getdetail());
                address_type.setText("Nhà hàng");
                //idaddresshidde.setText(choose_address.get_id());

                String url = "http://totnghiep.herokuapp.com" + choose_address.getArImage();
                Picasso.with(this).load(url).error(R.drawable.no_images).into(address_picture, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        image_progressbar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        image_progressbar.setVisibility(View.GONE);
                    }
                });
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                slidingLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

                    @Override
                    public void onPanelSlide(View panel, float slideOffset) {

                    }

                    @Override
                    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                        if(nearestLocation.contains(marker)) {
                            //choose to stop
                            addStopLocation.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    new AlertDialog.Builder(MapsActivity.this)
                                    .setMessage("Bạn chắc chắn chọn điểm dừng gần nhất này ?")
                                    .setCancelable(false)
                                    .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            //remove all marker except current marker
                                            for (int index = 0; index < nearestLocation.size();index ++) {
                                                Marker marker_inArray = nearestLocation.get(index);
                                                if(!marker_inArray.equals(marker)) {
                                                    ListMarkerChoose.remove(marker_inArray);
                                                    marker_inArray.remove();
                                                }

                                            }
                                            nearestLocation.clear();
                                            //change icon current marker
                                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_choose_stop));
                                            //notify to all member include user out of road
                                            Address choose_address_local = ListMarkerChoose.get(marker);
                                            String objString = choose_address_local.getAddressStringObj();
                                            JSONObject dataInsert = null;
                                            try {
                                                dataInsert = new JSONObject(objString);
                                                mSocket.emit("notifyNearStopLocation", dataInsert);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    })
                                    .setNegativeButton("Không", null)
                                    .show();
                                    slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                                }
                            });
                            addStopLocation.setVisibility(View.VISIBLE);
                        } else {
                            addStopLocation.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }   else {
                Log.d("TAG","NULL");
            }
        return false;
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
        try {
            updateUI();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.close();
        }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        mGoogleApiClient.disconnect();
        mSocket.disconnect();
        mSocket.close();
    }
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }
    protected void onStop() {
        super.onStop();
    }
    private void updateUI() throws JSONException {

        Log.e("TAG", "UI update initiated .............");
        if (null != mCurrentLocation) {
            String lat = String.valueOf(mCurrentLocation.getLatitude());
            String lng = String.valueOf(mCurrentLocation.getLongitude());

            LatLng myLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            Marker newmarker = ListMarkerByUser.get(globalUser.getCur_user().get_id());

            if(newmarker == null) {

                newmarker = mMap.addMarker(new MarkerOptions().position(myLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.current_marker)).title(globalUser.getCur_user().getName()));

                ListMarkerByUser.put(globalUser.getCur_user().get_id(),newmarker);
            } else {
                newmarker.setPosition(myLocation);
            }
            double distance2Route = MDistance.minDistance(cur_event.getArLocs(),myLocation);

            info_detail.setText(String.valueOf(distance2Route));

            System.out.println("distance = " + distance2Route);

            try {

                JSONObject dataInsert = new JSONObject();
                dataInsert.put("lng",lng);
                dataInsert.put("lat",lat);
                if(distance2Route > MIN_DISTANCE_TO_ALERT) {
                    dataInsert.put("isOutofRoad",true);
                } else {
                    dataInsert.put("isOutofRoad",false);
                }
                dataInsert.put("distance",distance2Route);

                mSocket.emit("sendUserPosition", dataInsert);
            } catch (JSONException e){
                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
            }

            if(watchlocation_flag) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16));
            }
        } else {
            Log.e("TAG", "location is null ...............");

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
                    String numOnline;
                    Double Lastlng;
                    Double Lastlat;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                        userid = data.getString("userid");
                        numOnline = data.getString("numOnline");
                        User socket_user = ListCurUser.get(userid);
                        nummember.setText(numOnline);
                        Marker LastMarker = ListMarkerByUser.get(userid);
                        if(LastMarker == null) {
                           return;
                        } else {
                            Lastlng = LastMarker.getPosition().longitude;
                            Lastlat = LastMarker.getPosition().latitude;
                            LatLng LastPosition = new LatLng(Lastlat,Lastlng);
                            LastMarker.remove();
                            LastMarker = mMap.addMarker(new MarkerOptions().position(LastPosition)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_disconect)).title(username));
                        }
                        ListMarkerByUser.put(userid,LastMarker);
                        if(arUserOutofRoad.contains(socket_user)) {
                            arUserOutofRoad.remove(socket_user);
                        }
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
                    final String userid, username;
                    Double lng;
                    Double lat;
                    Boolean isOutofRoad;
                    Double distance;
                    try {
                        userid = data.getString("userid");
                        username = data.getString("username");
                        lng = data.getDouble("lng");
                        lat = data.getDouble("lat");
                        isOutofRoad = data.getBoolean("isRoutofRoad");
                        distance = data.getDouble("distance");
                        LatLng myLocation = new LatLng(lat,lng);
                        Marker newmarker = ListMarkerByUser.get(userid);

                        Bitmap bitmap_marker_ = listBitmap.get(userid);
                        User socket_user = ListCurUser.get(userid);

                        if(bitmap_marker_ == null) {
                            PicassoMarker target_ = new PicassoMarker();
                            Picasso.with(MapsActivity.this).load(socket_user.getAvatarLink()).into(target_);
                            bitmap_marker_ = target_.getMbitmap();
                            listBitmap.put(userid,bitmap_marker_);
                        }

                        if(newmarker == null) {
                            newmarker = mMap.addMarker(new MarkerOptions().position(myLocation).title(username));
                            newmarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap_marker_));
                            ListMarkerByUser.put(userid,newmarker);
                        } else {
                            newmarker.setPosition(myLocation);
                            newmarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap_marker_));
                        }
                        if(cur_event.isowner(globalUser.getCur_user().get_id())) {
                            if (isOutofRoad) {
                                info_warning.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (arUserOutofRoad.size() > 0)
                                            showDialog(arUserOutofRoad);
                                    }
                                });

                                if (!arUserOutofRoad.contains(socket_user)) {
                                    arUserOutofRoad.add(socket_user);
                                }
                                info_warning.setText(arUserOutofRoad.size() + "thành viên đang bị lạc!");

                            } else {
                                if (arUserOutofRoad.contains(socket_user)) {
                                    arUserOutofRoad.remove(socket_user);
                                    info_detail.setText(username + "đã trở lại đúng đường ");
                                    info_warning.setText("");
                                }
                            }
                        }
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
                    String numOnline;
                    Double lng;
                    Double lat;
                    try {
                        message = data.getString("message");
                        userid = data.getString("userid");
                        username = data.getString("username");
                        numOnline = data.getString("numOnline");
                        nummember.setText(numOnline);
                        if(!userid.equals(globalUser.getCur_user().get_id()))
                        {
                            lng = data.getDouble("lng");
                            lat = data.getDouble("lat");
                            LatLng myLocation = new LatLng(lat, lng);
                            Marker newmarker = ListMarkerByUser.get(userid);

                            Bitmap bitmap_marker_ = listBitmap.get(userid);

                            if(bitmap_marker_ == null) {
                                PicassoMarker target_ = new PicassoMarker();
                                Picasso.with(MapsActivity.this).load(ListCurUser.get(userid).getAvatarLink()).into(target_);
                                bitmap_marker_ = target_.getMbitmap();
                                listBitmap.put(userid,bitmap_marker_);
                            }
                            if (newmarker == null) {
                                newmarker = mMap.addMarker(new MarkerOptions().position(myLocation).title(username));
                                newmarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap_marker_));
                                ListMarkerByUser.put(userid, newmarker);

                            } else {
                                newmarker.setPosition(myLocation);
                                newmarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap_marker_));
                            }
                        }

                        info_detail.setText(message);
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };


    private Emitter.Listener receivenotifyNearStopLocationListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final JSONObject data = (JSONObject) args[0];
                    new AlertDialog.Builder(MapsActivity.this)
                    .setMessage("Có người đi lạc. Cả đội tập trung vào địa điểm được đánh dấu nhé.")
                    .setCancelable(false)
                    .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //draw new address
                            Address nearStopAddress = null;
                            try {
                                nearStopAddress = new Address(data.getJSONObject("address"));
                                LatLng mylocation = nearStopAddress.getLocs();
                                MarkerOptions marker = new MarkerOptions().position(mylocation)
                                    .title(nearStopAddress.getName())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_choose_stop));
                                Marker _marker = mMap.addMarker(marker);
                                //turn of watch location
                                watchlocation_flag = false;
                                watchlocation.setChecked(false);
                                //move camera to new address
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 16));
                                ListMarkerChoose.put(_marker,nearStopAddress);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .show();

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
//            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }
    }
    public class PicassoMarker implements Target {

        Bitmap mbitmap;

        public Bitmap getMbitmap() {
            return mbitmap;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Log.d("test: ", "bitmap loaded");
            mbitmap = getCircularBitmap(bitmap);
//            mMarker.setIcon(BitmapDescriptorFactory.fromBitmap(getCircularBitmap(bitmap)));
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.d("test: ", "bitmap fail");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            Log.d("test: ", "bitmap preload");
        }
        public Bitmap getCircularBitmap(Bitmap inputbitmap) {
            Bitmap bitmap = Bitmap.createScaledBitmap(inputbitmap, 50, 50, false);
            Bitmap output;

            if (bitmap.getWidth() > bitmap.getHeight()) {
                output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            } else {
                output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            float r = 0;

            if (bitmap.getWidth() > bitmap.getHeight()) {
                r = bitmap.getHeight() / 2;
            } else {
                r = bitmap.getWidth() / 2;
            }

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(r, r, r, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            return output;
        }
    }
    protected Bitmap addBorderToCircularBitmap(Bitmap srcBitmap, int borderWidth, int borderColor){
        // Calculate the circular bitmap width with border
        int dstBitmapWidth = srcBitmap.getWidth()+borderWidth*2;

        // Initialize a new Bitmap to make it bordered circular bitmap
        Bitmap dstBitmap = Bitmap.createBitmap(dstBitmapWidth,dstBitmapWidth, Bitmap.Config.ARGB_8888);

        // Initialize a new Canvas instance
        Canvas canvas = new Canvas(dstBitmap);
        // Draw source bitmap to canvas
        canvas.drawBitmap(srcBitmap, borderWidth, borderWidth, null);

        // Initialize a new Paint instance to draw border
        Paint paint = new Paint();
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);
        paint.setAntiAlias(true);


        canvas.drawCircle(
                canvas.getWidth() / 2, // cx
                canvas.getWidth() / 2, // cy
                canvas.getWidth()/2 - borderWidth / 2, // Radius
                paint // Paint
        );

        // Free the native object associated with this bitmap.
        srcBitmap.recycle();

        // Return the bordered circular bitmap
        return dstBitmap;
    }
    public void showDialog(ArrayList<User> arUser){
        final View view = this.getLayoutInflater().inflate (R.layout.out_of_road_dialog, null);

        final Dialog mDetailEventDialog = new Dialog(this,R.style.MaterialDialogSheet);
        mDetailEventDialog.setContentView (view);
        mDetailEventDialog.setCancelable (true);
        mDetailEventDialog.getWindow ().setLayout (LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mDetailEventDialog.getWindow ().setGravity (Gravity.BOTTOM);
        mDetailEventDialog.show();

        Button choosenearstop = (Button) view.findViewById(R.id.choosenearstop);
        Button CANCELbtn = (Button) view.findViewById(R.id.CANCEL);


        //draw nearlocation
        choosenearstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Marker newmarker = ListMarkerByUser.get(globalUser.getCur_user().get_id());
                getNearestLocation aroundLocation = new getNearestLocation(newmarker.getPosition(), new getNearestLocation.OnArrountCompleted() {
                    @Override
                    public void getListAddress(ArrayList<Address> result) {
                        ArrayList<Address> mArAddress = result;
                        for (int i = 0;i < mArAddress.size();i++) {
                            Address eachAddress = mArAddress.get(i);
                            LatLng mylocation = eachAddress.getLocs();
                            MarkerOptions marker = new MarkerOptions().position(mylocation)
                                    .title(eachAddress.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_marker));
                            Marker _marker = mMap.addMarker(marker);
                            if(!nearestLocation.contains(_marker)) {
                                nearestLocation.add(_marker);
                                ListMarkerChoose.put(_marker,eachAddress);
                            }
                        }
                        mDetailEventDialog.dismiss();
                    }
            });
                aroundLocation.execute("http://totnghiep.herokuapp.com/api/ListAddressByLocation");
            }
        });

        ListView gvlistMember = (ListView) view.findViewById(R.id.gvlistMember);
        UserOutOfRoadCustomListview adapter = new UserOutOfRoadCustomListview(getApplicationContext(), R.layout.out_of_road_layout,arUser);
        gvlistMember.setAdapter(adapter);
        adapter.setOnEachItemChangeListener(new UserOutOfRoadCustomListview.OnClickListener() {
            @Override
            public void onCallClick(final User mUser, int Position) {
                if(ContextCompat.checkSelfPermission(MapsActivity.this,
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MapsActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);
                } else {
                    MapsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:"+ mUser.getPhone()));
                            startActivity(callIntent);
                        }
                    });
                }
            }

            @Override
            public void onFindLocationClick(User mUser, int Position) {
                Marker user_marker = ListMarkerByUser.get(mUser.get_id());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(user_marker.getPosition()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(user_marker.getPosition(), 16));
            }
        });
        Button startbtn = (Button) view.findViewById(R.id.START);
        Button cancelbtn = (Button) view.findViewById(R.id.CANCEL);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
        }
    }
    public Bitmap resizeMapIcons(String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }


}
