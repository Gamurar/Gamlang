package com.gamurar.gamlang.Model.database;

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
    long insertCard(CardEntry cardEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCard(CardEntry taskEntry);

    @Delete
    void deleteCard(CardEntry cardEntry);

    @Query("DELETE FROM card WHERE id IN(:cardsId)")
    void deleteCardsById(Integer[] cardsId);

    @Query("DELETE FROM card")
    void deleteAllCards();

    @Query("SELECT * FROM card WHERE id = :id")
    CardEntry loadCardById(int id);

    @Query("SELECT image.file_name AS image, sound.file_name AS sound " +
            "FROM card " +
            "INNER JOIN image ON card.id = image.card_id " +
            "INNER JOIN sound ON card.id = sound.card_id " +
            "WHERE card.id = :cardId")
    ImageAndSound loadImageAndSoundFileNamesByCardId(int cardId);
}