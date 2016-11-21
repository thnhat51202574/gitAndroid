package com.example.a51202_000.testbug;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class RegisterActivity extends AppCompatActivity {
    private EditText edtEmail, edtPass, edtRepass;
    private Button btnRegister, btnLogin;
    private ProgressDialog progressDialog;

    private static String RegisterURL = "http://totnghiep.herokuapp.com/api/user";
    private String stringStatusRegister = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_register);

        edtEmail = (EditText) findViewById(R.id.edtEmailRe);
        edtPass = (EditText) findViewById(R.id.edtPasswordRe);
        edtRepass = (EditText) findViewById(R.id.edtRePasswordRe);

        btnLogin = (Button) findViewById(R.id.btnLoginRe);
        btnRegister = (Button) findViewById(R.id.btnRegisterRe);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uName = edtEmail.getText().toString();
                String uPass = edtPass.getText().toString();
                String uRePass = edtRepass.getText().toString();

                if(uName.isEmpty() ||uPass.isEmpty()||uRePass.isEmpty() ) {
                    Toast.makeText(getApplicationContext()," Hãy điền đầy đủ thông tin", Toast.LENGTH_LONG).show();
                }
                else if(uPass.length() < 4 || uPass.length() >10)
                {
                    Toast.makeText(getApplicationContext()," Mật khẩu nhiều hơn 4 ký tự", Toast.LENGTH_LONG).show();
                }
                else if(!uRePass.equals(uPass))
                {
                    Toast.makeText(getApplicationContext(),"Mật khẩu nhập lại không đúng", Toast.LENGTH_LONG).show();
                }
                else {
                    new connectServerRe().execute(RegisterURL);
                }

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }


    private class connectServerRe extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setMessage(" Kiểm tra đăng Ký... ");
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
            if(progressDialog != null)
            {
                progressDialog.dismiss();
            }

            try{
                if(checkLogin(result) == true) {
                    Toast.makeText(getApplicationContext(),stringStatusRegister, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),stringStatusRegister, Toast.LENGTH_LONG).show();
                }

            }catch(JSONException ex)
            {
                Toast.makeText(getApplicationContext(),"Lỗi đăng ký, thử lại sau!", Toast.LENGTH_LONG).show();
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

        stringStatusRegister = resultJSON.getString("message");
        String flag = resultJSON.getString("CODE");
        if (flag.equals("SUCCESS")) return true;
        else return false;

    }

}
