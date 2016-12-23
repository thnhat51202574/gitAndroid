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
import at.markushi.ui.CircleButton;

/**
 * Created by 51202_000 on 15/11/2016.
 */

public class FriendcustomListView extends ArrayAdapter<User> {
    ArrayList<User> users;
    Context context;
    int resource;
    public interface OnUnfriendClickListener{
        void onClick(User user,int Position);
    }
    public OnUnfriendClickListener mListener;
    public void setonUnfriendClickListener(OnUnfriendClickListener onUnfriendClickListener){
        mListener = onUnfriendClickListener;
    }
    public FriendcustomListView(Context context, int resource, ArrayList<User> objects) {
        super(context, resource, objects);
        this.users = objects;
        this.context = context;
        this.resource = resource;
    }
    static class ViewHolder {
        TextView Frient_name;
        CircularImageView Avatar;
        CircleButton unfriend;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder h;
        final User user = users.get(position);
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.friend_layout, null, true);
            h = new ViewHolder();
            h.Frient_name = (TextView)  convertView.findViewById(R.id.friend_name);
            h.Avatar =(CircularImageView) convertView.findViewById(R.id.frient_avatar);
            h.unfriend = (CircleButton) convertView.findViewById(R.id.unfriend);
            h.unfriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onClick(user,position);
                }
            });
        } else {
            h =(ViewHolder) convertView.getTag();
        }
        String userNamedisplay = user.getFullName();
        if(userNamedisplay.isEmpty()) userNamedisplay = user.getName();
        h.Frient_name.setText(userNamedisplay);
        String url = user.getAvatarLink();
        if(!url.isEmpty()) {
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
