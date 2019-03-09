package com.example.android.fireapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.fireapp.Challenges.Challenge;
import com.example.android.fireapp.Challenges.LeaderBoardEntry;
import com.example.android.fireapp.R;

import java.util.ArrayList;

public class LeaderBoardListAdapter extends BaseAdapter {


    CompletedChallenteListener completedChallenteListener;

    public interface CompletedChallenteListener{
        public void completedClick(int position);
    }


    ArrayList<LeaderBoardEntry> listItem;

    Context mContext;
    //constructor
    public LeaderBoardListAdapter(Context mContext, ArrayList<LeaderBoardEntry> listItem, CompletedChallenteListener listener) {
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
        View row = inflater.inflate(R.layout.leaderboard_list_item, viewGroup, false);
        TextView leaderBoardUserName_TV = row.findViewById(R.id.leaderBoardUserName);
        TextView challengesCompletedScore_TV = row.findViewById(R.id.leaderBoardScore);
        TextView leaderBoardID_TV = row.findViewById(R.id.leaderBoardId);


        leaderBoardUserName_TV.setText(listItem.get(position).getLastName());
        challengesCompletedScore_TV.setText(Integer.toString(listItem.get(position).getCompletedChallenges()));
        leaderBoardID_TV.setText(Integer.toString(listItem.get(position).getID()) + ".");
//        creator_TV.setText(listItem.get(position).getCreatorUser());
//        date_TV.setText(listItem.get(position).getDate().toString());
//        summary_TV.setText(listItem.get(position).getSummary());
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                completedChallenteListener.completedClick(position);
//            }
//        });
        return row;
    }


}
