package com.example.a51202_000.testbug;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


import layout.HomeFragment;

public class UnLoginActivity extends AppCompatActivity {
    HomeFragment homeFragment;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        // toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setLogo(R.mipmap.ic_launcher);
        homeFragment = new HomeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);

        ft.replace(R.id.mapView, homeFragment).addToBackStack("MapFragment");
        ft.commit();

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String Username = pref.getString("username", "");
        String Password = pref.getString("password", "");
        if(!Username.equals("") && !Password.equals("")) {
            Intent intent = new Intent(UnLoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login:
                Intent intent = new Intent(UnLoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }
}
