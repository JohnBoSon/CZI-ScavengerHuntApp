package com.example.firebasetest.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.firebasetest.Activities.Classes.Question;
import com.example.firebasetest.R;

import java.util.ArrayList;

public class ViewQuestionActivity extends AppCompatActivity {

    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_question);


        listView = (ListView) findViewById(R.id.question_list);

        final ArrayList<Question> arrayList = new ArrayList <>();

        arrayList.add(new Question( "descript", "id ", "Hey", "reply type"));
        arrayList.add(new Question( "descript", "id ", "OH", "reply type"));

        arrayList.add(new Question( "descript", "id ", "ya", "reply type"));
        arrayList.add(new Question( "descript", "id ", "hear", "reply type"));
        arrayList.add(new Question( "descript", "id ", "me", "reply type"));

        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayList);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i , long l){
                Toast.makeText(ViewQuestionActivity.this,"Clicked item:" + i + " " + arrayList.get(i).getTitle(), Toast.LENGTH_SHORT).show();
            }

        });

    }
}
