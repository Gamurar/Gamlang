package com.gamurar.gamlang.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.gamurar.gamlang.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private CardView mMenuLearn;
    private CardView mMenuDictionary;
    private CardView mMenuWatch;
    private FloatingActionButton mMenuAddWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        // Hide the status bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        mMenuLearn = findViewById(R.id.menu_learn);
        mMenuDictionary = findViewById(R.id.menu_dictionary);
        mMenuWatch = findViewById(R.id.menu_watch);
        mMenuAddWords = findViewById(R.id.fab_add_words);

        mMenuLearn.setOnClickListener(new StartActivity(LearnWordsActivity.class));
        mMenuDictionary.setOnClickListener(new StartActivity(MyDictionaryActivity.class));
        mMenuWatch.setOnClickListener(new StartActivity(VideoActivity.class));
        mMenuAddWords.setOnClickListener(new StartActivity(AddWordsActivity.class));
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
