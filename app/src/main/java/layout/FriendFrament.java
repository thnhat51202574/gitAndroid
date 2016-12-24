package layout;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.a51202_000.testbug.AddFriendActivity;
import com.example.a51202_000.testbug.FriendcustomListView;
import com.example.a51202_000.testbug.R;


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
import AsyncTask.unFriendTask;

import Model.Event;
import Model.User;
import globalClass.GlobalUserClass;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendFrament.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendFrament#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendFrament extends Fragment {
    private static final int ADDFRIENDCODE = 1307;
    private static final String UNFRIEND_URL = "http://totnghiep.herokuapp.com/api/user/unfriend";
    private OnFragmentInteractionListener mListener;
    private ListView friends_lv;
    FriendcustomListView adapter;
    GlobalUserClass globalUser;
    ArrayList<User> friends;
    public FriendFrament() {
    }
    public static FriendFrament newInstance(String param1, String param2) {
        FriendFrament fragment = new FriendFrament();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_friend_frament, container, false);
        friends_lv = (ListView) rootView.findViewById(R.id.list_frient);
        globalUser = (GlobalUserClass) getActivity().getApplicationContext();
        friends = new ArrayList<>();
        ArrayList<User> listfriend = globalUser.getCur_user().getFriends_list();
        if((listfriend.size())>0) {
             adapter = new FriendcustomListView(
                    getActivity().getApplicationContext(), R.layout.friend_layout,listfriend);
            friends_lv.setAdapter(adapter);
        } else {
            new ReadFriendJSON().execute("http://totnghiep.herokuapp.com/api/user/"+globalUser.getCur_user().get_id());
        }

        return rootView;
    }

    public void AddFriend() {
        Intent intent_add_event = new Intent(getActivity(), AddFriendActivity.class);
        startActivityForResult(intent_add_event,ADDFRIENDCODE);
//        intent_add_event.putExtra("userid",globalUser.get_id());
//        startActivityForResult(intent_add_event, ADDEVENTCODE);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode, data );

        if(resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == ADDFRIENDCODE) {
            final Bundle extras = data.getExtras();
            if (extras != null) {
                friends = new ArrayList<>();
                new ReadFriendJSON().execute("http://totnghiep.herokuapp.com/api/user/"+globalUser.getCur_user().get_id());
            }
        }

    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
            friends = new ArrayList<>();
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
                globalUser.getCur_user().setFriends_list(friends);
            }
            catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

             adapter = new FriendcustomListView(
                    getActivity().getApplicationContext(), R.layout.friend_layout,friends);
            friends_lv.setAdapter(adapter);
            adapter.setonUnfriendClickListener(new FriendcustomListView.OnUnfriendClickListener() {
                @Override
                public void onClick(final User user, final int Position) {
                    String userNamedisplay = user.getFullName();
                    if(userNamedisplay.isEmpty()) userNamedisplay = user.getName();
                        new AlertDialog.Builder(getActivity())
                                .setMessage("Bạn muốn huỷ bạn bè với" + userNamedisplay)
                                .setCancelable(false)
                                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //unfriend
                                        unFriendTask unFriendTask_ = new unFriendTask(globalUser.getCur_user().get_id(),user.get_id());
                                        unFriendTask_.execute(UNFRIEND_URL);
                                        unFriendTask_.setOnUnfriendListener(new unFriendTask.OnUnfriendListener() {
                                            @Override
                                            public void unfriendSuccess() {
                                                //refresh list friend
                                                friends = new ArrayList<>();
                                                new ReadFriendJSON().execute("http://totnghiep.herokuapp.com/api/user/"+globalUser.getCur_user().get_id());
                                            }

                                            @Override
                                            public void unfriendFail() {
                                                Log.d("USEID","unfriend Fail");
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton("Không", null)
                                .show();
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
