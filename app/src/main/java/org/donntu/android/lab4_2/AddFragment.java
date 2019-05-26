package org.donntu.android.lab4_2;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.navigation.Navigation;

import org.donntu.android.lab4_2.exception.WordExistException;
import org.donntu.android.lab4_2.service.AddService;

public class AddFragment extends Fragment {
    private AddService addService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addService = new AddService(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }


    private void init() {
        FragmentActivity activity = getActivity();
        if(activity != null) {
            Button addButton = activity.findViewById(R.id.addButton);
            TextView russian = activity.findViewById(R.id.russianWord);
            TextView english = activity.findViewById(R.id.englishWord);
            addButton.setOnClickListener(v1 -> {
                addWord(russian.getText().toString(), english.getText().toString());
                russian.setText("");
                english.setText("");
            });

            Button backButton = activity.findViewById(R.id.backButton);
            backButton.setOnClickListener(v1 -> {
                russian.setText("");
                english.setText("");
                backToHome();
            });
        }
    }

    private void addWord(String russian, String english) {
        try {
            addService.addWord(russian, english);
            Toast.makeText(this.getContext(), "Добавлено!", Toast.LENGTH_SHORT).show();
        } catch (WordExistException e) {
            Toast.makeText(this.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void backToHome() {
        Navigation.findNavController(getView()).popBackStack();
    }
}
