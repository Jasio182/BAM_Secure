package com.bam.mokracki.projekt.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bam.mokracki.projekt.R;
import com.bam.mokracki.projekt.activity.DataAccessActivity;
import com.bam.mokracki.projekt.entity.SecretEntity;

import java.util.ArrayList;

public class EntryAdapter extends RecyclerView.Adapter<EntryViewHolder>{

    ArrayList<SecretEntity> entries;
    DataAccessActivity context;

    public EntryAdapter(ArrayList<SecretEntity> entries, DataAccessActivity context) {
        this.entries = entries;
        this.context = context;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.entry,parent, false);
        EntryViewHolder viewHolder = new EntryViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        final SecretEntity entity = entries.get(position);
        holder.id.setText(entity.getId().toString());
        holder.entry.setText(entity.getCreditCardNumber());
        holder.itemView.setOnClickListener(view -> {
            context.currentId = entity.getId();
        });
    }

    public void updateData(ArrayList<SecretEntity> entities) {
        entries.clear();
        entries.addAll(entities);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }
}
