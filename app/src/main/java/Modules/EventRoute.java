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
    public Distance distance;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;
    public Address AddressAroundLastLocation;

    public EventRoute(ArrayList<Route> listRouteInEvent, Duration duration, Distance distance,
                      String endAddress, LatLng endLocation, String startAddress, LatLng startLocation) {
        this.ListRouteInEvent = listRouteInEvent;
        this.duration = duration;
        this.distance = distance;
        this.endAddress = endAddress;
        this.endLocation = endLocation;
        this.startAddress = startAddress;
        this.startLocation = startLocation;
    }



}
