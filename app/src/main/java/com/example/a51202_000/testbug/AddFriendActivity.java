package com.example.a51202_000.testbug;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;

import Model.User;
import globalClass.GlobalUserClass;

public class AddFriendActivity extends AppCompatActivity {

    MaterialSearchView searchView;
    ListView listPeople;
    GlobalUserClass globalUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        globalUser = (GlobalUserClass) getApplicationContext();
//        initial value
        listPeople = (ListView) findViewById(R.id.list_frient_search);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        new ReadPeopleJSON().execute("http://totnghiep.herokuapp.com/api/listuser");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }
    class ReadPeopleJSON extends AsyncTask<String, Integer,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return getListUserString(params[0]);
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
                JSONArray arFriend = object.getJSONArray("users");
                for (int i = 0; i < arFriend.length(); i++) {
                    JSONObject userObject = arFriend.getJSONObject(i);
                    User user = new User(userObject);
                    friends.add(user);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            AddFriendCustomListview adapter
                    = new AddFriendCustomListview(getApplicationContext(), R.layout.each_friend_search, friends);
            listPeople.setAdapter(adapter);
            adapter.setOnDataChangeListener(new AddFriendCustomListview.OnDataChangeListener() {
                @Override
                public void onUserAdd(User user, CircularProgressButton btnProgress) {
                    new addFriendRequest(user,btnProgress).execute("http://totnghiep.herokuapp.com/api/user/addfriend");
                }

            });
        }

        private String getListUserString(String urlpath) throws IOException {
            StringBuilder result = new StringBuilder();
            BufferedReader bufferedReader = null;
            try {
                String path = urlpath +"?_id=582723e004a985001276defb";
                URL url = new URL(path);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json"); //set header
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line).append("\n");
                }
            } finally {
                if (bufferedReader != null)
                    bufferedReader.close();
            }
            return result.toString();
        }
    }
    private class addFriendRequest extends AsyncTask<String, Void, String> {

        User mfriend;
        CircularProgressButton mbtn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        public addFriendRequest(User userfriend, CircularProgressButton btnProgress) {
            mfriend = userfriend;
            mbtn = btnProgress;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return addFriend(params[0]);
            } catch (IOException ex){
                return "Network Error ";
            } catch (JSONException ex) {
                return "Invalid data!";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject resultObject = new JSONObject(result);
                String  CODE = resultObject.getString("CODE");
                if (CODE.equals("SUCCESS")) {
                    mbtn.setProgress(100);
                } else {
                    mbtn.setProgress(-1);
                }
            } catch (JSONException ex){
                ex.printStackTrace();
            }
        }

        private String addFriend(String urlPath) throws IOException, JSONException {
            StringBuilder reSult = new StringBuilder();
            BufferedWriter bufferedWriter = null;
            BufferedReader bufferedReader = null;

            try {
                JSONObject dataInsert = new JSONObject();
                //create JSONdata to send to server
                dataInsert.put("_id","582723e004a985001276defb");
                dataInsert.put("friend_id",mfriend.get_id());

                //initialize and config request , then connect the server.
                URL url = new URL(urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000); //milliseconds
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoOutput(true); //enable output data
                urlConnection.setRequestProperty("Content-Type","application/json"); //set header
                urlConnection.connect();

                //write data to server
                OutputStream outputStream = urlConnection.getOutputStream();
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                bufferedWriter.write(dataInsert.toString());
                bufferedWriter.flush();

                //read data response from server

                InputStream inputStream = urlConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                if((line = bufferedReader.readLine())!=null) {
                    reSult.append(line);
                }

            } finally {
                if(bufferedWriter !=null) {
                    bufferedWriter.close();
                }
                if(bufferedReader !=null) {
                    bufferedReader.close();
                }
            }

            return reSult.toString();
        }
    }

}
