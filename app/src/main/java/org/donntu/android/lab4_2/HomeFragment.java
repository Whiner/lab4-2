package org.donntu.android.lab4_2;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.Navigation;

import org.donntu.android.lab4_2.dto.TranslationType;
import org.donntu.android.lab4_2.dto.Word;
import org.donntu.android.lab4_2.service.FileService;
import org.donntu.android.lab4_2.service.SpeechService;
import org.donntu.android.lab4_2.service.WordService;

import java.util.List;

public class HomeFragment extends Fragment {
    private String EXPORT_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String EXPORT_FILE_NAME = "words.txt";

    private WordService wordService;

    private SpeechService speechService;

    private FileService fileService = new FileService();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wordService = new WordService(
                TranslationType.RUS_TO_ENG,
                this.getContext()
        );
        speechService = new SpeechService(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMainLayout(view);
    }

    private void initMainLayout(View view) {
        updateAvailableWordsCount();
        Button startButton = view.findViewById(R.id.startButton);
        startButton.setOnClickListener(event ->
                Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment).navigate(R.id.gameFragment));

        Button addNewWordButton = view.findViewById(R.id.addNewWordButton);
        addNewWordButton.setOnClickListener(v ->
                Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment)
                        .navigate(R.id.action_homeFragment_to_addFragment));

        Button changeLangButton = view.findViewById(R.id.changeLangButton);
        TextView langTextBox = view.findViewById(R.id.langTextbox);
        langTextBox.setText(wordService.getType().getValue());
        changeLangButton.setOnClickListener(event -> langTextBox.setText(wordService.revertLang()));
    }


    private void showExceptionDialog(String message) {
        new AlertDialog.Builder(this.getContext())
                .setMessage(message)
                .setNeutralButton("Хорошо", (dialog, which) -> this.updateAvailableWordsCount()) // тута
                .create()
                .show();
    }

    private void updateAvailableWordsCount() {
        TextView wordsCount = getActivity().findViewById(R.id.availableWordsCountTextView);
        wordsCount.setText(String.valueOf(wordService.getAvailableWordsCount()));
    }


    public void importWords(MenuItem item) {
        fileService.openFileDialog(this.getContext(), fileName -> {
            try {
                List<Word> words = fileService.importFromFile(fileName);
                wordService.addAll(words);
                updateAvailableWordsCount();
                Toast.makeText(this.getContext(), "Успешно импортировано!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                showExceptionDialog(e.getMessage());
            }
        });
    }

    public void exportWords(MenuItem item) {
        try {
            fileService.exportToFile(EXPORT_FILE_PATH, EXPORT_FILE_NAME, wordService.getWords());
        } catch (Exception e) {
            showExceptionDialog(e.getMessage());
        }
    }

    public void refreshArchive(MenuItem item) {
        wordService.refreshArchive();
        updateAvailableWordsCount();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wordService != null) {
            wordService.close();
        }
        speechService.destroy();
    }
}
