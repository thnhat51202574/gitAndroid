package Model;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import Modules.DirectionFinder;

/**
 * Created by 51202_000 on 01/11/2016.
 */

public class Event {
    private String _id;
    private String event_name,startAddress,endAddress;
    private LatLng startLatlng;
    private LatLng endLatLng;
    private User event_owner;
    private ArrayList<User> list_member;
    private ArrayList<Address> list_address;
    private Date event_startTime;
    private Date event_endTime;
    private String event_description;
    private String StringJson;
    private List<LatLng> arLocs;
    private boolean isowner;

    public List<LatLng> getArLocs() {
        return arLocs;
    }

    public Event(JSONObject object, String stringEvent, boolean owner) throws JSONException, ParseException {
        if((object.has("_id")) && (!object.isNull("_id"))){
            this._id = object.getString("_id");
        } else {this._id ="";}

        if((object.has("eventname")) && (!object.isNull("eventname"))){
            this.event_name = object.getString("eventname");
        } else {this.event_name ="";}

        if((object.has("description")) && (!object.isNull("description"))){
            this.event_description = object.getString("description");
        } else {this.event_description ="";}

        if((object.has("startAddress")) && (!object.isNull("startAddress"))){
            this.startAddress = object.getString("startAddress");
        } else {this.startAddress ="";}

        if((object.has("endAddress")) && (!object.isNull("endAddress"))){
            this.endAddress = object.getString("endAddress");
        } else {this.endAddress ="";}

        if((object.has("created")) && (!object.isNull("created"))){
            JSONObject created_object = object.getJSONObject("created");
            event_owner = new User(created_object);
        } else {event_owner = null;}

        if((object.has("arUser")) && (!object.isNull("arUser"))){
            JSONArray arUser = object.getJSONArray("arUser");
            list_member = new ArrayList<>();
            for (int i = 0; i<arUser.length(); i++) {
                JSONObject member = arUser.getJSONObject(i);
                User member_user = new User(member);
                list_member.add(member_user);
            }
        } else {list_member = new ArrayList<>();}

        if((object.has("startLocs")) && (!object.isNull("startLocs"))){
            JSONArray start_Locs = object.getJSONArray("startLocs");
            startLatlng = new LatLng(start_Locs.getDouble(1),start_Locs.getDouble(0));
        } else {startLatlng =null;}

        if((object.has("endLocs")) && (!object.isNull("endLocs"))){
            JSONArray end_locs = object.getJSONArray("endLocs");
            endLatLng = new LatLng(end_locs.getDouble(1),end_locs.getDouble(0));
        } else {endLatLng =null;}

        if((object.has("starttime")) && (!object.isNull("starttime"))){
            String starttime_ = object.getString("starttime");
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            format.setTimeZone(TimeZone.getTimeZone("Asia/Saigon"));
            this.event_startTime = format.parse(starttime_);
        } else {
            String birthday_ = "1970-01-01T00:00:00Z";
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            format.setTimeZone(TimeZone.getTimeZone("Asia/Saigon"));
            this.event_startTime = format.parse(birthday_);
        }

        if((object.has("endtime")) && (!object.isNull("endtime"))){
            String starttime_ = object.getString("endtime");
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            format.setTimeZone(TimeZone.getTimeZone("Asia/Saigon"));
            this.event_endTime = format.parse(starttime_);
        } else {
            String birthday_ = "1970-01-01T00:00:00Z";
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            format.setTimeZone(TimeZone.getTimeZone("Asia/Saigon"));
            this.event_endTime = format.parse(birthday_);
        }
        if((object.has("arAddress")) && (!object.isNull("arAddress"))){
            JSONArray arAddress = object.getJSONArray("arAddress");
            list_address = new ArrayList<>();
            for (int i = 0; i<arAddress.length(); i++) {
                JSONObject addressObj = arAddress.getJSONObject(i);
                Address each_address = new Address(addressObj);
                list_address.add(each_address);
            }
        } else {list_address = new ArrayList<>();}

        if((object.has("points")) && (!object.isNull("points"))){
            String stringPoints = object.getString("points");
            arLocs = DirectionFinder.decodePolyLine(stringPoints);
        } else {arLocs = new ArrayList<>();}


        this.StringJson = stringEvent;
        this.isowner = owner;
    }

    public boolean isowner() {
        return isowner;
    }
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public ArrayList<Address> getList_address() {
        return list_address;
    }

    public String getStringJson() {
        return StringJson;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public LatLng getStartLatlng() {
        return startLatlng;
    }

    public void setStartLatlng(LatLng startLatlng) {
        this.startLatlng = startLatlng;
    }

    public LatLng getEndLatLng() {
        return endLatLng;
    }

    public void setEndLatLng(LatLng endLatLng) {
        this.endLatLng = endLatLng;
    }

    public User getEvent_owner() {
        return event_owner;
    }

    public void setEvent_owner(User event_owner) {
        this.event_owner = event_owner;
    }

    public ArrayList<User> getList_member() {
        return list_member;
    }

    public void setList_member(ArrayList<User> list_member) {
        this.list_member = list_member;
    }

    public Date getEvent_startTime() {
        return event_startTime;
    }

    public void setEvent_startTime(Date event_startTime) {
        this.event_startTime = event_startTime;
    }

    public Date getEvent_endTime() {
        return event_endTime;
    }

    public void setEvent_endTime(Date event_endTime) {
        this.event_endTime = event_endTime;
    }

    public String getEvent_description() {
        return event_description;
    }

    public void setEvent_description(String event_description) {
        this.event_description = event_description;
    }

    public int getCountUser() {
        return this.list_member.size()+1;
    }

    public String getFullDateTimeStart() {
        return android.text.format.DateFormat.format("dd/MM/yyyy HH:mm", this.event_startTime).toString();
    }

    public String getFullDateTimeEnd() {
        return android.text.format.DateFormat.format("dd/MM/yyyy HH:mm", this.event_endTime).toString();
    }
}
