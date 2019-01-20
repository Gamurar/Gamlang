package com.hfad.gamlang.utilities;

import com.hfad.gamlang.database.AppDatabase;
import com.hfad.gamlang.utilities.MyDictionaryViewModel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MyDictionaryViewModelFactory extends ViewModelProvider.NewInstanceFactory {


    private final AppDatabase mDb;

    public MyDictionaryViewModelFactory(AppDatabase database) {
        mDb = database;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MyDictionaryViewModel(mDb);
    }
}
