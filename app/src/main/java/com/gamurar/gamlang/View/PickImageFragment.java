package com.gamurar.gamlang.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gamurar.gamlang.R;
import com.gamurar.gamlang.ViewModel.CardCreationViewModel;
import com.gamurar.gamlang.utilities.Updatable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PickImageFragment extends Fragment implements Updatable {
    private static final String TAG = "PickImageFragment";

    private TextView mWord;
    private TextView mTranslation;
    private TextView mIPA;
    private CardCreationViewModel viewModel;

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
        CardCreationActivity parent = (CardCreationActivity) getActivity();
        viewModel = parent.viewModel;

        mWord = view.findViewById(R.id.word);
        mTranslation = view.findViewById(R.id.translation);
        mIPA = view.findViewById(R.id.IPA);
        mWord.setText(viewModel.getWord().getName());
        mTranslation.setText(viewModel.getWord().getTranslation());
    }

    @Override
    public void update() {
        String IPA = viewModel.getWord().getIPA();
        mIPA.setText(getString(R.string.IPA, IPA));
    }
}
