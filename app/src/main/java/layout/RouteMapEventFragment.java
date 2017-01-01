package layout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a51202_000.testbug.AddEventActivity;
import com.example.a51202_000.testbug.R;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import AsyncTask.getNearestLocationTask;
import Model.Address;
import Model.Event;
import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.EventRoute;
import Modules.EventRouteFinder;
import Modules.EventRouterFinderListener;
import Modules.Route;


public class RouteMapEventFragment extends Fragment implements GoogleMap.OnMarkerClickListener,DirectionFinderListener, EventRouterFinderListener {
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Marker> addressMarker = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    static private HashMap<Marker,Address> ListAddressChoose;
    private HashMap<String,Marker> mMarkerbyid;
    private ProgressDialog progressDialog;

    public interface RooteComunicate {
        public void senlocation(LatLng start,LatLng destination);
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    RooteComunicate callback;
    private String mParam1;
    private String mParam2;
    public GoogleMap googleMap;
    private MapView mMapView;
    private SlidingUpPanelLayout slidingLayout;
    ImageView address_picture;
    private View image_progressbar;
    TextView address_name, address_rate, address_position, address_phone, address_type, address_detail,idaddresshidde;
    LatLng StartAddress;
    LatLng Destination;
    public RouteMapEventFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RouteMapEventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RouteMapEventFragment newInstance(String param1, String param2) {
        RouteMapEventFragment fragment = new RouteMapEventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        ListAddressChoose = new HashMap<>();
        mMarkerbyid = new HashMap<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_route_map_event, container, false);
        // Inflate the layout for this fragment
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        image_progressbar = (View) rootView.findViewById(R.id.image_progressbar);
        address_picture = (ImageView) rootView.findViewById(R.id.picture);
        address_name = (TextView) rootView.findViewById(R.id.detailname);
        address_rate = (TextView) rootView.findViewById(R.id.detailRate);
        address_position = (TextView) rootView.findViewById(R.id.nameAddress);
        address_phone = (TextView) rootView.findViewById(R.id.detailphone);
        address_type = (TextView) rootView.findViewById(R.id.detailtype);
        address_detail = (TextView) rootView.findViewById(R.id.detailContent);
        idaddresshidde = (TextView) rootView.findViewById(R.id.idaddresshidde);
        slidingLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
        addressMarker = new ArrayList<>();
        //some "demo" event
//        slidingLayout.setPanelSlideListener(onSlideListener());
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        slidingLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        AddEventActivity addEventActivity = (AddEventActivity) getActivity();
        StartAddress = addEventActivity.getStartaddress();
        Destination = addEventActivity.getDestination();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                String origin = String.valueOf(StartAddress.latitude) + "," +String.valueOf(StartAddress.longitude);
                String destination_str = String.valueOf(Destination.latitude) + "," +String.valueOf(Destination.longitude);
                try {
                    new DirectionFinder(RouteMapEventFragment.this, origin, destination_str, ((AddEventActivity) getActivity()).arStopAddressObj).execute();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(StartAddress).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.setOnMarkerClickListener(RouteMapEventFragment.this);
            }
        });
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_member_event, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_ok:
                getActivity().onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onResume() {
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
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;
        if (context instanceof Activity){
            activity=(Activity) context;
            callback = (RouteMapEventFragment.RooteComunicate) getActivity();
        }
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(getActivity(), "Vui lòng đợi.",
                "Tìm dẫn đường..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }
        if (addressMarker != null) {
            for (Marker marker : addressMarker) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes, List<Route> arRoute,String points_) throws UnsupportedEncodingException {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();
        new EventRouteFinder(RouteMapEventFragment.this,arRoute,getActivity()).execute();
        for (Route route : routes) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));

            originMarkers.add(googleMap.addMarker(new MarkerOptions()
                    .title(route.startAddress)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_marker1))
                    .position(route.startLocation)));
            destinationMarkers.add(googleMap.addMarker(new MarkerOptions()
                    .title(route.endAddress)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_marker1))
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(ContextCompat.getColor(getActivity(),R.color.colorAccent)).
                    width(5);

            for (int i = 0; i < route.getPoints().size(); i++) {
                LatLng cur_point = route.getPoints().get(i);
                polylineOptions.add(cur_point);
            }
            ((AddEventActivity) getActivity()).setPoints(points_) ;
            polylinePaths.add(googleMap.addPolyline(polylineOptions));
        }
    }
    @Override
    public void onEventRouterFinderStart() {
        progressDialog = ProgressDialog.show(getActivity(), "Vui lòng đợi.",
                "Tìm gợi ý..!", true);
    }

    @Override
    public void onEventRouterFinderFinish(ArrayList<EventRoute> arEventRoutes) {
//        stopMarkers = new ArrayList<>();
        for (int i = 0; i < arEventRoutes.size(); i++) {
            EventRoute eachRoute = arEventRoutes.get(i);
            ArrayList<Address>  listAdderss = eachRoute.getAddressAroundLastLocation();
            for (int idx = 0; idx < listAdderss.size(); idx++) {
                Address eachAddress = listAdderss.get(idx);
                LatLng mylocation = eachAddress.getLocs();
                MarkerOptions marker = new MarkerOptions().position(mylocation).title(eachAddress.getName());

                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_marker));
                Marker _marker = googleMap.addMarker(marker);
                addressMarker.add(_marker);
                ListAddressChoose.put(_marker,eachAddress);
                if(((AddEventActivity) getActivity()).arStopAddressId.contains(eachAddress.get_id())) {
                    _marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.stop_marker));
                }
                mMarkerbyid.put(eachAddress.get_id(),_marker);

            }
        }
        progressDialog.dismiss();
    }
    public Bitmap resizeMapIcons(String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getActivity().getResources(),getActivity().getResources().getIdentifier(iconName, "drawable", getActivity().getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    @Override
    public boolean onMarkerClick(final Marker marker){
        if(originMarkers.contains(marker) || destinationMarkers.contains(marker))
            Toast.makeText(getActivity().getApplicationContext(),"HERE",Toast.LENGTH_LONG).show();
        else {
            image_progressbar.setVisibility(View.VISIBLE);
            final Address choose_address = this.ListAddressChoose.get(marker);
            address_name.setText(choose_address.getName());
            address_rate.setText(String.valueOf(choose_address.getRate()));
            address_position.setText(choose_address.getAddress());
            address_phone.setText(choose_address.getPhone());
            address_detail.setText(choose_address.getdetail());
            address_type.setText("Nhà hàng");
            idaddresshidde.setText(choose_address.get_id());

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
            slidingLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                @Override
                public void onPanelSlide(View panel, float slideOffset) {


                }

                @Override
                public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState,
                                                SlidingUpPanelLayout.PanelState newState) {
                    if(newState.toString().equals("EXPANDED")) {
                        if(((AddEventActivity) getActivity()).arStopAddressId.contains(choose_address.get_id())) {
                            panel.findViewById(R.id.OkBtnAdd).setVisibility(View.GONE);
                            panel.findViewById(R.id.CancelBtnAdd).setVisibility(View.VISIBLE);
                        } else {
                            panel.findViewById(R.id.OkBtnAdd).setVisibility(View.VISIBLE);
                            panel.findViewById(R.id.CancelBtnAdd).setVisibility(View.GONE);
                        }
                        panel.findViewById(R.id.OkBtnAdd).setOnClickListener(new View.OnClickListener(){
                            public void onClick(View v) {
//                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.stop_marker));
                                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                                if(!((AddEventActivity) getActivity()).arStopAddressId.contains(choose_address.get_id())) {
                                    ((AddEventActivity) getActivity()).arStopAddressId.add(choose_address.get_id());
                                    ((AddEventActivity) getActivity()).arStopAddressObj.add(choose_address);
                                }
                                //update polyline
                                updateRoute();
                            }
                        });
                        panel.findViewById(R.id.CancelBtnAdd).setOnClickListener(new View.OnClickListener(){
                            public void onClick(View v) {
//                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_marker));
                                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                                if(((AddEventActivity) getActivity()).arStopAddressId.contains(choose_address.get_id())) {
                                    ((AddEventActivity) getActivity()).arStopAddressId.remove(choose_address.get_id());
                                    ArrayList<Address> tmp = ((AddEventActivity) getActivity()).arStopAddressObj;
                                    for (int idex = 0;idex < tmp.size(); idex ++) {
                                        if (tmp.get(idex).get_id().equals(choose_address.get_id())) {
                                            ((AddEventActivity) getActivity()).arStopAddressObj.remove(idex);
                                            break;
                                        }
                                    }
                                }
                                //update polyline
                                updateRoute();
                            }
                        });
                    }
                }
            });
        }
        return false;
    }
    private void updateRoute() {
        String origin = String.valueOf(StartAddress.latitude) + "," +String.valueOf(StartAddress.longitude);
        String destination_str = String.valueOf(Destination.latitude) + "," +String.valueOf(Destination.longitude);
        //remove old marker;

        try {
            new DirectionFinder(RouteMapEventFragment.this, origin, destination_str, ((AddEventActivity) getActivity()).arStopAddressObj).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
