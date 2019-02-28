package com.gamurar.gamlang.Model.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "Intermediate",
        foreignKeys = {
                @ForeignKey(entity = CardEntry.class,
                        parentColumns = "id",
                        childColumns = "card_id",
                        onDelete = CASCADE),
                @ForeignKey(entity = ImageEntry.class,
                        parentColumns = "file_name",
                        childColumns = "image_file",
                        onDelete = CASCADE),
                @ForeignKey(entity = SoundEntry.class,
                        parentColumns = "file_name",
                        childColumns = "sound_file",
                        onDelete = CASCADE)},
        indices = {@Index("card_id"),
                    @Index("image_file"),
                    @Index("sound_file")})
public class IntermediateEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name="card_id")
    private int cardId;
    @ColumnInfo(name="image_file")
    private String imageFile;
    @ColumnInfo(name="sound_file")
    private String soundFile;

    public IntermediateEntry(int cardId) {
        this.cardId = cardId;
    }

    @Ignore
    public IntermediateEntry(int cardId, String imageFile, String soundFile) {
        this.cardId = cardId;
        this.imageFile = imageFile;
        this.soundFile = soundFile;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    public String getSoundFile() {
        return soundFile;
    }

    public void setSoundFile(String soundFile) {
        this.soundFile = soundFile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
