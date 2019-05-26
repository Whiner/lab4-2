package org.donntu.android.lab4_2;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.navigation.Navigation;

import org.donntu.android.lab4_2.dto.TranslationType;
import org.donntu.android.lab4_2.exception.NotEnoughWordsException;
import org.donntu.android.lab4_2.service.MyTimerTask;
import org.donntu.android.lab4_2.service.SpeechService;
import org.donntu.android.lab4_2.service.WordService;

import java.util.Locale;
import java.util.Timer;

public class GameFragment extends Fragment {
    private WordService wordService;
    private SpeechService speechService;
    private Timer timer = new Timer();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wordService = new WordService(
                TranslationType.RUS_TO_ENG,
                this.getContext()
        );
        speechService = new SpeechService(this.getContext());
        init();
    }

    private void init() {
        wordService.updateData();
        FragmentActivity activity = getActivity();
        if (isAvailableWordsExist() && activity != null) {

            activity.setContentView(R.layout.game);
            wordService.setViews(
                    new TextView[]{
                            activity.findViewById(R.id.word1),
                            activity.findViewById(R.id.word2),
                            activity.findViewById(R.id.word3),
                            activity.findViewById(R.id.word4)}
            );
            wordService.setMainView(activity.findViewById(R.id.word));
            nextWord();

            setLayoutListener(R.id.word1_layout);
            setLayoutListener(R.id.word2_layout);
            setLayoutListener(R.id.word3_layout);
            setLayoutListener(R.id.word4_layout);


            Button playButton = activity.findViewById(R.id.playWord);
            playButton.setOnClickListener(v -> {
                TextView word = activity.findViewById(R.id.word);
                listenWord(word.getText().toString());
            });

            Button stopButton = activity.findViewById(R.id.stopButton);
            stopButton.setOnClickListener(v -> backToHome());

        }
    }

    private void listenWord(String text) {
        if (wordService.getType() == TranslationType.RUS_TO_ENG) {
            speechService.speech(text, new Locale("ru"));
        } else {
            speechService.speech(text, Locale.ENGLISH);
        }
    }

    private boolean isAvailableWordsExist() {
        try {
            wordService.checkAvailableWords();
            return true;
        } catch (NotEnoughWordsException e) {
            showExceptionDialog(e.getMessage());
            return false;
        }
    }


    private void setLayoutListener(int layout) {
        LinearLayout word_layout = getActivity().findViewById(layout);
        word_layout.setOnClickListener(event -> {
            boolean right = wordService.checkAnswer(
                    (TextView) word_layout.getChildAt(0)
            );
            showResult(right);
            nextWord();
        });
    }

    private void showMessage(String text, int color) {
        TextView message = getActivity().findViewById(R.id.message);
        if (message != null) {
            MyTimerTask myTimerTask = new MyTimerTask(message, this.getActivity());
            timer.schedule(myTimerTask, 1000);
            message.setText(text);
            message.setTextColor(color);
            message.setVisibility(View.VISIBLE);
        }
    }

    private void showResult(boolean success) {
        if (success) {
            showMessage("Правильно!", Color.GREEN);
        } else {
            showMessage("Не то(", Color.RED);
        }
    }

    private void nextWord() {
        try {
            wordService.nextWord();
        } catch (NotEnoughWordsException e) {
            showExceptionDialog(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showExceptionDialog(String message) {
        new AlertDialog.Builder(this.getContext())
                .setMessage(message)
                .setNeutralButton("Хорошо", (dialog, which) -> this.backToHome())
                .create()
                .show();
    }

    private void backToHome() {
        Navigation.findNavController(getView()).navigate(R.id.homeFragment);
    }
}
