package com.example.a51202_000.testbug;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.wdullaer.materialdatetimepicker.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.TimeZone;

public class AddEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private DatePicker datePicker;
    private Calendar calendar;
    private EditText dateView, timeView;
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        String id = intent.getStringExtra("userid");
        Toast.makeText(getApplicationContext(),id,Toast.LENGTH_LONG).show();

        dateView = (EditText) findViewById(R.id.DateTimePickerEditText);
        timeView = (EditText) findViewById(R.id.TimePickerEditText);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        dateView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AddEventActivity.this,
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
        timeView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        AddEventActivity.this,
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
    }



    @Override
    protected void onResume() {
        super.onResume();
        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialog");
        TimePickerDialog tpd = (TimePickerDialog) getFragmentManager().findFragmentByTag("TimepickerDialog");

        if(tpd != null) tpd.setOnTimeSetListener(this);
        if(dpd != null) dpd.setOnDateSetListener(this);
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

    private void updateDisplay() {
        dateView.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(day).append("/").append(month + 1).append("/").append(year).append(" "));
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year_, int monthOfYear, int dayOfMonth) {
        year = year_;
        month = monthOfYear;
        day = dayOfMonth;
        updateDisplay();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        String minuteString = minute < 10 ? "0"+minute : ""+minute;
        String time = hourString+":"+minuteString;
        timeView.setText(time);
    }
}
