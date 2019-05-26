package org.donntu.android.lab4_2.dto;

import android.content.ContentValues;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Word {
    private int id;
    private String russianTranslate;
    private String englishTranslate;
    private boolean isInArchive;
    private int correctAnswersCount = 0;

    public ContentValues toContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put("russian", russianTranslate);
        contentValues.put("english", englishTranslate);
        contentValues.put("isInArchive", isInArchive);
        return contentValues;
    }

    public Word(String russianTranslate, String englishTranslate, boolean isInArchive) {
        this.russianTranslate = russianTranslate;
        this.englishTranslate = englishTranslate;
        this.isInArchive = isInArchive;
    }

    public void incCorrectAnswer() {
        correctAnswersCount++;
    }

    public void sendToArchive() {
        isInArchive = true;
        correctAnswersCount = 0;
    }

    public String toFileForm() {
        return "[" + russianTranslate + "]:[" + englishTranslate + "]:[" + isInArchive + "]";
    }
}
