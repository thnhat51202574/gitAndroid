package layout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a51202_000.testbug.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import AsyncTask.getNearestLocationTask;
import AsyncTask.DownloadImageTask;
import Model.Address;

public class HomeFragment extends Fragment implements GoogleMap.OnMarkerClickListener, getNearestLocationTask.OnTaskCompleted, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public interface comuticateParent {
        public void sendMess(String text);
    }
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    comuticateParent callback;
    Button btn1;
    TextView textView;
    MapView mMapView;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    public GoogleMap googleMap;
    private View hiddenPanel;
    private Button upbtn, downbtn;
    private HashMap<Marker,Address> listAddressbyMaker = new HashMap<Marker,Address>();;

//    detail address
    ImageView address_picture;
    TextView address_name, address_rate, address_position, address_phone, address_type, address_detail;
//    end detail
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
//        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"ON MAP",Toast.LENGTH_LONG).show();
            }
        });
//        btn1 = (Button) rootView.findViewById(R.id.button);
//        btn1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                callback.sendMess("Đây là tin nhắn từ frament");
//            }
//        });
//        textView = (TextView) rootView.findViewById(R.id.texthello);
//        textView.setText("Đây là fragment 2");

        //init map to fragment
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        upbtn = (Button) rootView.findViewById(R.id.upbtn);
        downbtn = (Button) rootView.findViewById(R.id.downbtn);
        hiddenPanel = rootView.findViewById(R.id.hidden_panel);
        address_picture =(ImageView) rootView.findViewById(R.id.picture);
        address_name = (TextView) rootView.findViewById(R.id.detailname);
        address_rate = (TextView) rootView.findViewById(R.id.detailRate);
        address_position = (TextView) rootView.findViewById(R.id.nameAddress);
        address_phone = (TextView) rootView.findViewById(R.id.detailphone);
        address_type = (TextView) rootView.findViewById(R.id.detailtype);
        address_detail = (TextView) rootView.findViewById(R.id.detailContent);
        downbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideUpDown(hiddenPanel);
            }
        });
        return rootView;
    }
    private boolean isPanelShown() {
        return hiddenPanel.getVisibility() == View.VISIBLE;
    }
    public void slideUpDown(final View view) {
        if (!isPanelShown()) {
            // Show the panel
            Animation bottomUp = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.bottom_up);
            hiddenPanel.startAnimation(bottomUp);
            hiddenPanel.setVisibility(View.VISIBLE);
        }
        else {
            // Hide the Panel
            Animation bottomDown = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.bottom_down);
            hiddenPanel.startAnimation(bottomDown);
            hiddenPanel.setVisibility(View.GONE);
        }
    }
    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        Log.d("ConnectonStart", "Connected ");
        Log.d("ONstart", Boolean.toString(mGoogleApiClient.isConnected()));
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onResume() {
        mGoogleApiClient.connect();
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Connect", "Connected ");
        Log.d("onConnected", Boolean.toString(mGoogleApiClient.isConnected()));
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                googleMap.setMyLocationEnabled(true);

                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                LatLng mylocation =new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mylocation , 14));
                getNearestLocationTask nearestLocationTask = new getNearestLocationTask(getActivity(),googleMap,mLastLocation,HomeFragment.this);
                nearestLocationTask.execute("http://totnghiep.herokuapp.com/api/nearestAddress");
                googleMap.setOnMarkerClickListener(HomeFragment.this);
                address_name.setText("12312312");
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker){
        Address choose_address = this.listAddressbyMaker.get(marker);
        address_name.setText(choose_address.getName());
        address_rate.setText(String.valueOf(choose_address.getRate()));
        address_position.setText(choose_address.getAddress());
        address_phone.setText(choose_address.getPhone());
        address_detail.setText(choose_address.getdetail());
        address_type.setText("Nhà hàng");

        String url ="http://totnghiep.herokuapp.com"+ choose_address.getArImage();
        new DownloadImageTask(address_picture)
                .execute("http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png");


        slideUpDown(hiddenPanel);
        return false;
    }

    @Override
    public void getListAddress(HashMap<Marker, Address> result) {
        this.listAddressbyMaker = result;
    }
    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Connect", "failed ");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;
        if (context instanceof Activity){
            activity=(Activity) context;
            callback = (comuticateParent) getActivity();
        }
    }
    public void receiveMess(String Text) {
        Toast.makeText(getActivity(),Text,Toast.LENGTH_LONG).show();
//        textView.setText(Text);
    }

}
