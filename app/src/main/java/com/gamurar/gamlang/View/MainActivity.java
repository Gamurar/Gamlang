package com.gamurar.gamlang.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gamurar.gamlang.Model.CardRepository;
import com.gamurar.gamlang.R;
import com.gamurar.gamlang.utilities.PreferencesUtils;
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
    private TextView mCardInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCardsInfo();
        CardRepository.getInstance(this).initLocal();
    }

    private void init() {
        SystemUtils.hideStatusBar(this);

        mMenuLearn = findViewById(R.id.menu_learn);
        mMenuDictionary = findViewById(R.id.menu_dictionary);
        mMenuWatch = findViewById(R.id.menu_watch);
        mMenuSettings = findViewById(R.id.menu_settings);
        mMenuAddWords = findViewById(R.id.fab_add_words);
        mCardInfo = findViewById(R.id.cards_info);

        mMenuLearn.setOnClickListener(new StartActivity(LearnWordsActivity.class));
        mMenuDictionary.setOnClickListener(new StartActivity(DictionaryActivity.class));
        mMenuWatch.setOnClickListener(new StartActivity(VideoActivity.class));
        mMenuSettings.setOnClickListener(new StartActivity(SettingsActivity.class));
        mMenuAddWords.setOnClickListener(new StartActivity(ExploreActivity.class));
    }

    private void setCardsInfo() {
        int totalCards = PreferencesUtils.getTotalCards(this);
        mCardInfo.setText(getString(R.string.cards_info, totalCards));
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
