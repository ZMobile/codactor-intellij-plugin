package com.translator.service.codactor.ai.chat.matrix_test;

import static org.junit.Assert.assertArrayEquals;
import org.junit.Before;
import org.junit.Test;

public class EmptyMatrixRotationTest {

    private TwoDimensionalMatrixRotatorService rotatorService;

    @Before
    public void setUp() {
        // Assuming an implementation class is named TwoDimensionalMatrixRotatorServiceImpl
        rotatorService = new TwoDimensionalMatrixRotatorServiceImpl();
    }

    @Test
    public void testRotateEmptyMatrix() {
        int[][] emptyMatrix = {};
        int[][] expectedOutput = {};

        int[][] result90DegreesClockwise = rotatorService.rotate90DegreesClockwise(emptyMatrix);
        assertArrayEquals("Rotating an empty matrix 90 degrees clockwise should return an empty matrix.", expectedOutput, result90DegreesClockwise);

        int[][] result90DegreesCounterClockwise = rotatorService.rotate90DegreesCounterClockwise(emptyMatrix);
        assertArrayEquals("Rotating an empty matrix 90 degrees counter-clockwise should return an empty matrix.", expectedOutput, result90DegreesCounterClockwise);

        int[][] result180Degrees = rotatorService.rotate180Degrees(emptyMatrix);
        assertArrayEquals("Rotating an empty matrix 180 degrees should return an empty matrix.", expectedOutput, result180Degrees);

        int[][] result360Degrees = rotatorService.rotateByDegrees(emptyMatrix, 360);
        assertArrayEquals("Rotating an empty matrix by 360 degrees should return an empty matrix.", expectedOutput, result360Degrees);
    }
}
