package com.bam.mokracki.projekt.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bam.mokracki.projekt.helper.DbHelper;
import com.bam.mokracki.projekt.R;

public class RestoreActivity extends AppCompatActivity {

    private String[] readWritePermissions = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private int GETREADWRITEPERMISSIONSREQUESTCODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore);

        Button restoreActionButton = findViewById(R.id.restoreActionButton);
        restoreActionButton.setOnClickListener(view -> {
            ActivityCompat.requestPermissions(this, readWritePermissions , GETREADWRITEPERMISSIONSREQUESTCODE);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == GETREADWRITEPERMISSIONSREQUESTCODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                DbHelper dbHelper = new DbHelper(this.getApplicationContext());
                TextView entityValue = findViewById(R.id.RestorePasswordInput);
                String value = entityValue.getText().toString();
                if(value.length() == 16 || value.length() == 24 || value.length() == 32)
                {
                    try {
                        dbHelper.Restore(value);
                        Toast.makeText(getApplicationContext(),"Successfully restored database",Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        Toast.makeText(getApplicationContext(),"Unable to perform a restore",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Password needs to be 16, 24 or 32 characters long", Toast.LENGTH_SHORT).show();
                }
            }  else {
                Toast.makeText(getApplicationContext(), "You do not have permissions to perform a restore", Toast.LENGTH_SHORT).show();
            }
        }

    }
}