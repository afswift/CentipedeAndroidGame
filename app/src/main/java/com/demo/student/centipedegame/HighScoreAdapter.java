package com.demo.student.centipedegame;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by butle on 4/28/2018.
 */

public class HighScoreAdapter  extends RecyclerView.Adapter<HighScoreAdapter.ViewHolder>{
    ArrayList<PlayerScoreInfo> arrayList = new ArrayList<PlayerScoreInfo>();
    private String playerName;
    private long playerScore;

    public HighScoreAdapter(ArrayList<PlayerScoreInfo> arrayList){
       this.arrayList = arrayList;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView playerName;
        public TextView playerScore;
        public ViewHolder(View v) {
            super(v);
            playerName = (TextView)v.findViewById(R.id.textViewPlayerName);
            playerScore = (TextView)v.findViewById(R.id.textViewPlayerScore);
        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public HighScoreAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if(position %2 == 1)
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#010101"));
        }
        else
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#212121"));
        }

        holder.playerName.setText(arrayList.get(position).getPlayerName());
        holder.playerScore.setText(Long.toString(arrayList.get(position).getPlayerScore()));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}