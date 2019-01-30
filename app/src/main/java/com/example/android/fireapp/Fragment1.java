package com.example.android.fireapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.fireapp.ActivityLogs;
//import com.example.android.fireapp.DashFragment;
import com.example.android.fireapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Fragment1 extends Fragment {
//TODO display fragments in order - most recent to least recent
    private FirebaseAuth mFireBaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private View v;
    private FrameLayout frameLayout;
    private View testInflate;
    private View testView;
    private ListView listView;
    private List<ActivityLogs> logs;
    private View mContentView;
    private View mLoadingView;
    private int mShortAnimationDuration;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFireBaseAuth = mFireBaseAuth.getInstance();
        final String userID = mFireBaseAuth.getCurrentUser().getUid();
        mFirebaseFirestore = FirebaseFirestore.getInstance();


        logs = new ArrayList<ActivityLogs>();
        v = inflater.inflate(R.layout.fragment_dash2, container, false);
        listView = v.findViewById(R.id.dashListView);

        //retrieve all workouts from a particular user ordering them starting with the most recent one
        //TODO if no events



        mFirebaseFirestore.collection("Users").document(userID).collection("Events")
                .orderBy("Training Date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //TODO if empty
                            //TODO orderby date desc
                            if (!task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("EVS", document.getId() + " => " + document.getData());
                                    String title = document.get("Training Title").toString();
                                    Date date = (Date) document.get("Training Date");
                                    String time = document.get("Training Time").toString();
                                    String type = document.get("Training Type").toString();
                                    String duration = document.get("Training Duration").toString();
                                    //requires casting due to FireStore cloud storing the value as Number
                                    //casting to Long  - which extends number
                                    int effortLevel = ((Long) document.get("Effort Level")).intValue();


                                    DateFormat df = new SimpleDateFormat("MM-dd-yyyy");

                                    ActivityLogs log = new ActivityLogs(title, type, date,
                                                                 time,duration, effortLevel);
                                    logs.add(log);
                                }
                                Fragment1.CustomAdapter customAdapter = new Fragment1.CustomAdapter();
                                listView.setAdapter(customAdapter);

                            } else {
                                Log.d("EVS", "Error getting documents: ", task.getException());
                            }
                        }
                    }
                });




        return v;

    }





    class CustomAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return logs.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.dashbord_activities_layout,null);
            TextView titleTV = view.findViewById(R.id.challangesTitle);
            TextView typeTV = view.findViewById(R.id.challangesType);
            TextView timeTV = view.findViewById(R.id.trainingActivityTime);
            TextView dateTV = view.findViewById(R.id.challangesDate);
            TextView durationTV = view.findViewById(R.id.trainingActivityDuration);

            titleTV.setText(logs.get(i).getActivityTitle());
            timeTV.setText(logs.get(i).getActivityTime());
            dateTV.setText(logs.get(i).getActivityDate());
            typeTV.setText(logs.get(i).getActivityType());
            durationTV.setText(logs.get(i).getActivityDuration());

            return view;
        }
    }
}
