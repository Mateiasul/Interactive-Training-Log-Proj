package com.example.android.fireapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.android.fireapp.AccountFragment;
import com.example.android.fireapp.Challenges.ChallengesFragment;
import com.example.android.fireapp.Challenges.TabbedChallengeFragment;
import com.example.android.fireapp.Dashboard.TabbedDashFragment;
import com.example.android.fireapp.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNav;
    private FrameLayout mFrameLayout;
    //private DashFragment dashFragment;
    private ChallengesFragment challengesFragment;
    private AccountFragment accountFragment;
    private TabbedDashFragment tabbedDashFragment;
    private TabbedChallengeFragment tabbedChallengeFragment;
    //to be deleted

    /*
    private AddFragment addFragment;
*/
    private FirebaseFirestore firebaseFirestore;
    private String userId;
    private Button logoutButton;
    private FirebaseAuth mAuth;
    private boolean mShowingBack;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topbar_support_menu,menu);
        return  true;
    }

    @Override
    protected void onStart()
    {
        //on activity start, if user doesn t exist, send to login page
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        if(currentUser == null)
        {
            toLoginActivity();
        }
        else{
            //if data for the current user is inexistent send the user straight to the account setup activity
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);
        mFrameLayout = findViewById(R.id.frame_layout);
        mBottomNav = findViewById(R.id.main_nav_bar);
        //dashFragment = new DashFragment();
        challengesFragment = new ChallengesFragment();
        tabbedDashFragment = new TabbedDashFragment();
        tabbedChallengeFragment = new TabbedChallengeFragment();

        accountFragment = new AccountFragment();

        /*
        addFragment = new AddFragment();
*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mateiasul's App");
        mAuth = FirebaseAuth.getInstance();
        selectFragment(tabbedDashFragment,"Dashboard");
        Intent myIntent = getIntent(); // gets the previously created intent
        if(myIntent.hasExtra("Fragment"))
        {
            String initFragment = myIntent.getStringExtra("Fragment");
            if(initFragment.equals("Challenges"))
            {
                selectFragment(tabbedChallengeFragment,"Challenges");
            }
        }


    /*    if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.cont, new DashFragment())
                    .commit();
        }*/

        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.nav_dashboard:
                        selectFragment(tabbedDashFragment,"Dashboard");
                        return  true;

                    case R.id.nav_challanges:
                        selectFragment(tabbedChallengeFragment,"Challenges");
                        return  true;

          /*          case R.id.nav_add:
                        selectFragment(addFragment,"Log Training");
                        return  true;*/

                    case R.id.nav_account:
                        selectFragment(accountFragment,"Account Setup");
                        return  true;

                        default: return  false;
                }

            }
        });
    }

    private void flipCard() {
        if (mShowingBack) {
            getSupportFragmentManager().popBackStack();
            return;
        }

        // Flip to the back.

        mShowingBack = true;

        // Create and commit a new fragment transaction that adds the fragment for
        // the back of the card, uses custom animations, and is part of the fragment
        // manager's back stack.

        getSupportFragmentManager()
                .beginTransaction()

                // Replace the default fragment animations with animator resources
                // representing rotations when switching to the back of the card, as
                // well as animator resources representing rotations when flipping
                // back to the front (e.g. when the system Back button is pressed).
                .setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out)

                // Replace any fragments currently in the container view with a
                // fragment representing the next page (indicated by the
                // just-incremented currentPage variable).
                .replace(R.id.container, new AccountFragment())

                // Add this transaction to the back stack, allowing users to press
                // Back to get to the front of the card.
                .addToBackStack(null)

                // Commit the transaction.
                .commit();
    }

    private void selectFragment(Fragment fragment, String title)
    {
        //flipCard();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logoutItem:
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                toLoginActivity();
                return  true;

            case R.id.accountSetupItem:
                toAccountSetupActivity();
                return  true;


        }
        return false;
    }
    private void toLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
    //send
    private void toAccountSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetupAccount.class);
        startActivity(setupIntent);
    }
}
