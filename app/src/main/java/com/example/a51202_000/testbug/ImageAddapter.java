package com.example.a51202_000.testbug;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Model.User;

/**
 * Created by 51202_000 on 24/11/2016.
 */

public class ImageAddapter extends BaseAdapter {
    private ArrayList<User> users;
    private Context context;
    int resource;
    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    public ImageAddapter( Context context, int resource, ArrayList<User> users) {
        this.users = users;
        this.context = context;
        this.resource = resource;
    }

    @Override

    public long getItemId(int position) {
        return 0;
    }

    public void addUser(User user) {
        users.add(users.size(),user);
        this.notifyDataSetChanged();
    }

    public void removeUser(User user) {
        users.indexOf(user);
        users.remove(user);
        this.notifyDataSetChanged();
    }

    private static class ViewHolder {
        CircularImageView Avatar;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder h;
        final User user = users.get(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(resource, parent, false);
            h = new ViewHolder();
            h.Avatar =(CircularImageView) convertView.findViewById(R.id.frient_avatar);
        }
        else {
            h =(ViewHolder) convertView.getTag();
        }
        if(h!=null) {
            String url ="http://totnghiep.herokuapp.com"+ user.getAvatarLink();
            Picasso.with(context).load(url).error(R.drawable.no_images).into(h.Avatar, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError() {

                }
            });
        }
        return convertView;
    }
}
