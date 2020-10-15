package com.example.androidlab2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    //instance variable for our shared pref
    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_linear);

        //initialize prefs to a file with private mode
        prefs = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        //get the saved email. if nothing, use the placeholder.
        String savedUser = prefs.getString("ReservedName", "");
        EditText emailField = findViewById(R.id.emailEditText);
        Button loginButton = findViewById(R.id.loginButton);

        //display a saved user email if any
        emailField.setText(savedUser);

        //make login button send us to the profile page
        Intent nextPage = new Intent(this, ProfileActivity.class);
        loginButton.setOnClickListener(click -> startActivity(nextPage));




        //adding listener to button
//        final Button btn = findViewById(R.id.button1);
//        btn.setOnClickListener( (click) -> {
//            Toast.makeText(MainActivity.this, getResources().getString(R.string.toastMessage), Toast.LENGTH_LONG).show();
//        });
//
//        //adding change listener to check box
//        final CheckBox cb = findViewById(R.id.check1);
//
//        cb.setOnCheckedChangeListener( (compoundButton, isChecked) -> {
//            Snackbar.make(findViewById(R.id.layout),
//                    getResources().getString(R.string.snack1) + " " + ((isChecked)?
//                            getResources().getString(R.string.snackOn) : getResources().getString(R.string.snackOff)),
//                    Snackbar.LENGTH_LONG).setAction("Undo", click -> compoundButton.setChecked(!isChecked)).show();
//        });
//
//        final Switch sw = findViewById(R.id.switch1);
//
//        sw.setOnCheckedChangeListener( (compoundButton, isChecked) -> {
//            Snackbar.make(findViewById(R.id.layout), getResources().getString(R.string.snack1) + " " + ((isChecked)?
//                    getResources().getString(R.string.snackOn) : getResources().getString(R.string.snackOff)),
//                    Snackbar.LENGTH_LONG).setAction("Undo", click -> compoundButton.setChecked(!isChecked)).show();
//        });


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

        //when app is paused I want to save the user's email to our file
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
        editor.commit();
    }

}
