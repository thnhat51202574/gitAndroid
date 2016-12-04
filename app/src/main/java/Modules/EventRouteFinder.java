package Modules;

import com.google.android.gms.maps.model.LatLng;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 51202_000 on 04/12/2016.
 */

public class EventRouteFinder {
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyCRtFrFmd_KSJuTfi6jc1121xzaIaNmolY";
    private List<Route> arRoute;
    private EventRouterFinderListener listener;
    public EventRouteFinder(EventRouterFinderListener listener, List<Route> arParamsRoute) {
        this.listener = listener;
        this.arRoute = arParamsRoute;
    }

    public void execute() throws UnsupportedEncodingException {
        listener.onEventRouterFinderStart();
        this.getDistanceAfterTime(100);

//        new DirectionFinder.DownloadRawData().execute(createUrl());
    }
    public ArrayList<EventRoute> getDistanceAfterTime(int value) {
        ArrayList<Route> Routes = new ArrayList<>();
        ArrayList<EventRoute> routeReturn = new ArrayList<>();
        int distance = 0;
        int time_value = 0;
        for (int i = 0; i < this.arRoute.size(); i++) {
            Route cur_route = this.arRoute.get(i);
            distance += cur_route.getDistance().getValue();
            time_value += cur_route.getDuration().getValue();
            Routes.add(cur_route);
            if (time_value >= value) {
                LatLng start_route = cur_route.getEndLocation();
                String startAddress = cur_route.getEndAddress();
                LatLng end_route = cur_route.getEndLocation();
                String end_address = cur_route.getEndAddress();
                EventRoute tmp_eventRout = new EventRoute(Routes,new Duration(time_value),new Distance(distance),
                        startAddress, start_route,end_address, end_route);
                routeReturn.add(tmp_eventRout);
                Routes = new ArrayList<>();
                distance = 0;
                time_value = 0;
            }
        }
        listener.onEventRouterFinderFinish(routeReturn);
        return routeReturn;
    }

}
