package org.abhijith.utils;

import org.abhijith.users.Faculty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilsTest {
    private       InputStream           systemInput  = System.in;
    private final PrintStream           systemOutput = System.out;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut( new PrintStream( outputStream ) );
    }

    @AfterEach
    void tearDown() {
        try {
            outputStream.flush();
        } catch ( Exception error ) {
        }
        System.setOut( systemOutput );
        System.setIn( systemInput );
    }

    private void setInputToString( String input ) {
        InputStream in = new ByteArrayInputStream( input.getBytes() );
        System.setIn( in );
    }

    private String extractOutput( int index ) {
        String[] output = outputStream.toString().split( "\\r?\\n" );
        return output[output.length - 1 + index].trim();
    }

    @Test
    void getGradeValueTest() {
        assertEquals( 10, Utils.getGradeValue( "A" ) );
        assertEquals( 9, Utils.getGradeValue( "A-" ) );
        assertEquals( 8, Utils.getGradeValue( "B" ) );
        assertEquals( 7, Utils.getGradeValue( "B-" ) );
        assertEquals( 6, Utils.getGradeValue( "C" ) );
        assertEquals( 5, Utils.getGradeValue( "C-" ) );
        assertEquals( 4, Utils.getGradeValue( "D" ) );
        assertEquals( 2, Utils.getGradeValue( "E" ) );
        assertEquals( 0, Utils.getGradeValue( "F" ) );
        assertEquals( 0, Utils.getGradeValue( "-" ) );
    }

    @Test
    void printTest() {
        Utils.prettyPrint( new String[]{ "A" }, new String[][]{ { "A", "B" }, { "B" } } );
        assertEquals( "", extractOutput( 0 ) );

        Utils.prettyPrint( new String[]{ "A", "B" }, new String[][]{ { "A", "B" }, { "B" } } );
        assertEquals( "", extractOutput( 0 ) );

        Utils.prettyPrint( new String[]{ "A" }, new String[][]{} );
        assertEquals( "", extractOutput( 0 ) );

        Utils.prettyPrint( null, new String[][]{} );
        assertEquals( "", extractOutput( 0 ) );

        Utils.prettyPrint( new String[]{ "A" }, null );
        assertEquals( "", extractOutput( 0 ) );

        Utils.prettyPrint( new String[]{ "A" }, new String[][]{ null } );
        assertEquals( "", extractOutput( 0 ) );

        Utils.prettyPrintGrades( 2023, 2, null );
        assertEquals( "", extractOutput( 0 ) );

        Utils.prettyPrintGrades( 2023, 2, new String[][]{} );
        assertEquals( "", extractOutput( 0 ) );

        Utils.prettyPrintGrades( 2023, 2, 8.5, new String[][]{} );
        assertEquals( "", extractOutput( 0 ) );

        Utils.prettyPrintGrades( 2023, 2, 8.5, null );
        assertEquals( "", extractOutput( 0 ) );
    }
}