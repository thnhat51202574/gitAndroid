package AsyncTask;


import android.os.AsyncTask;
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

public class getAroundLocation extends AsyncTask<String, Void, String> {

    List<Route> marRoute;
    private OnArrountCompleted mlistener;

    public interface OnArrountCompleted {
        void getListAddress(HashMap<Route,ArrayList<Address>> result);
    }
    public getAroundLocation(List<Route> arRoute, OnArrountCompleted listener) {
        this.marRoute = arRoute;
        this.mlistener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return getNearestLocation(params[0],this.marRoute);
        } catch (IOException ex) {
            return "Network error!";
        }
    }

    @Override
    protected void onPostExecute(String s) {

        HashMap<Route,ArrayList<Address>> arResult = new HashMap<>();
        super.onPostExecute(s);
        try {
            //json array
            JSONObject object = new JSONObject(s);
            JSONArray arAddressJson = object.getJSONArray("addresses");
            for (int i = 0; i < arAddressJson.length(); i++) {
                JSONArray lisAddressperRoute = arAddressJson.getJSONArray(i);
                ArrayList<Address> address = new ArrayList<Address>();

                //get List Address per route
                for (int idx = 0; idx < lisAddressperRoute.length(); idx++) {
                    Address address_tmp = new Address((JSONObject) lisAddressperRoute.get(idx));
                    address.add(address_tmp);
                }
                arResult.put(this.marRoute.get(i),address);
            }
            mlistener.getListAddress(arResult);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getNearestLocation(String urlpath,List<Route> arRoute) throws IOException{
        StringBuilder result = new StringBuilder();
        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;
        try {
            JSONArray arrayLgnLat = new JSONArray();
            for (int i = 0; i < arRoute.size(); i++) {
                Route cur_route = arRoute.get(i);
                JSONObject lgnlatObj = new JSONObject();
                lgnlatObj.put("lgn",cur_route.getEndLocation().longitude);
                lgnlatObj.put("lat",cur_route.getEndLocation().latitude);
                arrayLgnLat.put(lgnlatObj);
            }
            String distance = "1";

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
