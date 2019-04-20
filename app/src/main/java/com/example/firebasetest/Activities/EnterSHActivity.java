package com.example.firebasetest.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.firebasetest.Activities.Beta.Swipe;
import com.example.firebasetest.Activities.Beta.UploadGallery;
import com.example.firebasetest.Fragments.HomeFragment;
import com.example.firebasetest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EnterSHActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    FirebaseAuth mAuth;
    FirebaseUser currentUser ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();

        getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar old_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }
        */
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view old_item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            //getSupportActionBar().setTitle("Home");
            //getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();

            //Intent viewUpload = new Intent(getApplicationContext(), UploadGallery.class);
            //startActivity(viewUpload);


            //getSupportActionBar().setTitle("Profile");
            //getSupportFragmentManager().beginTransaction().replace(R.id.container,new ProfileFragment()).commit();

            //Intent manageSH = new Intent(getApplicationContext(),ManageSHActivity.class);
            //startActivity(manageSH);

            Intent Swipe = new Intent(getApplicationContext(), com.example.firebasetest.Activities.Beta.Swipe.class);
            startActivity(Swipe);

        } else if (id == R.id.nav_manage_sh) {
            //getSupportActionBar().setTitle("Settings");
            //getSupportFragmentManager().beginTransaction().replace(R.id.container,new SettingsFragment()).commit();

            Intent manageSHActivity = new Intent(getApplicationContext(),ManageSHActivity.class);
            startActivity(manageSHActivity);

        }else if (id == R.id.nav_new_sh) {
            //getSupportActionBar().setTitle("Settings");
            //getSupportFragmentManager().beginTransaction().replace(R.id.container,new SettingsFragment()).commit();

            Intent enterSHActivity = new Intent(getApplicationContext(),EnterSHActivity.class);
            startActivity(enterSHActivity);

        }else if (id == R.id.nav_signout) {

            FirebaseAuth.getInstance().signOut();
            Intent loginActivity = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(loginActivity);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateNavHeader() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        TextView navUsername = headerView.findViewById(R.id.nav_username);
        TextView navUserMail = headerView.findViewById(R.id.nav_user_mail);

        navUserMail.setText(currentUser.getEmail());
        navUsername.setText(currentUser.getDisplayName());



    }
}
