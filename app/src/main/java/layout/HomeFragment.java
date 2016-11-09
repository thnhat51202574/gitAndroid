package layout;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a51202_000.testbug.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import AsyncTask.getNearestLocationTask;
import Model.Address;
import globalClass.GlobalUserClass;

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
    private HashMap<Marker,Address> listAddressbyMaker = new HashMap<Marker,Address>();
    private SlidingUpPanelLayout slidingLayout;
    private View image_progressbar;
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
        image_progressbar = (View) rootView.findViewById(R.id.image_progressbar);
        address_picture =(ImageView) rootView.findViewById(R.id.picture);
        address_name = (TextView) rootView.findViewById(R.id.detailname);
        address_rate = (TextView) rootView.findViewById(R.id.detailRate);
        address_position = (TextView) rootView.findViewById(R.id.nameAddress);
        address_phone = (TextView) rootView.findViewById(R.id.detailphone);
        address_type = (TextView) rootView.findViewById(R.id.detailtype);
        address_detail = (TextView) rootView.findViewById(R.id.detailContent);
        slidingLayout = (SlidingUpPanelLayout)rootView.findViewById(R.id.sliding_layout);

        //some "demo" event
//        slidingLayout.setPanelSlideListener(onSlideListener());
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        slidingLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i("TAG", "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i("TAG", "onPanelStateChanged " + newState);
            }
        });
        slidingLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

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
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
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
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
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
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker){
        image_progressbar.setVisibility(View.VISIBLE);
        final GlobalUserClass globalUser = (GlobalUserClass) getActivity().getApplicationContext();
        Toast.makeText(getActivity(),globalUser.get_id(),Toast.LENGTH_LONG).show();
        Address choose_address = this.listAddressbyMaker.get(marker);
        address_name.setText(choose_address.getName());
        address_rate.setText(String.valueOf(choose_address.getRate()));
        address_position.setText(choose_address.getAddress());
        address_phone.setText(choose_address.getPhone());
        address_detail.setText(choose_address.getdetail());
        address_type.setText("Nhà hàng");

        String url ="http://totnghiep.herokuapp.com"+ choose_address.getArImage();
        Picasso.with(getActivity()).load(url).error(R.drawable.no_images).into(address_picture, new com.squareup.picasso.Callback() {
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