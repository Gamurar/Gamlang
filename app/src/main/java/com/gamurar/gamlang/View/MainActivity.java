package com.gamurar.gamlang.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.gamurar.gamlang.R;
import com.gamurar.gamlang.Word;
import com.gamurar.gamlang.utilities.AppExecutors;
import com.gamurar.gamlang.utilities.NetworkUtils;
import com.gamurar.gamlang.utilities.SystemUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private CardView mMenuLearn;
    private CardView mMenuDictionary;
    private CardView mMenuWatch;
    private CardView mMenuSettings;
    private FloatingActionButton mMenuAddWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        SystemUtils.hideStatusBar(this);

        mMenuLearn = findViewById(R.id.menu_learn);
        mMenuDictionary = findViewById(R.id.menu_dictionary);
        mMenuWatch = findViewById(R.id.menu_watch);
        mMenuSettings = findViewById(R.id.menu_settings);
        mMenuAddWords = findViewById(R.id.fab_add_words);

        mMenuLearn.setOnClickListener(new StartActivity(LearnWordsActivity.class));
        mMenuDictionary.setOnClickListener(new StartActivity(MyDictionaryActivity.class));
        mMenuWatch.setOnClickListener(new StartActivity(VideoActivity.class));
        mMenuSettings.setOnClickListener(new StartActivity(SettingsActivity.class));
        mMenuAddWords.setOnClickListener(new StartActivity(ExploreActivity.class));
    }

    private class StartActivity implements View.OnClickListener {

        private Class<?> mActivity;

        public StartActivity(Class<?> cls) {
            mActivity = cls;
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: " + mActivity.getSimpleName() + " menu option clicked!");
            Intent intent = new Intent(MainActivity.this, mActivity);
            startActivity(intent);
        }
    }
}
