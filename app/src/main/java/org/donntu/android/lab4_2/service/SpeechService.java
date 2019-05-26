package org.donntu.android.lab4_2.service;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;

public class SpeechService {
    private TextToSpeech textToSpeech;

    public SpeechService(Context context) {
        textToSpeech = new TextToSpeech(context, status -> Log.d("tss_debug", String.valueOf(status)));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void speech(String text, Locale locale) {
        textToSpeech.setLanguage(locale);
        textToSpeech.speak(text, QUEUE_ADD, null);
    }

    public void destroy() {
        textToSpeech.shutdown();
    }
}
