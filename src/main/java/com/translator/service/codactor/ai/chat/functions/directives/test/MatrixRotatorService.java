package com.translator.service.codactor.ai.chat.functions.directives.test;

public interface MatrixRotatorService {
    /**
     * Rotates a 2D matrix of integers 90 degrees clockwise.
     *
     * @param matrix the original 2D matrix to rotate
     * @return a new 2D matrix rotated 90 degrees clockwise
     */
    int[][] rotateMatrix(int[][] matrix);
}
