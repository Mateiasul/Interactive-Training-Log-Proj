package com.example.android.fireapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.android.fireapp.DashFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Fragment1 extends Fragment {
//TODO display fragments in order - most recent to least recent
    private FirebaseAuth mFireBaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private View v;

    private ListView listView;
    private List<ActivityLogs> logs;

    private ActionMode mActionMode;
    private int selectedItem = -1;
    private CustomAdapter customAdapter;
    private String userID;
    private String docID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFireBaseAuth = mFireBaseAuth.getInstance();
        userID = mFireBaseAuth.getCurrentUser().getUid();
        mFirebaseFirestore = FirebaseFirestore.getInstance();


        logs = new ArrayList<ActivityLogs>();
        v = inflater.inflate(R.layout.fragment_dash1, container, false);

        customAdapter = new CustomAdapter();
        listView = v.findViewById(R.id.dashListView);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //i represents the selected's item position - starts from 0
                Toast.makeText(getActivity(), "long click toast ", Toast.LENGTH_SHORT).show();
                //when click register item id
                selectedItem = i;
                //start action mode
                mActionMode = getActivity().startActionMode(mActionModeCallback1);
                return true;
            }
        });

        //retrieve all workouts from a particular user ordering them starting with the most recent one
        //TODO if no events
        mFirebaseFirestore.collection("Users").document(userID).collection("Events")
                .orderBy("Training Date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //TODO if empty - done
                            //TODO orderby date desc - done
                            if (!task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //for each document(workout) store its data
                                    Log.d("EVS", document.getId() + " => " + document.getData());
                                    String title = document.get("Training Title").toString();
                                    Date date = (Date) document.get("Training Date");
                                    String time = document.get("Training Time").toString();
                                    String type = document.get("Training Type").toString();
                                    String duration = document.get("Training Duration").toString();
                                    String userName = document.get("Training Duration").toString();
                                    String docRef = document.getId();
                                    //requires casting due to FireStore cloud storing the value as Number
                                    //casting to Long  - which extends number
                                    int effortLevel = ((Long) document.get("Effort Level")).intValue();
                                    DateFormat df = new SimpleDateFormat("MM-dd-yyyy");

                                    ActivityLogs log = new ActivityLogs(title, type, date,
                                                                 time,duration, effortLevel,docRef,userName);
                                    //map of all the logged activities
                                    logs.add(log);
                                }
                                //set the adapter for the list view which basically acts as a bridge
                                //between the data and the view
                                //TODO - start using VIEWHOLDER for extra efficiency
                                listView.setAdapter(customAdapter);
                            } else {
                                Log.d("EVS", "Error getting documents: ", task.getException());
                            }
                        }
                    }
                });
        return v;
        //return fragments view

    }



 /*   @Override
    public void onResume() {
        super.onResume();
        ((BottomNav)getActivity()).getSupportActionBar().show();

    }
    @Override
    public void onStop() {
        super.onStop();
        ((BottomNav)getActivity()).getSupportActionBar().hide();
    }*/



    private ActionMode.Callback mActionModeCallback1 = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            //inflate the action mode menu
            mode.getMenuInflater().inflate(R.menu.action_mode_menu, menu);
            mode.setTitle("");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                //option 2 is delete a workout
                case R.id.option2:
                    Toast.makeText(getActivity(), "Option 2 selected", Toast.LENGTH_SHORT).show();
                    //get the item that was selected
                    ActivityLogs activityLog = (ActivityLogs)customAdapter.getItem(selectedItem);
                    //delete the item from the user/events db
                    deleteUserEvent(activityLog);
                    //using a callback retrieve the corresponding document id from the EVENTS db
                    //callback is passed as a parameter through the getevents method
                    //the onCallBack method will be called once the initial one is fully completed
                    getEventsDocID(activityLog,new Fragment1.FirestoreCallback() {
                        @Override
                        public void onCallback(String docRef) {
                            //once the document reference is retrieved delete it from the events DB
                            DeleteFromEvents(docRef);
                        }
                    });
                    //close the action mode
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }


        @Override
        public void onDestroyActionMode(ActionMode mode) {
            //reset mode and selected item
            mActionMode = null;
            selectedItem = -1;
        }
    };


    public void DeleteFromEvents(String docRef) {
    //deletes workouts from the EVENTS DB
        mFirebaseFirestore.collection("Events").document(docRef)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    //success and error debugging
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DEL", "EVENTS DocumentSnapshot successfully deleted!");
                        Toast.makeText(getActivity(), "Workout successfully deleted!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error deleting workout! " + e, Toast.LENGTH_SHORT).show();
                        Log.w("DEL", "EVENTS Error deleting document", e);
                    }
                });
    }

    private interface FirestoreCallback
    {
        //implementation for the Callback interface - used to check if a method completed
        void onCallback(String documentID);
    }


    private void getEventsDocID(ActivityLogs activityLog, final Fragment1.FirestoreCallback callback) {
        //gets the selected workout and the callback variable
        //compares the selected workout's date with all the dates from EVENTS and retrieve matchs
        mFirebaseFirestore.collection("Events")
                .whereEqualTo("Training Date",activityLog.getActivityDateFormat())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    //go through all the retrieved docs and store ID
                    //should only be one as date is accurate to the second
                    for(QueryDocumentSnapshot document : task.getResult())
                    {
                        docID = document.getId();
                    }

                }
                //when query is completed and id retrieved call the callback method
                callback.onCallback(docID);
            }
        });

    }


    private void deleteUserEvent(ActivityLogs activityLog) {
        //deletes event from the USER/EVENTS db
        //gets the selected workout as parameter in order to use the unique doc id
        mFirebaseFirestore.collection("Users").document(userID)
                .collection("Events").document(activityLog.getDocReference())
                .delete()
                //on success/error for debugging
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DEL", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DEL", "Error deleting document", e);
                    }
                });
    }


    //adapter used as a bridge between listview and the data
    class CustomAdapter extends BaseAdapter
    {
        //returns size
        @Override
        public int getCount() {
            return logs.size();
        }

        //get the selected item
        @Override
        public Object getItem(int i) {
            if(null != logs){
                try {
                    return logs.get(i);
                } catch (IndexOutOfBoundsException e) {
                    return null;
                }
            }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        //The ListView instance calls the  getView() method on the adapter for each data element.
        // In this method the adapter creates
        // the row layout and maps the data to the views in the layout.
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            //prepare the view of one list entry
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
