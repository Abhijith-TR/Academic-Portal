package org.abhijith.utils;

import java.util.HashMap;

public class Utils {
    public static int getGradeValue( String grade ) {
        // Returns the numeric value corresponding to the particular grade
        return switch ( grade ) {
            case "A" -> 10;
            case "A-" -> 9;
            case "B" -> 8;
            case "B-" -> 7;
            case "C" -> 6;
            case "C-" -> 5;
            case "D" -> 4;
            case "E" -> 2;
            default -> 0;
        };
    }

    public static void prettyPrintGrades( int year, int semester, String[][] records ) {
        // If there is no content to print, simply return
        if ( records == null || records.length == 0 ) {
            return;
        }
        // Print a summary of the semester i.e., the year and semester
        System.out.printf( "Records - Year: %d Semester: %d\n", year, semester );
        prettyPrint( new String[]{ "Course Code", "Course Title", "Grade", "Credits" }, records );
    }

    public static void prettyPrintGrades( int year, int semester, double SGPA, String[][] records ) {
        // If there is no content to print, simply return
        if ( records == null || records.length == 0 ) {
            return;
        }
        // Print a summary of the semester i.e., the year, semester and SGPA
        System.out.printf( "Records - Year: %d Semester: %d SGPA: %.2f\n", year, semester, SGPA );
        prettyPrint( new String[]{ "Course Code", "Course Title", "Grade", "Credits" }, records );
    }

    public static void prettyPrint( String[] headings, String[][] printableContent ) {
        // If there is no content or headings to print, do not print anything
        if ( headings == null || printableContent == null ) return;
        // If any of the arrays inside the content list is empty, do not print anything
        for ( String[] temp: printableContent ) if ( temp == null ) return;
        int rows = printableContent.length;
        // If there is no content to print, return
        if ( rows == 0 ) {
            return;
        }
        // All the arrays inside printable content must be of the same length
        // If not do not print anything
        int   columns            = printableContent[0].length;
        if ( columns != headings.length ) return;
        for ( String[] temp : printableContent ) if ( temp.length != columns ) return;
        int[] maximumFieldLength = new int[columns];

        // Determining the maximum required field length of any column using only the heading
        for ( int i = 0; i < headings.length; i++ )
            maximumFieldLength[i] = Math.max( headings[i].length(), maximumFieldLength[i] );

        // Determining the maximum required field length of any column using the actual contents in the array
        for ( int row = 0; row < rows; row++ ) {
            for ( int column = 0; column < columns; column++ ) {
                maximumFieldLength[column] = Math.max( maximumFieldLength[column], printableContent[row][column].length() );
            }
        }
        // Insert some spaces between the content being printed
        for ( int i = 0; i < columns; i++ ) maximumFieldLength[i] += 3;

        // Creating the format string for printing onto the screen
        String formatString = "";
        for ( int i = 0; i < columns; i++ ) {
            formatString += "%-" + maximumFieldLength[i] + "s";
        }
        formatString += "\n";

        // Once the format string is created, actually print the contents onto the screen
        System.out.format( formatString, (Object[]) headings );
        for ( final Object[] rowContent : printableContent ) {
            System.out.format( formatString, rowContent );
        }
        System.out.println();
    }

    public static void prettyPrintCreditRequirements( HashMap<String, Double> creditsLeft ) {
        // If there are no requirements to print, simple return
        if ( creditsLeft == null || creditsLeft.size() == 0 ) return;
        String[]   headings = new String[creditsLeft.size()];
        String[][] records  = new String[1][creditsLeft.size()];

        // The headings are the keys found in the hashmap
        int i = 0;
        for ( String heading : creditsLeft.keySet() ) {
            headings[i] = heading;
            i++;
        }

        // The values under each heading are the values found for the corresponding keys in the hashmap
        for ( i = 0; i < creditsLeft.size(); i++ ) {
            records[0][i] = String.valueOf( creditsLeft.get( headings[i] ) );
        }

        prettyPrint( headings, records );
    }
}
