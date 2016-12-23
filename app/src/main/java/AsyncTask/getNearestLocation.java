
package AsyncTask;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Model.Address;
import Modules.Route;


/**
 * Created by 51202_000 on 11/12/2016.
 */

public class getNearestLocation extends AsyncTask<String, Void, String> {

    LatLng mCurrentLocation;
    private OnArrountCompleted mlistener;

    public interface OnArrountCompleted {
        void getListAddress(ArrayList<Address> result);
    }
    public getNearestLocation(LatLng location, OnArrountCompleted listener) {
        this.mCurrentLocation = location;
        this.mlistener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return getNearestLocation(params[0],this.mCurrentLocation);
        } catch (IOException ex) {
            return "Network error!";
        }
    }

    @Override
    protected void onPostExecute(String s) {

        ArrayList<Address> arResult = new ArrayList<>();
        super.onPostExecute(s);
        try {
            //json array
            JSONObject object = new JSONObject(s);
            JSONArray arAddressJson = object.getJSONArray("addresses");
            for (int i = 0; i < arAddressJson.length(); i++) {
                JSONArray lisAddressperRoute = arAddressJson.getJSONArray(i);
                for (int idx = 0; idx < lisAddressperRoute.length(); idx++) {
                    Address address_tmp = new Address((JSONObject) lisAddressperRoute.get(idx));
                    arResult.add(address_tmp);
                }
            }
            mlistener.getListAddress(arResult);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getNearestLocation(String urlpath,LatLng mCurrentLocation) throws IOException{
        StringBuilder result = new StringBuilder();
        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;
        try {
            JSONArray arrayLgnLat = new JSONArray();

            JSONObject lgnlatObj = new JSONObject();
            lgnlatObj.put("lgn",mCurrentLocation.longitude);
            lgnlatObj.put("lat",mCurrentLocation.latitude);
            arrayLgnLat.put(lgnlatObj);

            String distance = "0.75";

            JSONObject dataInsert = new JSONObject();
            //create JSONdata to send to server
            dataInsert.put("arrayLongLat",arrayLgnLat);
            dataInsert.put("distance",distance);
            //txtLoginView.setText(urlPath);

            URL url = new URL(urlpath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type","application/json"); //set header
            urlConnection.connect();

            //write data to server
            OutputStream outputStream = urlConnection.getOutputStream();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(dataInsert.toString());
            bufferedWriter.flush();


            InputStream inputStream = urlConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line).append("\n");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(bufferedReader != null)
                bufferedReader.close();
        }
        return result.toString();
    }
}
