package com.example.smartparking;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;*/

public class MainActivity extends AppCompatActivity
            implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, RoutingListener {
    private TextView textViewUserEmail, textViewUsername;
    private FirebaseAuth firebaseAuth;
    private TabLayout tabLayout;
    private TabAdapter tabAdapter;
    private ViewPager viewPager;
    private GoogleMap mMap;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    MapView mapView;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private Button mLogout;
    private Button mFindParking;
    LatLng ParkingLot1;
    Double Latitude1, Longitude1;
    private Boolean isLoggingOut = false;
    private String destination;
    private String apiKey;
    LatLng destinationLatLng;
    private CoordinatorLayout coordinatorLayout;
    private EditText mSearchText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();


        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            return;//stops execution in null check
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();

        String name = user.getDisplayName();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        int color = Color.rgb(0, 188, 212);
        headerView.setBackgroundColor(color);

        mSearchText = (EditText) findViewById(R.id.inputSearch);


        textViewUserEmail = (TextView) headerView.findViewById(R.id.email_textView2);
        textViewUsername = (TextView) headerView.findViewById(R.id.name_textView2);
        //if(!TextUtils.isEmpty(name))
        // textViewUserEmail.setText("Welcome " + user.getEmail());
        textViewUserEmail.setText(user.getEmail());
        textViewUsername.setText(name);


        mFindParking = (Button) findViewById(R.id.map_find_parking);
        mFindParking.setText("Find Parking Spaces Near Me");
        mFindParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mFindParking.setText("Finding Parking Space...");
                String userId = firebaseAuth.getCurrentUser().getUid();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Find Parking Requests");
                GeoFire geoFire = new GeoFire(databaseReference);
                geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));//the child in which the information of the user is stored

                // ParkingLot1 = new LatLng(Latitude1,Longitude1);
                // mMap.addMarker(new MarkerOptions().position(ParkingLot1).title("Rainbow Parking"));

                //  GetClosestParking();
                if (!findParkingLocationsStarted) {
                    findParkingLocations();
                    mFindParking.setText("Parking Lots Found");
                }


            }
        });

/*        tabLayout = (TabLayout) findViewById(R.id.tab1);
        viewPager = (ViewPager) findViewById(R.id.viewpager_id1);

        tabAdapter = new TabAdapter(getSupportFragmentManager());
        tabAdapter.addFragment(new FragmentMapView(),null);
        //tabAdapter.addFragment(new FragmentListView(),"List View");

        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);
*/


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
        getMenuInflater().inflate(R.menu.mainactivity, menu);
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
            startActivity(new Intent(MainActivity.this, UserProfile.class));

        } else if (id == R.id.nav_find_parking) {
            finish();
            startActivity(new Intent(MainActivity.this, MainActivity.class));

        } else if (id == R.id.nav_my_vehicles) {
            finish();
            startActivity(new Intent(MainActivity.this, MyVehicles.class));
        } else if (id == R.id.nav_add_parking) {
            finish();
            startActivity(new Intent(MainActivity.this, ParkingInformation.class));


        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getLayoutInflater().getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getLayoutInflater().getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        buildGoogleApiClient();//comes before location enabled
        mMap.setMyLocationEnabled(true);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                final String markerTitle = marker.getTitle();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Parking Lots Information").child(markerTitle);
                final String markerKey = reference.getKey();
                final String parking_name = reference.getKey();

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Map<String, Object> stringObjectMap = (Map<String, Object>) dataSnapshot.getValue();

                            if (stringObjectMap.get("Number of slots") != null) {
                                String parkingSlots = stringObjectMap.get("Number of slots").toString();

                                if (markerTitle.equals(markerKey)) {
                                    final Snackbar snackbar = Snackbar.make(coordinatorLayout, "Parking: " + markerTitle + "\n" + "Available Spaces: " + parkingSlots, Snackbar.LENGTH_INDEFINITE);

                                    snackbar.setAction("CLICK HERE TO PARK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(MainActivity.this, ParkingInformation.class);
                                            //passing parking name to parking information activity
                                            intent.putExtra("Parking Name", parking_name);//pass in key value pairs

                                            startActivity(intent);
                                        }
                                    });

                                    View sbView = snackbar.getView();
                                    sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                                    TextView tvSnackbar = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                    tvSnackbar.setTextColor(Color.parseColor("#000000"));
                                    snackbar.setActionTextColor(Color.parseColor("#FF9800"));
                                    tvSnackbar.setMaxLines(7);
                                    snackbar.show();


                                }

                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getLayoutInflater().getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user == null) {
                    startActivity(new Intent(getLayoutInflater().getContext(), LoginActivity.class));

                }


            }
        };

        //will be called every second
        mLastLocation = location;//location is passed in this function
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        String user_id = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserLocation");
        GeoFire geoFire = new GeoFire(databaseReference);
        geoFire.setLocation(user_id, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));//the child in which the information of the user is stored


    }

    private Marker ParkingMarker;//Resets every time a new location is put into place a marker needs to be removed


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //when the map has been connected and the location has been requested
        //when everything is ready and working
        //create request for location from second to second
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//the highest accuracy a phone can handle and also drains a lot of battery

        if (ActivityCompat.checkSelfPermission(getLayoutInflater().getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getLayoutInflater().getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);// triggers the refreshment of the location
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void disconnect() {
        String user_id = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserLocation");
        GeoFire geoFire = new GeoFire(databaseReference);
        geoFire.removeLocation(user_id);//the child in which the information of the user is stored

    }


    @Override
    public void onStop() {
        if (!isLoggingOut) {
            firebaseAuth = FirebaseAuth.getInstance();
            final Marker parkingMarker;

            firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    if (user == null) {
                        startActivity(new Intent(getLayoutInflater().getContext(), LoginActivity.class));

                    }

                    if (user != null) {
                        if (ParkingMarker != null) {
                            ParkingMarker.remove();
                        }
                        disconnect();

                    }

                }
            };

            //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // String user_id = user.getUid();
            super.onStop();
        }


    }

    //to ensure the findParkingLocation is only called once
    boolean findParkingLocationsStarted = false;
    //create a list of the type marker
    //add markers from map and store them in list
    List<Marker> markerList = new ArrayList<Marker>();

    private void findParkingLocations() {
        findParkingLocationsStarted = true;
        DatabaseReference ParkingLocationsReference = FirebaseDatabase.getInstance().getReference().child("Parking Lots");
        GeoFire geoFire = new GeoFire(ParkingLocationsReference);
        //the coordinates of the center of the radius that is being built i.e the users location
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 10000);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //this method will always be called when the geo query event listener is called
                for (Marker markerIt : markerList) {
                    if (markerIt.getTag().equals(key)) {
                        //if a key is found that means it already exists in the list therefore will not be added to the list
                        return;
                    }
                }
                LatLng parkingLocation = new LatLng(location.latitude, location.longitude);
                //passing driver loactions
                final Marker parkingMarker = mMap.addMarker(new MarkerOptions().position(parkingLocation).title(key));
                parkingMarker.setTag(key);

                DatabaseReference referencePrice = FirebaseDatabase.getInstance().getReference().child("Parking Lots Information").child(key);
                referencePrice.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Map<String, Object> stringObjectMap = (Map<String, Object>) dataSnapshot.getValue();

                            if (stringObjectMap.get("Price") != null) {
                                String ParkingPrice = stringObjectMap.get("Price").toString();
                                parkingMarker.setSnippet("Price: KES " + ParkingPrice + " /hr");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                //add the markers to the list
                markerList.add(parkingMarker);

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

                for (Marker markerIt : markerList) {
                    if (markerIt.getTag().equals(key)) {
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                    }
                }


            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {

    }

    @Override
    public void onRoutingCancelled() {

    }

    private void init() {
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //execute our method for searching
                    geolocate();

                }

                return false;
            }
        });
    }

    private void geolocate() {
        Log.d(TAG, "geoLocate: geolocating");
        String mSearchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(MainActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(mSearchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geolocate:IOException: " + e.getMessage());

        }

        if (list.size() > 0) {
            Address address = list.get(0);
        }
        //Log.d(TAG, "geoLocate: found a location: "+addre);
    }
}