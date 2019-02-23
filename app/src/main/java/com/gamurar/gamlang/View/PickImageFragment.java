package com.gamurar.gamlang.View;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.gamurar.gamlang.Model.database.CardEntry;
import com.gamurar.gamlang.R;
import com.gamurar.gamlang.ViewModel.CardCreationViewModel;
import com.gamurar.gamlang.Word;
import com.gamurar.gamlang.utilities.ImagesAdapter;
import com.gamurar.gamlang.utilities.ImagesLoadable;
import com.gamurar.gamlang.utilities.WordInfoLoader;
import com.gamurar.gamlang.views.ImageViewBitmap;

import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PickImageFragment extends Fragment implements WordInfoLoader, ImagesAdapter.ImageClickListener, ImagesLoadable {
    private static final String TAG = "PickImageFragment";

    private TextView mWord;
    private TextView mTranslation;
    private TextView mIPA;
    private RecyclerView mImagesRV;
    private CardCreationViewModel viewModel;
    private ImagesAdapter mAdapter;
    private LottieAnimationView mPlayBtn;
    private Button mAddImagesBtn;
    private CardCreationActivity parent;
    private LottieAnimationView mPreloader;
    private boolean isSoundLoading;

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
        isSoundLoading = true;
        mPreloader = view.findViewById(R.id.preloader);
        parent = (CardCreationActivity) getActivity();
        viewModel = parent.viewModel;
        viewModel.getWord().setUpdatesListener(new Word.UpdatesListener() {
            @Override
            public void IPAupdated() {
                setIPA();
            }

            @Override
            public void soundUpdated() {
                mPlayBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.accent_circle));
                mPlayBtn.setAnimation(R.raw.speaker);
                mPlayBtn.setMinFrame(67);
                mPlayBtn.loop(false);
                isSoundLoading = false;
            }

            @Override
            public void soundNotFound() {
                mPlayBtn.setVisibility(View.GONE);
            }
        });

        mWord = view.findViewById(R.id.word);
        mTranslation = view.findViewById(R.id.translation);
        mIPA = view.findViewById(R.id.IPA);
        mImagesRV = view.findViewById(R.id.rv_word_pictures);
        mPlayBtn = view.findViewById(R.id.play_btn);
        mPlayBtn.setAnimation(R.raw.material_loding);
        mPlayBtn.playAnimation();
        mAddImagesBtn = view.findViewById(R.id.btn_add_images);

        mWord.setText(viewModel.getWord().getName());
        mTranslation.setText(viewModel.getWord().getTranslation());

        mPlayBtn.setOnClickListener(v -> {
            if (isSoundLoading) return;
            Word word = viewModel.getWord();
            word.pronounce();
            mPlayBtn.setMinAndMaxFrame(67, 90);
            mPlayBtn.playAnimation();
            word.setPronunciationListener(() -> {
                mPlayBtn.setMinAndMaxFrame(0, 18);
                mPlayBtn.playAnimation();
            });

        });
        mAddImagesBtn.setOnClickListener(v -> addToDictionary());

        mAdapter = new ImagesAdapter(this);
        mImagesRV.setLayoutManager(
                new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false)
        );
        mImagesRV.setAdapter(mAdapter);

    }

    public void setIPA() {
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
        if (mAdapter.getItemCount() > 3) {
            mPreloader.cancelAnimation();
            mPreloader.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoadImagesStart() {
    }

    @Override
    public void onLoadImagesFinished() {
        mPreloader.cancelAnimation();
        mPreloader.setVisibility(View.GONE);
    }

    @Override
    public void showImagesErrorMessage() {

    }

    private void addToDictionary() {
        Word word = viewModel.getWord();
        CardEntry newCardEntry = new CardEntry(word.getName(), word.getTranslation());
        if (selectedImages != null && !selectedImages.isEmpty()) {
            viewModel.savePictures(selectedImages);
        }
        if (word.hasSoundURL()) {
            String soundURL = word.getSoundURL();
            viewModel.saveSound(soundURL);
        }

        viewModel.insert(newCardEntry);
        Log.d(TAG, "The word " + newCardEntry.getQuestion() + " has been inserted to the Database");
        parent.onCardAdded();
    }


    @Override
    public void update() {

    }
}
