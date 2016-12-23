package Model;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 51202_000 on 26/10/2016.
 */

public class Address {
    private String _id;
    private String name,address,phonenumber,detail,website;
    private Double rate,latitue,longtitue;
    private int type;
    private ArrayList<String> arImage;
    private String AddressStringObj;
    public Address() {
        this._id = "";
        this.name = this.address = this.phonenumber = this.detail = this.website = "";
        this.rate = this.latitue = this.longtitue = 0.0;
        this.type = 0;
        this.arImage = new ArrayList<String>();
    }

    public String getAddressStringObj() {
        return AddressStringObj;
    }

    public Address(JSONObject object) throws JSONException {
        if((object.has("_id")) && (!object.isNull("_id"))){
            this._id = object.getString("_id");
        } else {this._id ="";}
        if((object.has("name")) && (!object.isNull("name"))) {
            this.name = object.getString("name");
        } else this.name ="";
        if((object.has("address")) && (!object.isNull("address"))) {
            this.address = object.getString("address");
        } else this.address ="";
        if((object.has("locs")) && (!object.isNull("locs"))){
            JSONArray locs = object.getJSONArray("locs");
            this.longtitue =  locs.getDouble(0);
            this.latitue = locs.getDouble(1);
        }
        if((object.has("rate")) && (!object.isNull("rate"))) {
            this.rate = object.getDouble("rate");
        }  else this.rate =0.0;
        if((object.has("type")) && (!object.isNull("type"))) {
            this.type = object.getInt("type");
        }  else this.type =0;

        if((object.has("description")) && (!object.isNull("description"))) {
            this.detail = object.getString("description");
        }  else this.detail = "";

        if((object.has("phonenumber")) && (!object.isNull("phonenumber"))) {
            this.detail = object.getString("phonenumber");
        }  else this.phonenumber = "";

        if((object.has("picture")) && (!object.isNull("picture"))) {
            this.arImage = new ArrayList<String>();
            JSONArray arPicture = object.getJSONArray("picture");
            String pictureUrl = "";
            for (int i = 0; i< arPicture.length(); i++)
            {
                pictureUrl = arPicture.getString(i);
                this.arImage.add(pictureUrl);
            }
        } else this.arImage = new ArrayList<String>();
        this.AddressStringObj = object.toString();


    }
    public LatLng getLocs() {
        return new LatLng(this.latitue,this.longtitue);
    }

    public String getName() {
        return this.name;
    }
    public String getAddress() {
        return this.address;
    }
    public String get_id() {return this._id;}
    public String getPhone() {
        return this.phonenumber;
    }
    public int getType() {
        return this.type;
    }
    public String getdetail() {return this.detail;}
    public String getArImage() {
        return this.arImage.get(0);
    }
    public Double getRate() {
        return this.rate;
    }

}
