package com.gamurar.gamlang.View;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.gamurar.gamlang.Card;
import com.gamurar.gamlang.R;
import com.gamurar.gamlang.ViewModel.CardViewModel;
import com.gamurar.gamlang.utilities.DictionaryAdapter;
import com.gamurar.gamlang.utilities.PreferencesUtils;

import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DictionaryActivity extends AppCompatActivity
    implements DictionaryAdapter.DictWordSelectListener {
    private static final String TAG = "DictionaryActivity";
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
        mViewModel.getAllCards().observe(this, (cardEntries) -> {
            mAdapter.setCards(cardEntries);
            PreferencesUtils.setTotalCards(this, cardEntries.size());
        });
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
        mViewModel.delete(selectedCards);
//        PreferencesUtils.decrementTotalCards(selectedCards.size(), this);
        Log.d(TAG, "deleteSelectedCards: The selected cards deleted");
        selectedCards.clear();

        mDeleteMenuItem.setVisible(false);
        mAdapter.haveSelection = false;
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
        if (selectedCards.contains(card)) {
            return onUnselect(view, card);
        } else {
            view.setBackgroundResource(R.color.colorAccentLight);
            selectedCards.add(card);
            return true;
        }
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


