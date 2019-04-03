package com.example.firebasetest.Adapters;
/*
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.firebasetest.Activities.Classes.Question;
import com.example.firebasetest.R;

import java.util.ArrayList;

public class QuestionListAdapter extends ArrayAdapter<Question> {

    private static final String TAG = "QuestionListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    //Holds variables in a View
    private static class ViewHolder {
        TextView name;
        TextView birthday;
        TextView sex;
    }

    public QuestionListAdapter(Context context, int resource, ArrayList<Question> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the persons information

        String description = getItem(position).getDescription();
        String id = getItem(position).getId();
        String title = getItem(position).getTitle();
        String replyType = getItem(position).getReplyType();

        //Create the question object with the information
        Question question = new Question(description,id,title,replyType);

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


        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        result.startAnimation(animation);
        lastPosition = position;

        holder.name.setText(person.getName());
        holder.birthday.setText(person.getBirthday());
        holder.sex.setText(person.getSex());


        return convertView;
    }
}












*/











