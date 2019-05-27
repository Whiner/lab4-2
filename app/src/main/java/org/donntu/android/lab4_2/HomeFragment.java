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
    private FileService fileService = new FileService();
    private WordService wordService;

    private SpeechService speechService;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

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

        startButton.setOnClickListener(event -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("type", wordService.getType());
            Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment)
                    .navigate(R.id.gameFragment, bundle);
        });

        Button addNewWordButton = view.findViewById(R.id.addNewWordButton);
        addNewWordButton.setOnClickListener(v ->
                Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment)
                        .navigate(R.id.addFragment));

        Button listButton = view.findViewById(R.id.listButton);
        listButton.setOnClickListener(v ->
                Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment)
                        .navigate(R.id.listFragment));

        Button changeLangButton = view.findViewById(R.id.changeLangButton);
        TextView langTextBox = view.findViewById(R.id.langTextbox);
        langTextBox.setText(wordService.getType().getValue());
        changeLangButton.setOnClickListener(event -> langTextBox.setText(wordService.revertLang()));
    }


    private void updateAvailableWordsCount() {
        TextView wordsCount = getActivity().findViewById(R.id.availableWordsCountTextView);
        wordsCount.setText(String.valueOf(wordService.getAvailableWordsCount()));
    }


    public void refreshArchive() {
        wordService.refreshArchive();
        updateAvailableWordsCount();
    }

    public void importWords() {
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

    public void exportWords() {
        try {
            fileService.exportToFile(EXPORT_FILE_PATH, EXPORT_FILE_NAME, wordService.getWords());
        } catch (Exception e) {
            showExceptionDialog(e.getMessage());
        }
    }


    private void showExceptionDialog(String message) {
        new AlertDialog.Builder(this.getContext())
                .setMessage(message)
                .setNeutralButton("Хорошо", (dialog, which) -> this.updateAvailableWordsCount()) // тута
                .create()
                .show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wordService != null) {
            wordService.close();
        }
        speechService.destroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, 1, 0, "Импортировать словарь").setOnMenuItemClickListener(item -> {
            importWords();
            return true;
        });
        menu.add(0, 2, 0, "Экспортировать словарь").setOnMenuItemClickListener(item -> {
            exportWords();
            return true;
        });
        menu.add(0, 3, 0, "Сбросить словарь").setOnMenuItemClickListener(item -> {
            refreshArchive();
            return true;
        });
        /*inflater.inflate(R.menu.main, menu);*/
        super.onCreateOptionsMenu(menu, inflater);
    }
}
