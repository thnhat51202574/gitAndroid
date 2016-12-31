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
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Model.Event;
import at.markushi.ui.CircleButton;

/**
 * Created by 51202_000 on 01/11/2016.
 */

public class EventcustomListview extends ArrayAdapter<Event> {
    ArrayList<Event> events;
    Context context;
    int resource;

    public interface OnDataChangeListener{
        public void onClick(int Position);
    }
    public interface OnEditMemberEvent{
        public void onClick(int Position,Event event);
    }
    public interface OnDeleteListener{
        public void onBeginDelete(int Position);
    }
    public interface OnexitEventListener{
        public void onExitClick(int Position, Event event);
    }
    OnDataChangeListener onItemclickListener;
    OnDeleteListener mListenerDelete;
    OnexitEventListener mListenerExit;
    OnEditMemberEvent mListenerEditMember;
    public void setOnEachItemChangeListener(OnDataChangeListener onDataChangeListener){
        onItemclickListener = onDataChangeListener;
    }
    public void setOnEditMemberListener(OnEditMemberEvent onEditMemberListener) {
        mListenerEditMember = onEditMemberListener;
    }
    public void setOnexitItemListener(OnexitEventListener onexitItemListener) {
        mListenerExit = onexitItemListener;
    }
    public void setOnDeleteItemListener(OnDeleteListener onDeleteItemListener) {
        mListenerDelete = onDeleteItemListener;
    }
    public EventcustomListview(Context context, int resource, ArrayList<Event> objects) {
        super(context, resource, objects);
        this.events = objects;
        this.context = context;
        this.resource = resource;
    }
    static class ViewHolder {
        TextView name,date1,date2,time_start,date_start,count_member;
        CircleButton btn1,btn2,exit_btn;

    }
    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder h;
        final Event event = events.get(position);
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.event_layout, null, true);
            h = new ViewHolder();
            h.date1 = (TextView) convertView.findViewById(R.id.event_date1);
            h.date2 = (TextView) convertView.findViewById(R.id.event_date2);
            h.time_start = (TextView) convertView.findViewById(R.id.event_time_start);
            h.date_start = (TextView) convertView.findViewById(R.id.event_date_start);
            h.count_member = (TextView) convertView.findViewById(R.id.event_num_member);
            h.name = (TextView) convertView.findViewById(R.id.event_name);
            h.btn1 = (CircleButton) convertView.findViewById(R.id.event_edit_btn);
            h.btn2 = (CircleButton) convertView.findViewById(R.id.event_delete_btn);
            h.exit_btn = (CircleButton) convertView.findViewById(R.id.event_out_btn);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemclickListener.onClick(position);
                }
            });
            h.btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListenerEditMember.onClick(position,event);
                }
            });
            h.btn2.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mListenerDelete.onBeginDelete(position);
                }
            });
            h.exit_btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mListenerExit.onExitClick(position,event);
                }
            });
            convertView.setTag(h);
        } else {
            h =(ViewHolder) convertView.getTag();
        }
        if(!event.isowner()) {
            h.btn1.setVisibility(View.GONE);
            h.btn2.setVisibility(View.GONE);
            h.exit_btn.setVisibility(View.VISIBLE);
        } else {
            h.btn1.setVisibility(View.VISIBLE);
            h.btn2.setVisibility(View.VISIBLE);
            h.exit_btn.setVisibility(View.GONE);
        }
        Date  dateStarttime = event.getEvent_startTime();
        String Date_inWeek = new SimpleDateFormat("EE").format(dateStarttime);
        String Date_inMonth = (String) android.text.format.DateFormat.format("dd", dateStarttime);
        h.date1.setText(Date_inWeek.toUpperCase());
        h.date2.setText(Date_inMonth);
        h.time_start.setText(android.text.format.DateFormat.format("HH:mm", dateStarttime));
        h.date_start.setText(android.text.format.DateFormat.format("dd/MM", dateStarttime));
        h.count_member.setText(String.valueOf(event.getCountUser()));
        h.name.setText(event.getEvent_name());


        return convertView;
    }
}
