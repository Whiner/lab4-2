package org.donntu.android.lab4_2.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NotEnoughWordsException extends Exception {
    public NotEnoughWordsException(String message) {
        super(message);
    }

}
