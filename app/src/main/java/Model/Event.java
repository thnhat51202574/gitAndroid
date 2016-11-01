package Model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 51202_000 on 01/11/2016.
 */

public class Event {
    private String _id;
    private String event_name;
    private User event_owner;
    private ArrayList<User> event_member;
    private Date event_startTime;
    private Date event_endTime;
    private String event_description;

    public Event(String _id, String event_name, String event_startTime, String event_endTime, String event_description) throws ParseException {
        this.event_name = event_name;
        this._id = _id;
        this.event_owner = null;
        this.event_member = new ArrayList<>();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.CHINA);
        this.event_startTime = format.parse(event_startTime);
        this.event_endTime = format.parse(event_endTime);
        this.event_description = event_description;
    }

    public Event(String _id, String event_name, User event_owner, ArrayList<User> event_member, Date event_startTime, Date event_endTime, String event_description) {
        this._id = _id;
        this.event_name = event_name;
        this.event_owner = event_owner;
        this.event_member = event_member;
        this.event_startTime = event_startTime;
        this.event_endTime = event_endTime;
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

    public ArrayList<User> getEvent_member() {
        return event_member;
    }

    public void setEvent_member(ArrayList<User> event_member) {
        this.event_member = event_member;
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
        return this.event_member.size()+1;
    }
}
