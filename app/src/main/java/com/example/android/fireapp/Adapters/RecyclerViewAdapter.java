package com.example.android.fireapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.fireapp.R;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private ArrayList<String> squadNames = new ArrayList<>();
    private Context mContext;
    private RecyclerViewAdapterInterface listener;

    public RecyclerViewAdapter(ArrayList<String> squadNames, Context mContext, RecyclerViewAdapterInterface listener) {
        this.listener = listener;
        this.squadNames = squadNames;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment2_recyclerview_squad_items,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        viewHolder.squadNameTV.setText(squadNames.get(position));
        viewHolder.squadNameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(squadNames.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return squadNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        TextView squadNameTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            squadNameTV = itemView.findViewById(R.id.squadTvRecyclerView);

        }
    }

    public interface RecyclerViewAdapterInterface
    {
        //method to be implemented by activities that implement the interface
        void onItemClicked(String name);
    }
}
