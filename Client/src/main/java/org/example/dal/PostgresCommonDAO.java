package org.example.dal;

import org.example.daoInterfaces.CommonDAO;

import java.sql.*;
import java.util.ArrayList;

public class PostgresCommonDAO implements CommonDAO {
    protected Connection databaseConnection;

    public PostgresCommonDAO( String connectionURL, String username, String password ) {
        try {
            databaseConnection = DriverManager.getConnection(
                    connectionURL,
                    username,
                    password
            );
        } catch ( Exception error ) {
            System.out.println( "Database error. Please try again later" );
            System.exit( 0 );
        }
    }

    @Override
    // Returns the current year and semester if successful
    // Otherwise returns default year and session i.e., 2020-1
    public int[] getCurrentAcademicSession() {
        try {
            PreparedStatement getSessionQuery = databaseConnection.prepareStatement( "SELECT * FROM current_year_and_semester ORDER BY year, semester DESC LIMIT 1" );
            ResultSet         currentSession  = getSessionQuery.executeQuery();

            currentSession.next();
            int currentYear     = currentSession.getInt( 1 );
            int currentSemester = currentSession.getInt( 2 );

            return new int[]{ currentYear, currentSemester };
        } catch ( SQLException error ) {
            System.out.println( error.getMessage() );
            return new int[]{ 2020, 1 };
        }
    }

    public String[][] getStudentGradesForSemester( String entryNumber, int year, int semester ) {
        try {
            PreparedStatement gradeQuery = databaseConnection.prepareStatement( "SELECT course_code, course_title, grade, credits FROM student_course_registration NATURAL JOIN course_catalog WHERE entry_number = ? AND year = ? AND semester = ?" );

            gradeQuery.setString( 1, entryNumber );
            gradeQuery.setInt( 2, year );
            gradeQuery.setInt( 3, semester );
            ResultSet gradeQueryResult = gradeQuery.executeQuery();

            ArrayList<String[]> records = new ArrayList<>();
            while ( gradeQueryResult.next() ) {
                String courseCode  = gradeQueryResult.getString( 1 );
                String courseTitle = gradeQueryResult.getString( 2 );
                String grade       = gradeQueryResult.getString( 3 );
                String credits     = gradeQueryResult.getString( 4 );
                records.add( new String[]{ courseCode, courseTitle, grade, credits } );
            }
            return records.toArray( new String[records.size()][] );
        } catch ( Exception error ) {
            System.out.println( "Database Error. Try again later" );
            return new String[][]{};
        }
    }

    @Override
    public int getBatch( String entryNumber ) {
        try {
            PreparedStatement batchQuery = databaseConnection.prepareStatement( "SELECT batch FROM student WHERE entry_number = ?" );
            batchQuery.setString( 1, entryNumber );
            ResultSet batchQueryResult = batchQuery.executeQuery();
            batchQueryResult.next();
            int batch = batchQueryResult.getInt( 1 );
            return batch;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Try again later" );
            return -1;
        }
    }

    @Override
    public boolean checkCourseCatalog( String courseCode ) {
        try {
            PreparedStatement checkCourseCatalogQuery = databaseConnection.prepareStatement("SELECT course_code FROM course_catalog WHERE course_code = ?");
            checkCourseCatalogQuery.setString( 1, courseCode );
            ResultSet checkCourseCatalogQueryResult = checkCourseCatalogQuery.executeQuery();
            return checkCourseCatalogQueryResult.next();
        } catch ( Exception error ) {
            System.out.println( "Database Error. Try again later" );
            return false;
        }
    }
}
