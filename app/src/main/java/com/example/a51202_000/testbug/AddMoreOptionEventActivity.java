package com.example.a51202_000.testbug;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddMoreOptionEventActivity extends AppCompatActivity {
    EditText mindistanceEditext;
    String mindistance;
    String CODE;
    TextView labeldistance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_more_option_event);
        labeldistance =(TextView) findViewById(R.id.labeldistance);
        mindistanceEditext = (EditText) findViewById(R.id.mindistance);
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mindistance = extras.getString("mindistance");
            CODE = extras.getString("CODE");
            mindistanceEditext.setText(mindistance);
        } else {
            mindistanceEditext.setText("1000");
        }
        if(CODE.equals("event")) {
            labeldistance.setText("Khoảng cách cảnh báo:");
        } else {
            labeldistance.setText("Bán kính tìm địa điểm:");
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_member_event, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_ok:
                if(validate()) {
                    Intent output  = new Intent();
                    output.putExtra("distance",Integer.parseInt(mindistanceEditext.getText().toString()));
//                output.putStringArrayListExtra("listAvatar",ListmemUser_avatar);
                    setResult(RESULT_OK,output);
                    finish();
                }
                break;
        }
        return true;
    }
    public boolean validate() {
        boolean result = true;
        String text =mindistanceEditext.getText().toString();

        if ((text.isEmpty())||(!text.isEmpty() && (Integer.parseInt(mindistanceEditext.getText().toString()) <= 0))) {
            if(CODE.equals("event")) {
                mindistanceEditext.setError("Vui lòng nhập khoảng cách cảnh báo");
            } else {
                mindistanceEditext.setError("Vui lòng nhập bán kính tìm kiếm");
            }
            result = false;
        } else {
            mindistanceEditext.setError(null);
        }

        return result;

    }
}
