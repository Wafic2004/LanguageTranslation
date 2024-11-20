package com.ahmed.languagetranslation;

public class NotDownloadedLanguageCodeSeparation {
    //Variable to get the code and name of the language and to set or return on call
    String languageCode;
    String languageName;

    NotDownloadedLanguageCodeSeparation(String languageCode, String languageName) {
        this.languageCode = languageCode;
        this.languageName = languageName;
    }

    //Below functions is to return the code or name of the language
    String getLanguageCode() {
        return languageCode;
    }

    String getLanguageName() {
        return languageName;
    }
}
