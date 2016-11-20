package Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by 51202_000 on 01/11/2016.
 */

public class User {
    private String _id, name,firstName,lastName,phone,avatarLink;
    private Date birthday;
    private ArrayList<String> friends_id;

    public User(String _id, String name) throws ParseException{
        this._id = _id;
        this.name = name;
        this.firstName = "";
        this.lastName = "";
        String birthday_ = "1970-01-01T00:00:00Z";
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Saigon"));
        this.birthday = format.parse(birthday_);
        this.friends_id = new ArrayList<>();
    }

    public User(String _id, String name, String firstName, String lastName, String birthday) throws ParseException{
        this._id = _id;
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Saigon"));
        this.birthday = format.parse(birthday);
        this.friends_id = new ArrayList<>();
    }

    public User(String _id, String name, String firstName, String lastName, Date birthday, ArrayList<String> friend) {
        this._id = _id;
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
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

        if((object.has("lastName")) && (!object.isNull("lastName"))){
            this.lastName = object.getString("lastName");
        } else {this.lastName ="";}

        if((object.has("firstName")) && (!object.isNull("firstName"))){
            this.firstName = object.getString("firstName");
        } else {this.firstName ="";}

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
            for (int i = 0; i < arrayFriend.length(); i++) {
                String friend_id = arrayFriend.getString(i);

                friends_id.add(friend_id);
            }
        } else {this.friends_id = new ArrayList<>();}

    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public ArrayList<String> getFriend() {
        return friends_id;
    }
}
