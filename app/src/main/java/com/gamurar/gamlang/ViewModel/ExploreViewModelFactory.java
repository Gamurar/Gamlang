package com.gamurar.gamlang.ViewModel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

public class ExploreViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public ExploreViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ExploreViewModel create(@NonNull Class modelClass) {
        return new ExploreViewModel(context);
    }
}
