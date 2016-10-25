package layout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.Button;
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
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;
import AsyncTask.getNearestLocationTask;

public class HomeFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {



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
        return rootView;
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
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mylocation , 13));

                getNearestLocationTask nearestLocationTask = new getNearestLocationTask(getActivity(),googleMap);
                nearestLocationTask.execute("PRAMA");
            }
        });
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
