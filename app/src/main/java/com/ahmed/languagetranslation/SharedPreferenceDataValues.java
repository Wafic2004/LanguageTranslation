package com.ahmed.languagetranslation;

public class SharedPreferenceDataValues {
    //Variables to get conversation data
    String fromLang;
    String toLang;
    String firstConversationFrom;
    String firstConversationTo;
    String secondConversationFrom;
    String secondConversationTo;

    //Setting the values by Constructor
    SharedPreferenceDataValues (String fromLang, String toLang, String firstConversationFrom, String firstConversationTo, String secondConversationFrom, String secondConversationTo) {
        this.fromLang = fromLang;
        this.toLang = toLang;
        this.firstConversationFrom = firstConversationFrom;
        this.firstConversationTo = firstConversationTo;
        this.secondConversationFrom = secondConversationFrom;
        this.secondConversationTo = secondConversationTo;
    }

    String getFromLang() {
        return fromLang;
    }

    String getToLang() {
        return toLang;
    }

    String getFirstConversationFrom() {
        return firstConversationFrom;
    }

    String getFirstConversationTo() {
        return firstConversationTo;
    }

    String getSecondConversationFrom() {
        return secondConversationFrom;
    }

    String getSecondConversationTo() {
        return secondConversationTo;
    }
}
