package com.example.firebasetest.Activities.Beta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebasetest.Activities.Classes.Response;
import com.example.firebasetest.Activities.Classes.SH;
import com.example.firebasetest.Activities.Qdash;
import com.example.firebasetest.R;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

public class Swipe extends AppCompatActivity {

    private ArrayList<String> al;
    private ArrayAdapter<String> arrayAdapter;
    private int i;

    private Button swipeB;
    SwipeFlingAdapterView flingContainer;

    //TEST
    FirebaseListAdapter adapter;
    ArrayList<Response> rlist;
    Response r0;
    private FirebaseDatabase database;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);

        swipeB = findViewById(R.id.swipeBack);

        al = new ArrayList<>();
        al.add("I");
        al.add("Like");
        al.add("Green");
        al.add("Eggs");
        al.add("And");
        al.add("Ham");
        al.add("Sam");
        al.add("I am");

        rlist = new ArrayList<>();

        r0 = new Response("0","2","3",false, "na0");
        Response r1 = new Response("DAS","2","3",false, "na4");
        Response r2 = new Response("NOICE","2","3",false, "na6");
        Response r3 = new Response("POSSIBLE?","2","3",false, "na7");
        Response r4 = new Response("https://firebasestorage.googleapis.com/v0/b/fir-test-d36c4.appspot.com/o/Photos%2FBATMAN?alt=media&token=77f78f34-bdab-4331-b06a-a58419ac5ea1","2","3",true, "na8");
        Response r5 = new Response("5","2","3",false, "na1");

        rlist.add(r1);
        rlist.add(r2);
        rlist.add(r3);
        rlist.add(r4);
        rlist.add(r5);

        database = FirebaseDatabase.getInstance();
        database.getReference("beta").child("swipeTest").setValue(rlist);



        //arrayAdapter = new ArrayAdapter<>(this, R.layout.item, R.id.textTV, al );

        flingContainer = findViewById(R.id.frame);

        //flingContainer.setAdapter(arrayAdapter);

        //test
        Query query = FirebaseDatabase.getInstance().getReference("beta").child("swipeTest");
        FirebaseListOptions<Response> options = new FirebaseListOptions.Builder<Response>()
                .setLayout(R.layout.item)
                .setLifecycleOwner(Swipe.this)
                .setQuery(query, Response.class)
                .build();
        adapter = new FirebaseListAdapter<Response>(options) {
            @Override
            protected void populateView(View v, Response model, int position) {
                TextView title = (TextView) v.findViewById(R.id.textTV);
                ImageView image = (ImageView) v.findViewById(R.id.imageV);
                if(model.isImage()){
                    Glide.with(image.getContext()).load(model.getReply()).into(image);
                }else {
                    title.setText(model.getReply());
                }
            }
        };

        flingContainer.setAdapter(adapter);

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rlist.remove(0);
                database.getReference("beta").child("swipeTest").setValue(rlist);

                //arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Toast.makeText(Swipe.this, "Left" + ((Response)dataObject).getReply(), Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Toast.makeText(getApplicationContext(), "Right", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                rlist.add(r0);
                database.getReference("beta").child("swipeTest").setValue(rlist);

                //add("Empty ".concat(String.valueOf(i)));
                //arrayAdapter.notifyDataSetChanged();


                //Toast.makeText(getApplicationContext(), "added", Toast.LENGTH_SHORT).show();
                Log.d("LIST", "notified");
                i++;
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });


        swipeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                al.add(0,"XML ".concat(String.valueOf(i)));
                arrayAdapter.notifyDataSetChanged();
*/

            }
        });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(Swipe.this, "Clicked", Toast.LENGTH_SHORT);

            }
        });

    }
/*
    static void makeToast(Context ctx, String s){
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }


    @OnClick(R.id.right)
    public void right() {

        flingContainer.getTopCardListener().selectRight();
    }

    @OnClick(R.id.left)
    public void left() {
        flingContainer.getTopCardListener().selectLeft();
    }

*/


}


