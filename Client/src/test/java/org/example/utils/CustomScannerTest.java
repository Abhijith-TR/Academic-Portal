package org.example.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomScannerTest {
    CustomScanner keyboardInput;
    @BeforeEach
    void setUp() {
        keyboardInput = new CustomScanner();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void doubleInput() {
    }

    @Test
    void integerInput() {
    }

    @Test
    void stringInput() {
    }

    @Test
    void fileInput() {
        keyboardInput.CSVFileInput( "CSV File" );
    }
}