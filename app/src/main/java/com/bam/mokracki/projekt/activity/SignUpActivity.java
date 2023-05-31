package com.bam.mokracki.projekt.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bam.mokracki.projekt.helper.DbHelper;
import com.bam.mokracki.projekt.R;

import java.io.File;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        DbHelper dbHelper = new DbHelper(this.getApplicationContext());
        Button buttonRegister = findViewById(R.id.buttonCreateAccount);
        if(dbHelper.CheckRegister()) {
            Toast.makeText(getApplicationContext(),"WARNING!\nCreating a new account will remove the old one all its data!",Toast.LENGTH_LONG).show();
        }
        buttonRegister.setOnClickListener(view -> {
            TextView login = findViewById(R.id.LoginInput);
            TextView password = findViewById(R.id.PasswordInput);
            dbHelper.Register(login.getText().toString(), password.getText().toString());
            File creds = new File(this.getApplicationContext().getFilesDir(), "creds");
            creds.delete();
            Intent signInIntent = new Intent(SignUpActivity.this, SignInActivity.class);
            SignUpActivity.this.startActivity(signInIntent);
        });
    }
}