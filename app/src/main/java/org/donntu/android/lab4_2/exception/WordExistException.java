package org.donntu.android.lab4_2.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class WordExistException extends Exception {
    public WordExistException(String message) {
        super(message);
    }

}
