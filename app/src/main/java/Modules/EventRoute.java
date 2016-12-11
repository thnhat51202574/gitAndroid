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
    public ArrayList<Address> AddressAroundLastLocation;

    public ArrayList<Route> getListRouteInEvent() {
        return ListRouteInEvent;
    }

    public Duration getDuration() {
        return duration;
    }

    public Distance getDistance() {
        return distance;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public LatLng getEndLocation() {
        return endLocation;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public LatLng getStartLocation() {
        return startLocation;
    }

    public  ArrayList<Address> getAddressAroundLastLocation() {
        return AddressAroundLastLocation;
    }

    public EventRoute(ArrayList<Route> listRouteInEvent, Duration duration, Distance distance,
                       String startAddress, LatLng startLocation,String endAddress, LatLng endLocation,ArrayList<Address> AddressAroundLastLocation_ ) {
        this.ListRouteInEvent = listRouteInEvent;
        this.duration = duration;
        this.distance = distance;
        this.endAddress = endAddress;
        this.endLocation = endLocation;
        this.startAddress = startAddress;
        this.AddressAroundLastLocation = AddressAroundLastLocation_;
        this.startLocation = startLocation;
    }

}
