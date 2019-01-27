package com.hfad.gamlang;

import android.content.Intent;
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

import com.hfad.gamlang.Model.database.AppDatabase;
import com.hfad.gamlang.Model.database.CardEntry;
import com.hfad.gamlang.ViewModel.CardViewModel;
import com.hfad.gamlang.tasks.TranslateQueryTask;
import com.hfad.gamlang.utilities.AppExecutors;
import com.hfad.gamlang.utilities.ImagesAdapter;
import com.hfad.gamlang.utilities.StorageHelper;
import com.hfad.gamlang.views.ImageViewBitmap;

import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
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
    private CardViewModel mViewModel;
    private StorageHelper storageHelper;

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

        addToDictBtn.setOnClickListener(btn -> addToDictionary());

        mAdapter = new ImagesAdapter(this);
        wordPictureRecyclerView.setLayoutManager(
                new GridLayoutManager(getContext(), 3)
        );
        wordPictureRecyclerView.setAdapter(mAdapter);
        new TranslateQueryTask(this).translate();

        mViewModel = ViewModelProviders.of(this).get(CardViewModel.class);
        storageHelper = new StorageHelper(getContext());
    }

    private void addToDictionary() {
        final CardEntry newCard;
        if (selectedImages != null && !selectedImages.isEmpty()) {
            StringBuilder strBuilder = new StringBuilder();
            for (ImageViewBitmap image : selectedImages) {
                strBuilder.append(storageHelper.saveImage(image));
                strBuilder.append(" ");
            }
            String imagesString = strBuilder.toString();

            newCard = new CardEntry(word.name, word.getTranslation(), imagesString);
        } else {
            newCard = new CardEntry(word.name, word.getTranslation());
        }
        
        mViewModel.insert(newCard);
        Log.d(TAG, "The word " + newCard.getWord() + " has been inserted to the Database");
        Toast.makeText(getContext(), "The word " + word.getName() + " added to the dictionary.", Toast.LENGTH_SHORT).show();
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
