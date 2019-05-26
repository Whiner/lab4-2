package org.donntu.android.lab4_2.dto;


import org.donntu.android.lab4_2.service.WordService;

import lombok.Getter;

@Getter
public enum TranslationType {
    RUS_TO_ENG(WordService.RUS_TO_ENG), ENG_TO_RUS(WordService.ENG_TO_RUS);

    private String value;
    TranslationType(String value) {
        this.value = value;
    }
}
