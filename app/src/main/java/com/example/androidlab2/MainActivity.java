package com.example.androidlab2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    SharedPreferences prefs = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_linear);

        prefs = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        String savedUser = prefs.getString("ReservedName", "");
        EditText emailField = findViewById(R.id.emailEditText);
        Button loginButton = findViewById(R.id.loginButton);

        emailField.setText(savedUser);

        Intent nextPage = new Intent(this, ProfileActivity.class);
        loginButton.setOnClickListener(click -> startActivity(nextPage));


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        EditText emailField = findViewById(R.id.emailEditText);
        String userEmail = emailField.getText().toString();
        saveToPrefs(userEmail);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void saveToPrefs(String s){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Username", s);
        editor.apply();
    }

}
