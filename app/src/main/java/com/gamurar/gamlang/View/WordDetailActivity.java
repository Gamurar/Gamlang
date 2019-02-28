package com.gamurar.gamlang.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamurar.gamlang.Card;
import com.gamurar.gamlang.R;
import com.gamurar.gamlang.View.DictionaryActivity;

public class WordDetailActivity extends AppCompatActivity {

    ActionBar mActionBar;
    TextView word;
    TextView translation;
    ImageView picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);
        setupToolbar();
        supportPostponeEnterTransition();

        Bundle extras = getIntent().getExtras();
        Card card = extras.getParcelable(DictionaryActivity.EXTRA_WORD_ITEM);

        mActionBar.setTitle(card.getQuestion());

        picture = findViewById(R.id.picture);
        word = findViewById(R.id.word);
        translation = findViewById(R.id.translation);
        word.setText(card.getQuestion());
        translation.setText(card.getAnswer());
        if (card.hasPictures()) {
            picture.setImageBitmap(card.getPictures().get(0));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String wordTransitionName = extras.getString(DictionaryActivity.EXTRA_WORD_TRANSITION_NAME);
            word.setTransitionName(wordTransitionName);

            if (extras.containsKey(DictionaryActivity.EXTRA_WORD_IMAGE_TRANSITION_NAME)) {
                String imageTransitionName = extras.getString(DictionaryActivity.EXTRA_WORD_IMAGE_TRANSITION_NAME);
                picture.setTransitionName(imageTransitionName);
            }
        }
        supportStartPostponedEnterTransition();
    }

    private void setupToolbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window mWindow = this.getWindow();
            mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            mWindow.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
