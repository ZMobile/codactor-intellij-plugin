package com.translator.service.line;

public class LineCounterServiceImpl implements LineCounterService {
    @Override
    public int countLines(String code, int index) {
        int lineCounter = 1;
        for (int i = 0; i < index && i < code.length(); i++) {
            if (code.charAt(i) == '\n') {
                lineCounter++;
            }
        }
        return lineCounter;
    }
}
