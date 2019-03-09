package com.example.android.fireapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.android.fireapp.Challenges.CompletedChallengesActivity;
import com.example.android.fireapp.Challenges.LeaderBoardActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {


    public AccountFragment() {
        // Required empty public constructor
    }
    private Button button;
    private Button leaderBoardButton;
    private Button heightWeightBtn;

    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_account, container, false);
        button = view.findViewById(R.id.completed_Challenges_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent completedChallenges = new Intent(getActivity(),CompletedChallengesActivity.class);
                startActivity(completedChallenges);
            }
        });
        leaderBoardButton = view.findViewById(R.id.leaderBoardButton);
        leaderBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent completedChallenges = new Intent(getActivity(),LeaderBoardActivity.class);
                startActivity(completedChallenges);
            }
        });

        heightWeightBtn = view.findViewById(R.id.heightWeightBtn);
        heightWeightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent heightWeightIntent = new Intent(getActivity(),LeaderBoardActivity.class);
                startActivity(heightWeightIntent);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

}
