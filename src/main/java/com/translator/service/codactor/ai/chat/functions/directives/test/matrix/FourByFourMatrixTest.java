package com.translator.service.codactor.ai.chat.functions.directives.test.matrix;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FourByFourMatrixTest {
    
    private TwoDimensionalMatrixRotatorService matrixRotatorService;

    @Before
    public void setUp() {
        // Assuming TwoDimensionalMatrixRotatorServiceImpl is available and properly implements the interface
        matrixRotatorService = new TwoDimensionalMatrixRotatorServiceImpl();
    }

    @Test
    public void testRotate4x4Matrix() {
        int[][] originalMatrix = {
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 10, 11, 12},
            {13, 14, 15, 16}
        };

        int[][] expectedRotatedMatrix = {
            {13, 9, 5, 1},
            {14, 10, 6, 2},
            {15, 11, 7, 3},
            {16, 12, 8, 4}
        };

        int[][] rotatedMatrix = matrixRotatorService.rotateMatrix90DegreesClockwise(originalMatrix);

        // Check if the rotated matrix matches the expected result
        Assert.assertArrayEquals(expectedRotatedMatrix, rotatedMatrix);
    }
}
