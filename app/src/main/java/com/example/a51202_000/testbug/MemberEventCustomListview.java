package com.example.a51202_000.testbug;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Model.User;

/**
 * Created by 51202_000 on 15/11/2016.
 */

public class MemberEventCustomListview extends ArrayAdapter<User> {
    ArrayList<User> users;
    Context context;
    ArrayList<User> arUserReturn;
    int resource;

    public interface OnDataChangeListener{
        public void onUserAdd(User user);
        public void onUserRemove(User user);

    }
    OnDataChangeListener mOnDataChangeListener;
    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener){
        mOnDataChangeListener = onDataChangeListener;
    }
    public MemberEventCustomListview(Context context, int resource, ArrayList<User> objects) {
        super(context, resource, objects);
        this.users = objects;
        this.context = context;
        this.resource = resource;
        this.arUserReturn = new ArrayList<>();
    }
    static class ViewHolder {
        TextView Frient_name;
        CircularImageView Avatar;
        CheckBox friendcheckbox;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder h;
        final User user = users.get(position);
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(resource, parent, false);
            h = new ViewHolder();
            h.Frient_name = (TextView)  convertView.findViewById(R.id.friend_name);
            h.Avatar =(CircularImageView) convertView.findViewById(R.id.frient_avatar);
            h.friendcheckbox = (CheckBox) convertView.findViewById(R.id.checkUser);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(h.friendcheckbox.isChecked()) {
                        h.friendcheckbox.setChecked(false);
                        if(mOnDataChangeListener != null){
                            mOnDataChangeListener.onUserRemove(user);
                        }
                    } else {
                        h.friendcheckbox.setChecked(true);
                        if(mOnDataChangeListener != null){
                            mOnDataChangeListener.onUserAdd(user);
                        }
                    }

                    Toast.makeText(context,"Chi tiết" + user.get_id(),Toast.LENGTH_LONG).show();
                }
            });
        } else {
            h =(ViewHolder) convertView.getTag();
        }
        if(h!=null) {
            h.Frient_name.setText(user.getFullName());
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
