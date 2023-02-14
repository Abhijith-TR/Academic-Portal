package org.example.dal;

import org.example.daoInterfaces.AdminDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class PostgresAdminDAO extends PostgresCommonDAO implements AdminDAO {
    public PostgresAdminDAO( String connectionURL, String username, String password ) {
        super( connectionURL, username, password );
    }

    public String[][] getGradesOfCourse( String courseCode, int year, int semester ) {
        try {
            PreparedStatement getGradesQuery = databaseConnection.prepareStatement( "SELECT entry_number, grade FROM student_course_registration WHERE course_code = ? AND year = ? AND semester = ? ORDER BY entry_number" );
            getGradesQuery.setString( 1, courseCode );
            getGradesQuery.setInt( 2, year );
            getGradesQuery.setInt( 3, semester );
            ResultSet getGradesQueryResult = getGradesQuery.executeQuery();

            ArrayList<String[]> records = new ArrayList<>();
            while ( getGradesQueryResult.next() ) {
                String entry_number = getGradesQueryResult.getString( 1 );
                String grade        = getGradesQueryResult.getString( 2 );
                records.add( new String[]{ entry_number, grade } );
            }
            return records.toArray( new String[records.size()][] );
        } catch ( Exception error ) {
            System.out.println( error.getMessage() );
            System.out.println( "Database Error. Please try again later" );
            return new String[][]{};
        }
    }

    public boolean insertStudent( String entryNumber, String name, String departmentID, int batch ) {
        try {
            // You should probably set autocommit to false and execute both of the below statements together.
            PreparedStatement insertStudentQuery = databaseConnection.prepareStatement( "INSERT INTO student(entry_number, name, department_id, batch) VALUES (?, ?, ?, ?)" );
            insertStudentQuery.setString( 1, entryNumber );
            insertStudentQuery.setString( 2, name );
            insertStudentQuery.setString( 3, departmentID );
            insertStudentQuery.setInt( 4, batch );
            int successStatus = insertStudentQuery.executeUpdate();

            PreparedStatement insertPasswordQuery = databaseConnection.prepareStatement( "INSERT INTO user_login_details VALUES (?, ?, ?)" );
            insertPasswordQuery.setString( 1, entryNumber );
            // the default password is set here
            insertPasswordQuery.setString( 2, "iitropar" );
            insertPasswordQuery.setString( 3, "student" );
            successStatus &= insertPasswordQuery.executeUpdate();

            return successStatus == 1;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return false;
        }
    }

    public boolean insertFaculty( String facultyID, String name, String departmentID ) {
        try {
            PreparedStatement insertFacultyQuery = databaseConnection.prepareStatement( "INSERT INTO faculty(faculty_id, name, department_id) VALUES (?, ?, ?)" );
            insertFacultyQuery.setString( 1, facultyID );
            insertFacultyQuery.setString( 2, name );
            insertFacultyQuery.setString( 3, departmentID );
            int successStatus = insertFacultyQuery.executeUpdate();

            PreparedStatement insertPasswordQuery = databaseConnection.prepareStatement( "INSERT INTO user_login_details VALUES (?, ?, ?)" );
            insertPasswordQuery.setString( 1, facultyID );
            // the default password is set here
            insertPasswordQuery.setString( 2, "iitropar" );
            insertPasswordQuery.setString( 3, "faculty" );
            successStatus &= insertPasswordQuery.executeUpdate();
            return successStatus == 1;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return false;
        }
    }

    public boolean insertCourse( String courseCode, String courseTitle, double[] creditStructure, String[] prerequisites, String departmentID ) {
        try {
            PreparedStatement insertCourseQuery = databaseConnection.prepareStatement( "INSERT INTO course_catalog VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)" );
            insertCourseQuery.setString( 1, courseCode );
            insertCourseQuery.setString( 2, courseTitle );
            insertCourseQuery.setDouble( 3, creditStructure[0] );
            insertCourseQuery.setDouble( 4, creditStructure[1] );
            insertCourseQuery.setDouble( 5, creditStructure[2] );
            insertCourseQuery.setDouble( 6, creditStructure[3] );
            insertCourseQuery.setDouble( 7, creditStructure[4] );
            insertCourseQuery.setObject( 8, prerequisites );
            insertCourseQuery.setString( 9, departmentID );
            int insertCourseQueryResult = insertCourseQuery.executeUpdate();
            return insertCourseQueryResult == 1;
        } catch ( Exception error ) {
            System.out.println( error.getMessage() );
            System.out.println( "Database Error. Please try again later" );
            return false;
        }
    }

    @Override
    public boolean checkAllPrerequisites( String[] prerequisites ) {
        try {
            boolean coursesFound = true;
            for ( String course : prerequisites ) {
                PreparedStatement findCourseQuery = databaseConnection.prepareStatement( "SELECT course_code FROM course_catalog WHERE course_code = ?" );
                findCourseQuery.setString( 1, course );
                ResultSet findCourseQueryResult = findCourseQuery.executeQuery();
                coursesFound &= findCourseQueryResult.next();
            }
            return coursesFound;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return false;
        }
    }

    @Override
    public boolean dropCourseFromCatalog( String courseCode ) {
        try {
            PreparedStatement dropCourseQuery = databaseConnection.prepareStatement( "DELETE FROM course_catalog WHERE course_code = ?" );
            dropCourseQuery.setString( 1, courseCode );
            int dropCourseQueryResult = dropCourseQuery.executeUpdate();
            return dropCourseQueryResult == 1;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return false;
        }
    }

    @Override
    public boolean createBatch( int batchYear ) {
        try {
            PreparedStatement createBatchQuery = databaseConnection.prepareStatement( "INSERT INTO batch VALUES(?)" );
            createBatchQuery.setInt( 1, batchYear );
            int createBatchQueryResult = createBatchQuery.executeUpdate();
            return createBatchQueryResult == 1;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Something went wrong" );
            return false;
        }
    }

    @Override
    public boolean createCurriculum( int batchYear, double[] creditRequirements ) {
        try {
            PreparedStatement createCurriculumQuery = databaseConnection.prepareStatement( "INSERT INTO ug_curriculum VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" );
            createCurriculumQuery.setInt( 1, batchYear );
            for ( int i = 2; i <= 12; i++ ) {
                createCurriculumQuery.setDouble( i, creditRequirements[i - 2] );
            }
            int createCurriculumQueryResult = createCurriculumQuery.executeUpdate();
            return createCurriculumQueryResult == 1;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Something went wrong" );
            return false;
        }
    }

    @Override
    public boolean createBatchCourseTable( int batch ) {
        try {
            String            tableName = "core_courses_" + batch;
            PreparedStatement dropQuery = databaseConnection.prepareStatement( "DROP TABLE IF EXISTS " + tableName );
            dropQuery.executeUpdate();

            PreparedStatement createTableQuery = databaseConnection.prepareStatement( "CREATE TABLE " + tableName + "(course_code VARCHAR(6), category VARCHAR(2), department_id VARCHAR(2), FOREIGN KEY (department_id) REFERENCES department(department_id), CHECK (category IN ('SC', 'GR', 'PC', 'HC', 'CP', 'II', 'NN')))" );
            // There seems to be no way to verify whether this works. Apparently the create table does not return anything as it does not affect any row of the table
            createTableQuery.executeUpdate();
            return true;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Something went wrong" );
            return false;
        }
    }

    @Override
    public boolean insertCoreCourse( int batch, String courseCode, String courseCategory, String[] departmentCodes ) {
        try {
            String tableName = "core_courses_" + batch;
            int insertCourseQueryResult = 1;
            PreparedStatement insertCourseQuery = databaseConnection.prepareStatement("INSERT INTO " + tableName + " VALUES (?, ?, ?)");
            insertCourseQuery.setString( 1, courseCode );
            insertCourseQuery.setString( 2, courseCategory );
            for ( String department : departmentCodes) {
                insertCourseQuery.setString( 3, department );
                insertCourseQueryResult &= insertCourseQuery.executeUpdate();
            }
            return insertCourseQueryResult == 1;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please check that all courses exist in the course catalog" );
            return false;
        }
    }
}
