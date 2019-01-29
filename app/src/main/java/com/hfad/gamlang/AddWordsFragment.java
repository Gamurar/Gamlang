package com.hfad.gamlang;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hfad.gamlang.Model.database.CardEntry;
import com.hfad.gamlang.ViewModel.CardViewModel;
import com.hfad.gamlang.utilities.ImagesAdapter;
import com.hfad.gamlang.views.ImageViewBitmap;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AddWordsFragment extends Fragment implements ImagesAdapter.ImageClickListener {

    private static final String TAG = "AddWordsFragment";

    private TextView wordTextView;
    private TextView translationTextView;
    private TextView imagesErrorMessage;
    private ImageView playSoundImageView;
    private TextView mWordContext;
    private RecyclerView wordPictureRecyclerView;
    private ProgressBar loadingIndicator;
    private ProgressBar imagesLoadingIndicator;
    private Button addToDictBtn;
    private Word mWord = new Word("universe");

    private ImagesAdapter mAdapter;

    private static boolean canAddToDict = true;
    private CardViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_words, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init(view);
        setupViewModel();
        setWordFromContext();
        mViewModel.translateWord(this, mWord.getName());
        super.onViewCreated(view, savedInstanceState);
    }

    private void init(@NonNull View view) {
        setHasOptionsMenu(true);
        wordTextView = view.findViewById(R.id.tv_word);
        wordTextView.setText(mWord.getName());
        translationTextView = view.findViewById(R.id.tv_translation);
        //mWordContext = view.findViewById(R.id.tv_word_context);
        loadingIndicator = view.findViewById(R.id.pb_loading_indicator);
        imagesLoadingIndicator = view.findViewById(R.id.pb_images_loading_indicator);
        playSoundImageView = view.findViewById(R.id.play_new_word);
        wordPictureRecyclerView = view.findViewById(R.id.rv_word_pictures);
        addToDictBtn = view.findViewById(R.id.btn_add_to_dict);
        imagesErrorMessage = view.findViewById(R.id.tv_images_error_msg);

        playSoundImageView.setOnClickListener(soundBtn -> mWord.pronounce());
        addToDictBtn.setOnClickListener(btn -> addToDictionary());

        mAdapter = new ImagesAdapter(this);
        wordPictureRecyclerView.setLayoutManager(
                new GridLayoutManager(getContext(), 3)
        );
        wordPictureRecyclerView.setAdapter(mAdapter);
    }

    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this).get(CardViewModel.class);
//        mViewModel.getQueriedWord().observe(this, new Observer<Word>() {
//            @Override
//            public void onChanged(Word word) {
//                mWord = word;
//                wordTextView.setText(mWord.getName());
//                if (mWord.isTranslated()) {
//                    translationTextView.setText(mWord.getTranslation());
//                }
//            }
//        });
    }

    private void setWordFromContext() {
        if (getArguments() != null) {
            CharSequence text = getArguments().getCharSequence(Intent.EXTRA_PROCESS_TEXT);
            if (text != null && !TextUtils.isEmpty(text)) {
                mWord.setName(text.toString());
            }
        }
    }

    private void addToDictionary() {
        CardEntry newCard = new CardEntry(mWord.getName(), mWord.getTranslation());
        String soundURL = null;
        if (selectedImages != null && !selectedImages.isEmpty()) {
            if (mWord.hasSoundURL()) {
                soundURL = mWord.getSoundURL();
                mViewModel.saveSound(soundURL);
            }
            String imagesString = mViewModel.savePictures(selectedImages);
            newCard = new CardEntry(mWord.getName(), mWord.getTranslation(), imagesString, soundURL);
        }

        mViewModel.insert(newCard);
        Log.d(TAG, "The word " + newCard.getWord() + " has been inserted to the Database");
        Toast.makeText(getContext(), "The word " + mWord.getName() + " added to the dictionary.", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.add_word_from_context_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.actionRefresh: {
                mViewModel.translateWord(this, mWord.getName());
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void showTranslationErrorMessage() {
        translationTextView.setText(R.string.error_no_translation);
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
        wordTextView.setText(name);
        mWord.setName(name);
    }

    public void setTranslation(String translation) {
        translationTextView.setText(translation);
        mWord.setTranslation(translation);
    }

    public void setImages(HashMap<String, Bitmap> images) {
        mAdapter.setImages(images);
        allowAddToDict();
    }

    public void setSound(String soundUrl) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(soundUrl);
            mediaPlayer.prepare();
            mediaPlayer.start();
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


}
