package com.translator.service.codactor.ai.chat.functions.directives.test;

public interface MatrixRotatorService {

    /**
     * Rotates the given 2D matrix by 90 degrees clockwise.
     *
     * @param matrix the 2D matrix to be rotated
     * @return the rotated 2D matrix
     */
    int[][] rotateClockwise(int[][] matrix);

    /**
     * Rotates the given 2D matrix by 90 degrees counterclockwise.
     *
     * @param matrix the 2D matrix to be rotated
     * @return the rotated 2D matrix
     */
    int[][] rotateCounterClockwise(int[][] matrix);
}
