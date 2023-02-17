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
        try {
            FileDialog dialog = new FileDialog( (Frame) null, "Select File to Open" );
            dialog.setMode( FileDialog.LOAD );
            dialog.setVisible( true );
            String fileName = dialog.getDirectory() + dialog.getFile();
            if ( !fileName.substring( fileName.length() - 3 ).equals( "csv" ) )
                throw new RuntimeException( "Enter a valid CSV file" );
            dialog.dispose();
            BufferedReader CSVFile = new BufferedReader( new FileReader( fileName ) );
            return CSVFile;
        } catch ( Exception error ) {
            System.out.println( "Enter a valid CSV file" );
            keyboardInput = new Scanner( System.in );
            return null;
        }
    }

    public BufferedReader openCourseCSVFile( String courseCode, int year, int semester ) {
        try {
            String expectedFileName = courseCode + "_" + year + "_" + semester + ".csv";
            FileDialog dialog = new FileDialog( (Frame) null, "Select File to Open" );
            dialog.setMode( FileDialog.LOAD );
            dialog.setVisible( true );
            String fileName = dialog.getDirectory() + dialog.getFile();
            if ( !fileName.substring( fileName.length() - 3 ).equals( "csv" ) )
                throw new RuntimeException( "Enter a valid CSV file" );
            dialog.dispose();

            if ( !dialog.getFile().equals( expectedFileName ) ) {
                System.out.println( dialog.getFile() + " " + expectedFileName );
                throw new RuntimeException( "Unexpected file name" );
            }
            BufferedReader CSVFile = new BufferedReader( new FileReader( fileName ) );
            return CSVFile;
        } catch ( Exception error ) {
            System.out.println( error.getMessage() );
            return null;
        }
    }
}
