package Modules;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import Model.Address;

/**
 * Created by 51202_000 on 12/11/2016.
 */

public class DirectionFinder {
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyCRtFrFmd_KSJuTfi6jc1121xzaIaNmolY";
    private DirectionFinderListener listener;
    private String origin;
    private String destination;
    private ArrayList<Address> arListAddress;

    public DirectionFinder(DirectionFinderListener listener, String origin, String destination,ArrayList<Address> marListAddress) {
        this.listener = listener;
        this.origin = origin;
        this.destination = destination;
        this.arListAddress = marListAddress;
    }

    public void execute() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();
        new DownloadRawData().execute(createUrl());
    }

    private String createUrl() throws UnsupportedEncodingException {
        String urlOrigin = URLEncoder.encode(origin, "utf-8");
        String urlDestination = URLEncoder.encode(destination, "utf-8");
        String urlwaypoints = "";
        if(this.arListAddress.size() > 0) {
            for (int i = 0 ; i < this.arListAddress.size(); i++) {
                Address cur_address = this.arListAddress.get(i);
                String locs = String.valueOf(cur_address.getLocs().latitude) + "," +String.valueOf(cur_address.getLocs().longitude);
                urlwaypoints += "|" + locs;
            }
            urlwaypoints = "&waypoints=optimize:true" + URLEncoder.encode(urlwaypoints, "utf-8");
        }
        return DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + urlwaypoints +"&key=" + GOOGLE_API_KEY;
    }

    private class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            try {
                parseJSon(res);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseJSon(String data) throws JSONException, UnsupportedEncodingException {
        if (data == null)
            return;

        List<Route> routes = new ArrayList<Route>();
        List<Route> arRoutes = new ArrayList<Route>();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");
        String points = "";
        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            Route route = new Route();

            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");

            JSONObject jsonDistance = new JSONObject();
            JSONObject jsonDuration = new JSONObject();
            JSONObject jsonEndLocation = new JSONObject();
            JSONObject jsonStartLocation = new JSONObject();
            int distance_value, duration_value;
            String distance_text, duration_text;
            distance_value = duration_value = 0;
            distance_text = duration_text = "";

            for (int legid = 0; legid < jsonLegs.length() ; legid++) {
                JSONObject jsonLeg = jsonLegs.getJSONObject(legid);
                jsonDistance = jsonLeg.getJSONObject("distance");
                jsonDuration = jsonLeg.getJSONObject("duration");
                distance_value += jsonDistance.getInt("value");
                duration_value += jsonDuration.getInt("value");
                if(legid == 0) {
                    jsonStartLocation = jsonLeg.getJSONObject("start_location");
                    route.startAddress = jsonLeg.getString("start_address");
                    route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
                }
                if(legid == (jsonLegs.length() - 1)) {
                    jsonEndLocation = jsonLeg.getJSONObject("end_location");
                    route.endAddress = jsonLeg.getString("end_address");
                    route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
                }
            }

            distance_text = String.format("%.1f", (float) distance_value/1000) + " km";
            duration_text = String.valueOf((float) duration_value/1000) + " phút";

            route.distance = new Distance(distance_text, distance_value);
            route.duration = new Duration(duration_text, duration_value);
            route.points = decodePolyLine(overview_polylineJson.getString("points"));
            route.arArrayAddress = this.arListAddress;
            routes.add(route);

            for (int legid = 0; legid < jsonLegs.length() ; legid++) {
                JSONArray stepObj = jsonLegs.getJSONObject(legid).getJSONArray("steps");
                for (int idx = 0; idx < stepObj.length(); idx++) {
                    Route route_ = new Route();
                    JSONObject jsoneachStep = stepObj.getJSONObject(idx);
                    JSONObject each_polylineJson = jsoneachStep.getJSONObject("polyline");
                    JSONObject jsoneachDistance = jsoneachStep.getJSONObject("distance");
                    JSONObject jsoneachDuration = jsoneachStep.getJSONObject("duration");
                    JSONObject jsoneachEndLocation = jsoneachStep.getJSONObject("end_location");
                    JSONObject jsoneachStartLocation = jsoneachStep.getJSONObject("start_location");

                    route_.distance = new Distance(jsoneachDistance.getString("text"), jsoneachDistance.getInt("value"));
                    route_.duration = new Duration(jsoneachDuration.getString("text"), jsoneachDuration.getInt("value"));
                    route_.endAddress = "";
                    route_.startAddress = "";
                    route_.startLocation = new LatLng(jsoneachStartLocation.getDouble("lat"), jsoneachStartLocation.getDouble("lng"));
                    route_.endLocation = new LatLng(jsoneachEndLocation.getDouble("lat"), jsoneachEndLocation.getDouble("lng"));
                    route_.points = decodePolyLine(each_polylineJson.getString("points"));
                    arRoutes.add(route_);
                }
            }

            points = overview_polylineJson.getString("points");

        }
        listener.onDirectionFinderSuccess(routes,arRoutes,points);
    }

    static public List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }
}