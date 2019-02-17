package com.gamurar.gamlang.View;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.gamurar.gamlang.R;
import com.gamurar.gamlang.ViewModel.CardCreationViewModel;
import com.gamurar.gamlang.Word;
import com.gamurar.gamlang.utilities.SystemUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

public class CardCreationActivity extends AppCompatActivity {

    private static final String TAG = "CardCreationActivity";
    public static final String EXTRA_WORD_INFO = "adding_word_info";

    public CardCreationViewModel viewModel;

    private LinearLayout mTopAlert;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_creation);
        init();
    }

    private void init() {
        viewModel = ViewModelProviders.of(this).get(CardCreationViewModel.class);
        mTopAlert = findViewById(R.id.top_alert);

        if (getIntent().hasExtra(EXTRA_WORD_INFO)) {
            String[] wordInfo = getIntent().getStringArrayExtra(EXTRA_WORD_INFO);
            Word word = new Word(wordInfo[0], wordInfo[1]);
            viewModel.setWord(word);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PickImageFragment fragment = new PickImageFragment();
        viewModel.gatherWordInfo(fragment, fragment);
        fragmentTransaction.add(R.id.fragment_container, fragment).commit();
    }

    public void closeActivity(View view) {
        onBackPressed();
    }

    public void onCardAdded() {
        SystemUtils.setStatusBarColor(this, R.color.colorPrimary);
        mTopAlert.setVisibility(View.VISIBLE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new CardCreationFinishFragment())
                .commit();
    }
}
