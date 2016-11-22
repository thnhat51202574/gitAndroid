package com.example.a51202_000.testbug;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;

import Model.User;
import globalClass.GlobalUserClass;

public class AddMemberEventActivity extends AppCompatActivity {
    private ListView list_frient_search;
    private EditText searchEdittext;
    GlobalUserClass globalUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member_event);
        list_frient_search = (ListView) findViewById(R.id.list_frient_search);
        searchEdittext = (EditText) findViewById(R.id.searchNameInput);
        globalUser = (GlobalUserClass) getApplicationContext();
        ArrayList<User> listfriend = globalUser.getCur_user().getFriends_list();
        if((listfriend.size())>0) {
            MemberEventCustomListview adapter
                    = new MemberEventCustomListview(getApplicationContext(), R.layout.friend_in_event_add,listfriend);
            list_frient_search.setAdapter(adapter);
        } else {
            new ReadFriendJSON().execute("http://totnghiep.herokuapp.com/api/user/"+globalUser.getCur_user().get_id());
        }
    }
    class ReadFriendJSON extends AsyncTask<String, Integer,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return getListEventString(params[0]);
            } catch (IOException ex) {
                return "Network error!";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            ArrayList<User> friends = new ArrayList<>();
            super.onPostExecute(s);
            try {
                //json array
                JSONObject object = new JSONObject(s);
                JSONObject userObject_ = object.getJSONObject("users");
                JSONArray arFriend = userObject_.getJSONArray("friends");
                for (int i = 0; i < arFriend.length(); i++) {
                    JSONObject userObject = arFriend.getJSONObject(i);
                    User user = new User(userObject);
                    friends.add(user);
                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            MemberEventCustomListview adapter
                    = new MemberEventCustomListview(getApplicationContext(), R.layout.friend_in_event_add,friends);
            list_frient_search.setAdapter(adapter);
        }


        private String getListEventString(String urlpath) throws IOException{
            StringBuilder result = new StringBuilder();
            BufferedReader bufferedReader = null;
            try {
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
    }
}
