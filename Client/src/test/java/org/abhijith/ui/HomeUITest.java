package org.abhijith.ui;

import org.abhijith.daoInterfaces.PasswordDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.mockito.Mockito.when;

class HomeUITest {
    private       InputStream           systemInput  = System.in;
    private final PrintStream           systemOutput = System.out;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    HomeUI      homeUI = new HomeUI();
    PasswordDAO passwordDAO;

    @BeforeEach
    void setUp() {
//        System.setOut( new PrintStream( outputStream ) );
        passwordDAO = Mockito.mock( PasswordDAO.class );
        homeUI.setPasswordAuthConnection( passwordDAO );
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

    private String extractOutput( String splitter, int index ) {
        String[] output = outputStream.toString().split( "\\r?\\n" );
        output = output[output.length - 16 + index].split( splitter );
        return output[output.length - 1].trim();
    }

    @Test
    void mainInterface() {
        String input = "1\n2020CSB1062\niitropar\n12\n4\nADMIN1\niitropar\n";
        when( passwordDAO.logoutPreviousUser() ).thenReturn( true );
        when( passwordDAO.authenticateUser( "2020CSB1062", "iitropar", "STUDENT" )).thenReturn( true );
        when( passwordDAO.logLogoutEntry( "2020CSB1062", "STUDENT" )).thenReturn( true );
        setInputToString( input );
//        homeUI.mainInterface();
//        System.setOut( systemOutput );
//        System.out.println( outputStream );
    }
}