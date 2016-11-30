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
    private ArrayList<String> users_avatar;
    private Context context;
    int resource;
    @Override
    public int getCount() {
        return users_avatar.size();
    }

    @Override
    public Object getItem(int position) {
        return users_avatar.get(position);
    }

    public ImageAddapter( Context context, int resource, ArrayList<String> UserAvatar) {
        this.users_avatar = UserAvatar;
        this.context = context;
        this.resource = resource;
    }

    @Override

    public long getItemId(int position) {
        return 0;
    }

    public void addUser(String user) {
        users_avatar.add(users_avatar.size(),user);
        this.notifyDataSetChanged();
    }

    public void removeUser(String user) {
        users_avatar.indexOf(user);
        users_avatar.remove(user);
        this.notifyDataSetChanged();
    }

    private static class ViewHolder {
        CircularImageView Avatar;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder h;
        final String user_linkavatar = users_avatar.get(position);
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
            String url =user_linkavatar;
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
