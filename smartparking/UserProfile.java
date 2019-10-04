package com.example.smartparking;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
  private FirebaseAuth firebaseAuth;
  TextView FirstName, LastName, PhoneNumber, EmailAddress, NumberPlate;
  private String userId;
  private DatabaseReference databaseReference;//user database
  private Button finish;
  private String ProfileName;
  private String ProfileLastName;
  private String ProfilePhone;
  private String ProfileEmail;
  private String ProfileImageUrl;
  private ImageView imageViewProfile;
  private Uri resultUri;
  private TextView textViewUserEmail,textViewUsername;




  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_profile);
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
    textViewUserEmail = (TextView) headerView.findViewById(R.id.email_textView7);
    textViewUsername = (TextView) headerView.findViewById(R.id.name_textView8);
    //if(!TextUtils.isEmpty(name))
    // textViewUserEmail.setText("Welcome " + user.getEmail());
    textViewUserEmail.setText(user.getEmail());
    textViewUsername.setText(name);

    //Text View initialization
    FirstName = (TextView) findViewById(R.id.user_profile_name);
    LastName = (TextView) findViewById(R.id.user_profile_last_name);
    PhoneNumber = (TextView) findViewById(R.id.user_profile_phone);
    EmailAddress = (TextView) findViewById(R.id.user_profile_email);


    imageViewProfile = (ImageView) findViewById(R.id.imageViewProfile);

    //Button initialization
    finish = (Button) findViewById(R.id.button_finish);

    userId = firebaseAuth.getCurrentUser().getUid();
    databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userId);

    UserInfo();

    imageViewProfile.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        //let request code for opening gallery be 1
        startActivityForResult(intent,1);//we just dont want to start the gallery activity we want something from it
      }
    });


    FirstName.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
        LayoutInflater layoutInflater = LayoutInflater.from(UserProfile.this);
        final View dialogView = layoutInflater.inflate(R.layout.activity_custom_dialog,null);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        final EditText editTextDialog = (EditText) dialogView.findViewById(R.id.edit1);

        builder.setTitle("Enter Details");
        builder.setMessage("Enter First Name Below");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            String EditFirstName = editTextDialog.getText().toString().trim();

            FirstName.setText(EditFirstName);



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
    LastName.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
        LayoutInflater layoutInflater = LayoutInflater.from(UserProfile.this);
        final View dialogView = layoutInflater.inflate(R.layout.activity_custom_dialog,null);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        final EditText editTextDialog = (EditText) dialogView.findViewById(R.id.edit1);
        // LastName = (TextView) findViewById(R.id.user_profile_last_name);


        builder.setTitle("Enter Details");
        builder.setMessage("Enter Last Name Below");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            String EditLastName = editTextDialog.getText().toString().trim();

            LastName.setText(EditLastName);


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
    PhoneNumber.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
        LayoutInflater layoutInflater = LayoutInflater.from(UserProfile.this);
        final View dialogView = layoutInflater.inflate(R.layout.activity_custom_dialog,null);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        final EditText editTextDialog = (EditText) dialogView.findViewById(R.id.edit1);
        // LastName = (TextView) findViewById(R.id.user_profile_last_name);


        builder.setTitle("Enter Details");
        builder.setMessage("Enter Phone Number Below");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            String EditPhoneNumber = editTextDialog.getText().toString().trim();

            PhoneNumber.setText(EditPhoneNumber);


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
    EmailAddress.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
        LayoutInflater layoutInflater = LayoutInflater.from(UserProfile.this);
        final View dialogView = layoutInflater.inflate(R.layout.activity_custom_dialog,null);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        final EditText editTextDialog = (EditText) dialogView.findViewById(R.id.edit1);
        // LastName = (TextView) findViewById(R.id.user_profile_last_name);


        builder.setTitle("Enter Details");
        builder.setMessage("Enter EmailAddress Below");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            String EditEmailAddress = editTextDialog.getText().toString().trim();

            EmailAddress.setText(EditEmailAddress);


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

    finish.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        SaveUserInfo();

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
    getMenuInflater().inflate(R.menu.user_profile, menu);
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
      startActivity(new Intent(UserProfile.this,UserProfile.class));

    } else if (id == R.id.nav_find_parking) {
      finish();
      startActivity(new Intent(UserProfile.this,MainActivity.class));

    } else if (id == R.id.nav_add_parking) {


    } else if (id == R.id.nav_share) {

    } else if (id == R.id.nav_send) {

    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }
  private void showDialog()
  {


  }
  private void UserInfo()
  {
    databaseReference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
        {
          Map<String,Object> stringObjectMap = (Map<String, Object>) dataSnapshot.getValue();
          if (stringObjectMap.get("FirstName")!=null)
          {
            ProfileName = stringObjectMap.get("FirstName").toString();
            FirstName.setText(ProfileName);
          }
          if (stringObjectMap.get("LastName")!=null)
          {
            ProfileLastName = stringObjectMap.get("LastName").toString();
            LastName.setText(ProfileLastName);
          }
          if (stringObjectMap.get("PhoneNumber")!=null)
          {
            ProfilePhone = stringObjectMap.get("PhoneNumber").toString();
            PhoneNumber.setText(ProfilePhone);
          }
          if (stringObjectMap.get("Email")!=null)
          {
            ProfileEmail = stringObjectMap.get("Email").toString();
            EmailAddress.setText(ProfileEmail);
          }


          if (stringObjectMap.get("ProfileImageUrl")!=null)
          {
            ProfileImageUrl = stringObjectMap.get("ProfileImageUrl").toString();
            Glide.with(getApplication()).load(ProfileImageUrl).into(imageViewProfile);
          }
        }

      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });
  }
  private void SaveUserInfo()
  {
    ProfileName = FirstName.getText().toString();
    ProfileLastName = LastName.getText().toString();
    ProfilePhone = PhoneNumber.getText().toString();
    ProfileEmail = EmailAddress.getText().toString();

    //using hash map cause many items are being bundled up together in order to be saved to the same location
    Map userInfo = new HashMap();

    userInfo.put("FirstName",ProfileName);
    userInfo.put("LastName",ProfileLastName);
    userInfo.put("PhoneNumber",ProfilePhone);
    userInfo.put("Email",ProfileEmail);


    databaseReference.updateChildren(userInfo);

    if (resultUri!=null)
    {
      //this where all profile images are going to be stored
      StorageReference filePath = FirebaseStorage.getInstance().getReference("ProfileImages").child(userId);
      Bitmap bitmap = null;
      try {
        //passes the result uri string int o birmap i.e. gets the image from the result uri location
        bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUri);
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
      //compress image
      ByteArrayOutputStream compressedImage = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.JPEG,20,compressedImage);
      //move image into an array
      byte[] data = compressedImage.toByteArray();
      //create an upload task to upload image into storage
      UploadTask uploadTask = filePath.putBytes(data);

      uploadTask.addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          Toast.makeText(UserProfile.this,"Image upload failed",Toast.LENGTH_SHORT).show();
          finish();

        }
      });
      uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
          Uri downloadUrl = taskSnapshot.getDownloadUrl();
          Map newImage = new HashMap();
          newImage.put("ProfileImageUrl",downloadUrl.toString());
          databaseReference.updateChildren(newImage);
          finish();
          return;


        }
      });



    }
    else {
      finish();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode ==1 && resultCode == Activity.RESULT_OK)
    {
      final Uri imageUri = data.getData();
      resultUri = imageUri;
      imageViewProfile.setImageURI(resultUri);

    }
  }
}
