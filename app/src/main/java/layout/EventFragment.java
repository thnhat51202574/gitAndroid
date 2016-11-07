package layout;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a51202_000.testbug.EventcustomListview;
import com.example.a51202_000.testbug.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

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

import Model.Event;
import Model.User;

public class EventFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SlidingUpPanelLayout slidingLayout;
    private Button btnShow;
    private Button btnHide;
    private TextView textView;
    private ListView lv_event;

    public EventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventFragment newInstance(String param1, String param2) {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_event, container, false);
        lv_event = (ListView) rootView.findViewById(R.id.list_event);
        new ReadEventJSON().execute("http://192.168.1.114:3000/api/event");
        return rootView;
    }
    public void receiveMess(String Text) {
        textView.setText(Text);
    }

    class ReadEventJSON extends AsyncTask<String, Integer,String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Đang tải dữ liệu....");
            progressDialog.show();
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
            ArrayList<Event> events = new ArrayList<>();
            super.onPostExecute(s);
            try {
                //json array
                JSONObject object = new JSONObject(s);
                JSONArray arEventJson = object.getJSONArray("events");
                for (int i = 0; i < arEventJson.length(); i++) {
                    JSONObject eventObject = (JSONObject) arEventJson.getJSONObject(i);
                    JSONObject userObject = (JSONObject) eventObject.get("created");

                    String User_firstName = "";
                    String User_lastName = "";
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    String User_birthday = df.format(c.getTime());
                    String User_name = "";
                    String User_id = "";
                    if((userObject.has("firstName")) && (!userObject.isNull("firstName"))) {
                        User_firstName = userObject.getString("firstName");
                    }
                    if((userObject.has("firstName")) && (!userObject.isNull("firstName"))) {
                        User_lastName = userObject.getString("firstName");
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
                            User_firstName,
                            User_lastName,
                            User_birthday);
                    JSONArray arMemIDJson = eventObject.getJSONArray("arUser");
                    ArrayList<String> listID = new ArrayList<>();
                    for (int idx = 0; idx < arMemIDJson.length(); idx++) {
                        JSONObject UserIDObject = (JSONObject) arMemIDJson.getJSONObject(idx);
                        listID.add(UserIDObject.getString("_id"));
                    }
                    events.add(new Event(
                            eventObject.getString("_id"),
                            eventObject.getString("eventname"),
                            user,
                            listID,
                            eventObject.getString("starttime"),
                            eventObject.getString("endtime"),
                            eventObject.getString("description")
                    ));
                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            EventcustomListview adapter = new EventcustomListview(
                    getActivity().getApplicationContext(), R.layout.event_layout,events);
            lv_event.setAdapter(adapter);

            if (progressDialog != null) {
                progressDialog.dismiss();
            }
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
