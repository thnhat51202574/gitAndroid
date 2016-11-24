package com.example.a51202_000.testbug;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.List;

import Model.User;
import globalClass.GlobalUserClass;

public class AddMemberEventActivity extends AppCompatActivity {
    private ListView list_frient_search;
    private EditText searchEdittext;
    private GridView gvAvatar;
    private ArrayList<User> ListmemUser;
    private ArrayList<String> ListmemUser_id;
    GlobalUserClass globalUser;
    ImageAddapter adapter_listmember;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member_event);
        ListmemUser = new ArrayList<>();
        ListmemUser_id = new ArrayList<>();
        list_frient_search = (ListView) findViewById(R.id.list_frient_search);
        searchEdittext = (EditText) findViewById(R.id.searchNameInput);
        gvAvatar = (GridView) findViewById(R.id.listAvatarUser);
        globalUser = (GlobalUserClass) getApplicationContext();
        ArrayList<User> listfriend = globalUser.getCur_user().getFriends_list();
        adapter_listmember
                = new ImageAddapter(getApplicationContext(), R.layout.single_image_gridview,ListmemUser);
        gvAvatar.setAdapter(adapter_listmember);

        if((listfriend.size())>0) {
            MemberEventCustomListview adapter
                    = new MemberEventCustomListview(getApplicationContext(), R.layout.friend_in_event_add,listfriend);
            list_frient_search.setAdapter(adapter);
            adapter.setOnDataChangeListener(new MemberEventCustomListview.OnDataChangeListener(){
                @Override
                public void onUserAdd(User user) {
                    ListmemUser.add(user);
                    ListmemUser_id.add(user.get_id());
                    adapter_listmember.notifyDataSetChanged();
                }
                @Override
                public void onUserRemove(User user) {
                    ListmemUser.remove(user);
                    ListmemUser_id.remove(user.get_id());
                    adapter_listmember.removeUser(user);
                }
            });
        } else {
            new ReadFriendJSON().execute("http://totnghiep.herokuapp.com/api/user/"+globalUser.getCur_user().get_id());
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_member_event, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_ok:
                Intent output  = new Intent();
                output.putStringArrayListExtra("listMember",ListmemUser_id);
                setResult(RESULT_OK,output);
                finish();
                Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
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
            adapter.setOnDataChangeListener(new MemberEventCustomListview.OnDataChangeListener(){
                @Override
                public void onUserAdd(User user) {
                    ListmemUser.add(user);
                    Log.d("onUserAdd", "onUserAdd: "+ user.getAvatarLink());
                    adapter_listmember.addUser(user);
                    adapter_listmember.notifyDataSetChanged();
                }
                @Override
                public void onUserRemove(User user) {
                    ListmemUser.remove(user);
                    adapter_listmember.removeUser(user);
                    adapter_listmember.notifyDataSetChanged();
                    Log.d("onUserRemove", "onUserAdd: "+ user.getFullName());
                }
            });
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
