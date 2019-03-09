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

public class MyChallengesListAdapter extends BaseAdapter {


    CompleteCommunication completeCommunicationListener;

    public interface CompleteCommunication{
        public void completeCommunication(int position);
    }


    ArrayList<Challenge> listItem;

    Context mContext;
    //constructor
    public MyChallengesListAdapter(Context mContext, ArrayList<Challenge> listItem,CompleteCommunication completeCommunicationListener) {
        this.mContext = mContext;
        this.listItem = listItem;
        this.completeCommunicationListener = completeCommunicationListener;
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
        View row = inflater.inflate(R.layout.my_challenges_list_item_layout, viewGroup, false);

        TextView creator_TV = row.findViewById(R.id.myChallengeCreator_TV);
        TextView date_TV = row.findViewById(R.id.myChallengeDate_TV);
        TextView summary_TV = row.findViewById(R.id.myChallenge_sumTV);
        Button button = row.findViewById(R.id.completeButton);
        creator_TV.setText(listItem.get(position).getCreatorUser());
        date_TV.setText(listItem.get(position).getDate().toString());
        summary_TV.setText(listItem.get(position).getSummary());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeCommunicationListener.completeCommunication(position);
            }
        });
        return row;
    }
}
