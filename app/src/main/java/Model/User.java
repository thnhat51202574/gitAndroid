package Model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by 51202_000 on 01/11/2016.
 */

public class User {
    private String _id, name,firstName,lastName;
    private Date birthday;
    private ArrayList<User> friend;

    public User(String _id, String name, String firstName, String lastName, Date birthday) {
        this._id = _id;
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.friend = new ArrayList<>();
    }

    public User(String _id, String name, String firstName, String lastName, Date birthday, ArrayList<User> friend) {
        this._id = _id;
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.friend = friend;
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

    public void setFriend(ArrayList<User> friend) {
        this.friend = friend;
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

    public ArrayList<User> getFriend() {
        return friend;
    }
}
