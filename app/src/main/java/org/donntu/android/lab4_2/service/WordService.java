package org.donntu.android.lab4_2.service;

import android.content.Context;
import android.widget.TextView;

import org.donntu.android.lab4_2.dto.TranslationType;
import org.donntu.android.lab4_2.dto.Word;
import org.donntu.android.lab4_2.exception.NotEnoughWordsException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lombok.Getter;

public class WordService {
    public static final String RUS_TO_ENG = "Русский -> Английский";
    public static final String ENG_TO_RUS = "Английский -> Русский";

    private int answersVersionsMaxCount = 4;
    private int maxCorrectAnswersCount = 3;

    @Getter
    private TranslationType type;
    @Getter
    private List<Word> words;
    private TextView[] views;
    private DatabaseService databaseService;

    private Map<TextView, Word> answersVersions = new HashMap<>();
    private TextView rightAnswer;

    Random random = new Random();

    private TextView mainView;

    public WordService(TranslationType type, Context context) {
        this.type = type;
        databaseService = new DatabaseService(context);
        updateData();
    }

    public void nextWord() throws Exception {
        rightAnswer = null;
        generateAnswerVersions();

        for (TextView textView : views) {
            String text;
            if (type == TranslationType.RUS_TO_ENG) {
                text = answersVersions.get(textView).getEnglishTranslate();
            } else {
                text = answersVersions.get(textView).getRussianTranslate();
            }
            text = text.substring(0, 1).toUpperCase() + text.substring(1);
            textView.setText(text);
        }
        Word word = answersVersions.get(rightAnswer);
        if (word != null) {
            String text;
            if (type == TranslationType.RUS_TO_ENG) {
                text = word.getRussianTranslate();
            } else {
                text = word.getEnglishTranslate();
            }
            text = text.substring(0, 1).toUpperCase() + text.substring(1);
            mainView.setText(text);
        } else {
            throw new Exception("Ответ = null");
        }
    }

    private void generateAnswerVersions() throws Exception {
        answersVersions.clear();
        checkAvailableWords();
        int rightAnswerIndex = random.nextInt(answersVersionsMaxCount);
        for (int i = 0; i < views.length; i++) {
            if (i == rightAnswerIndex) {
                rightAnswer = views[i];
            }
            answersVersions.put(views[i], getNextRandomAnswerVersion());
        }
    }

    public void checkAvailableWords() throws NotEnoughWordsException {
        if (countWordsInArchive() >= words.size() - answersVersionsMaxCount) {
            throw new NotEnoughWordsException(
                    "Доступных слов слишком мало, либо они в архиве. " +
                            "Очистите архив или добавьте еще слов."
            );
        }
    }

    private Word getNextRandomAnswerVersion() throws Exception {
        if (words.size() <= answersVersions.size()
                || answersVersions.size() >= answersVersionsMaxCount) {
            throw new Exception("Не хватает слов");
        }

        Word word;
        do {
            word = words.get(random.nextInt(words.size()));
        } while (answersVersions.containsValue(word) || word.isInArchive());
        return word;
    }


    public String revertLang() {
        switch (type) {
            case RUS_TO_ENG:
                this.type = TranslationType.ENG_TO_RUS;
                return ENG_TO_RUS;
            case ENG_TO_RUS:
                this.type = TranslationType.RUS_TO_ENG;
                return RUS_TO_ENG;
            default:
                return "";
        }
    }

    public void updateData() {
        List<Word> all = databaseService.findAll();
        if (words != null && !words.isEmpty()) {
            for (Word newWord : all) {
                for (Word oldWord : words) {
                    if (newWord.getId() == oldWord.getId()) {
                        newWord.setCorrectAnswersCount(oldWord.getCorrectAnswersCount());
                    }
                }
            }
        }
        words = all;
    }

    public boolean isRightAnswer(TextView textView) {
        if (!answersVersions.isEmpty() && rightAnswer != null) {
            return textView == rightAnswer;
        }
        return false;
    }

    public boolean checkAnswer(TextView textView) {
        if (isRightAnswer(textView)) {
            Word word = answersVersions.get(textView);
            if (word != null) {
                word.incCorrectAnswer();
                if (word.getCorrectAnswersCount() >= maxCorrectAnswersCount) {
                    word.sendToArchive();
                    databaseService.sendToArchive(word);
                }
            }
            return true;
        }
        return false;
    }

    public void close() {
        databaseService.close();
    }

    public void setViews(TextView[] textViews) {
        this.views = textViews;
    }

    public void setMainView(TextView main) {
        this.mainView = main;
    }

    private int countWordsInArchive() {
        int count = 0;
        for (Word word : words) {
            if (word.isInArchive()) {
                count++;
            }
        }
        return count;
    }

    public int getAvailableWordsCount() {
        return databaseService.getAvailableWordsCount();
    }

    public void refreshArchive() {
        databaseService.refreshArchive();
        updateData();
    }

    public void addAll(List<Word> words) {
        databaseService.insertAllWords(words);
        updateData();
    }
}
