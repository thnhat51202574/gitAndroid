package AsyncTask;

import android.app.Activity;
import android.app.ProgressDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * Created by 51202_000 on 26/10/2016.
 */

public class getNearestLocationTask extends AsyncTask<String, Void, String> {

    private double lon,lat;
    Activity ActivityContext;
    GoogleMap googleMap;
    ProgressDialog progressDialog;

    public getNearestLocationTask(FragmentActivity activity, GoogleMap mMap) {
        this.ActivityContext = activity;
        this.googleMap = mMap;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(this.ActivityContext);
        progressDialog.setMessage("Loading data....");
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        return getNearestLocation(params[0], this.lon, this.lat);

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONObject object = new JSONObject(s);
            String test = object.getString("lat");
            Log.d("TAG", test);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        LatLng mylocation =new LatLng(10.761797,106.666894);
        MarkerOptions marker = new MarkerOptions().position(mylocation).title("abd");
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant));
        googleMap.addMarker(marker);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private String getNearestLocation(String param,double latitude, double longitude) {
        return "{\"lat\":\"106.666894\",\"10.761797\":\"WP\"}";
    }
}
