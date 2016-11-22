package layout;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import Model.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendFrament.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendFrament#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendFrament extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ListView friends_lv;

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
        new ReadFriendJSON().execute("http://totnghiep.herokuapp.com/api/user/57f374a398cf132bd49b6483");
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
            ArrayList<User> friends = new ArrayList<>();
            super.onPostExecute(s);
            try {
                //json array
                JSONObject object = new JSONObject(s);
                JSONObject userObject_ = object.getJSONObject("users");
                JSONArray arFriend = userObject_.getJSONArray("friends");
                for (int i = 0; i < arFriend.length(); i++) {
                    JSONObject userObject = arFriend.getJSONObject(i);

                    String User_fullName = "";
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    String User_birthday = df.format(c.getTime());
                    String User_name = "";
                    String User_id = "";
                    String User_phone = "";
                    String User_address = "";
                    if((userObject.has("fullName")) && (!userObject.isNull("fullName"))) {
                        User_fullName = userObject.getString("fullName");
                    }
                    if((userObject.has("address")) && (!userObject.isNull("address"))) {
                        User_address = userObject.getString("address");
                    }
                    if((userObject.has("phone")) && (!userObject.isNull("phone"))) {
                        User_phone = userObject.getString("phone");
                    }
                    if((userObject.has("birthday")) && (!userObject.isNull("birthday"))) {
                        User_birthday = userObject.getString("birthday");
                    }
                    if((userObject.has("username")) && (!userObject.isNull("username"))) {
                        User_name= userObject.getString("username");
                    }
                    if((userObject.has("_id")) && (!userObject.isNull("_id"))) {
                        User_id= userObject.getString("_id");
                    }
                    User user = new User(
                            User_id,
                            User_name,
                            User_fullName,
                            User_address,
                            User_phone,
                            User_birthday);

                    friends.add(user);
                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            FriendcustomListView adapter = new FriendcustomListView(
                    getActivity().getApplicationContext(), R.layout.friend_layout,friends);
            friends_lv.setAdapter(adapter);

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
