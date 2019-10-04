package com.example.smartparking;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
  private Button signin;
  private EditText email,username;
  private EditText password;
  private EditText confirm_password;
  private Button register;
  private FirebaseAuth firebaseAuth;
  private FirebaseAuth.AuthStateListener firebaseAuthListener;
//SpotsDialog loading_dialog;
//private static final int DIALOG_SHOW_TIME = 3000;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    firebaseAuth = FirebaseAuth.getInstance();

    firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null)
        {

          startActivity(new Intent(getApplicationContext(), MainActivity.class));
          finish();
          return;
        }

      }
    };

    //loading_dialog = new SpotsDialog(this);


    //intialize views

    signin = (Button) findViewById(R.id.button_signin);
    username = (EditText) findViewById(R.id.register_username);
    email = (EditText) findViewById(R.id.register_email);
    password = (EditText) findViewById(R.id.register_password);
    confirm_password = (EditText) findViewById(R.id.register_confirm_password);
    register = (Button) findViewById(R.id.button_registration);



    register.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        registerUser();
      }
    });

    signin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
      }
    });



  }
  private void registerUser()
  {
    String get_email = email.getText().toString().trim();
    String get_password = password.getText().toString().trim();
    String get_confirm_password = confirm_password.getText().toString().trim();

    if (TextUtils.isEmpty(get_email))
    {
      //email is empty
      Toast.makeText(this,"Please enter your email address",Toast.LENGTH_SHORT).show();
      //stop function from executing further
      return;
    }

    if (TextUtils.isEmpty(get_password))
    {
      //password is empty
      Toast.makeText(this,"Please enter your password",Toast.LENGTH_SHORT).show();
      //stop function from executing further
      return;
    }

    if (TextUtils.isEmpty(get_confirm_password))
    {
      //confirm_password is empty
      Toast.makeText(this,"Please confirm your password",Toast.LENGTH_SHORT).show();
      //stop function from executing further
      return;
    }
    if (!get_password.equals(get_confirm_password))
    {
      Toast.makeText(this,"Yours passwords do not match",Toast.LENGTH_SHORT).show();
      return;

    }
    //if validations are ok then register user
    // registration is not local, therefore a progress bar will be used

    //loading_dialog.show();

    firebaseAuth.createUserWithEmailAndPassword(get_email,get_password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
      @Override
      public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful())
        {
          //user is successfully registered
          String user_id = firebaseAuth.getCurrentUser().getUid();
          DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_id);
          current_user_db.setValue(true);
          userProfile();
          Toast.makeText(RegisterActivity.this,"You have registered sucessfully",Toast.LENGTH_SHORT).show();
          finish();
          startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }
        else{
          Toast.makeText(RegisterActivity.this,"Could not register. Please try again",Toast.LENGTH_SHORT).show();
        }
      }
    });


  }

  private void userProfile()
  {
    FirebaseUser user = firebaseAuth.getCurrentUser();

    if (user!=null)
    {
      UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
              .setDisplayName(username.getText().toString().trim()).build();

      user.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {

        }
      });


    }

  }

  @Override
  protected void onStart() {
    super.onStart();
    firebaseAuth.addAuthStateListener(firebaseAuthListener);
  }

  @Override
  protected void onStop() {
    super.onStop();
    firebaseAuth.removeAuthStateListener(firebaseAuthListener);
  }
}
