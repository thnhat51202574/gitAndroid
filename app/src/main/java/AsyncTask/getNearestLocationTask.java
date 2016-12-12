package AsyncTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.a51202_000.testbug.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.net.URL;
import Model.Address;

import static android.content.ContentValues.TAG;
import static android.provider.ContactsContract.CommonDataKinds.Website.URL;


/**
 * Created by 51202_000 on 26/10/2016.
 */

public class getNearestLocationTask extends AsyncTask<String, Void, String> {

    Activity ActivityContext;
    GoogleMap googleMap;
    Location  currentLocation;
    ProgressDialog progressDialog;
    private OnTaskCompleted listener;

    public interface OnTaskCompleted {
        void getListAddress(HashMap<Marker,Address> result);
    }
    public getNearestLocationTask(FragmentActivity activity, GoogleMap mMap, Location location,OnTaskCompleted fragmentContext) {
        this.ActivityContext = activity;
        this.googleMap = mMap;
        this.currentLocation = location;
        this.listener = fragmentContext;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(this.ActivityContext);
        progressDialog.setMessage("Loading data....");
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params){
        try {
            return getNearestLocation(params[0],this.currentLocation);
        } catch (IOException ex) {
            return "Network error!";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        List<Address> address = new ArrayList<Address>();
        HashMap<Marker,Address> result = new HashMap<Marker, Address>();
        super.onPostExecute(s);
        try {
            //json array
            JSONObject object = new JSONObject(s);
            JSONArray arAddressJson = object.getJSONArray("addresses");
            for (int i = 0; i < arAddressJson.length(); i++) {
                Address address_tmp = new Address((JSONObject) arAddressJson.get(i));
                address.add(address_tmp);
//                  draw to map
                LatLng mylocation = address_tmp.getLocs();
                MarkerOptions marker = new MarkerOptions().position(mylocation).title(address_tmp.getName());
                marker.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("restaurant",50,50)));

                Marker _marker = googleMap.addMarker(marker);
                result.put(_marker,address_tmp);
            }
            listener.getListAddress(result);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

    }

    private String getNearestLocation(String urlpath,Location currentLocation) throws IOException{
        StringBuilder result = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            String latitude = String.valueOf(currentLocation.getLatitude());
            String longtitude = String.valueOf(currentLocation.getLongitude());
            String distance = "1";
            urlpath +="?lon="+longtitude+"&lat="+latitude+"&distance=" + distance;
            URL url = new URL(urlpath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type","application/json"); //set header
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line).append("\n");
            }
        } finally {
            if(bufferedReader != null)
                bufferedReader.close();
        }
        return result.toString();
    }
    public Bitmap resizeMapIcons(String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(this.ActivityContext.getResources(),this.ActivityContext.getResources().getIdentifier(iconName, "drawable", this.ActivityContext.getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }
}
