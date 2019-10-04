package com.example.smartparking;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Button;

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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FragmentMapView extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, RoutingListener {
  View view;
  private GoogleMap mMap;
  private FirebaseAuth firebaseAuth;
  private FirebaseAuth.AuthStateListener firebaseAuthListener;
  MapView mapView;
  GoogleApiClient mGoogleApiClient;
  Location mLastLocation;
  LocationRequest mLocationRequest;
  private Button mLogout;
  private Button mFindParking;
  LatLng ParkingLot1;
  Double Latitude1,Longitude1;
  private Boolean isLoggingOut = false;

  public FragmentMapView() {
  }

  @Nullable

  public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    view = layoutInflater.inflate(R.layout.activity_maps, container, false);
    firebaseAuth = FirebaseAuth.getInstance();

    firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null)
        {

          startActivity(new Intent(getLayoutInflater().getContext(), LoginActivity.class));

        }

      }
    };

    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
            .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

/*        mLogout = (Button) view.findViewById(R.id.map_logout);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //before signing out disconnection has to happen
                isLoggingOut = true;
                disconnect();
                firebaseAuth.signOut();
                startActivity(new Intent(getLayoutInflater().getContext(), LoginActivity.class));

            }
        });*/
    mFindParking = (Button) view.findViewById(R.id.map_find_parking);
    mFindParking.setText("Find Parking");
    mFindParking.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        mFindParking.setText("Finding Parking Space...");
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Find Parking Requests");
        GeoFire geoFire = new GeoFire(databaseReference);
        geoFire.setLocation(userId,new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()));//the child in which the information of the user is stored

        // ParkingLot1 = new LatLng(Latitude1,Longitude1);
        // mMap.addMarker(new MarkerOptions().position(ParkingLot1).title("Rainbow Parking"));

        //  GetClosestParking();
        if (!findParkingLocationsStarted)
        {
          findParkingLocations();
          mFindParking.setText("Parking Lots Found");
        }


      }
    });


    return view;
  }
  /*    private int radius=1;
      private Boolean ParkingFound = false; // if parking is found, if so the id
      private String ParkingFoundName;

      private void GetClosestParking()
      {
          DatabaseReference ParkingLotLocation = FirebaseDatabase.getInstance().getReference().child("Parking Lots");
          GeoFire geoFire = new GeoFire(ParkingLotLocation);

          //use this query to create radius around customer's request
          GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()),radius);
          geoQuery.removeAllListeners();//a precaution in case a parking lot location changes

          geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
              @Override
              public void onKeyEntered(String key, GeoLocation location) {
                  // any time parking lot is found within the radius, this function is called
                  //If a parking has been found we can say parking = true

                  if (!ParkingFound)
                  {
                      ParkingFound = true;
                      ParkingFoundName=key;

                      getParkingLocation();
                      mFindParking.setText("Looking for Parking Lot Location");
                  }
  *//*                if (radius<2)
                {
                    //the radius grows by 1 km then 2 then 3...
                    radius++;
                    //then function starts over again
                    GetClosestParking();

                }*//*

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                //if parking was not found. i.e onKey Entered was not called
                if (!ParkingFound)
                {
                    //the radius grows by 1 km then 2 then 3...
                    radius++;
                    //then function starts over again
                    GetClosestParking();

                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });


    }*/
  private Marker ParkingMarker;//Resets every time a new location is put into place a marker needs to be removed
  /*  private void getParkingLocation()
   {
       DatabaseReference ParkingSpotLocation = FirebaseDatabase.getInstance().getReference().child("Parking Lots").child(ParkingFoundName).child("l");
       ParkingSpotLocation.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               //if datasnapshot does not exist then the app will crash
               if (dataSnapshot.exists())
               {
                   List<Object> map = (List<Object>) dataSnapshot.getValue();//map will store everything that is gotten from datasnapshot into a list
                   double LocationLat = 0;
                   double LocationLng = 0;

                   mFindParking.setText("Parking Lots Found...");
                   if (map.get(0)!=null)
                   {
                       LocationLat = Double.parseDouble(map.get(0).toString());

                   }
                   if (map.get(1)!=null)
                   {
                       LocationLng = Double.parseDouble(map.get(1).toString());

                   }
                   LatLng ParkingLatLng = new LatLng(LocationLat,LocationLng);
                 if (ParkingMarker != null)
                   {
                       ParkingMarker.remove();
                   }
                   Location location1 = new Location("");
                   location1.setLatitude(mLastLocation.getLatitude());
                   location1.setLongitude(mLastLocation.getLongitude());

                   Location location2 = new Location("");
                   location2.setLatitude(ParkingLatLng.latitude);
                   location2.setLongitude(ParkingLatLng.longitude);
                   float distance = location1.distanceTo(location2);
                   float distance1 = distance/1000;
                   float final_distance = roundTwoDecimals(distance1);
                   float final_distance_metres = roundTwoDecimals(distance);
                   if (distance<100)
                   {
                       mFindParking.setText("Parking Lots Found... "+String.valueOf(final_distance_metres)+ " metres away");
                   }
                   else {
                       mFindParking.setText("Parking Lots Found... " + String.valueOf(final_distance) + " KM away");
                   }




                  // ParkingMarker = mMap.addMarker(new MarkerOptions().position(ParkingLatLng).title(ParkingFoundName).icon(BitmapDescriptorFactory.fromResource(R.drawable.parking_marker_location)));
                   ParkingMarker = mMap.addMarker(new MarkerOptions().position(ParkingLatLng).title(ParkingFoundName));
               }
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
   }*/
  float roundTwoDecimals(float d)
  {
    DecimalFormat twoDForm = new DecimalFormat("#.##");
    return Float.valueOf(twoDForm.format(d));
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
  }
  protected synchronized void buildGoogleApiClient()
  {
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

        if (user == null)
        {
          startActivity(new Intent(getLayoutInflater().getContext(), LoginActivity.class));

        }




      }
    };

    //will be called every second
    mLastLocation = location;//location is passed in this function
    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
    String user_id = firebaseAuth.getCurrentUser().getUid();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserLocation");
    GeoFire geoFire = new GeoFire(databaseReference);
    geoFire.setLocation(user_id,new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()));//the child in which the information of the user is stored





  }


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
    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);// triggers the refreshment of the location
  }

  @Override
  public void onConnectionSuspended(int i) {

  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

  }


  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }
  private void disconnect()
  {
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
            if (ParkingMarker != null)
            {
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

  private void findParkingLocations()
  {
    findParkingLocationsStarted = true;
    DatabaseReference ParkingLocationsReference = FirebaseDatabase.getInstance().getReference().child("Parking Lots");
    GeoFire geoFire = new GeoFire(ParkingLocationsReference);
    //the coordinates of the center of the radius that is being built i.e the users location
    GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()),10000);
    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
      @Override
      public void onKeyEntered(String key, GeoLocation location) {
        //this method will always be called when the geo query event listener is called
        for (Marker markerIt : markerList)
        {
          if (markerIt.getTag().equals(key)) {
            //if a key is found that means it already exists in the list therefore will not be added to the list
            return;
          }
        }
        LatLng parkingLocation = new LatLng(location.latitude,location.longitude);
        //passing driver loactions
        Marker parkingMarker = mMap.addMarker(new MarkerOptions().position(parkingLocation).title(key));
        parkingMarker.setTag(key);
        //add the markers to the list
        markerList.add(parkingMarker);

      }

      @Override
      public void onKeyExited(String key) {

      }

      @Override
      public void onKeyMoved(String key, GeoLocation location) {

        for (Marker markerIt : markerList)
        {
          if (markerIt.getTag().equals(key)) {
            markerIt.setPosition(new LatLng(location.latitude,location.longitude));
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

}