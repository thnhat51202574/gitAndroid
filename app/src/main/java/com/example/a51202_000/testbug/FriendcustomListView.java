package com.example.a51202_000.testbug;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Model.User;

/**
 * Created by 51202_000 on 15/11/2016.
 */

public class FriendcustomListView extends ArrayAdapter<User> {
    ArrayList<User> users;
    Context context;
    int resource;
    public FriendcustomListView(Context context, int resource, ArrayList<User> objects) {
        super(context, resource, objects);
        this.users = objects;
        this.context = context;
        this.resource = resource;
    }
    static class ViewHolder {
        TextView Frient_name;
        CircularImageView Avatar;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder h;
        final User user = users.get(position);
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.friend_layout, null, true);
            h = new ViewHolder();
            h.Frient_name = (TextView)  convertView.findViewById(R.id.friend_name);
            h.Avatar =(CircularImageView) convertView.findViewById(R.id.frient_avatar);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,"Chi tiết" + user.get_id(),Toast.LENGTH_LONG).show();
                }
            });
        } else {
            h =(ViewHolder) convertView.getTag();
        }
        h.Frient_name.setText(user.getFullName());
        h.Frient_name.setText(user.getFullName());
        String url = user.getAvatarLink();
        Picasso.with(context).load(url).error(R.drawable.no_images).into(h.Avatar, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {

            }
            @Override
            public void onError() {

            }
        });
        return convertView;


    }
}
