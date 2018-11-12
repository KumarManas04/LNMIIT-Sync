package com.infinitysolutions.lnmiitsync.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.infinitysolutions.lnmiitsync.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

public class ClubsRecyclerViewAdapter extends RecyclerView.Adapter<ClubsRecyclerViewAdapter.ViewHolder> {

    private List<String> mClubsList;
    private int isChecked[];

    public ClubsRecyclerViewAdapter(List<String> clubsList){
        mClubsList = clubsList;
        isChecked = new int[clubsList.size()];
    }

    public int[] getCheckedList(){
        return isChecked;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView mCheckedImageView;
        TextView mClubNameTextView;
        LinearLayout mClubItemView;
        public ViewHolder(View v) {
            super(v);
            mCheckedImageView = (AppCompatImageView)v.findViewById(R.id.checked_indicator);
            mClubNameTextView = (TextView)v.findViewById(R.id.club_name);
            mClubItemView = (LinearLayout)v.findViewById(R.id.list_item);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.clubs_list_item,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.mClubNameTextView.setText(mClubsList.get(position));

        if(isChecked[position] == 0){
            holder.mCheckedImageView.setImageResource(R.drawable.unchecked_icon);
            holder.mClubItemView.setBackgroundResource(R.drawable.table_item);
            holder.mClubNameTextView.setTextColor(Color.parseColor("#90a4ae"));
        }else{
            holder.mCheckedImageView.setImageResource(R.drawable.checked_icon);
            holder.mClubItemView.setBackgroundResource(R.drawable.table_item_selected);
            holder.mClubNameTextView.setTextColor(Color.parseColor("#000000"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isChecked[position] == 0){
                    isChecked[position] = 1;
                }else{
                    isChecked[position] = 0;
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mClubsList.size();
    }
}
