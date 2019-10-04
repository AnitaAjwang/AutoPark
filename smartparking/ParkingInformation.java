package com.example.smartparking;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ParkingInformation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private TextView textViewUserEmail,textViewUsername,textViewline1,textViewline2;
    private TextView textViewparkingName,textViewNotReserved;
    private Button reserveButton;
    private String parkingName;
    private String Parking;
    String parking_name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_information);

        firebaseAuth = FirebaseAuth.getInstance();


        if (firebaseAuth.getCurrentUser() == null)
        {
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            return;//stops execution in null check
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();

        final String name = user.getDisplayName();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        int color = Color.rgb(0,188,212);
        headerView.setBackgroundColor(color);
        //textview initialization
        textViewUserEmail = (TextView) headerView.findViewById(R.id.user_emailtextView);
        textViewUsername = (TextView) headerView.findViewById(R.id.user_nameTextView);
        textViewline1 = (TextView) findViewById(R.id.textViewline1);
        textViewline2 = (TextView) findViewById(R.id.textViewline2);
        textViewparkingName = (TextView) findViewById(R.id.textView_ParkingName);
        textViewNotReserved = (TextView) findViewById(R.id.notReservedTextView);

        //if(!TextUtils.isEmpty(name))
        // textViewUserEmail.setText("Welcome " + user.getEmail());

        Bundle bundle = getIntent().getExtras();
        parking_name = bundle.getString("Parking Name");

        textViewUserEmail.setText(user.getEmail());
        textViewUsername.setText(name);
        textViewparkingName.setText(parking_name);
        parkingName = textViewparkingName.getText().toString().trim();
        reserve();


        reserveButton = (Button) findViewById(R.id.reserveButton);

        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ParkingInformation.this,MpesaActivity.class));
            }
        });


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
        getMenuInflater().inflate(R.menu.parking_information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));

        } else if (id == R.id.nav_edit_profile) {

        } else if (id == R.id.nav_find_parking) {
            finish();
            startActivity(new Intent(ParkingInformation.this,MainActivity.class));

        } else if (id == R.id.nav_add_parking) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void reserve() {


        DatabaseReference databaseReferenceReserve = FirebaseDatabase.getInstance().getReference().child("Parking Lots Information").child(parking_name);
        DatabaseReference databaseReferenceReserve2 = FirebaseDatabase.getInstance().getReference().child("Parking Lots Information");
        String ParkingName1 = databaseReferenceReserve2.push().getKey();

            databaseReferenceReserve.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        Map<String, Object> stringObjectMap = (Map<String, Object>) dataSnapshot.getValue();

                        if (stringObjectMap.get("Reserve Status") != null) {
                            Parking = stringObjectMap.get("Reserve Status").toString();

                        }
                    }
                    if (Parking.equals("Reserve")) {
                        reserveButton.setVisibility(View.VISIBLE);
                    }
                    else {
                        textViewline1.setVisibility(View.VISIBLE);
                        textViewNotReserved.setVisibility(View.VISIBLE);
                        textViewline2.setVisibility(View.VISIBLE);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



    }


}
