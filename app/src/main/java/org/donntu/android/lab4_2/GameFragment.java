package org.donntu.android.lab4_2;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            wordService = new WordService(
                    (TranslationType) arguments.getSerializable("type"),
                    this.getContext()
            );
            if (speechService == null) {
                speechService = new SpeechService(this.getContext());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(view);
    }


    private void init(View view) {
        wordService.updateData();
        if (isAvailableWordsExist()) {
            wordService.setViews(
                    new TextView[]{
                            view.findViewById(R.id.word1),
                            view.findViewById(R.id.word2),
                            view.findViewById(R.id.word3),
                            view.findViewById(R.id.word4)}
            );
            wordService.setMainView(view.findViewById(R.id.word));
            nextWord();

            setLayoutListener(R.id.word1_layout);
            setLayoutListener(R.id.word2_layout);
            setLayoutListener(R.id.word3_layout);
            setLayoutListener(R.id.word4_layout);


            Button playButton = view.findViewById(R.id.playWord);
            playButton.setOnClickListener(v -> {
                TextView word = view.findViewById(R.id.word);
                listenWord(word.getText().toString());
            });

            Button stopButton = view.findViewById(R.id.stopButton);
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
        Navigation.findNavController(getView()).popBackStack();
    }
}
