package layout;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
    private SwipeRefreshLayout swipeContainer;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SlidingUpPanelLayout slidingLayout;
    private Button btnShow;
    private Button btnHide;
    private TextView textView;
    private ListView lv_event;
    ProgressDialog progressDialog;
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
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_event, container, false);

        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);

        lv_event = (ListView) rootView.findViewById(R.id.list_event);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshListEvent();
            }
        });
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //load event
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Đang tải dữ liệu....");
        progressDialog.show();

        new ReadEventJSON().execute("http://totnghiep.herokuapp.com/api/event/");
        return rootView;
    }

    private void refreshListEvent() {
        new ReadEventJSON().execute("http://totnghiep.herokuapp.com/api/event/");
        swipeContainer.setRefreshing(false);
    }


    public void receiveMess(String Text) {
        textView.setText(Text);
    }

    class ReadEventJSON extends AsyncTask<String, Integer,String> {

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
            ArrayList<Event> events = new ArrayList<>();
            super.onPostExecute(s);
            try {
                //json array
                JSONObject object = new JSONObject(s);
                JSONArray arEventJson = object.getJSONArray("events");
                for (int i = 0; i < arEventJson.length(); i++) {
                    JSONObject eventObject = (JSONObject) arEventJson.getJSONObject(i);
                    JSONObject userObject = (JSONObject) eventObject.get("created");

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
