package com.translator.service.codactor.ai.chat.functions.directives.test;

public interface MatrixRotatorService {
    /**
     * Rotates a given 2D matrix of numbers by 90 degrees clockwise.
     *
     * @param matrix the 2D matrix to be rotated
     * @return a new 2D matrix that is rotated 90 degrees clockwise
     * @throws IllegalArgumentException if the input matrix is not valid
     */
    int[][] rotateMatrix(int[][] matrix) throws IllegalArgumentException;
}
