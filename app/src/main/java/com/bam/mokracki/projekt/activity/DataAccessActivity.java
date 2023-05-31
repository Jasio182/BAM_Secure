package com.bam.mokracki.projekt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bam.mokracki.projekt.helper.DbHelper;
import com.bam.mokracki.projekt.adapter.EntryAdapter;
import com.bam.mokracki.projekt.R;
import com.bam.mokracki.projekt.entity.SecretEntity;

import java.util.ArrayList;

public class DataAccessActivity extends AppCompatActivity {
    private String credentials;
    public int currentId = -1;
    ArrayList<SecretEntity> entities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_access);
        Intent intent = getIntent();
        credentials = intent.getStringExtra("USER_CREDS");
        DbHelper dbHelper = new DbHelper(this.getApplicationContext());
        entities = dbHelper.GetEntries(credentials);
        RecyclerView recyclerView = findViewById(R.id.entriesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        EntryAdapter entryAdapter = new EntryAdapter(entities, DataAccessActivity.this);
        recyclerView.setAdapter(entryAdapter);
        Button buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(view -> {
            TextView entityValue = findViewById(R.id.EntityValue);
            String value = entityValue.getText().toString();
            if(value.length() > 0)
            {
                dbHelper.AddEntry(credentials, new SecretEntity(value));
                entities = dbHelper.GetEntries(credentials);
                entryAdapter.updateData(entities);
            }
            else{
                Toast.makeText(getApplicationContext(),"You need to enter a value",Toast.LENGTH_SHORT).show();
            }
        });
        Button buttonDelete = findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(view -> {
            dbHelper.DeleteEntry(credentials, currentId);
            entities = dbHelper.GetEntries(credentials);
            entryAdapter.updateData(entities);
        });
        Button buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonUpdate.setOnClickListener(view -> {
            TextView entityValue = findViewById(R.id.EntityValue);
            String value = entityValue.getText().toString();
            if(value.length() > 0) {
                dbHelper.UpdateEntry(credentials, new SecretEntity(currentId, value));
                entities = dbHelper.GetEntries(credentials);
                entryAdapter.updateData(entities);
            }
            else{
                Toast.makeText(getApplicationContext(),"You need to enter a value",Toast.LENGTH_SHORT).show();
            }
        });
        Button buttonBackup = findViewById(R.id.buttonBackup);
        buttonBackup.setOnClickListener(view -> {
            Intent backupIntent = new Intent(DataAccessActivity.this, BackupActivity.class);
            DataAccessActivity.this.startActivity(backupIntent);
        });
    }
}