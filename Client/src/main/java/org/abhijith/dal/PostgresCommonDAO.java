package org.abhijith.dal;

import org.abhijith.daoInterfaces.CommonDAO;
import org.abhijith.users.Student;
import org.abhijith.utils.Utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class PostgresCommonDAO implements CommonDAO {
    protected Connection databaseConnection;

    public PostgresCommonDAO() {
        try {
            Properties  databaseConfig = new Properties();
            ClassLoader classLoader    = Student.class.getClassLoader();
            InputStream inputStream    = classLoader.getResourceAsStream( "config.properties" );
            databaseConfig.load( inputStream );

            String connectionURL = databaseConfig.getProperty( "db.connectionURL" );
            String username      = databaseConfig.getProperty( "db.username" );
            String password      = databaseConfig.getProperty( "db.password" );
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

    public Connection getDatabaseConnection() {
        return databaseConnection;
    }

    @Override
    // Returns the current year and semester if successful
    // Otherwise returns default year and session i.e., 2020-1
    public int[] getCurrentAcademicSession() {
        try {
            PreparedStatement getSessionQuery = databaseConnection.prepareStatement( "SELECT * FROM current_year_and_semester ORDER BY year DESC, semester DESC LIMIT 1" );
            ResultSet         currentSession  = getSessionQuery.executeQuery();

            currentSession.next();
            int currentYear     = currentSession.getInt( 1 );
            int currentSemester = currentSession.getInt( 2 );

            return new int[]{ currentYear, currentSemester };
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return new int[]{ 2020, 1 };
        }
    }

    public String[][] getStudentGradesForSemester( String entryNumber, int year, int semester ) {
        try {
            if ( entryNumber == null || year < 0 || semester < 0 ) return new String[][]{};
            entryNumber = entryNumber.toUpperCase();

            PreparedStatement gradeQuery = databaseConnection.prepareStatement( "SELECT course_code, course_title, grade, credits FROM student_course_registration NATURAL JOIN course_catalog WHERE entry_number = ? AND year = ? AND semester = ? ORDER BY course_code" );

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
            if ( entryNumber == null ) return -1;
            entryNumber = entryNumber.toUpperCase();

            PreparedStatement batchQuery = databaseConnection.prepareStatement( "SELECT batch FROM student WHERE entry_number = ?" );
            batchQuery.setString( 1, entryNumber );
            ResultSet batchQueryResult = batchQuery.executeQuery();

            boolean studentExists = batchQueryResult.next();
            if ( !studentExists ) return -1;

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
            if ( courseCode == null ) return false;
            courseCode = courseCode.toUpperCase();

            PreparedStatement checkCourseCatalogQuery = databaseConnection.prepareStatement( "SELECT course_code FROM course_catalog WHERE course_code = ?" );
            checkCourseCatalogQuery.setString( 1, courseCode );
            ResultSet checkCourseCatalogQueryResult = checkCourseCatalogQuery.executeQuery();
            return checkCourseCatalogQueryResult.next();
        } catch ( Exception error ) {
            System.out.println( "Database Error. Try again later" );
            return false;
        }
    }

    @Override
    public HashMap<String, Double> getUGCurriculum( int batch ) {
        try {
            if ( batch < 0 ) return null;

            // Execute the SQL statement that will fetch the corresponding columns from the UG curriculum table
            PreparedStatement getCurriculumQuery = databaseConnection.prepareStatement( "SELECT sc, se, gr, pc, pe, hc, he, cp, ii, nn, oe FROM ug_curriculum WHERE year = ?" );
            getCurriculumQuery.setInt( 1, batch );
            ResultSet getCurriculumQueryResult = getCurriculumQuery.executeQuery();
            getCurriculumQueryResult.next();

            // Now insert the categories into the hashmap along with the corresponding credit requirements
            String[]                categories         = new String[]{ "SC", "SE", "GR", "PC", "PE", "HC", "HE", "CP", "II", "NN", "OE" };
            HashMap<String, Double> creditRequirements = new HashMap<>();

            // Just read the values from the returned set of columns and insert them into the hashmap
            for ( int i = 0; i < categories.length; i++ ) {
                Double categoryRequirement = getCurriculumQueryResult.getDouble( i + 1 );
                creditRequirements.put( categories[i], categoryRequirement );
            }
            return creditRequirements;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return null;
        }
    }

    @Override
    public HashMap<String, Double> getCreditsInAllCategories( String entryNumber ) {
        try {
            if ( entryNumber == null ) return null;
            // SQL query to get the categories and the corresponding credits completed in all of them
            // Note that the SQL query contains the grades that will allow the course to be marked as passed
            PreparedStatement getCreditsQuery = databaseConnection.prepareStatement( "SELECT UPPER(category), sum(credits) FROM student_course_registration NATURAL JOIN course_catalog WHERE entry_number = ? AND grade IN ('A', 'A-', 'B', 'B-', 'C', 'C-', 'D') GROUP BY category" );
            getCreditsQuery.setString( 1, entryNumber );
            ResultSet getCreditsQueryResult = getCreditsQuery.executeQuery();

            // Now you just have to insert all the records that were returned into the hashmap and return it
            HashMap<String, Double> categoryCredits = new HashMap<>();
            while ( getCreditsQueryResult.next() ) {
                String category        = getCreditsQueryResult.getString( 1 );
                Double creditsObtained = getCreditsQueryResult.getDouble( 2 );
                categoryCredits.put( category, creditsObtained );
            }
            return categoryCredits;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later " );
            return null;
        }
    }

    @Override
    public boolean setPhoneNumber( String id, String phoneNumber ) {
        try {
            if ( id == null || phoneNumber == null ) return false;

            // SQL query to update this particular id with the phone number that is provided
            PreparedStatement setPhoneNumberQuery = databaseConnection.prepareStatement( "UPDATE common_user_details SET phone = ? WHERE id = ?" );
            setPhoneNumberQuery.setString( 1, phoneNumber );
            setPhoneNumberQuery.setString( 2, id );
            setPhoneNumberQuery.executeUpdate();

            // If the query was successfully executed return true ( the only case where the update would affect 0 rows would be when the same phone number is there in the database, which is fine )
            return true;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return false;
        }
    }

    @Override
    public boolean setEmail( String id, String email ) {
        try {
            if ( id == null || email == null ) return false;

            // Create an SQL query to set the email of the specified user
            PreparedStatement setEmailQuery = databaseConnection.prepareStatement( "UPDATE common_user_details SET email = ? WHERE id = ?" );
            setEmailQuery.setString( 1, email );
            setEmailQuery.setString( 2, id );
            setEmailQuery.executeUpdate();

            // If the query is successful, return true
            return true;
        } catch ( Exception error ) {
            System.out.println( "Database error. Please try again later" );
            return false;
        }
    }

    @Override
    public String[] getContactDetails( String userID ) {
        try {
            if ( userID == null ) return new String[]{};

            // SQL query to fetch the contact details of the specified ID from the database
            PreparedStatement getContactDetailsQuery = databaseConnection.prepareStatement( "SELECT email, phone FROM common_user_details WHERE id = ?" );
            getContactDetailsQuery.setString( 1, userID );
            ResultSet getContactDetailsQueryResult = getContactDetailsQuery.executeQuery();

            // Check if the user ID is actually valid and any records were returned
            boolean isIDValid = getContactDetailsQueryResult.next();
            if ( !isIDValid ) return new String[]{};

            // If the user id that was entered is valid, get the email and phone number and return it
            String email = getContactDetailsQueryResult.getString( 1 );
            String phone = getContactDetailsQueryResult.getString( 2 );
            return new String[]{ email, phone };
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return new String[]{};
        }
    }

    @Override
    public boolean setPassword( String id, String password ) {
        try {
            if ( id == null || password == null ) return false;

            // SQL statement to update the password in the database
            PreparedStatement setPasswordQuery = databaseConnection.prepareStatement( "UPDATE common_user_details SET password = ? WHERE id = ?" );
            setPasswordQuery.setString( 1, password );
            setPasswordQuery.setString( 2, id );
            setPasswordQuery.executeUpdate();

            // If the query executes successfully, simply return true
            return true;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return false;
        }
    }

    @Override
    public String[][] getCourseCatalog() {
        try {
            // SQL statement to fetch the entire course catalog from the database
            PreparedStatement getCatalogQuery       = databaseConnection.prepareStatement( "SELECT * FROM course_catalog ORDER BY course_code" );
            ResultSet         getCatalogQueryResult = getCatalogQuery.executeQuery();

            ArrayList<String[]> courseList = new ArrayList<>();
            while ( getCatalogQueryResult.next() ) {
                ArrayList<String> course = new ArrayList<>();
                course.add( getCatalogQueryResult.getString( 1 ) );
                course.add( getCatalogQueryResult.getString( 2 ) );
                course.add( getCatalogQueryResult.getString( 3 ) );
                course.add( getCatalogQueryResult.getString( 4 ) );
                course.add( getCatalogQueryResult.getString( 5 ) );
                course.add( getCatalogQueryResult.getString( 6 ) );
                course.add( getCatalogQueryResult.getString( 7 ) );
                course.add( getCatalogQueryResult.getString( 8 ) );
                courseList.add( course.toArray( new String[course.size()] ) );
            }
            return courseList.toArray( new String[courseList.size()][] );
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return new String[][]{};
        }
    }

    @Override
    public String getCourseGrade( String entryNumber, String courseCode ) {
        try {
            if ( entryNumber == null || courseCode == null ) return "";

            // Setting up the SQL statement
            PreparedStatement getGradeQuery = databaseConnection.prepareStatement( "SELECT grade FROM student_course_registration WHERE entry_number = ? AND course_code = ?" );
            getGradeQuery.setString( 1, entryNumber );
            getGradeQuery.setString( 2, courseCode );
            ResultSet getGradeQueryResult = getGradeQuery.executeQuery();

            // If the student has done the course before, he may have done it multiple times. Get the highest possible grade
            // In the worst case, the student has not done the course before and his grade is "-" which is the not applicable symbol
            String maximumGrade      = "F";
            int    maximumGradeValue = 0;
            while ( getGradeQueryResult.next() ) {
                // Convert the grade to a number to determine what the highest grade was
                String grade = getGradeQueryResult.getString( 1 );
                if ( Utils.getGradeValue( grade ) >= maximumGradeValue ) {
                    maximumGrade = grade;
                    maximumGradeValue = Utils.getGradeValue( grade );
                }
            }
            // Return the maximum grade that was found
            return maximumGrade;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return "";
        }
    }

    @Override
    public String getStudentDepartment( String entryNumber ) {
        try {
            if ( entryNumber == null ) return "";

            // SQL query to fetch the department from the database
            PreparedStatement studentBranchQuery = databaseConnection.prepareStatement( "SELECT department_id FROM student WHERE entry_number = ?" );
            studentBranchQuery.setString( 1, entryNumber );
            ResultSet studentBranchQueryResult = studentBranchQuery.executeQuery();

            // If the student is not found, return ""
            boolean doesStudentExist = studentBranchQueryResult.next();
            if ( !doesStudentExist ) return "";

            // If the student exists, return the corresponding branch
            return studentBranchQueryResult.getString( 1 );
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return "";
        }
    }
}
