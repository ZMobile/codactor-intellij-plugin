package com.translator.service.codactor.ai.chat.functions.directives.test.matrix;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class EmptyMatrixTest {

    private TwoDimensionalMatrixRotatorService matrixRotator;

    @Before
    public void setUp() {
        matrixRotator = new TwoDimensionalMatrixRotatorServiceImpl();
    }

    @Test
    public void testEmptyMatrixRotation() {
        int[][] emptyMatrix = new int[0][0];
        int[][] result = matrixRotator.rotateMatrix90DegreesClockwise(emptyMatrix);

        assertNotNull("The result should not be null", result);
        assertEquals("The result should be an empty matrix", 0, result.length);
    }
}
