package com.example.smartparking;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
  private Button register;
  private EditText email;
  private EditText password;
  private Button login;
  private FirebaseAuth firebaseAuth;
  private FirebaseAuth.AuthStateListener firebaseAuthListener;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
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

    //initialize views

    register = (Button) findViewById(R.id.button_register);
    email = (EditText) findViewById(R.id.login_email);
    password = (EditText) findViewById(R.id.login_password);
    login = (Button) findViewById(R.id.button_signin);


    register.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
      }
    });

    login.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        userLogin();
      }
    });

  }
  private void userLogin()
  {
    String get_email = email.getText().toString().trim();
    String get_password = password.getText().toString().trim();

    if (TextUtils.isEmpty(get_email))
    {
      //email is empty
      //Toast.makeText(this,"Please enter your email address",Toast.LENGTH_SHORT).show();
      email.setError("Email is required");
      //stop function from executing further
      return;
    }

    if (TextUtils.isEmpty(get_password))
    {
      //password is empty
      //Toast.makeText(this,"Please enter your password",Toast.LENGTH_SHORT).show();
      password.setError("Password is required");
      //stop function from executing further
      return;
    }
    if(!isEmail(email))
    {
      email.setError("Please enter a valid email address");
    }

    firebaseAuth.signInWithEmailAndPassword(get_email,get_password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {

                  Toast.makeText(LoginActivity.this,"You have logged in sucessfully",Toast.LENGTH_SHORT).show();
                  finish();
                  startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
                else{
                  Toast.makeText(LoginActivity.this,"Could not sign in. Please try again",Toast.LENGTH_SHORT).show();
                }
              }
            });

  }

  boolean isEmail(EditText editText)
  {
    CharSequence email = editText.getText().toString();
    return (Patterns.EMAIL_ADDRESS.matcher(email).matches());
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
