package layout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.a51202_000.testbug.EditprofileAcitivity;
import com.example.a51202_000.testbug.EventcustomListview;
import com.example.a51202_000.testbug.ImageAddapter;
import com.example.a51202_000.testbug.MainTabActivity;
import com.example.a51202_000.testbug.MapsActivity;
import com.example.a51202_000.testbug.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
import globalClass.GlobalUserClass;

public class EventFragment extends Fragment{
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
    ArrayList<Event> events;
    GlobalUserClass globalUser;
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
        globalUser = (GlobalUserClass) getActivity().getApplicationContext();
        lv_event = (ListView) rootView.findViewById(R.id.list_event);
        events = new ArrayList<>();
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

        new ReadEventJSON().execute("http://totnghiep.herokuapp.com/api/event/createdby/");
        return rootView;
    }

    public void showDialog(ArrayList<Event> Events,int position){
        final Event view_event = Events.get(position);
        final String event_id = view_event.get_id();
        View view = getActivity().getLayoutInflater().inflate (R.layout.bottom_event_dialog, null);

        final Dialog mDetailEventDialog = new Dialog(getActivity(),R.style.MaterialDialogSheet);
        mDetailEventDialog.setContentView (view);
        mDetailEventDialog.setCancelable (true);
        mDetailEventDialog.getWindow ().setLayout (LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mDetailEventDialog.getWindow ().setGravity (Gravity.BOTTOM);
        mDetailEventDialog.show();

        TextView txtEventName =(TextView) view.findViewById(R.id.txtEventName);
        TextView txtDateStart = (TextView) view.findViewById(R.id.txtDateStart);
        TextView txtDateEnd = (TextView) view.findViewById(R.id.txtDateEnd);
        TextView txtAddressStart = (TextView) view.findViewById(R.id.txtLocationStart);
        TextView txtAddressEnd = (TextView) view.findViewById(R.id.txtLocationEnd);
        TextView txtDescription = (TextView) view.findViewById(R.id.txtDescription);
        GridView gvlistMember = (GridView) view.findViewById(R.id.gvlistMember);
        Button startbtn = (Button) view.findViewById(R.id.START);
        Button cancelbtn = (Button) view.findViewById(R.id.CANCEL);

        txtEventName.setText(view_event.getEvent_name());
        txtDateStart.setText(view_event.getFullDateTimeStart());
        txtDateEnd.setText(view_event.getFullDateTimeEnd());
        txtAddressStart.setText(view_event.getStartAddress());
        txtAddressEnd.setText(view_event.getEndAddress());
        txtDescription.setText(view_event.getEvent_description());
        ArrayList<User> ListMember = view_event.getList_member();
        ArrayList<String> ArListMemAvatar = new ArrayList<>();
        for (int i = 0;i <ListMember.size(); i++) {
            ArListMemAvatar.add(ListMember.get(i).getAvatarLink());
        }
        ImageAddapter adapter_listmember
                = new ImageAddapter(getActivity().getApplicationContext(), R.layout.single_image_gridview,ArListMemAvatar);
        gvlistMember.setAdapter(adapter_listmember);
        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent WeGoIntent = new Intent(getActivity(), MapsActivity.class);
                WeGoIntent.putExtra("event_id", event_id);
                WeGoIntent.putExtra("currentUser_id",globalUser.getCur_user().get_id());
                startActivity(WeGoIntent);
            }
        });
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDetailEventDialog.dismiss();
            }
        });
    }

    private void refreshListEvent() {
        events = new ArrayList<>();
        new ReadEventJSON().execute("http://totnghiep.herokuapp.com/api/event/createdby/");
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
            super.onPostExecute(s);
            try {
                //json array
                JSONObject object = new JSONObject(s);
                JSONArray arEventJson = object.getJSONArray("events");
                JSONArray arEventMember = object.getJSONArray("member");
                for (int i = 0; i < arEventJson.length(); i++) {
                    JSONObject eventObject = (JSONObject) arEventJson.getJSONObject(i);
                    events.add(new Event(eventObject,true));
                }

                for (int i = 0; i < arEventMember.length(); i++) {
                    JSONObject eventObject = (JSONObject) arEventMember.getJSONObject(i);
                    events.add(new Event(eventObject,false));
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
            adapter.setOnEachItemChangeListener(new EventcustomListview.OnDataChangeListener() {
                @Override
                public void onClick(int Position) {
                    showDialog(events,Position);
                }
            });
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }


        private String getListEventString(String urlpath) throws IOException{
            StringBuilder result = new StringBuilder();
            BufferedReader bufferedReader = null;
            try {
                String path = globalUser.getCur_user().get_id();
                URL url = new URL(urlpath + path);
//                URL url = new URL("http://totnghiep.herokuapp.com/api/event/");
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
