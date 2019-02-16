package com.gamurar.gamlang.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.gamurar.gamlang.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "DrawerActivity";

    private final String SAVE_IMAGES_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final int PERMISSION_REQUEST_CODE = 198;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        initViews();
//        if (getIntent().hasExtra(Intent.EXTRA_PROCESS_TEXT)) {
//            ExploreActivity addWord = new ExploreActivity();
//            Log.d(TAG, "Text to pass to the add words fragment: "
//                    + getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT));
//            addWord.setArguments(getIntent().getExtras());
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                    addWord).commit();
//        } else if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                    new LearnWordsActivity()).commit();
//            navigationView.setCheckedItem(R.id.nav_learn_words);
//        }

        //Permissions
        if ((ContextCompat.checkSelfPermission(this, SAVE_IMAGES_PERMISSION)
                != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{SAVE_IMAGES_PERMISSION},
                    PERMISSION_REQUEST_CODE);
        }
        //

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission granted
                } else {
                    //Permission denied
                    if (Build.VERSION.SDK_INT >= 21) {
                        finishAndRemoveTask();
                    } else {
                        this.finishAffinity();
                    }

                }
            }
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
//        switch (item.getItemId()) {
//            case R.id.nav_learn_words: {
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new LearnWordsActivity()).commit();
//                break;
//            }
//            case R.id.nav_my_dictionary: {
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        //new MyDictionaryActivity()).commit();
//                break;
//            }
//            case R.id.nav_add_words: {
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new ExploreActivity()).commit();
//                break;
//            }
//            case R.id.nav_video: {
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new VideoActivity()).commit();
//                break;
//            }
//            case R.id.nav_settings: {
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new SettingsFragment()).commit();
//                break;
//            }
//        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


//    public void onClickLearnWords(View view) {
//        Intent intent = new Intent(this, LearnWordsActivity.class);
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
