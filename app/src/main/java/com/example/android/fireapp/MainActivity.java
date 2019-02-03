package com.example.android.fireapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

        private EditText email_signup;
        private EditText password_signup;
        private EditText confirmPass_signup;
        private Button signUpButton;
        private TextView loginTextView;
        private TextView signupTextview;


    private FirebaseAuth mAuth;
// ...
// Initialize Firebase Auth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email_signup = findViewById(R.id.editText_emailSignup);
        password_signup = findViewById(R.id.editText_passwordSignup);
        confirmPass_signup = findViewById(R.id.editText_passConfirm);
        signUpButton = findViewById(R.id.SignupButton);
        mAuth = FirebaseAuth.getInstance();
        loginTextView = findViewById(R.id.LoginTextView);
        signupTextview = findViewById(R.id.SignupTextView);

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),LoginActivity.class);
                startActivity(intent);
            }

        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_signup.getText().toString();
                String password = password_signup.getText().toString();
                String confirmPassword = confirmPass_signup.getText().toString();

                if (!email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty())
                {
                    if (confirmPassword.equals(password))
                    {
                        mAuth.createUserWithEmailAndPassword(email,password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(MainActivity.this, "Account succesfully created", Toast.LENGTH_SHORT).show();
                                            toAccountSetupActivity();
                                        }
                                        else
                                        {
                                            String errorMessage = task.getException().getMessage();
                                            Toast.makeText(MainActivity.this, "Error : " + errorMessage, Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Confirm password does not match password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    private void toAccountSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetupAccount.class);
        startActivity(setupIntent);
        finish();
    }
}
