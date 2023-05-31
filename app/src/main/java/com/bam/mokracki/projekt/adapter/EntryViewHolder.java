package com.bam.mokracki.projekt.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bam.mokracki.projekt.R;

public class EntryViewHolder extends RecyclerView.ViewHolder {
    TextView id;
    TextView entry;
    public EntryViewHolder(@NonNull View itemView) {
        super(itemView);
        id = itemView.findViewById(R.id.id);
        entry = itemView.findViewById(R.id.number);
    }
}
