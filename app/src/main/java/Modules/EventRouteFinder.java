package Modules;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import AsyncTask.getAroundLocation;
import Model.Address;

/**
 * Created by 51202_000 on 04/12/2016.
 */

public class EventRouteFinder {
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyCRtFrFmd_KSJuTfi6jc1121xzaIaNmolY";
    HashMap<Route,ArrayList<Address>> ListAddress = new HashMap<>();
    private List<Route> arRoute;
    private EventRouterFinderListener listener;
    private int timetoRest;
    SharedPreferences pref;
    private Context mcontext;
    public EventRouteFinder(EventRouterFinderListener listener, List<Route> arParamsRoute, Context context) {
        this.listener = listener;
        this.arRoute = arParamsRoute;
        this.mcontext = context;
    }

    public void execute() throws UnsupportedEncodingException {
        listener.onEventRouterFinderStart();
        getAroundLocation aroundLocation = new getAroundLocation(arRoute, new getAroundLocation.OnArrountCompleted() {
            @Override
            public void getListAddress(HashMap<Route,ArrayList<Address>> result) {
                pref = mcontext.getSharedPreferences("MyPref", 0);
                ListAddress = result;
                timetoRest = pref.getInt("timetoRest", 3600);
                getDistanceAfterTime(timetoRest);
            }
        });
        aroundLocation.execute("http://totnghiep.herokuapp.com/api/ListAddressByLocation");

//        new DirectionFinder.DownloadRawData().execute(createUrl());
    }
    public ArrayList<EventRoute> getDistanceAfterTime(int value) {
        ArrayList<Route> Routes = new ArrayList<>();
        ArrayList<EventRoute> routeReturn = new ArrayList<>();
        int distance = 0;
        int time_value = 0;
        LatLng start_route = null;
        String startAddress = "";
        boolean start = true;
        for (int i = 0; i < this.arRoute.size(); i++) {
            Route cur_route = this.arRoute.get(i);
            if (start) {
                start_route = cur_route.getEndLocation();
                startAddress = cur_route.getEndAddress();
                start = false;
            }
            distance += cur_route.getDistance().getValue();
            time_value += cur_route.getDuration().getValue();
            Routes.add(cur_route);
            if (time_value >= value) {
                ArrayList<Address> listAddress = ListAddress.get(cur_route);
                if(listAddress.size()>0) {
                    LatLng end_route = cur_route.getEndLocation();
                    String end_address = cur_route.getEndAddress();
                    EventRoute tmp_eventRout = new EventRoute(Routes,new Duration(time_value),new Distance(distance),
                            startAddress, start_route,end_address, end_route,listAddress);
                    routeReturn.add(tmp_eventRout);
                    Routes = new ArrayList<>();
                    distance = 0;
                    time_value = 0;
                    start = true;
                }

            }
        }
        listener.onEventRouterFinderFinish(routeReturn);
        return routeReturn;
    }

}
