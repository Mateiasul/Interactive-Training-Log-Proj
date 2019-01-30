package com.example.android.fireapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class StartActivity extends AppCompatActivity {

    private Button logoutButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userId;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topbar_support_menu,menu);
        return  true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mateiasul's App");
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        if(currentUser == null)
        {
            toLoginActivity();
        }
        else{
            userId = mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        if (!task.getResult().exists())
                        {
                            toAccountSetupActivity();
                        }
                    }
                }
            });
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logoutItem:
                mAuth.signOut();
                toLoginActivity();

            case R.id.accountSetupItem:
                toAccountSetupActivity();
        }
        return true;
    }
    private void toLoginActivity() {
        Intent loginIntent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void toAccountSetupActivity() {
        Intent setupIntent = new Intent(StartActivity.this, SetupAccount.class);
        startActivity(setupIntent);
    }
}
