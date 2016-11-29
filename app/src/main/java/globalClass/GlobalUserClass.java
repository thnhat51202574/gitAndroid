package globalClass;

import android.app.Application;

import Model.User;

/**
 * Created by 51202_000 on 01/11/2016.
 */

public class GlobalUserClass extends Application {
    private User cur_user;
    private String _id;
    private String username;
    private String password;

    public User getCur_user() {
        return cur_user;
    }

    public void setCur_user(User cur_user) {

        this.cur_user = cur_user;
    }
    public String get_id() {
        return _id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    public void setUsername(String userName) {
        this.username = userName;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void set_id(String id) {
        this._id = id;
    }
}
