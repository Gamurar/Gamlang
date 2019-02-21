package com.gamurar.gamlang.View;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gamurar.gamlang.Model.database.CardEntry;
import com.gamurar.gamlang.R;
import com.gamurar.gamlang.ViewModel.CardCreationViewModel;
import com.gamurar.gamlang.Word;
import com.gamurar.gamlang.utilities.ImagesAdapter;
import com.gamurar.gamlang.utilities.ImagesLoadable;
import com.gamurar.gamlang.utilities.Updatable;
import com.gamurar.gamlang.views.ImageViewBitmap;

import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PickImageFragment extends Fragment implements Updatable, ImagesAdapter.ImageClickListener, ImagesLoadable {
    private static final String TAG = "PickImageFragment";

    private TextView mWord;
    private TextView mTranslation;
    private TextView mIPA;
    private RecyclerView mImagesRV;
    private CardCreationViewModel viewModel;
    private ImagesAdapter mAdapter;
    private ImageButton mPlayBtn;
    private Button mAddImagesBtn;
    private CardCreationActivity parent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pick_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init(view);
        super.onViewCreated(view, savedInstanceState);
    }

    private void init(View view) {
        parent = (CardCreationActivity) getActivity();
        viewModel = parent.viewModel;

        mWord = view.findViewById(R.id.word);
        mTranslation = view.findViewById(R.id.translation);
        mIPA = view.findViewById(R.id.IPA);
        mImagesRV = view.findViewById(R.id.rv_word_pictures);
        mPlayBtn = view.findViewById(R.id.play_btn);
        mAddImagesBtn = view.findViewById(R.id.btn_add_images);

        mWord.setText(viewModel.getWord().getName());
        mTranslation.setText(viewModel.getWord().getTranslation());

        mPlayBtn.setOnClickListener(v -> viewModel.getWord().pronounce());
        mAddImagesBtn.setOnClickListener(v -> addToDictionary());

        mAdapter = new ImagesAdapter(this);
        mImagesRV.setLayoutManager(
                new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false)
        );
        mImagesRV.setAdapter(mAdapter);
    }

    @Override
    public void update() {
        String IPA = viewModel.getWord().getIPA();
        if (IPA != null && !IPA.isEmpty()) {
            mIPA.setText(getString(R.string.IPA, IPA));
            mIPA.setVisibility(TextView.VISIBLE);
        }
    }

    private HashSet<ImageViewBitmap> selectedImages = new HashSet<>();
    @Override
    public void onImageClick(ImageViewBitmap imgView) {
        if (!selectedImages.contains(imgView)) {
            selectedImages.add(imgView);
            imgView.setBorderColor(getResources().getColor(R.color.colorAccent));
        } else {
            selectedImages.remove(imgView);
            imgView.setBorderColor(getResources().getColor(android.R.color.white));
        }
    }

    @Override
    public void addImage(Pair<String, Bitmap> image) {
        mAdapter.addImage(image);
    }

    @Override
    public void onLoadImagesStart() {

    }

    @Override
    public void onLoadImagesFinished() {

    }

    @Override
    public void showImagesErrorMessage() {

    }

    private void addToDictionary() {
        Word word = viewModel.getWord();
        CardEntry newCard = new CardEntry(word.getName(), word.getTranslation());
        if (selectedImages != null && !selectedImages.isEmpty()) {
            String imagesString = viewModel.savePictures(selectedImages);
            newCard.setImage(imagesString);
        }
        if (word.hasSoundURL()) {
            String soundURL = word.getSoundURL();
            viewModel.saveSound(soundURL);
            newCard.setPronunciation(soundURL);
        }

        viewModel.insert(newCard);
        Log.d(TAG, "The word " + newCard.getWord() + " has been inserted to the Database");
        parent.onCardAdded();
    }


}
