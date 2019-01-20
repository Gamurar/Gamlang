package com.hfad.gamlang;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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

import com.hfad.gamlang.database.AppDatabase;
import com.hfad.gamlang.database.CardEntry;
import com.hfad.gamlang.tasks.ImagesQueryTask;
import com.hfad.gamlang.tasks.TranslateQueryTask;
import com.hfad.gamlang.utilities.ImagesAdapter;

import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AddWordsFragment extends Fragment implements ImagesAdapter.ImageClickListener {

    private static final String TAG = "AddWordsFragment";

    private TextView wordTextView;
    public TextView translationTextView;
    public TextView imagesErrorMessage;
    private ImageView playSoundImageView;
    private RecyclerView wordPictureRecyclerView;
    public ProgressBar loadingIndicator;
    public ProgressBar imagesLoadingIndicator;
    private Button addToDictBtn;

    public ImagesAdapter mAdapter;
    public static Word word = new Word("way");

    private static boolean canAddToDict = true;

    private AppDatabase mDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_words, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init(view);
        super.onViewCreated(view, savedInstanceState);
    }

    private void init(@NonNull View view) {
        setHasOptionsMenu(true);
        wordTextView = view.findViewById(R.id.tv_word);
        translationTextView = view.findViewById(R.id.tv_translation);
        loadingIndicator = view.findViewById(R.id.pb_loading_indicator);
        imagesLoadingIndicator = view.findViewById(R.id.pb_images_loading_indicator);
        playSoundImageView = view.findViewById(R.id.iv_play);
        wordPictureRecyclerView = view.findViewById(R.id.rv_word_pictures);
        addToDictBtn = view.findViewById(R.id.btn_add_to_dict);
        imagesErrorMessage = view.findViewById(R.id.tv_images_error_msg);
        word.setName("way");

        if (getArguments() != null) {
            CharSequence text = getArguments().getCharSequence(Intent.EXTRA_PROCESS_TEXT);
            if (text != null && !TextUtils.isEmpty(text)) {
                word.setName(text.toString());
            }
        }
        wordTextView.setText(word.getName());

        playSoundImageView.setOnClickListener(soundBtn -> word.playPronunc());

        addToDictBtn.setOnClickListener(btn -> {
            final CardEntry newCard = new CardEntry(word.name, word.getTranslation());
            final Toast toast = Toast.makeText(getContext(), "The word " + word.getName() + " added to the dictionary.", Toast.LENGTH_SHORT);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.cardDao().insertCard(newCard);
                    Log.d(TAG, "The word " + newCard.getWord() + " has been inserted to the Database");
                    toast.show();
                }
            });
        });

        //

        mAdapter = new ImagesAdapter(this);
        wordPictureRecyclerView.setLayoutManager(
                new GridLayoutManager(getContext(), 3)
        );
        wordPictureRecyclerView.setAdapter(mAdapter);
        new TranslateQueryTask(this).translate();
        new ImagesQueryTask(this).fetchImages();

        mDb = AppDatabase.getInstance(getActivity().getApplicationContext());
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
                new TranslateQueryTask(this).translate();
                new ImagesQueryTask(this).fetchImages();

                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void forbidAddToDict() {
        if (canAddToDict) {
            canAddToDict = false;
            addToDictBtn.setEnabled(false);
        }
    }

    public void allowAddToDict() {
        if (!canAddToDict) {
            canAddToDict = true;
            addToDictBtn.setEnabled(true);
        }
    }

    private HashSet<Drawable> selectedImages = new HashSet<>();

    @Override
    public void onImageClick(ImageView imgView) {
        Drawable image = imgView.getDrawable();
        if (selectedImages.contains(image)) {
            imgView.setBackgroundColor(getResources().getColor(android.R.color.white));
            selectedImages.remove(image);
        } else {
            imgView.setBackground(getResources().getDrawable(R.drawable.image_shadow));
            selectedImages.add(imgView.getDrawable());
        }
    }
}
