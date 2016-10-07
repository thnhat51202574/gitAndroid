package com.example.a51202_000.testbug;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button start;
    EditText name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.startbtn);
        name = (EditText) findViewById(R.id.nameeditText);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = name.getText().toString();
                if(uname.trim().equals("")) {
                    Toast.makeText(getApplicationContext(),"Please Input your Name", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    intent.putExtra("username",uname);
                    startActivity(intent);
                }
            }
        });

    }
}
