package com.example.firebasetest.Adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.firebasetest.Activities.Classes.SH;
import com.example.firebasetest.R;

import java.util.ArrayList;
import java.util.List;


public class SHAdapter extends ArrayAdapter<SH> {

    private static final String TAG = "scavHuntAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView name;
        TextView birthday;
        TextView sex;
    }

    /**
     * Default constructor for the PersonListAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public scavHuntAdapter(Context context, int resource, ArrayList<SH> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the persons information
        String name = getItem(position).getTitle();
        String birthday = "hi";
        String sex = "test2";

        //Create the person object with the information
        SH person = new SH("chris", "beau", name , "jimmy");

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        ViewHolder holder;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.textView1);
            holder.birthday = (TextView) convertView.findViewById(R.id.textView2);
            holder.sex = (TextView) convertView.findViewById(R.id.textView3);

            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


        holder.name.setText(person.getTitle());
        holder.birthday.setText(person.getId());
        holder.sex.setText(person.getOwnerId());


        return convertView;
    }
}