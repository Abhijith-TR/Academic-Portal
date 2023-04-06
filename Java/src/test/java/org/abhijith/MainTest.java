package org.abhijith;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

class MainTest {
    private       InputStream           systemInput  = System.in;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        System.setIn( systemInput );
    }

    private void setInputToString( String input ) {
        InputStream in = new ByteArrayInputStream( input.getBytes() );
        System.setIn( in );
    }

    @Test
    void main() {
        Main main = new Main();
        String input = "4\nADMIN1\niitropar\n";
        setInputToString( input );
        Main.main( null );
    }
}