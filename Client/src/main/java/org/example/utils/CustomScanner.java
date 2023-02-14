package org.example.utils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

public class CustomScanner {
    Scanner keyboardInput;

    public CustomScanner() {
        keyboardInput = new Scanner( System.in );
    }

    public double doubleInput( String message ) {
        while ( true ) {
            try {
                System.out.print( message + ": " );
                double input = keyboardInput.nextDouble();
                keyboardInput.nextLine();
                return input;
            } catch ( Exception error ) {
                System.out.println( "Please Enter Valid Input" );
                keyboardInput = new Scanner( System.in );
            }
        }
    }

    public int integerInput( String message ) {
        while ( true ) {
            try {
                System.out.print( message + ": " );
                int input = keyboardInput.nextInt();
                keyboardInput.nextLine();
                return input;
            } catch ( Exception error ) {
                System.out.println( "Please Enter Valid Input" );
                keyboardInput = new Scanner( System.in );
            }
        }
    }

    public String stringInput( String message ) {
        while ( true ) {
            try {
                System.out.print( message + ": " );
                return keyboardInput.nextLine();
            } catch ( Exception error ) {
                System.out.println( "Please Enter Valid Input" );
                keyboardInput = new Scanner( System.in );
            }
        }
    }

    public BufferedReader CSVFileInput( String message ) {
        while ( true ) {
            try {
                FileDialog dialog = new FileDialog( (Frame) null, "Select File to Open" );
                dialog.setMode( FileDialog.LOAD );
                dialog.setVisible( true );
                String fileName = dialog.getDirectory() + dialog.getFile();
                dialog.dispose();
                BufferedReader CSVFile  = new BufferedReader( new FileReader( fileName ) );
                return CSVFile;
            } catch ( Exception error ) {
                System.err.println( error.getMessage() );
                System.out.println( "Enter a valid file" );
                keyboardInput = new Scanner( System.in );
            }
        }
    }
}
