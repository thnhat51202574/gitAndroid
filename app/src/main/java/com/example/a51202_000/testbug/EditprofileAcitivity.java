package com.example.a51202_000.testbug;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import globalClass.GlobalUserClass;

import static android.text.InputType.TYPE_CLASS_NUMBER;

public class EditprofileAcitivity extends AppCompatActivity {


    private TextView txtEditUserFullName, txtEditPhone, txtEditAddress, txtEditBirthday, txtEditPassword, txtEditRePassword;
    private TextView txtvShowEditPass;
    private LinearLayout linearLayoutEditPass;
    private Button btnEdit;
    private boolean editPass;
    GlobalUserClass globalUser;
    private Date userBrithday;
    private Calendar calendar;
    private Date date;
    private ProgressDialog progressDialog;
    private String stringStatusUpdateInfo = "";
    private static String updateInfoUserURL = "http://totnghiep.herokuapp.com/api/user";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile_acitivity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtEditUserFullName = (TextView) findViewById(R.id.txtEditUserName);
        txtEditPhone = (TextView) findViewById(R.id.txtEditPhone);
        txtEditAddress = (TextView) findViewById(R.id.txtEditAdress);
        txtEditBirthday = (TextView) findViewById(R.id.txtEditBrirthday);
        txtEditPassword = (TextView) findViewById(R.id.txtEditPass);
        txtEditRePassword = (TextView) findViewById(R.id.txtEditRePass);
        txtvShowEditPass = (TextView) findViewById(R.id.txtvShowPassword);
        linearLayoutEditPass = (LinearLayout) findViewById(R.id.passwordlayout);


        //disable editing
        txtEditBirthday.setFocusable(false);
        txtEditUserFullName.setFocusable(false);
        txtEditAddress.setFocusable(false);
        txtEditPhone.setFocusable(false);

        editPass = false;

        //set type pass
        txtEditPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        txtEditRePassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        //set max length of input
        txtEditPhone.setFilters(new InputFilter[] { new InputFilter.LengthFilter(11) });
        txtEditPhone.setInputType(TYPE_CLASS_NUMBER);


        txtEditUserFullName.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent e)
            {
               txtEditUserFullName.setFocusableInTouchMode(true);
                return false;
            }
        });

        txtEditPhone.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent e)
            {
                txtEditPhone.setFocusableInTouchMode(true);
                return false;
            }
        });

        txtEditAddress.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent e)
            {
                txtEditAddress.setFocusableInTouchMode(true);
                return false;
            }
        });

        txtvShowEditPass.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(editPass == false) {
                    txtvShowEditPass.setText("Nhấn để huỷ bỏ thay đổi mật khẩu");
                    linearLayoutEditPass.setVisibility(View.VISIBLE);
                    editPass = true;
                }
                else{
                    txtvShowEditPass.setText("Nhấn để thay đổi mật khẩu");
                    linearLayoutEditPass.setVisibility(View.INVISIBLE);
                    editPass = false;
                }
            }
        });


        reloadInfo();

        findViewById(R.id.btnEditOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txtEditUserFullName.getText().toString().equals("") || txtEditAddress.getText().toString().equals("")
                || txtEditPhone.getText().toString().equals("") || txtEditBirthday.getText().toString().equals("") ) {
                    Toast.makeText(getApplicationContext(),"Hãy điền đầy đủ thông tin",Toast.LENGTH_LONG).show();
                }
                else if(editPass == true){
                    if(txtEditPassword.getText().toString().equals("")||txtEditRePassword.getText().toString().equals("") ||txtEditPassword.getText().length()<4) {
                        if(txtEditPassword.getText().length()<4) {
                            Toast.makeText(getApplicationContext(),"Mật khẩu phải nhiều hơn hoặc bằng 4 ký tự",Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Hãy điền đầy đủ thông tin",Toast.LENGTH_LONG).show();
                        }
                    }
                    else if(!txtEditRePassword.getText().toString().equals(txtEditPassword.getText().toString())) {
                        Toast.makeText(getApplicationContext(),"Mật khẩu nhập lại không trùng",Toast.LENGTH_LONG).show();
                    }
                    else {
                        try {
                            convertDate(txtEditBirthday.getText().toString());
                            new connectServerUpDateInfoUser().execute(updateInfoUserURL);

                        } catch (ParseException ex) {
                            Toast.makeText(getApplicationContext(), "Lỗi Ngày Sinh", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else {

                    try {
                        convertDate(txtEditBirthday.getText().toString());
                        new connectServerUpDateInfoUser().execute(updateInfoUserURL);

                    } catch (ParseException ex) {
                        Toast.makeText(getApplicationContext(), "Lỗi Ngày Sinh", Toast.LENGTH_LONG).show();

                    }
                }
            }

        });

        findViewById(R.id.btnEditCancel).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

       txtEditBirthday.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {
               DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
                   //Sự kiện khi click vào nút Done trên Dialog
                   @Override
                   public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                       txtEditBirthday.setText(year + "-" + (month + 1) + "-" + day);

                   }
               };
               String s = txtEditBirthday.getText() + "";
               //Lấy ra chuỗi của textView Date
               String strArrtmp[] = s.split("-");
               int year = Integer.parseInt(strArrtmp[0]);
               int month = Integer.parseInt(strArrtmp[1]) - 1;
               int date = Integer.parseInt(strArrtmp[2]);
               //Hiển thị ra Dialog
               DatePickerDialog pic = new DatePickerDialog(
                       EditprofileAcitivity.this,
                       callback, year, month, date);
               pic.setTitle("Chọn ngày sinh nhật");
               pic.show();
           }
       });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private void reloadInfo() {
        globalUser = (GlobalUserClass) getApplicationContext();
        txtEditUserFullName.setText(globalUser.getCur_user().getFullName());
        txtEditBirthday.setText(android.text.format.DateFormat.format("yyyy-MM-dd", globalUser.getCur_user().getBirthday()));
        txtEditAddress.setText(globalUser.getCur_user().getAddress());
        txtEditPhone.setText(globalUser.getCur_user().getPhone());
    }

    private Date convertDate(String sDay) throws ParseException {
        //sDay = sDay+ "T10:00:00.000Z"; 'T'HH:mm:ss.SSS'Z'"

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        userBrithday= format.parse(sDay);
        return userBrithday ;
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("EditprofileAcitivity Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private class connectServerUpDateInfoUser extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(EditprofileAcitivity.this);
            progressDialog.setMessage(" Update thông tin cá nhân... ");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return SetData(params[0]);
            } catch (IOException ex){
                return "Lỗi mạng!";
            } catch (JSONException ex) {
                return "Lỗi dữ liệu!";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(progressDialog != null)
            {
                progressDialog.dismiss();
            }

            try{
                if(checkStateUpdate(result) == true) {
                    Toast.makeText(getApplicationContext(),stringStatusUpdateInfo, Toast.LENGTH_LONG).show();
                    globalUser.getCur_user().setFullName(txtEditUserFullName.getText().toString());
                    globalUser.getCur_user().setAddress(txtEditAddress.getText().toString());
                    globalUser.getCur_user().setPhone(txtEditPhone.getText().toString());
                    globalUser.getCur_user().setBirthday(userBrithday);

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("RESULTS", "update Userinfo success");
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Lỗi, Thử lại sau!", Toast.LENGTH_LONG).show();
                }

            }catch(JSONException ex)
            {
                Toast.makeText(getApplicationContext(),"Lỗi Update, thử lại sau!", Toast.LENGTH_LONG).show();
            }

        }

        private String SetData(String urlPath) throws IOException, JSONException {
            StringBuilder reSult = new StringBuilder();
            BufferedWriter bufferedWriter = null;
            BufferedReader bufferedReader = null;

            try {
                JSONObject dataInsert = new JSONObject();
                //create JSONdata to send to server
                dataInsert.put("_id", globalUser.getCur_user().get_id());
                dataInsert.put("fullName",txtEditUserFullName.getText());
                dataInsert.put("password","1234");
                dataInsert.put("birthday",txtEditBirthday.getText());
                dataInsert.put("address",txtEditAddress.getText());
                dataInsert.put("phone",txtEditPhone.getText());

                if(editPass == true) {
                    dataInsert.put("password", txtEditPassword.getText());
                    editPass = false;
                }

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
                // JSONObject resultJSON= new JSONObject(reSult.toString());
                //message = resultJSON.getString("message");

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

    Boolean checkStateUpdate(String reSult ) throws JSONException{
        JSONObject resultJSON= new JSONObject(reSult.toString());

        stringStatusUpdateInfo = resultJSON.getString("message");
        String flag = resultJSON.getString("CODE");
        if (flag.equals("SUCCESS")) return true;
        else return false;

    }

}
