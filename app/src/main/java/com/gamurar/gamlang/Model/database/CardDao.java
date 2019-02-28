package com.gamurar.gamlang.Model.database;

import java.util.Date;
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

    @Query("SELECT * FROM Cards")
    List<CardEntry> loadAllCards();

    @Insert
    long insertCard(CardEntry cardEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCard(CardEntry taskEntry);

    @Query("UPDATE Cards " +
            "SET last_review = :lastReview, next_review = :nextReview " +
            "WHERE id = :cardId")
    void updateReview(int cardId, Date lastReview, Date nextReview);

    @Delete
    void deleteCard(CardEntry cardEntry);

    @Query("DELETE FROM Cards WHERE id IN(:cardsId)")
    void deleteCardsById(Integer[] cardsId);

    @Query("DELETE FROM Cards")
    void deleteAllCards();

    @Query("SELECT * FROM Cards WHERE id = :id")
    CardEntry loadCardById(int id);
}