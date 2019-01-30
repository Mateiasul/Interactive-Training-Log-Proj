package com.example.android.fireapp;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
//Todo cross fade transition between fragments
public class DashFragment extends Fragment {


    public DashFragment() {
        // Required empty public constructor
    }
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

        //TODO if no events
        mFirebaseFirestore.collection("Users").document(userID).collection("Events")
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
                                    String date = document.get("Training Date").toString();
                                    String time = document.get("Training Time").toString();
                                    String type = document.get("Training Type").toString();
                                    int effortLevel =(int) document.get("Effort Level");

                                    ActivityLogs log = new ActivityLogs(title, type, new Date(), time, effortLevel);
                                    logs.add(log);
                                }
                                CustomAdapter customAdapter = new CustomAdapter();
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
            TextView timeTV = view.findViewById(R.id.challangesTime);
            TextView dateTV = view.findViewById(R.id.challangesDate);

            titleTV.setText(logs.get(i).getActivityTitle());
            timeTV.setText(logs.get(i).getActivityTime());
            dateTV.setText(logs.get(i).getActivityDate());
            typeTV.setText(logs.get(i).getActivityType());

            return view;
        }
    }

  /*  public void onAddField(View v)
   {
        Context context = getActivity();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.dashboard_fragment_log, null);
        dashboardLayout.addView(rowView, dashboardLayout.getChildCount() - 1);

    }

    public void addPlaces() {
        Button button = new Button(getActivity()); // needs activity context
        button.setText("button name");
        dashboardLayout.addView(button);
    }
*/
}
