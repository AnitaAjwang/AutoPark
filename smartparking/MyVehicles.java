package com.example.smartparking;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyVehicles extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView NumberPlate, VehicleModel;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseAuth firebaseAuth;
    private String PlateNumber;
    private String VehicleMake;
    private String userId;
    private DatabaseReference databaseReference;
    private TextView textViewUserEmail,textViewUsername;

    private List<VehicleData> vehicleData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vehicles);
        firebaseAuth = FirebaseAuth.getInstance();


        if (firebaseAuth.getCurrentUser() == null)
        {
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            return;//stops execution in null check
        }
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();

        String name = user.getDisplayName();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Cars").child(userId);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview4);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
         getUserInfo();



        VehicleAdapter vehicleAdapter = new VehicleAdapter(MyVehicles.this,vehicleData);
        recyclerView.setAdapter(vehicleAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add your vehicle", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(MyVehicles.this);
                LayoutInflater layoutInflater = LayoutInflater.from(MyVehicles.this);
                final View dialogView = layoutInflater.inflate(R.layout.my_vehicles_add,null);
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();

                final EditText editTextVehiclePlate = (EditText) dialogView.findViewById(R.id.edit_vehicle_plate);
                final EditText editTextVehicleModel = (EditText) dialogView.findViewById(R.id.edit_vehicle_model);

                builder.setTitle("Enter Car Details");
                builder.setMessage("Providing this information allows you to access the reserved space faster.");
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String EditVehiclePlate= editTextVehiclePlate.getText().toString().trim();
                        String EditVehicleModel = editTextVehicleModel.getText().toString().trim();
                        PlateNumber = EditVehiclePlate;
                        VehicleMake = EditVehicleModel;

                       VehicleData viewData = new VehicleData();
                        viewData.setPlate_number(PlateNumber);
                        viewData.setCar_model(VehicleMake);
                        //getting id from firebase

                        //String VehicleId = databaseReference.push().getKey();
                        //adding both plate and make values using VehicleData class object using ID

                        databaseReference.setValue(viewData);
                       // Map userInfo = new HashMap();
/*                        userInfo.put(PlateNumber);
                        userInfo.put("car_model",VehicleMake);*/
                        databaseReference.setValue(PlateNumber);
                        databaseReference.setValue(VehicleMake);

/*
                        Map UserInfo =new HashMap();

                        UserInfo.put("PlateNumber",PlateNumber);
                        UserInfo.put("VehicleMake",VehicleMake);

                        databaseReference.updateChildren(UserInfo);*/







                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.create().show();


            }
        });



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
        textViewUserEmail = (TextView) headerView.findViewById(R.id.user_emailtextView);
        textViewUsername = (TextView) headerView.findViewById(R.id.user_nameTextView);
        //if(!TextUtils.isEmpty(name))
        // textViewUserEmail.setText("Welcome " + user.getEmail());
        textViewUserEmail.setText(user.getEmail());
        textViewUsername.setText(name);
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
        getMenuInflater().inflate(R.menu.my_vehicles, menu);
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
            finish();
            startActivity(new Intent(MyVehicles.this,UserProfile.class));

        } else if (id == R.id.nav_find_parking) {
            finish();
            startActivity(new Intent(MyVehicles.this,MainActivity.class));

        }
        else if (id==R.id.nav_my_vehicles)
        {
            finish();
            startActivity(new Intent(MyVehicles.this,MyVehicles.class));
        }
        else if (id == R.id.nav_add_parking) {


        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getUserInfo()
    {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                    {
                        //VehicleData vehicleData1 = dataSnapshot1.getValue(VehicleData.class);

                        String user = dataSnapshot1.getKey();
                        String score = dataSnapshot1.getValue(String.class);
                        VehicleData vehicleData1 = new VehicleData(user,score);
                        vehicleData.add(vehicleData1);


                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


private void saveUserInfo()
{





}



}
