package com.example.firebasetest.Activities.Beta;

import android.view.View;
import android.widget.ListView;

import com.example.firebasetest.Activities.Classes.SH;
import com.example.firebasetest.R;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
/*
public class demoListView {
    ListView lv;
    FirebaseListAdapter adapter;

    Query query = FirebaseDatabase.getInstance().getReference().child("Student");

    void createOn(){
        lv = (ListView) findViewById(R.id.listView);
        FirebaseListOptions<SH> options = new FirebaseListOptions.Builder<SH>()
                .setLayout(R.layout.sh_adapter_view_layout)
                .setLifecycleOwner([name of class].this)
                .setQuery(query,SH.class)
                .build();


    }




    FirebaseListOptions<Chat> options = new FirebaseListOptions.Builder<Chat>()
            .setQuery(query, Chat.class)
            .build();

    FirebaseListAdapter<Chat> adapter = new FirebaseListAdapter<Chat>(options) {
        @Override
        protected void populateView(View v, Chat model, int position) {
            // Bind the Chat to the view
            // ...
        }
    };v

}*/
