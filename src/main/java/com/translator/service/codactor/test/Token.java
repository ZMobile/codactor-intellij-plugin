package com.translator.service.codactor.test;

public class Token {
    String value;
    int position;  // Position in the entire string (character index)

    Token(String value, int position) {
        this.value = value;
        this.position = position;
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
}
