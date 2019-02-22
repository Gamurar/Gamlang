package com.gamurar.gamlang.Model.database;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "sound",
        foreignKeys = {
        @ForeignKey(entity = CardEntry.class,
                parentColumns = "id",
                childColumns = "card_id")},
        indices = {@Index("card_id")})
public class SoundEntry {

    @PrimaryKey
    @ColumnInfo(name="file_name")
    @NonNull
    private String fileName;

    @ColumnInfo(name="card_id")
    private int cardId;

    public SoundEntry(@NonNull String fileName, int cardId) {
        this.fileName = fileName;
        this.cardId = cardId;
    }

    @NonNull
    public String getFileName() {
        return fileName;
    }

    public void setFileName(@NonNull String fileName) {
        this.fileName = fileName;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }
}
