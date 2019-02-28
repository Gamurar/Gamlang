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
public interface IntermediateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(IntermediateEntry intermediateEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(IntermediateEntry intermediateEntry);

    @Delete
    void delete(IntermediateEntry intermediateEntry);

    @Query("SELECT * FROM Intermediate WHERE card_id IN(:ids)")
    LiveData<List<IntermediateEntry>> loadSoundsAndImagesByCardId(Integer[] ids);

    @Query("SELECT * FROM Intermediate WHERE card_id = :id")
    LiveData<List<IntermediateEntry>> loadSoundsAndImagesByCardId(int id);

    @Query("SELECT * FROM Intermediate")
    LiveData<List<IntermediateEntry>> loadAllCardsData();
}
