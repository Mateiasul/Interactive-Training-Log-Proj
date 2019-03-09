package com.example.android.fireapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.fireapp.Challenges.Challenge;
import com.example.android.fireapp.R;

import java.util.ArrayList;

public class CompletedChallengesAdapter extends BaseAdapter {


    CompletedChallenteListener completedChallenteListener;

    public interface CompletedChallenteListener{
        public void completedClick(int position);
    }


    ArrayList<Challenge> listItem;

    Context mContext;
    //constructor
    public CompletedChallengesAdapter(Context mContext, ArrayList<Challenge> listItem, CompletedChallenteListener listener) {
        this.mContext = mContext;
        this.listItem = listItem;
        completedChallenteListener = listener;
    }

    public int getCount() {
        return listItem.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View arg1, ViewGroup viewGroup)
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.complted_challenges_listview_items, viewGroup, false);

        TextView creator_TV = row.findViewById(R.id.challengeCreator_TV);
        TextView date_TV = row.findViewById(R.id.challengeDate_TV);
        TextView summary_TV = row.findViewById(R.id.challenge_sumTV);
        Button button = row.findViewById(R.id.leaderboard_btn);
        creator_TV.setText(listItem.get(position).getCreatorUser());
        date_TV.setText(listItem.get(position).getDate().toString());
        summary_TV.setText(listItem.get(position).getSummary());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completedChallenteListener.completedClick(position);
            }
        });
        return row;
    }


}
