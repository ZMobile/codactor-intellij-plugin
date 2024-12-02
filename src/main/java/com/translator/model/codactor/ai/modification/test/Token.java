package com.translator.model.codactor.ai.modification.test;

public class Token {
    String value;
    int position;  // Position in the plain text string (character index)
    int intentLevel;  // Indentation level

    public Token(String value, int position) {
        this.value = value;
        this.position = position;
    }

    public Token(String value, int position, int intentLevel) {
        this.value = value;
        this.position = position;
        this.intentLevel = intentLevel;
    }

    @Override
    public String toString() {
        return value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getIntentLevel() {
        return intentLevel;
    }

    public void setIntentLevel(int intentLevel) {
        this.intentLevel = intentLevel;
    }
}
