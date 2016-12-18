package com.example.a51202_000.testbug;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Model.User;

/**
 * Created by khanhngo on 12/18/16.
 */

public class AddFriendCustomListview extends ArrayAdapter<User> {
    ArrayList<User> users;
    Context context;
    ArrayList<User> arUserReturn;
    int resource;

    public interface OnDataChangeListener{
        public void onUserAdd(User user,CircularProgressButton btnProgress );

    }
    AddFriendCustomListview.OnDataChangeListener mOnDataChangeListener;
    public void setOnDataChangeListener(AddFriendCustomListview.OnDataChangeListener onDataChangeListener){
        mOnDataChangeListener = onDataChangeListener;
    }
    public AddFriendCustomListview(Context context, int resource, ArrayList<User> objects) {
        super(context, resource, objects);
        this.users = objects;
        this.context = context;
        this.resource = resource;
        this.arUserReturn = new ArrayList<>();
    }
    static class ViewHolder {
        TextView Frient_name;
        CircularImageView Avatar;
        CircularProgressButton addFriendbtn;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AddFriendCustomListview.ViewHolder h;
        final User user = users.get(position);
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(resource, parent, false);
            h = new AddFriendCustomListview.ViewHolder();
            h.Frient_name = (TextView)  convertView.findViewById(R.id.friend_name);
            h.Avatar =(CircularImageView) convertView.findViewById(R.id.frient_avatar);
            h.addFriendbtn = (CircularProgressButton) convertView.findViewById(R.id.circularButton2);
        } else {
            h =(AddFriendCustomListview.ViewHolder) convertView.getTag();
        }
        if(h!=null) {
            String userNamedisplay = user.getFullName();
            if(userNamedisplay.isEmpty()) userNamedisplay = user.getName();
            h.Frient_name.setText(userNamedisplay);
            String url = user.getAvatarLink();
            h.addFriendbtn.setIndeterminateProgressMode(true);
            h.addFriendbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    h.addFriendbtn.setProgress(50);
                    mOnDataChangeListener.onUserAdd(user,h.addFriendbtn);
                }
            });
            if(url.isEmpty()) {

            } else {
                Picasso.with(context).load(url).error(R.drawable.no_images).into(h.Avatar, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError() {

                    }
                });
            }
        }
        return convertView;
    }

}
