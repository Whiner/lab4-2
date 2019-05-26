package org.donntu.android.lab4_2.service;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import java.util.TimerTask;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MyTimerTask extends TimerTask {
    private TextView textView;
    private Activity activity;

    @Override
    public void run() {
        activity.runOnUiThread(() -> textView.setVisibility(View.INVISIBLE));
    }
}
