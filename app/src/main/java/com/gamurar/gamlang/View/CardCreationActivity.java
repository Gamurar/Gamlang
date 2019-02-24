package com.gamurar.gamlang.View;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.gamurar.gamlang.Model.CardRepository;
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

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        CardRepository.getInstance(this).initLocal();
    }

    private void init() {
        mTopAlert = findViewById(R.id.top_alert);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PickImageFragment fragment = new PickImageFragment();
        if (getIntent().hasExtra(EXTRA_WORD_INFO)) {
            Bundle bundle = new Bundle();
            bundle.putStringArray(EXTRA_WORD_INFO,
                    getIntent().getStringArrayExtra(EXTRA_WORD_INFO));
            fragment.setArguments(bundle);
        }
        fragmentTransaction.add(R.id.fragment_container, fragment).commit();
    }

    public void closeActivity(View view) {
        onBackPressed();
    }

    public void onCardAdded() {
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_container, new CardCreationFinishFragment())
                .commit();
        SystemUtils.setStatusBarColor(this, R.color.colorPrimary);
        mTopAlert.setVisibility(View.VISIBLE);
    }
}
