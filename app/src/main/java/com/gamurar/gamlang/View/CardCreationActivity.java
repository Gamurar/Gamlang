package com.gamurar.gamlang.View;

import android.os.Bundle;

import com.gamurar.gamlang.R;
import com.gamurar.gamlang.ViewModel.CardCreationViewModel;
import com.gamurar.gamlang.Word;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_creation);
        init();
    }

    private void init() {
        viewModel = ViewModelProviders.of(this).get(CardCreationViewModel.class);
        if (getIntent().hasExtra(EXTRA_WORD_INFO)) {
            String[] wordInfo = getIntent().getStringArrayExtra(EXTRA_WORD_INFO);
            Word word = new Word(wordInfo[0], wordInfo[1]);
            viewModel.setWord(word);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PickImageFragment fragment = new PickImageFragment();
        viewModel.gatherWordInfo(fragment);
        fragmentTransaction.add(R.id.fragment_container, fragment).commit();
    }
}
