package com.gamurar.gamlang.View;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamurar.gamlang.R;
import com.gamurar.gamlang.utilities.WordClick;
import com.gamurar.gamlang.views.ClickableWords;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ExploreProcessTextFragment extends Fragment {

    private static final String TAG = "ExploreProcessTextFragm";

    private ClickableWords mClickableSentence;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore_process_text, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        mClickableSentence = view.findViewById(R.id.sentence);
        mClickableSentence.setText("This is a test sentence sentence sentence", new WordClick() {
            @Override
            public void onClick(String word) {
//                mWord.setName(word);
//                translate();
            }
        });
    }
}
