package edu.virginia.cs;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Students: This is just an example test file to show you where to
 * put test classes. You should not add meaningful tests of your code
 * to this file, but make your own test classes.
 */

public class ExampleTest {
    @Test
    public void testSQRT() {
        double input = 25.0;
        double expected = 5.0;
        double tolerance = 1e-10;
        double actual = Math.sqrt(input);
        assertEquals(expected, actual, tolerance);
    }
}
