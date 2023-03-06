package org.abhijith.utils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
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
                System.out.println( "Please enter valid input" );
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
            System.out.print( message + ": " );
            String fileName = keyboardInput.nextLine();
            if ( !fileName.substring( fileName.length() - 3 ).equals( "csv" ) )
                throw new RuntimeException( "Enter a valid CSV file" );
            BufferedReader CSVFile = new BufferedReader( new FileReader( fileName ) );
            System.out.println( CSVFile.readLine() );
            return CSVFile;
        } catch ( Exception error ) {
            System.out.println( "Enter a valid CSV file" );
            return null;
        }
    }

    public BufferedReader openCourseCSVFile( String courseCode, int year, int semester, String departmentID, String message ) {
        try {
            String expectedFileName = new StringBuilder().append( courseCode ).append( "_" ).append( year ).append( "_" ).append( semester ).append( "_" ).append( departmentID ).append( ".csv" ).toString();
            System.out.print( message + ": ");
            String filePath = keyboardInput.nextLine();
            File fileName = new File( filePath );
            if ( !filePath.substring( filePath.length() - 3 ).equals( "csv" ) )
                throw new RuntimeException( "Enter a valid CSV file" );

            if ( !fileName.getName().equals( expectedFileName ) ) {
                throw new RuntimeException( "Unexpected file name" );
            }
            BufferedReader CSVFile = new BufferedReader( new FileReader( filePath ) );
            return CSVFile;
        } catch ( Exception error ) {
            System.out.println( error.getMessage() );
            return null;
        }
    }
}
