package org.donntu.android.lab4_2.service;

import android.content.Context;

import org.donntu.android.lab4_2.dto.Word;
import org.donntu.android.lab4_2.exception.WordExistException;


public class AddService {
    private DatabaseService databaseService;

    public AddService(Context context) {
        this.databaseService = new DatabaseService(context);
    }

    public void addWord(String russian, String english) throws WordExistException {
        Word word = new Word(russian, english, false);
        if (!databaseService.insert(word)) {
            throw new WordExistException("Слово существует в базе на одном из языков");
        }
    }
}
