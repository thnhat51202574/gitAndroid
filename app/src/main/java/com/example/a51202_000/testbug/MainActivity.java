package com.example.a51202_000.testbug;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


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

import globalClass.GlobalUserClass;

public class MainActivity extends AppCompatActivity {
    private Button btnLogin, btnRegister;
    private EditText edtEmail, edtPass;
    private ProgressDialog progressDialog;
    GlobalUserClass globalUser;

    private static String loginURL = "http://totnghiep.herokuapp.com/api/login/user";



    EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        btnLogin = (Button) findViewById(R.id.btnLoginLg);
        btnRegister = (Button) findViewById(R.id.btnRegisterLg);
        edtEmail = (EditText) findViewById(R.id.edtNameLg);
        edtPass = (EditText) findViewById(R.id.edtPasswordLg);

        btnLogin.getBackground().setAlpha(100);
        btnRegister.getBackground().setAlpha(95);
		globalUser = (GlobalUserClass) getApplicationContext();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = edtEmail.getText().toString();
                if(uname.trim().equals("")) {
                    Toast.makeText(getApplicationContext(),"Please Input your Name", Toast.LENGTH_LONG).show();
                } else {
                    new connectServerLg().execute(loginURL);

                }
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    private class connectServerLg extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage(" Kiểm tra đăng nhập... ");
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
            try{
                if(checkLogin(result) == true) {
                    Toast.makeText(getApplicationContext(), "login sucess", Toast.LENGTH_LONG).show();
                    JSONObject resultJSON= new JSONObject(result.toString());
                    JSONObject user = resultJSON.getJSONObject("user");
                    String _id = user.getString("_id");
                    globalUser.set_id(_id);
                    Intent intent = new Intent(MainActivity.this, MainTabActivity.class);					
                    startActivity(intent);
                }
                else
                    Toast.makeText(getApplicationContext(),"login fail", Toast.LENGTH_LONG).show();
            }catch(JSONException ex)
            {
                Toast.makeText(getApplicationContext(),"login fail ex", Toast.LENGTH_LONG).show();
            }

        }

        private String SetData(String urlPath) throws IOException, JSONException {
            StringBuilder reSult = new StringBuilder();
            BufferedWriter bufferedWriter = null;
            BufferedReader bufferedReader = null;

            try {
                JSONObject dataInsert = new JSONObject();
                //create JSONdata to send to server
                dataInsert.put("username",edtEmail.getText());
                dataInsert.put("password",edtPass.getText());
                //txtLoginView.setText(urlPath);



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

    Boolean checkLogin(String reSult ) throws JSONException{
        JSONObject resultJSON= new JSONObject(reSult.toString());
        String flag = resultJSON.getString("CODE");
        if (flag.equals("SUCCESS")) return true;
        else return false;

    }
}
