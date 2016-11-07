package Model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by 51202_000 on 01/11/2016.
 */

public class Event {
    private String _id;
    private String event_name;
    private User event_owner;
    private ArrayList<String> event_member_id;
    private Date event_startTime;
    private Date event_endTime;
    private String event_description;

    public Event(String _id,User event_owner,String event_name, String event_startTime,
                 String event_endTime, String event_description) throws ParseException {
        this.event_name = event_name;
        this._id = _id;
        this.event_owner = event_owner;
        this.event_member_id = new ArrayList<>();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Saigon"));
        this.event_startTime = format.parse(event_startTime);
        this.event_endTime = format.parse(event_endTime);
        this.event_description = event_description;
    }

    public String get_id() {
        return _id;
    }

    public Event(String _id, String event_name, User event_owner, ArrayList<String> event_member,
                 String event_startTime, String event_endTime, String event_description) throws ParseException{
        this._id = _id;
        this.event_name = event_name;
        this.event_owner = event_owner;
        this.event_member_id = event_member;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Saigon"));
        this.event_startTime = format.parse(event_startTime);
        this.event_endTime = format.parse(event_endTime);
        this.event_description = event_description;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public User getEvent_owner() {
        return event_owner;
    }

    public void setEvent_owner(User event_owner) {
        this.event_owner = event_owner;
    }

    public ArrayList<String> getEvent_member() {
        return this.event_member_id;
    }

    public void setEvent_member(ArrayList<String> event_member) {
        this.event_member_id = event_member;
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
        return this.event_member_id.size()+1;
    }
}
