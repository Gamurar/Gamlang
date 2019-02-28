package com.gamurar.gamlang.Model.database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SoundDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSound(SoundEntry soundEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateSound(SoundEntry soundEntry);

    @Delete
    void deleteImage(SoundEntry soundEntry);

    @Query("DELETE FROM Sounds WHERE file_name IN(:fileNames)")
    void deleteSoundsByFileName(String[] fileNames);

    @Query("DELETE FROM Sounds WHERE file_name = :fileName")
    void deleteSoundByFileName(String fileName);

    @Query("DELETE FROM Sounds")
    void deleteAllSounds();
}