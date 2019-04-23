package com.example.firebasetest.Activities;

        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import com.example.firebasetest.R;


public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
}


   /* Intent intent = new Intent(this, OtherActivity.class);
intent.putExtra(OtherActivity.KEY_EXTRA, yourDataObject);
        startActivity(intent);
        In OtherActivity :

public static final String KEY_EXTRA = "com.example.yourapp.KEY_BOOK";

@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String yourDataObject = null;

        if (getIntent().hasExtra(KEY_EXTRA)) {
        yourDataObject = getIntent().getStringExtra(KEY_EXTRA);
        } else {
        throw new IllegalArgumentException("Activity cannot find  extras " + KEY_EXTRA);
        }
        // do stuff
        }*/