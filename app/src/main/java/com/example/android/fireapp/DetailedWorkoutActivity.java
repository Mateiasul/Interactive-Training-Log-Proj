package com.example.android.fireapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class DetailedWorkoutActivity extends AppCompatActivity {

    private TextView usernameTV;
    private TextView dateTV;
    private TextView titleTV;
    private TextView durationTV;
    private TextView effortLevelTV;
    private TextView typeTV;
    private TextView timeTV;
    private FirebaseAuth mFireBaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private String userID;
    private String workoutDocRef;
    private String workoutEventsDocRef;
    private Boolean isCoach;




    //inflate toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detailed_workout_menu, menu);
        return true;
    }

    //when an option from the menu is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.edit_workout_item:
                goToEditWorkoutActivity();
                return  true;

            case R.id.delete_wokout_item:
                return  true;

        }
        return false;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_workout);

        usernameTV = findViewById(R.id.userNameTV);
        dateTV = findViewById(R.id.workoutDateTV);
        titleTV = findViewById(R.id.titleTV);
        durationTV = findViewById(R.id.durationTV);
        effortLevelTV = findViewById(R.id.effortTV);
        typeTV = findViewById(R.id.workoutTypeTV);
        timeTV = findViewById(R.id.timeTV);
        mFireBaseAuth = mFireBaseAuth.getInstance();
        userID = mFireBaseAuth.getCurrentUser().getUid();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        final Toolbar toolbar = findViewById(R.id.toolbar);
        Intent myIntent = getIntent(); // gets the previously created intent
        workoutDocRef = myIntent.getStringExtra("workoutDocRef");
        workoutEventsDocRef = myIntent.getStringExtra("workoutEventsDocRef");
        isCoach = myIntent.getBooleanExtra("isCoach",false);
        //get from the passed document ref, all the necessary data to populate activity

        //if the currently logged user is a coach, use the EVENTS ID of the document to display full details
        //tooldbar not set for coach - hard to figure + coach should not be able to alter other user's logs
        if(isCoach)
        {
             mFirebaseFirestore.collection("Events").document(workoutEventsDocRef)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful())
                        {
                            //populate textfields with the current document data
                            setTextForTVFields(task, toolbar);

                        }
                    }
             });
        }
        else
        {
            //if currently logged user is not a coach then use the user/events id to show full details
            //also set the toolbar to allow further editing
            setSupportActionBar(toolbar);
            mFirebaseFirestore.collection("Users").document(userID)
                    .collection("Events").document(workoutDocRef)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        //populate textfields with the current document data
                        setTextForTVFields(task, toolbar);


                    }
                }
            });
        }



    }

    private void setTextForTVFields(@NonNull Task<DocumentSnapshot> task, Toolbar toolbar) {
        DocumentSnapshot document = task.getResult();
        titleTV.setText(document.get("Training Title").toString());
        dateTV.setText(document.get("Training Date").toString());
        timeTV.setText(document.get("Training Time").toString());
        typeTV.setText(document.get("Training Type").toString());
        durationTV.setText(document.get("Training Duration").toString());
        usernameTV.setText(document.get("User Name").toString());
        effortLevelTV.setText(document.get("Effort Level").toString());
        toolbar.setTitle(document.get("Training Title").toString());
    }


    private void goToEditWorkoutActivity() {
        //initiate the edit activity, sending necessary doc reference
        Intent intent = new Intent(DetailedWorkoutActivity.this, EditWorkoutActivity.class);
        intent.putExtra("workoutDocRef",workoutDocRef);
        intent.putExtra("workoutEventsDocRef",workoutEventsDocRef);
        startActivity(intent);
        //end current activity
        finish();
    }

}


