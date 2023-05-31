package com.bam.mokracki.projekt.activity;

import static com.bam.mokracki.projekt.helper.Cryptography.ToBase64;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bam.mokracki.projekt.helper.Cryptography;
import com.bam.mokracki.projekt.helper.DbHelper;
import com.bam.mokracki.projekt.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        DbHelper dbHelper = new DbHelper(this.getApplicationContext());
        Button buttonSignIn = findViewById(R.id.buttonSignIn);
        CheckBox rememberMeCheckBox = findViewById(R.id.RememberMeCheckBox);
        TextView login = findViewById(R.id.LoginInput1);
        TextView password = findViewById(R.id.PasswordInput1);
        File creds = new File(this.getApplicationContext().getFilesDir(), "creds");
        if(creds.exists())
        {
            rememberMeCheckBox.setChecked(true);
            Toast.makeText(getApplicationContext(),"Credentials restored, do not specify them, just push the button",Toast.LENGTH_LONG).show();
        }
        buttonSignIn.setOnClickListener(view -> {
            String credentials = null;
            if(creds.exists() && rememberMeCheckBox.isChecked())
            {
                try {
                    credentials = readFile(creds);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"You need to reenter the credentials",Toast.LENGTH_SHORT).show();
                }
            }
            if(credentials == null || (login.getText().length() > 0 && password.getText().length() > 0))
            {
                credentials = ToBase64(login.getText().toString(), password.getText().toString());
            }
            if(dbHelper.Login(credentials)) {
                if(rememberMeCheckBox.isChecked())
                {
                    try {
                        writeToFile(creds, credentials);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),"Something went wrong with remembering the credentials",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    creds.delete();
                }
                Intent dataAccessIntent = new Intent(SignInActivity.this, DataAccessActivity.class);
                dataAccessIntent.putExtra("USER_CREDS", credentials);
                SignInActivity.this.startActivity(dataAccessIntent);
            } else {
                Toast.makeText(getApplicationContext(),"Wrong login or password",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void writeToFile(File file, String credentials) throws Exception {
        Cryptography cryptography = new Cryptography("credentials");
        String encrypted = cryptography.encryptKeyStore(credentials);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
        writer.write(encrypted);
        writer.close();
    }

    private String readFile(File file) throws Exception {
        Cryptography cryptography = new Cryptography("credentials");
        FileInputStream fin = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        fin.close();
        return cryptography.decryptKeyStore(sb.toString());
    }
}