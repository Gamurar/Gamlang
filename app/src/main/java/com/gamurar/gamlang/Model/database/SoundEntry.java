package com.gamurar.gamlang.Model.database;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "Sounds")
public class SoundEntry {

    @PrimaryKey
    @ColumnInfo(name="file_name")
    @NonNull
    private String fileName;


    public SoundEntry(@NonNull String fileName) {
        this.fileName = fileName;
    }

    @NonNull
    public String getFileName() {
        return fileName;
    }

    public void setFileName(@NonNull String fileName) {
        this.fileName = fileName;
    }
}
