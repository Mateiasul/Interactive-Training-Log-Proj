package com.example.android.fireapp.Activities;

//this activity represents the login page
//it's made of a couple edit fields, email- password
//and ways to sign in/up

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.fireapp.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {
    private TextView signupTextview;
    private FirebaseAuth mAuth;
    private Button loginButton;
    private EditText emailLogin;
    private EditText passwordLogin;
    private CallbackManager mCallBackManager;

//fires when the system first creates the activity.
//In the onCreate() method, you perform
// basic application startup logic that should happen only once for the entire life of the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //initiate vars
        mCallBackManager = CallbackManager.Factory.create();
        signupTextview = findViewById(R.id.SignupTextView);
        mAuth = FirebaseAuth.getInstance();
        loginButton = findViewById(R.id.LoginButton);
        emailLogin = findViewById(R.id.editTextLoginEmail);
        passwordLogin = findViewById(R.id.editTextPasswordLogin);



        //when login button is clicked
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        //get user credentials
                String email = emailLogin.getText().toString();
                String password = passwordLogin.getText().toString();
                //if user entered both
                if (!email.isEmpty() && !password.isEmpty())
                {
                    //check with FireBase if credentials match - when  complete
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                //sent app to default start page
                                SendToStart();
                            }
                            else
                            {
                                //get the error message and display it for the user as a TOAST
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Authentication failed " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        //listener for the signup button
        signupTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when clicked send user to sign up screen
                Intent intent = new Intent(view.getContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });


        // Initialize Facebook Login button
        LoginButton loginButton = findViewById(R.id.login_button_facebook);
        loginButton.setReadPermissions("email", "public_profile");

        loginButton.registerCallback(mCallBackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                // ...
            }
        });
        // ...


    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            SendToStart();

                        } else {
                            String errorMsg = task.getException().getMessage();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed: " + errorMsg ,
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallBackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onStart() {
        super.onStart();
        //check if user is already authenticated
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
        {
            //if yes send user to initial page
            SendToStart();
        }
        else
        {
            //TODO error handling
        }

    }

    //send to initial screen
    private void SendToStart() {
        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
