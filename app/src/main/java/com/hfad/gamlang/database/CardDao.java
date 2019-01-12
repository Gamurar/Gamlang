package com.hfad.gamlang.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface CardDao {

    @Query("SELECT * FROM card")
    LiveData<List<CardEntry>> loadAllCards();

    @Insert
    void insertCard(CardEntry cardEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCard(CardEntry taskEntry);

    @Delete
    void deleteCard(CardEntry taskEntry);

    @Query("SELECT * FROM card WHERE id = :id")
    CardEntry loadCardById(int id);
}