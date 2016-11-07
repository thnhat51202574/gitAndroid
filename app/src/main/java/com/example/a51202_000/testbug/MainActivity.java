package com.example.a51202_000.testbug;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import globalClass.GlobalUserClass;
public class MainActivity extends AppCompatActivity {
    Button loginbtn,registerbtn;
    EditText name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        loginbtn = (Button) findViewById(R.id.startbtn);
        registerbtn = (Button) findViewById(R.id.registerbtn);
        name = (EditText) findViewById(R.id.nameeditText);
        loginbtn.getBackground().setAlpha(100);
        registerbtn.getBackground().setAlpha(95);
        final GlobalUserClass globalUser = (GlobalUserClass) getApplicationContext();
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = name.getText().toString();
                if(uname.trim().equals("")) {
                    Toast.makeText(getApplicationContext(),"Please Input your Name", Toast.LENGTH_LONG).show();
                } else {
                    globalUser.setUsername(uname);
                    Intent intent = new Intent(MainActivity.this, MainTabActivity.class);
                    intent.putExtra("username",uname);
                    startActivity(intent);
                }
            }
        });
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = name.getText().toString();
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("username",uname);
                startActivity(intent);
            }
        });

    }
}
