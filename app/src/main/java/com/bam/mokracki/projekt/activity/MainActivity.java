package com.bam.mokracki.projekt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.bam.mokracki.projekt.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(view -> {
            Intent signInIntent = new Intent(MainActivity.this, SignInActivity.class);
            MainActivity.this.startActivity(signInIntent);
        });
        Button buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(view -> {
            Intent signUpIntent = new Intent(MainActivity.this, SignUpActivity.class);
            MainActivity.this.startActivity(signUpIntent);
        });
        Button buttonRestore = findViewById(R.id.buttonRestore);
        buttonRestore.setOnClickListener(view -> {
            Intent restoreIntent = new Intent(MainActivity.this, RestoreActivity.class);
            MainActivity.this.startActivity(restoreIntent);
        });
    }
}