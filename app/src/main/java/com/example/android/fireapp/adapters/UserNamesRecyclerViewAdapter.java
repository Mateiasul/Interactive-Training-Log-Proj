package com.example.android.fireapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.fireapp.R;

import java.util.ArrayList;

public class UserNamesRecyclerViewAdapter extends RecyclerView.Adapter<UserNamesRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> userNames = new ArrayList<>();
    private Context mContext;
    private UserRecyclerViewAdapterInterface listener;

    public UserNamesRecyclerViewAdapter(ArrayList<String> userNames, Context mContext, UserRecyclerViewAdapterInterface listener) {
        this.listener = listener;
        this.userNames = userNames;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment2_recyclerview_squad_items,viewGroup,false);
        return new ViewHolder(view);     }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        viewHolder.userNameTV.setText(userNames.get(i));
        viewHolder.userNameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onUserItemClicked(userNames.get(i));
            }
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {

        TextView userNameTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTV = itemView.findViewById(R.id.squadTvRecyclerView);

        }
    }

    @Override
    public int getItemCount() {
        return userNames.size();
    }

    public interface UserRecyclerViewAdapterInterface{
        void onUserItemClicked(String name);
    }
}

