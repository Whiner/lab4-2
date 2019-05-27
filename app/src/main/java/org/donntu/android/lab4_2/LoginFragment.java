package org.donntu.android.lab4_2;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.navigation.Navigation;

public class LoginFragment extends Fragment {
    private static String LOGIN = "root";
    private static String PASSWORD = "root";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView login = view.findViewById(R.id.login);
        TextView password = view.findViewById(R.id.password);
        Button loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            if(login.getText().toString().equals(LOGIN)) {
                if(password.getText().toString().equals(PASSWORD)) {
                    Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment)
                            .navigate(R.id.action_loginFragment_to_homeFragment);
                }
            }
        });

    }
}
