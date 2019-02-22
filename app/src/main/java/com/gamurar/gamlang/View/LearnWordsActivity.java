package com.gamurar.gamlang.View;

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
import com.gamurar.gamlang.utilities.CardsAdapter;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;

public class LearnWordsActivity extends AppCompatActivity implements LifecycleOwner {

    private static final String TAG = "LearnWordsActivity";
    private ArrayList<Card> mCards;
    private CardsAdapter mAdapter;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_words);
        init();
//        setupViewModel();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window mWindow = this.getWindow();
            mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            mWindow.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(R.string.learn);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new LearnSessionFragment())
                .commit();
    }

    private void setupViewModel() {
        CardViewModel viewModel = ViewModelProviders.of(this).get(CardViewModel.class);
        viewModel.getAllCards().observe(this, (cards) -> {
            Log.d(TAG, "setupViewModel: receive data from ViewModel to 'Learn words'");
            mCards = (ArrayList<Card>) cards;
            mAdapter.setCards(mCards);
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.learn_words_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
//            case R.id.action_refresh: {
//                mAdapter.notifyDataSetChanged();
//                break;
//            }
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
}
