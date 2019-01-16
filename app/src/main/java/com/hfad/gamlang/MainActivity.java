package com.hfad.gamlang;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        if (getIntent().hasExtra(Intent.EXTRA_PROCESS_TEXT)) {
            AddWordsFragment addWord = new AddWordsFragment();
            Log.d(TAG, "Text to pass to the add words fragment: "
                    + getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT));
            addWord.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    addWord).commit();
        } else if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new LearnWordsFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_learn_words);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_learn_words: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new LearnWordsFragment()).commit();
                break;
            }
            case R.id.nav_my_dictionary: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MyDictionaryFragment()).commit();
                break;
            }
            case R.id.nav_add_words: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AddWordsFragment()).commit();
            }
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


//    public void onClickLearnWords(View view) {
//        Intent intent = new Intent(this, LearnWordsFragment.class);
//        startActivity(intent);
//    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //navigation drawer
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }
}
