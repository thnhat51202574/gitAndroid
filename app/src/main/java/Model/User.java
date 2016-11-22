package Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by 51202_000 on 01/11/2016.
 */

public class User {
    private String _id, name,fullName,address,phone,avatarLink;
    private Date birthday;
    private ArrayList<String> friends_id;

    public User(String _id, String name) throws ParseException{
        this._id = _id;
        this.name = name;
        this.fullName = "";
        this.address= "";
        this.phone ="";
        String birthday_ = "1970-01-01T00:00:00Z";
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Saigon"));
        this.birthday = format.parse(birthday_);
        this.friends_id = new ArrayList<>();
    }

    public User(String _id, String name, String fullName,String address,String phone, String birthday) throws ParseException{
        this._id = _id;
        this.name = name;
        this.fullName = fullName;
        this.address = address;
        this.phone = phone;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Saigon"));
        this.birthday = format.parse(birthday);
        this.friends_id = new ArrayList<>();
    }

    public User(String _id, String name, String fullName,String address, String phone, Date birthday, ArrayList<String> friend) {
        this._id = _id;
        this.name = name;
        this.fullName = fullName;
        this.phone = phone;
        this.birthday = birthday;
        this.address = address;
        this.friends_id = friend;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public User(JSONObject object) throws JSONException, ParseException {
        if((object.has("_id")) && (!object.isNull("_id"))){
            this._id = object.getString("_id");
        } else {this._id ="";}

        if((object.has("username")) && (!object.isNull("username"))){
            this.name = object.getString("username");
        } else {this.name ="";}

        if((object.has("fullName")) && (!object.isNull("fullName"))){
            this.fullName = object.getString("fullName");
        } else {this.fullName ="";}

        if((object.has("address")) && (!object.isNull("address"))){
            this.address = object.getString("address");
        } else {this.address ="";}

        if((object.has("phone")) && (!object.isNull("phone"))){
            this.phone = object.getString("phone");
        } else {this.phone ="";}

        if((object.has("avatarLink")) && (!object.isNull("avatarLink"))){
            this.avatarLink = object.getString("avatarLink");
        } else {this.avatarLink ="";}

        if((object.has("birthday")) && (!object.isNull("birthday"))){

            String birthday_ = object.getString("birthday");
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            format.setTimeZone(TimeZone.getTimeZone("Asia/Saigon"));
            this.birthday = format.parse(birthday_);
        } else {
            String birthday_ = "1970-01-01T00:00:00Z";
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            format.setTimeZone(TimeZone.getTimeZone("Asia/Saigon"));
            this.birthday = format.parse(birthday_);
        }

        if((object.has("friends")) && (!object.isNull("friends"))){
            JSONArray arrayFriend = object.getJSONArray("friends");
            String newFriend_id = "";
            for (int i = 0; i < arrayFriend.length(); i++) {
                newFriend_id = arrayFriend.getString(i);
                friends_id.add(newFriend_id);
            }
        } else {this.friends_id = new ArrayList<>();}

    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setFriend(ArrayList<String> friends) {
        this.friends_id = friends;
    }

    public String get_id() {

        return _id;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone(){
        return phone;
    }

    public String getAddress(){
        return address;
    }

    public Date getBirthday() {
        return birthday;
    }

    public ArrayList<String> getFriend() {
        return friends_id;
    }
}
