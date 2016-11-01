package com.example.a51202_000.testbug;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import Model.Event;

/**
 * Created by 51202_000 on 01/11/2016.
 */

public class EventcustomListview extends ArrayAdapter<Event> {
    ArrayList<Event> events;
    Context context;
    int resource;

    public EventcustomListview(Context context, int resource, ArrayList<Event> objects) {
        super(context, resource, objects);
        this.events = objects;
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.event_layout,null,true);
        }
        Event event = events.get(position);
        Calendar c = Calendar.getInstance();
        c.setTime(event.getEvent_startTime());

        TextView date1 = (TextView) convertView.findViewById(R.id.event_date1);
//        date1.setText(c.get(Calendar.DAY_OF_WEEK));
        TextView date2 = (TextView) convertView.findViewById(R.id.event_date2);
//        date2.setText(c.get(Calendar.DAY_OF_MONTH));
        TextView name = (TextView) convertView.findViewById(R.id.event_name);
        name.setText(event.getEvent_name());
//        TextView date_start = (TextView) convertView.findViewById(R.id.event_date_start);
//        date_start.setText(event.getEvent_startTime().toString());
//        TextView date_end = (TextView) convertView.findViewById(R.id.event_date_end);
//        date_end.setText(event.getEvent_endTime().toString());
//        TextView num_member = (TextView) convertView.findViewById(R.id.event_num_member);
//        num_member.setText(event.getCountUser());
//        ImageButton edit = (ImageButton) convertView.findViewById(R.id.event_edit_btn);
//
//        ImageButton detete = (ImageButton) convertView.findViewById(R.id.event_delete_btn);

        return super.getView(position, convertView, parent);
    }
}
