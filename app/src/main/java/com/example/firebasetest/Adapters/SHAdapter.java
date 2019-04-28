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

public class SHAdapter extends ArrayAdapter<SH> {

    private static final String TAG = "scavHuntAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    private static class ViewHolder {
        TextView shTitle;
        TextView participants;
        TextView ongoingStatus;
    }

    public SHAdapter(Context context, int resource, ArrayList<SH> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the persons information
        String shTitle = getItem(position).getTitle();

        String onGoingStatus;


        if(getItem(position).checkOngoing()){
            onGoingStatus = "OnGoing";
        }else{
            onGoingStatus = "Ended";
        }


        String numPeople =  getItem(position).participants.size() + " Participants";


                //public SH(String id, String ownerId, String title, String description, String date)

        SH shCell = new SH("n/a", "n/a", shTitle , "n/a", onGoingStatus);

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        ViewHolder holder;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.shTitle = (TextView) convertView.findViewById(R.id.textView1);
            holder.participants = (TextView) convertView.findViewById(R.id.textView2);
            holder.ongoingStatus = (TextView) convertView.findViewById(R.id.textView3);

            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
            //result = convertView;
        }

        //Animation animation = AnimationUtils.loadAnimation(mContext,
          //      (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        //result.startAnimation(animation);

        lastPosition = position;

        holder.shTitle.setText(shCell.getTitle());
        holder.participants.setText(onGoingStatus);
        holder.ongoingStatus.setText(numPeople);


        return convertView;
    }



}