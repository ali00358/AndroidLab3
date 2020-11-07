package com.example.androidlab2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class TestToolbar extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_toolbar);


        //For toolbar:
        Toolbar tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);


        //For NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        //Look at your menu XML file. Put a case for every id in that file:
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.item1:
                message = "You clicked Chrome";
                break;
            case R.id.item2:
                message = "You clicked Edge";
                break;
            case R.id.item3:
                message = "You clicked on Firefox";
                break;
            case R.id.overflow:
                message = "You clicked on Overflow";
                break;
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        return true;
    }



    @Override
    public boolean onNavigationItemSelected( MenuItem item) {

        String message = null;
        Intent newAct;
        switch(item.getItemId())
        {
            case R.id.item1:
                message = "Chat";
                newAct = new Intent(this, ChatActivity.class);
                startActivity(newAct);
                break;
            case R.id.item2:
                message = "Login";
                newAct = new Intent(this, MainActivity.class);
                startActivity(newAct);
                break;
            case R.id.item3:
                message = "Weather";
                newAct = new Intent(this, WeatherForecast.class);
                startActivity(newAct);
                break;
            case R.id.overflow:
                message = "Option Overflow";
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        Toast.makeText(this, "Toolbar Lab: " + message, Toast.LENGTH_LONG).show();

        return false;
    }
}
