package com.example.a51202_000.testbug;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
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

import globalClass.GlobalUserClass;

public class MainActivity extends AppCompatActivity {
    private Button btnLogin, btnRegister;
    private EditText edtEmail, edtPass;
    GlobalUserClass globalUser;
    private View mLoginFormView;
    private View mProgressView;
    private static String loginURL = "http://totnghiep.herokuapp.com/api/login/user";



    EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        mProgressView = (View) findViewById(R.id.login_progress);
        mLoginFormView = (View) findViewById(R.id.mLogin_FormView);

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
                if(!validate()) {
                    onLoginFailed();
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

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void onLoginSuccess() {
        btnLogin.setEnabled(true);
        finish();
    }
    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Đăng nhập thất bại", Toast.LENGTH_LONG).show();
        btnLogin.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = edtEmail.getText().toString();
        String password = edtPass.getText().toString();

        if (email.isEmpty()) {
            edtEmail.setError("Vui lòng nhập tên đăng nhập");
            valid = false;
        } else {
            edtEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            edtPass.setError("Mật khẩu lớn hơn 4 ký tự");
            valid = false;
        } else {
            edtPass.setError(null);
        }

        return valid;
    }

    private class connectServerLg extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
//            showProgress(true);
            progressDialog = new ProgressDialog(MainActivity.this,R.style.AppTheme_CustomDialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Đang đang nhập...");
            progressDialog.show();
            super.onPreExecute();
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
            try{
                if(checkLogin(result) == true) {
                    Toast.makeText(getApplicationContext(), "login sucess", Toast.LENGTH_LONG).show();
                    JSONObject resultJSON= new JSONObject(result.toString());
                    JSONObject user = resultJSON.getJSONObject("user");
                    String _id = user.getString("_id");
                    globalUser.set_id(_id);
                    globalUser.setUsername(edtEmail.getText().toString());
                    globalUser.setPassword(edtPass.getText().toString());
                    Intent intent = new Intent(MainActivity.this, MainTabActivity.class);					
                    startActivity(intent);
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    // On complete call either onLoginSuccess or onLoginFailed
                                    onLoginSuccess();
                                    // onLoginFailed();
                                    progressDialog.dismiss();
                                }
                            }, 3000);
                }
                else
                    Toast.makeText(getApplicationContext(),"Có lỗi xảy ra", Toast.LENGTH_LONG).show();
            }catch(JSONException ex)
            {
                ex.printStackTrace();
//                Toast.makeText(getApplicationContext(),"login fail ex", Toast.LENGTH_LONG).show();
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
