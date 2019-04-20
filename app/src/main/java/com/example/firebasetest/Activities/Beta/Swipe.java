package com.example.firebasetest.Activities.Beta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.example.firebasetest.R;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

public class Swipe extends AppCompatActivity {

    private ArrayList<String> al;
    private ArrayAdapter<String> arrayAdapter;
    private int i;

    private Button swipeB;
    SwipeFlingAdapterView flingContainer;


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

        arrayAdapter = new ArrayAdapter<>(this, R.layout.item, R.id.helloText, al );

        flingContainer = findViewById(R.id.frame);

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                al.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Toast.makeText(Swipe.this, "Left", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Toast.makeText(getApplicationContext(), "Right", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                al.add("Empty ".concat(String.valueOf(i)));
                arrayAdapter.notifyDataSetChanged();
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
                al.add(0,"XML ".concat(String.valueOf(i)));
                arrayAdapter.notifyDataSetChanged();


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


