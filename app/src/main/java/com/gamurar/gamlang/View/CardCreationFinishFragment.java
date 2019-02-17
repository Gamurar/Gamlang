package com.gamurar.gamlang.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gamurar.gamlang.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CardCreationFinishFragment extends Fragment {

    private Button mLearnBtn;
    private Button mAddWordBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_card_creation_finish, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init(view);
        super.onViewCreated(view, savedInstanceState);
    }

    private void init(View view) {
        mLearnBtn = view.findViewById(R.id.learn_btn);
        mAddWordBtn = view.findViewById(R.id.add_another_btn);

        mLearnBtn.setOnClickListener(v -> startActivity(LearnWordsActivity.class));
        mAddWordBtn.setOnClickListener(v -> startActivity(ExploreActivity.class));
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(getActivity(), cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
