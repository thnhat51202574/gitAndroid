package com.example.a51202_000.testbug;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.wdullaer.materialdatetimepicker.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

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
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import Model.User;
import globalClass.GlobalUserClass;
import layout.FriendFrament;
import layout.HomeFragment;
import layout.RouteMapEventFragment;

public class AddEventActivity extends AppCompatActivity implements RouteMapEventFragment.RooteComunicate {

    private int PLACE_PICKER_REQUEST = 101;
    private int PLACE_DESTINATION_PICKER_REQUEST = 102;
    private int ADD_MEMBER_REQUEST = 103;
    private DatePicker datePicker;
    private Calendar calendar;
    private ImageView mapViewDemo;
    GlobalUserClass globalUser;
    private EditText DateStart, TimeStart, DateReturn, TimeReturn;
    private EditText StartAddress, EndAddress, nameEvent,EventDescription;
    private String from_Date, to_Date, from_Time, to_Time;
    private int year, month, day;
    private LatLng startaddress_LatLgn,destination_LatLgn;
    private GridView gvlistMember;
    private ArrayList<User> ArrayMember;
    private ArrayList<String> ArListMemAvatar,arListIdMember;
    private JSONArray JSONListMember,JSONStartLocs,JSONEndLocs;
    ProgressDialog progressDialog;
    DatePickerDialog.OnDateSetListener from_dateListener,to_dateListener;
    TimePickerDialog.OnTimeSetListener from_timeListener, to_timeListener;
    String AddEventURL;
    Button OkButton;
    static public ArrayList<String> arStopAddressId;
    static private String points;
    static public void setPoints(String mPoints) {
        points = mPoints;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        globalUser = (GlobalUserClass) getApplicationContext();
        gvlistMember = (GridView) findViewById(R.id.gvlistMember);
        AddEventURL = "http://totnghiep.herokuapp.com/api/event";
//        AddEventURL = "http://172.16.1.101:3000/api/event";
        ArrayMember = new ArrayList<>();
        ArListMemAvatar = new ArrayList<>();
        arListIdMember = new ArrayList<>();
        JSONListMember = new JSONArray();
        JSONStartLocs = new JSONArray();
        JSONEndLocs = new JSONArray();
        EventDescription = (EditText) findViewById(R.id.EventDescription);
        mapViewDemo = (ImageView) findViewById(R.id.mapViewDemo);
        nameEvent = (EditText) findViewById(R.id.NameEvent);
        DateStart = (EditText) findViewById(R.id.DateStart);
        TimeStart = (EditText) findViewById(R.id.TimeStart);
        arStopAddressId = new  ArrayList<>();
        points = "";
        from_dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year_, int monthOfYear, int dayOfMonth) {
                year = year_;
                month = monthOfYear;
                day = dayOfMonth;
                updateDisplay_start();
            }
        };
        from_timeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                String minuteString = minute < 10 ? "0" + minute : "" + minute;
                String time = hourString + ":" + minuteString;
                from_Time = time +":"+"00";
                TimeStart.setText(time);
            }
        };
        to_dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year_, int monthOfYear, int dayOfMonth) {
                year = year_;
                month = monthOfYear;
                day = dayOfMonth;
                updateDisplay_return();
            }
        };
        to_timeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                String minuteString = minute < 10 ? "0" + minute : "" + minute;
                String time = hourString + ":" + minuteString;
                to_Time = time +":"+"00";
                TimeReturn.setText(time);
            }
        };

        DateReturn = (EditText) findViewById(R.id.DateReturn);
        TimeReturn = (EditText) findViewById(R.id.TimeReTurn);

        StartAddress = (EditText) findViewById(R.id.StartAddress);
        EndAddress = (EditText) findViewById(R.id.EndAddress);


        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        DateStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        from_dateListener,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setAccentColor(ContextCompat.getColor(getApplicationContext(),R.color.DateTimeBackground));
                dpd.setOkText("ĐỒNG Ý");
                dpd.setCancelText("HỦY");
                dpd.setTitle("Chọn ngày khởi hành");
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        TimeStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        from_timeListener,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                );
                tpd.setTitle("Chọn giờ khởi hành");
                tpd.setAccentColor(ContextCompat.getColor(getApplicationContext(),R.color.DateTimeBackground));
                tpd.setOkText("ĐỒNG Ý");
                tpd.setCancelText("HỦY");
                tpd.show(getFragmentManager(), "Timepickerdialog");
            }
        });

        DateReturn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        to_dateListener,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setAccentColor(ContextCompat.getColor(getApplicationContext(),R.color.DateTimeBackground));
                dpd.setOkText("ĐỒNG Ý");
                dpd.setCancelText("HỦY");
                dpd.setTitle("Chọn ngày khởi hành");
                dpd.show(getFragmentManager(), "DatepickerdialogReturn");
            }
        });
        TimeReturn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        to_timeListener,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                );
                tpd.setTitle("Chọn giờ khởi hành");
                tpd.setAccentColor(ContextCompat.getColor(getApplicationContext(),R.color.DateTimeBackground));
                tpd.setOkText("ĐỒNG Ý");
                tpd.setCancelText("HỦY");
                tpd.show(getFragmentManager(), "TimepickerdialogReturn");
            }
        });

        StartAddress.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder buider = new PlacePicker.IntentBuilder();
                Intent intent;
                try {
                    intent = buider.build(getApplicationContext());
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException ex) {
                    ex.printStackTrace();
                }

            }
        });
        EndAddress.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder buider = new PlacePicker.IntentBuilder();

                Intent intent;
                try {
                    intent = buider.build(getApplicationContext());
                    startActivityForResult(intent, PLACE_DESTINATION_PICKER_REQUEST);
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException ex) {
                    ex.printStackTrace();
                }

            }
        });
        final RouteMapEventFragment routeMapEventFragment = new RouteMapEventFragment();
        findViewById(R.id.OkBtnAdd).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(validate()) {
                    new addEvent().execute(AddEventURL);
                }
            }
        });
        findViewById(R.id.CancelBtnAdd).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                finish();
            }
        });
        mapViewDemo.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(startaddress_LatLgn==null) {
                    Toast.makeText(getApplicationContext(),"Chưa chọn điểm khởi hành", Toast.LENGTH_LONG).show();
                } else if (destination_LatLgn==null) {
                    Toast.makeText(getApplicationContext(),"Chưa chọn điểm đích", Toast.LENGTH_LONG).show();
                } else {
                    ShowFragment(routeMapEventFragment);
                }
            }
        });
        findViewById(R.id.Addmemberbtn).setOnClickListener(new View.OnClickListener() {
            public  void onClick(View v) {
                Intent intent = new Intent(AddEventActivity.this, AddMemberEventActivity.class);
                intent.putExtra("arListIdMember",arListIdMember);
                intent.putExtra("ArListMemAvatar",ArListMemAvatar);
                startActivityForResult(intent,ADD_MEMBER_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    private void ShowFragment(RouteMapEventFragment routeMapEventFragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);

        ft.replace(R.id.activity_add_event, routeMapEventFragment).addToBackStack("MapFragment");

// Start the animated transition.
        ft.commit();
    }


    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        if(resultCode != RESULT_OK) {
            return;
        }
        if(requestCode == PLACE_PICKER_REQUEST) {
                Place place = PlacePicker.getPlace(data, this);
                String address = String.format("%s",place.getAddress());
            try {
                this.setStartAddress(place.getLatLng());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            StartAddress.setText(address);
        } else if(requestCode == PLACE_DESTINATION_PICKER_REQUEST) {
                Place place = PlacePicker.getPlace(data, this);
                String address = String.format("%s",place.getAddress());
            try {
                this.setDestination(place.getLatLng());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            EndAddress.setText(address);
        }
        else if (requestCode == ADD_MEMBER_REQUEST) {
            JSONListMember = new JSONArray();
            arListIdMember = new ArrayList<>();
            ArListMemAvatar = new ArrayList<>();
            ArrayList<String> ListMember = data.getStringArrayListExtra("listMember");
            ArrayList<String> ListAvatar = data.getStringArrayListExtra("listAvatar");
            for (int i = 0;i < ListMember.size(); i++) {
                JSONListMember.put(ListMember.get(i));
                ArListMemAvatar.add(ListAvatar.get(i));
                arListIdMember.add(ListMember.get(i));
            }
            ImageAddapter adapter_listmember
                    = new ImageAddapter(getApplicationContext(), R.layout.single_image_gridview,ArListMemAvatar);
            gvlistMember.setAdapter(adapter_listmember);

        }
    }

    public void setStartAddress(LatLng start_) throws JSONException {
        JSONStartLocs.put(0,start_.longitude);
        JSONStartLocs.put(1,start_.latitude);
        this.startaddress_LatLgn = start_;
    }

    public void setDestination(LatLng destination) throws JSONException {
        JSONEndLocs.put(0,destination.longitude);
        JSONEndLocs.put(1,destination.latitude);
        this.destination_LatLgn = destination;
    }

    public LatLng getStartaddress() {
        return startaddress_LatLgn;
    }

    public LatLng getDestination() {
        return destination_LatLgn;
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialog");
        TimePickerDialog tpd = (TimePickerDialog) getFragmentManager().findFragmentByTag("TimepickerDialog");
        DatePickerDialog dpdReturn = (DatePickerDialog) getFragmentManager().findFragmentByTag("DatepickerdialogReturn");
        TimePickerDialog tpdReturn = (TimePickerDialog) getFragmentManager().findFragmentByTag("TimepickerDialogReturn");

        if(tpd != null) tpd.setOnTimeSetListener(this.from_timeListener);
        if(dpd != null) dpd.setOnDateSetListener(this.from_dateListener);
        if(dpdReturn != null) tpd.setOnTimeSetListener(this.to_timeListener);
        if(tpdReturn != null) dpd.setOnDateSetListener(this.to_dateListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateDisplay_start() {
        StringBuilder tmp_time = new StringBuilder().append(month + 1).append("/").append(day).append("/").append(year).append(" ");
        from_Date = tmp_time.toString();
        DateStart.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year).append(" "));
    }
    private void updateDisplay_return() {
        DateReturn.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year).append(" "));
        StringBuilder tmp_time = new StringBuilder().append(month + 1).append("/").append(day).append("/").append(year).append(" ");
        to_Date = tmp_time.toString();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void senlocation(LatLng start, LatLng destination) {
        return;
    }
    public boolean validate() {
        boolean valid = true;

        String nameEvent_ =  nameEvent.getText().toString();
        String DateStart_ = DateStart.getText().toString();
        String TimeStart_ = TimeStart.getText().toString();

        String DateReturn_ = DateReturn.getText().toString();
        String TimeReturn_ = TimeReturn.getText().toString();

        String StartAddress_ = StartAddress.getText().toString();
        String EndAddress_ = EndAddress.getText().toString();

        if (nameEvent_.isEmpty()|| nameEvent_.length() < 10) {
            nameEvent.setError("Nhập tên sự kiện lớn hơn 10 ký tự");
            valid = false;
        } else {
            nameEvent.setError(null);
        }

        if (DateStart_.isEmpty()) {
            DateStart.setError("Vui lòng chọn ngày khởi hành");
            valid = false;
        } else {
            DateStart.setError(null);
        }

        if (TimeStart_.isEmpty()) {
            TimeStart.setError("Vui lòng chọn giờ khởi hành");
            valid = false;
        } else {
            TimeStart.setError(null);
        }

        if (DateReturn_.isEmpty()) {
            DateReturn.setError("Vui lòng chọn ngày trở về");
            valid = false;
        } else {
            DateReturn.setError(null);
        }

        if (TimeReturn_.isEmpty()) {
            TimeReturn.setError("Vui lòng chọn giờ trở về");
            valid = false;
        } else {
            TimeReturn.setError(null);
        }

        if (StartAddress_.isEmpty()) {
            StartAddress.setError("Vui lòng chọn ngày trở về");
            valid = false;
        } else {
            StartAddress.setError(null);
        }

        if (EndAddress_.isEmpty()) {
            EndAddress.setError("Vui lòng chọn giờ trở về");
            valid = false;
        } else {
            EndAddress.setError(null);
        }

        return valid;
    }
    private class addEvent extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(AddEventActivity.this);
            progressDialog.setMessage("Đang thêm chuyến đi ");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return SetData(params[0]);
            } catch (IOException ex){
                return "Network Error ";
            } catch (JSONException ex) {
                return "Invalid data!";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            try {
                JSONObject resultObject = new JSONObject(result);
                String message = resultObject.getString("message");
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("RESULTS", "thêm thành công");
                setResult(RESULT_OK, resultIntent);
                finish();
            } catch (JSONException ex){
                ex.printStackTrace();
            }
        }

        private String SetData(String urlPath) throws IOException, JSONException {
            StringBuilder reSult = new StringBuilder();
            BufferedWriter bufferedWriter = null;
            BufferedReader bufferedReader = null;

            try {
                JSONObject dataInsert = new JSONObject();
                JSONArray JsonListStopId = new JSONArray();
                for (int idx = 0; idx < arStopAddressId.size(); idx++ ){
                    JsonListStopId.put(arStopAddressId.get(idx));
                }

                //create JSONdata to send to server
                dataInsert.put("eventname",nameEvent.getText());
                dataInsert.put("createID",globalUser.getCur_user().get_id());
                dataInsert.put("starttime",from_Date+from_Time);
                dataInsert.put("endtime",to_Date+to_Time);
                dataInsert.put("startAddress",StartAddress.getText());
                dataInsert.put("endAddress",EndAddress.getText());
                dataInsert.put("arUser",JSONListMember);
                dataInsert.put("araddress",JsonListStopId);
                dataInsert.put("startLocs",JSONStartLocs);
                dataInsert.put("endLocs",JSONEndLocs);
                dataInsert.put("description",EventDescription.getText());
                dataInsert.put("points",points);
                String toserver = dataInsert.toString();
                //initialize and config request , then connect the server.
                URL url = new URL(urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000); //milliseconds
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("POST");
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
