package com.example.android.fireapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.fireapp.adapters.ChallengesAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//TODO display charts on a per squad basis user should only be able to see his squad performance and no other
//TODO this should be moved to a different unique activity/fragment and not stay here.
/**
 * A simple {@link Fragment} subclass.
 */
public class ChallengesFragment extends Fragment
{


    private String userSquad;
    private String userLastName;
    private String currentChallengeRef;

    public ChallengesFragment() {
        // Required empty public constructor
    }

    private View view;
    private  Boolean isCoach;
    private  String userID;
    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseAuth mFireBaseAuth;
    private FloatingActionButton FAB_addChallenge;
    private ArrayList<Challenge> challengeList;
    private RecyclerView recyclerView;
    private ListView listView;
    private int currentCard;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_challenges, container, false);
        mFireBaseAuth = mFireBaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        userID = mFireBaseAuth.getCurrentUser().getUid();
        FAB_addChallenge = view.findViewById(R.id.FAB_ADD_CHALLENGE);
        challengeList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.challengesRecyclerView);
        listView = view.findViewById(R.id.challengesListView);
        FAB_addChallenge.hide();
        //sets isCoach variable -based on db response
        checkUserIsCoach();




        mFirebaseFirestore.collection("Challenges")
                .orderBy("Challenge Date", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot document : task.getResult())
                                    {
                                        String creatorUser = document.get("Creator Name").toString();
                                        Date date = (Date)document.get("Challenge Date");
                                        String challengeSummary = document.get("Challenge Summary").toString();
                                        String challengeDocRef = document.getId();
                                        Challenge challenge = new Challenge(challengeSummary,creatorUser,date,challengeDocRef);
                                        challengeList.add(challenge);
                                    }
                                    //initRyclcerView();

                                    listView.setAdapter(new ChallengesAdapter(getActivity(),challengeList,communication));
                                }
                            });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //i represents the selected's item position - starts from 0
                Toast.makeText(getActivity(), "long click toast ", Toast.LENGTH_SHORT).show();
                Intent challengeDetailsIntent = new Intent(getActivity(),ChallengeDetailsActivity.class);
                Challenge clickedChallenge  = (Challenge) challengeList.get(position);
                challengeDetailsIntent.putExtra("challengeDocRef",clickedChallenge.getChallengeDocRef());
                startActivity(challengeDetailsIntent);
                getActivity().finish();
            }
        });

        FAB_addChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addChallengeIntent = new Intent(getActivity(),AddNewChallengeActivity.class);
                startActivity(addChallengeIntent);
                getActivity().finish();
            }
        });




        return view;
    }


    //check cloud db see if user is coach or not, store it in field variable
    private void checkUserIsCoach() {
        mFirebaseFirestore.collection("Users").document(userID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful())
                        {
                            if (task.getResult().exists())
                            {
                                //set if the currently logged user is a coach or not ,
                                isCoach = Boolean.parseBoolean(task.getResult().get("Coach").toString());
                                if(isCoach)
                                {
                                    //replacement for visibility.VISIBLE
                                    FAB_addChallenge.show();
                                }
                                else
                                {
                                    //replacement for visibility.GONE
                                    FAB_addChallenge.hide();
                                }
                            }
                        }
                    }
                });
    }



    ChallengesAdapter.EnrollCommunication communication = new ChallengesAdapter.EnrollCommunication() {
        @Override
        public void enrollClicked(final int position) {
/*
            Toast.makeText(getContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
*/
            final Challenge challenge = challengeList.get(position);
            currentChallengeRef = challenge.getChallengeDocRef();

            RetrieveUserDetails(new RetrieveUserDetails() {
                @Override
                public void retrieveUserDetailsCallback(String userName, String userSquad) {
                    Map<String, Object> newChallengeMap = new HashMap<>();

                    newChallengeMap.put("Challenge ID",challenge.getChallengeDocRef());
                    newChallengeMap.put("User ID",userID);
                    newChallengeMap.put("User LastName", userLastName);
                    newChallengeMap.put("User Squad", userSquad);
                    newChallengeMap.put("Challenge Summary", challenge.getSummary());
                    newChallengeMap.put("Creator Name", challenge.getCreatorUser());

                    Calendar calendar = Calendar.getInstance();
                    final Date date = calendar.getTime();
                    newChallengeMap.put("Entry Time",date);

                    mFirebaseFirestore.collection("Challenges Entries")
                            .add(newChallengeMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful())
                            {
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.detach(ChallengesFragment.this).attach(ChallengesFragment.this).commit();
                                Toast.makeText(getActivity(), "Challenge Logged Successful", Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(getActivity(), "Challenge log unsuccessful  " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });




                }
            });



        }
    };

    private void RetrieveUserDetails(final RetrieveUserDetails callback) {
        mFirebaseFirestore.collection("Users").document(userID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            if (task.getResult().exists())
                            {
                                userLastName = task.getResult().get("LastName").toString();
                                userSquad = task.getResult().get("SquadName").toString();
                                mFirebaseFirestore.collection("Challenges Entries").whereEqualTo("User ID",userID)
                                                    .whereEqualTo("Challenge ID",currentChallengeRef).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful())
                                                {
                                                    if(task.getResult().isEmpty())
                                                    {
                                                        callback.retrieveUserDetailsCallback(userLastName,userSquad);
                                                    }
                                                    else{
                                                        Toast.makeText(getContext(), "already enrolled", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    private interface RetrieveUserDetails
    {
        //implementation for the Callback interface - used to check if a method completed
        void retrieveUserDetailsCallback(String eventId, String userSquad);
    }
}
