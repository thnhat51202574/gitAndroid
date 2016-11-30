package Modules;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import Model.Address;

/**
 * Created by nhatth2 on 11/30/2016.
 */

public class EventRoute {

    private ArrayList<Route> ListRouteInEvent;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;
    public Address AddressAroundLastLocation;

    public EventRoute(ArrayList<Route> listRouteInEvent) {
        ListRouteInEvent = listRouteInEvent;
    }

    public void setListRouteInEvent(ArrayList<Route> listRouteInEvent) {
        ListRouteInEvent = listRouteInEvent;
    }

    public ArrayList<Route> getListRouteInEvent() {

        return ListRouteInEvent;
    }
    public ArrayList<ArrayList<Route>> getDistanceAfterTime(int value) {
        ArrayList<Route> Routes = new ArrayList<>();
        ArrayList<ArrayList<Route>> routeReturn = new ArrayList<>();
        int  distance = 0;
        int  time_value = 0;
        for (int i = 0;i <= ListRouteInEvent.size();i++) {
            Route cur_route = ListRouteInEvent.get(i);
            distance += cur_route.getDistance().getValue();
            time_value += cur_route.getDuration().getValue();
            Routes.add(cur_route);
            if(time_value >= value) {
                LatLng end_route = cur_route.getEndLocation();
                routeReturn.add(Routes);
                Routes = new ArrayList<>();
            }
        }
        return routeReturn;
    }
}
