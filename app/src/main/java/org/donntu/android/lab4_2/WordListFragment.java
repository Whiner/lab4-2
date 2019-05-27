package org.donntu.android.lab4_2;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.donntu.android.lab4_2.dto.Word;
import org.donntu.android.lab4_2.service.DatabaseService;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class WordListFragment extends Fragment {
    private DatabaseService databaseService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseService = new DatabaseService(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.words_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView lvMain = view.findViewById(R.id.list);


        List<String> words = databaseService
                .findAll()
                .stream()
                .map(Word::toString)
                .collect(toList());

        // создаем адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this.getContext(),
                android.R.layout.simple_list_item_1,
                words
        );

        // присваиваем адаптер списку
        lvMain.setAdapter(adapter);
    }
}
