package com.gamurar.gamlang.View;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gamurar.gamlang.Model.database.CardEntry;
import com.gamurar.gamlang.R;
import com.gamurar.gamlang.ViewModel.CardViewModel;
import com.gamurar.gamlang.ViewModel.ExploreViewModel;
import com.gamurar.gamlang.ViewModel.ExploreViewModelFactory;
import com.gamurar.gamlang.Word;
import com.gamurar.gamlang.utilities.ImagesAdapter;
import com.gamurar.gamlang.utilities.SuggestionAdapter;
import com.gamurar.gamlang.utilities.WordClick;
import com.gamurar.gamlang.utilities.WordContext;
import com.gamurar.gamlang.utilities.WordTranslation;
import com.gamurar.gamlang.views.ClickableWords;
import com.gamurar.gamlang.views.ImageViewBitmap;
import com.google.android.material.tabs.TabLayout;
import com.zyyoona7.popup.EasyPopup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ExploreActivity extends AppCompatActivity implements ImagesAdapter.ImageClickListener, WordTranslation, WordContext {

    private static final String TAG = "ExploreActivity";
    private static final String EXPLORE_FRAGMENT_TAG = "explore_fragment";

    private static final int NUM_PAGES = 2;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    //    private EditText mWordEditText;
    private TextView translationTextView;
    private ClickableWords mClickableSentence;
    private TextView imagesErrorMessage;
    private ImageView playSoundImageView;
    private TextView mWordContext;
    private RecyclerView wordPictureRecyclerView;
    private ProgressBar loadingIndicator;
    private ProgressBar imagesLoadingIndicator;
    //    private Button mTranslateBtn;
    private Button addToDictBtn;
    private ActionBar mActionBar;
    private Word mWord = new Word("");

    private ImagesAdapter mAdapter;
    private EasyPopup mCirclePop;

    private static boolean canAddToDict = true;
    private CardViewModel mViewModel;
    private ExploreViewModel mExploreViewModel;
    private RecyclerView mSuggestions;
    public SuggestionAdapter suggestionAdapter;
    private ExploreFragment mFirstExploreFragment;
    private ExploreFragment mSecondExploreFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        //setActionBar();
        mPager = findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        TabLayout tabs = findViewById(R.id.lang_tabs);
        tabs.setupWithViewPager(mPager);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                suggestionAdapter.clear();
                mExploreViewModel.reverseSearchLang();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mFirstExploreFragment = new ExploreFragment();
        mSecondExploreFragment = new ExploreFragment();
        Bundle reverseBundle = new Bundle();
        reverseBundle.putBoolean(EXPLORE_FRAGMENT_TAG, true);
        mSecondExploreFragment.setArguments(reverseBundle);
        suggestionAdapter = new SuggestionAdapter(this, mFirstExploreFragment);


//        mActionBar.setTitle(R.string.explore);
//        init();
//        setupViewModel();
//        setWordFromContext();
    }

    private void init() {
//        mSearchView = findViewById(R.id.search_view);
////        mWordEditText = findViewById(R.id.tv_word);
////        mWordEditText.setText(mWord.getName());
////        mTranslateBtn = findViewById(R.id.btn_translate);
//        translationTextView = findViewById(R.id.tv_translation);
//        mClickableSentence = findViewById(R.id.cw_sentence);
//        mWordContext = findViewById(R.id.tv_context);
//        loadingIndicator = findViewById(R.id.pb_loading_indicator);
//        imagesLoadingIndicator = findViewById(R.id.pb_images_loading_indicator);
//        playSoundImageView = findViewById(R.id.play_new_word);
//        wordPictureRecyclerView = findViewById(R.id.rv_word_pictures);
//        addToDictBtn = findViewById(R.id.btn_add_to_dict);
//        imagesErrorMessage = findViewById(R.id.tv_images_error_msg);

        playSoundImageView.setOnClickListener(soundBtn -> mWord.pronounce());
//        mTranslateBtn.setOnClickListener(btn -> {
//            String word = mWordEditText.getText().toString();
//            if (!TextUtils.isEmpty(word)) {
//                closeKeyboard();
//                mWord.setName(word);
//                translate();
//            } else {
//                emptyFieldErrorMessage();
//            }
//        });
        addToDictBtn.setOnClickListener(btn -> addToDictionary());

        mAdapter = new ImagesAdapter(this);
        wordPictureRecyclerView.setLayoutManager(
                new GridLayoutManager(this, 3)
        );
        wordPictureRecyclerView.setAdapter(mAdapter);

        if (getIntent().hasExtra(Intent.EXTRA_PROCESS_TEXT)) {
            setWordFromContext();
        }
    }

    private void setActionBar() {
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
        mActionBar.setTitle(R.string.add_words);
    }

    private void translate() {
        mAdapter.clear();
        hidePronunciation();
        mViewModel.translateWord(this, mWord.getName());
    }

    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this).get(CardViewModel.class);
    }

    @TargetApi(23)
    private void setWordFromContext() {
        if (getIntent() != null) {
            CharSequence text = getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
            if (text != null && !TextUtils.isEmpty(text)) {
                if (text.toString().contains(" ")) {
                    showClickableSentence(text.toString());
                } else {
                    setWord(text.toString());
                    mViewModel.translateWord(this, mWord.getName());
                }
            }
        }
    }

    private void addToDictionary() {
        CardEntry newCard = new CardEntry(mWord.getName(), mWord.getTranslation());
        if (selectedImages != null && !selectedImages.isEmpty()) {
            String imagesString = mViewModel.savePictures(selectedImages);
            newCard.setImage(imagesString);
        }
        if (mWord.hasSoundURL()) {
            String soundURL = mWord.getSoundURL();
            mViewModel.saveSound(soundURL);
            newCard.setPronunciation(soundURL);
        }

        mViewModel.insert(newCard);
        Log.d(TAG, "The word " + newCard.getWord() + " has been inserted to the Database");
        Toast.makeText(this, "The word " + mWord.getName() + " added to the dictionary.", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_word_from_context_menu, menu);
//        MenuItem item = menu.findItem(R.id.action_search);
//        mSearchView.setMenuItem(item);
//        mSearchView.showSearch();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home: {
                onBackPressed();
                break;
            }
//            case R.id.actionRefresh: {
//                mAdapter.clear();
//                viewModel.translateWord(this, mWord.getName());
//                break;
//            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void showTranslationErrorMessage() {
        translationTextView.setText(R.string.error_no_translation);
        forbidAddToDict();
    }

    public void emptyFieldErrorMessage() {
        translationTextView.setText(R.string.error_empty_field);
    }

    public void hideTranslationErrorMessage() {
        translationTextView.setText("");
    }

    public void showImagesErrorMessage() {
        imagesErrorMessage.setText(R.string.error_no_images);
        imagesErrorMessage.setVisibility(TextView.VISIBLE);
        forbidAddToDict();
    }

    public void hideImagesErrorMessage() {
        imagesErrorMessage.setText(R.string.error_no_images);
    }

    public void forbidAddToDict() {
        if (canAddToDict) {
            canAddToDict = false;
            addToDictBtn.setEnabled(false);
        }
    }

    private void allowAddToDict() {
        if (!canAddToDict) {
            canAddToDict = true;
            addToDictBtn.setEnabled(true);
        }
    }

    public void onLoadTranslation() {
        hideTranslationErrorMessage();
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    public void onLoadTranslationFinished() {
        loadingIndicator.setVisibility(View.INVISIBLE);
    }

    public void onLoadImages() {
        imagesErrorMessage.setVisibility(TextView.INVISIBLE);
        imagesLoadingIndicator.setVisibility(View.VISIBLE);
    }

    public void onLoadImagesFinished() {
        imagesLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    public void setWord(String name) {
//        mWordEditText.setText(name);
        mClickableSentence.setText(name);
        mWord.setName(name);
    }

    public void setTranslation(String translation) {
        translationTextView.setText(translation);
        mWord.setTranslation(translation);
        allowAddToDict();
    }

    public void setImages(ArrayList<Pair<String, Bitmap>> images) {
        mAdapter.setImages(images);
        allowAddToDict();
    }

    public void addImage(Pair<String, Bitmap> image) {
        mAdapter.addImage(image);
    }

    public void setSound(String soundUrl) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(soundUrl);
            mediaPlayer.setOnPreparedListener(mp -> {
                showPronunciation();
                mediaPlayer.start();
            });
            mediaPlayer.prepareAsync();
            mWord.setPronunciation(mediaPlayer, soundUrl);
        } catch (IOException e) {
            e.printStackTrace();
            mWord.setPronunciation(soundUrl);
        }

    }

    private HashSet<ImageViewBitmap> selectedImages = new HashSet<>();

    @Override
    public void onImageClick(ImageViewBitmap imgView) {
        if (!selectedImages.contains(imgView)) {
            selectedImages.add(imgView);
            imgView.setBackground(getResources().getDrawable(R.drawable.image_shadow));
        } else {
            selectedImages.remove(imgView);
            imgView.setBackgroundColor(getResources().getColor(android.R.color.white));
        }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showPronunciation() {
        playSoundImageView.setVisibility(ImageButton.VISIBLE);
    }

    public void hidePronunciation() {
        playSoundImageView.setVisibility(ImageButton.INVISIBLE);
    }

    private void showClickableSentence(String sentence) {
//        mWordEditText.setVisibility(View.INVISIBLE);
//        mTranslateBtn.setVisibility(View.INVISIBLE);
        mClickableSentence.setText(sentence, new WordClick() {
            @Override
            public void onClick(String word) {
                mWord.setName(word);
                translate();
            }
        });
        mClickableSentence.setVisibility(View.VISIBLE);
    }


    @Override
    public void setContext(ArrayList<String[]> context) {
//        mWord.addContext(context);
        String[] firstExample = context.get(0);
        mWordContext.setText(getString(R.string.context_pattern, firstExample[0], firstExample[1]));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: {
                    return mFirstExploreFragment;
                }
                case 1: {
                    return mSecondExploreFragment;
                }
                default:
                    return mFirstExploreFragment;
            }


        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return getString(R.string.pref_lang_eng_label);
                case 1: return getString(R.string.pref_lang_ru_label);
                default: return getString(R.string.pref_lang_eng_label);
            }
        }
    }

    public void closeExplore(View view) {
        onBackPressed();
    }

    public ExploreViewModel setExploreRecyclerView(RecyclerView rv) {
        rv.setLayoutManager(
                new GridLayoutManager(this, 2)
        );
        rv.setAdapter(suggestionAdapter);

        ExploreViewModelFactory factory = new ExploreViewModelFactory(this);
        mExploreViewModel = ViewModelProviders.of(this, factory).get(ExploreViewModel.class);
        mExploreViewModel.getOpenSearchWords().observe(this, new Observer<String[]>() {
            @Override
            public void onChanged(String[] words) {
                Log.d(TAG, "suggested words changed");
                mExploreViewModel.loadSearchCards(words, suggestionAdapter);
            }
        });

        return mExploreViewModel;
    }


}
