package com.gamurar.gamlang.View;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.gamurar.gamlang.Card;
import com.gamurar.gamlang.R;
import com.gamurar.gamlang.ViewModel.CardViewModel;
import com.gamurar.gamlang.utilities.DictionaryAdapter;
import com.gamurar.gamlang.utilities.PreferencesUtils;
import com.github.abdularis.civ.AvatarImageView;

import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DictionaryActivity extends AppCompatActivity
    implements DictionaryAdapter.DictWordSelectListener {
    private static final String TAG = "DictionaryActivity";

    public static final String EXTRA_WORD_ITEM = "extra_word_item";
    public static final String EXTRA_WORD_IMAGE_TRANSITION_NAME = "extra_word_image_transition_name";
    public static final String EXTRA_WORD_TRANSITION_NAME = "extra_word_transition_name";

    private RecyclerView mWordsList;
    private DictionaryAdapter mAdapter;
    private HashSet<Card> selectedCards;
    private CardViewModel mViewModel;
    private MenuItem mDeleteMenuItem;
    private ActionBar mActionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window mWindow = this.getWindow();
            mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            mWindow.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        //Recycler View
        mWordsList = findViewById(R.id.rv_my_dictionary);
        mWordsList.setLayoutManager(new LinearLayoutManager(this));
        mWordsList.setHasFixedSize(true);
        mAdapter = new DictionaryAdapter(this);
        mWordsList.setAdapter(mAdapter);
        //
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(R.string.dictionary);
        setupViewModel();
    }

    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this).get(CardViewModel.class);
        mAdapter.setCards(mViewModel.getAllCurrentCards());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dictionary_selected_words_menu, menu);
        mDeleteMenuItem = menu.findItem(R.id.action_delete_words);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_delete_words: {
                deleteSelectedCards();
                break;
            }
            case android.R.id.home: {
                onBackPressed();
                break;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
        return true;
    }

    private void deleteSelectedCards() {
        mAdapter.removeSelectedCards();
        mViewModel.delete(selectedCards);
        Log.d(TAG, "deleteSelectedCards: The selected cards deleted");
        selectedCards.clear();

        mDeleteMenuItem.setVisible(false);
        mAdapter.haveSelection = false;
    }


    @Override
    public void onWordClick(int position, Card card, AvatarImageView picture, TextView word) {
        Intent intent = new Intent(this, WordDetailActivity.class);
        intent.putExtra(EXTRA_WORD_ITEM, card);
        intent.putExtra(EXTRA_WORD_TRANSITION_NAME, ViewCompat.getTransitionName(word));
        Pair wordPair = new Pair<>(word, ViewCompat.getTransitionName(word));
        ActivityOptionsCompat options;
        if (picture != null) {
            intent.putExtra(EXTRA_WORD_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(picture));
            Pair picPair = Pair.create(picture, ViewCompat.getTransitionName(picture));
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    picPair, wordPair);
        } else {
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this, wordPair);
        }

        startActivity(intent, options.toBundle());
    }

    @Override
    public void onFirstSelect(View view, Card card) {
        view.setBackgroundResource(R.color.colorAccentLight);
        selectedCards = new HashSet<>();
        selectedCards.add(card);
        mDeleteMenuItem.setVisible(true);
    }

    //return true if there are still selected items in the list and false otherwise
    @Override
    public boolean onNextSelect(View view, Card card) {
//        if (selectedCards.contains(card)) {
//            return onUnselect(view, card);
//        } else {
            view.setBackgroundResource(R.color.colorAccentLight);
            selectedCards.add(card);
            return true;
//        }
    }

    //return true if there are still selected items in the list and false otherwise
    @Override
    public boolean onUnselect(View view, Card card) {
        view.setBackgroundResource(android.R.color.white);
        selectedCards.remove(card);
        boolean isSelectedItem = !selectedCards.isEmpty();
        if (!isSelectedItem) {
            mDeleteMenuItem.setVisible(false);
        }

        return isSelectedItem;
    }
}


