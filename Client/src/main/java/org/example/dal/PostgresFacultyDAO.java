package org.example.dal;

import org.example.daoInterfaces.FacultyDAO;

import javax.xml.transform.Result;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class PostgresFacultyDAO extends PostgresCommonDAO implements FacultyDAO {
    public PostgresFacultyDAO( String connectionURL, String username, String password ) {
        super( connectionURL, username, password );
    }


    @Override
    public boolean insertCourseOffering( String courseCode, int currentYear, int currentSemester, String departmentID, String facultyID ) {
        try {
            PreparedStatement insertCourseQuery = databaseConnection.prepareStatement( "INSERT INTO course_offerings(course_code, faculty_id, year, semester, department_id) VALUES (?, ?, ?, ?, ?)" );
            insertCourseQuery.setString( 1, courseCode );
            insertCourseQuery.setString( 2, facultyID );
            insertCourseQuery.setInt( 3, currentYear );
            insertCourseQuery.setInt( 4, currentSemester );
            insertCourseQuery.setString( 5, departmentID );
            int insertCourseQueryResult = insertCourseQuery.executeUpdate();
            return insertCourseQueryResult == 1;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please verify details and try again" );
            return false;
        }
    }

    @Override
    public String getDepartment( String id ) {
        try {
            PreparedStatement getDepartmentQuery = databaseConnection.prepareStatement( "SELECT department_id FROM faculty WHERE faculty_id = ?" );
            getDepartmentQuery.setString( 1, id );
            ResultSet getDepartmentQueryResult = getDepartmentQuery.executeQuery();
            getDepartmentQueryResult.next();
            return getDepartmentQueryResult.getString( 1 );
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return "";
        }
    }

    @Override
    public boolean setCGCriteria( String facultyID, String courseCode, double minimumCGPA, int[] currentSession, String departmentID ) {
        try {
            PreparedStatement setCGQuery = databaseConnection.prepareStatement( "UPDATE course_offerings SET cgpa_criteria = ? WHERE course_code = ? AND faculty_id = ? AND year = ? AND semester = ? AND department_id = ?" );
            setCGQuery.setDouble( 1, minimumCGPA );
            setCGQuery.setString( 2, courseCode );
            setCGQuery.setString( 3, facultyID );
            setCGQuery.setInt( 4, currentSession[0] );
            setCGQuery.setInt( 5, currentSession[1] );
            setCGQuery.setString( 6, departmentID );
            int setCGQueryResult = setCGQuery.executeUpdate();
            return setCGQueryResult == 1;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please verify that the course offering exists in the current session" );
            return false;
        }
    }

    @Override
    public boolean setInstructorPrerequisites( String facultyID, String courseCode, String[][] prerequisites, int[] currentSession ) {
        try {
            int maximumLength = 0;
            for ( String[] courseGroup : prerequisites ) maximumLength = Math.max( maximumLength, courseGroup.length );
            String[][] cleanedData = new String[prerequisites.length][maximumLength];
            for ( int i = 0; i < prerequisites.length; i++ ) {
                for ( int j = 0; j < prerequisites[i].length; j++ ) {
                    cleanedData[i][j] = prerequisites[i][j];
                }
                for ( int j = prerequisites[i].length; j < maximumLength; j++ ) {
                    cleanedData[i][j] = "";
                }
            }
            PreparedStatement setPrerequisitesQuery = databaseConnection.prepareStatement( "UPDATE course_offerings SET instructor_prerequisites = ? WHERE faculty_id = ? AND course_code = ? AND year = ? AND semester = ?" );
            setPrerequisitesQuery.setObject( 1, cleanedData );
            setPrerequisitesQuery.setString( 2, facultyID );
            setPrerequisitesQuery.setString( 3, courseCode );
            setPrerequisitesQuery.setInt( 4, currentSession[0] );
            setPrerequisitesQuery.setInt( 5, currentSession[1] );
            return setPrerequisitesQuery.executeUpdate() == 1;
        } catch ( Exception error ) {
            System.err.println( error.getMessage() );
            System.out.println( "Database Error. Please verify details before trying again" );
            return false;
        }
    }

    @Override
    public boolean dropCourseOffering( String facultyID, String courseCode, int currentYear, int currentSemester ) {
        try {
            PreparedStatement dropOfferingQuery = databaseConnection.prepareStatement( "DELETE FROM course_offerings WHERE faculty_id = ? AND course_code = ? AND year = ? AND semester = ?" );
            dropOfferingQuery.setString( 1, facultyID );
            dropOfferingQuery.setString( 2, courseCode );
            dropOfferingQuery.setInt( 3, currentYear );
            dropOfferingQuery.setInt( 4, currentSemester );
            return dropOfferingQuery.executeUpdate() == 1;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return false;
        }
    }

    @Override
    public boolean checkIfOfferedBySelf( String facultyID, String courseCode, int currentYear, int currentSemester ) {
        try {
            PreparedStatement checkOfferingQuery = databaseConnection.prepareStatement( "SELECT course_code FROM course_offerings WHERE faculty_id = ? AND course_code = ? AND year = ? AND semester = ?" );
            checkOfferingQuery.setString( 1, facultyID );
            checkOfferingQuery.setString( 2, courseCode );
            checkOfferingQuery.setInt( 3, currentYear );
            checkOfferingQuery.setInt( 4, currentSemester );
            ResultSet checkOfferingQueryResult = checkOfferingQuery.executeQuery();
            return checkOfferingQueryResult.next();
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return false;
        }
    }

    @Override
    public boolean setCourseCategory( String courseCode, int currentYear, int currentSemester, String courseCategory, String department, int[] years, String facultyDepartment ) {
        try {
            // Sanitise the course category to prevent SQL injection
            boolean isValidCategory = false;
            // Check that the course category is among this list of allowed course categories in the database
            String[] validCategories = { "SC", "SE", "GR", "PC", "PE", "HC", "HE", "CP", "II", "NN", "OE" };
            for ( String category : validCategories ) {
                if ( courseCategory.equals( category ) ) isValidCategory = true;
            }
            if ( !isValidCategory ) return false;

            // Create an array to insert into the database
            // e.g. Suppose department is CS and years = { 2020, 2021 }
            // Then the string array that should be inserted into the database is { 2020-CS, 2021-CS }
            String[] cleanDepartmentData = new String[years.length];
            for ( int i = 0; i < years.length; i++ ) {
                cleanDepartmentData[i] = years[i] + "-" + department;
            }

            // Create the query to update the entry in the course offerings list
            // Concatenate the departments that you received into the departments that already exist
            PreparedStatement courseCategoryQuery = databaseConnection.prepareStatement( "UPDATE course_offerings SET " + courseCategory + "=array_cat(" + courseCategory + ", ?) WHERE course_code = ? AND year = ? AND semester = ? AND department_id = ?" );
            courseCategoryQuery.setObject( 1, cleanDepartmentData );
            courseCategoryQuery.setString( 2, courseCode );
            courseCategoryQuery.setInt( 3, currentYear );
            courseCategoryQuery.setInt( 4, currentSemester );
            courseCategoryQuery.setString( 5, facultyDepartment );
            // Returns true if any row was affected by the operation to indicate success
            return courseCategoryQuery.executeUpdate() == 1;
        } catch ( Exception error ) {
            System.out.println( "Database error. Please try again later" );
            return false;
        }
    }

    @Override
    public boolean verifyCore( String courseCode, String departmentID, int year ) {
        try {
            // From the table try and find the corresponding course code and the department
            PreparedStatement checkCoreCourseQuery = databaseConnection.prepareStatement( "SELECT * FROM core_courses WHERE course_code = ? AND department_id = ? AND batch = ?" );
            checkCoreCourseQuery.setString( 1, courseCode );
            checkCoreCourseQuery.setString( 2, departmentID );
            checkCoreCourseQuery.setInt( 3, year );
            ResultSet checkCoreCourseQueryResult = checkCoreCourseQuery.executeQuery();

            // If even a single record is returned, then you have found a course with the corresponding batch from the table
            return checkCoreCourseQueryResult.next();
        } catch ( Exception error ) {
            System.out.println( "Database error. Please try again later" );
            return false;
        }
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
            System.out.println( "Database Error. Please try again later" );
            return new String[][]{};
        }
    }

    @Override
    public String[][] getCourseEnrollmentsList( String courseCode, int year, int semester ) {
        try {
            PreparedStatement getEnrollmentsQuery = databaseConnection.prepareStatement( "SELECT name, entry_number FROM student_course_registration NATURAL JOIN student WHERE course_code = ? AND year = ? AND semester = ? ORDER BY entry_number" );
            getEnrollmentsQuery.setString( 1, courseCode );
            getEnrollmentsQuery.setInt( 2, year );
            getEnrollmentsQuery.setInt( 3, semester );
            ResultSet getEnrollmentsQueryResult = getEnrollmentsQuery.executeQuery();

            ArrayList<String[]> records = new ArrayList<>();
            while ( getEnrollmentsQueryResult.next() ) {
                String name        = getEnrollmentsQueryResult.getString( 1 );
                String entryNumber = getEnrollmentsQueryResult.getString( 2 );
                records.add( new String[]{ name, entryNumber } );
            }
            return records.toArray( new String[records.size()][] );
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return new String[][]{};
        }
    }

    @Override
    public String[] getListOfStudents( String courseCode, int year, int semester ) {
        try {
            // A list to store all the students enrolled in this course
            ArrayList<String> listOfStudents = new ArrayList<>();

            // SQL query to retrieve the list of students from the database
            PreparedStatement getStudentsQuery = databaseConnection.prepareStatement( "SELECT entry_number FROM student_course_registration WHERE course_code = ? AND year = ? AND semester = ?" );
            getStudentsQuery.setString( 1, courseCode );
            getStudentsQuery.setInt( 2, year );
            getStudentsQuery.setInt( 3, semester );
            ResultSet getStudentsQueryResult = getStudentsQuery.executeQuery();

            // Now get all the students from the result set into the array that was created above
            while ( getStudentsQueryResult.next() ) {
                String entryNumber = getStudentsQueryResult.getString( 1 );
                listOfStudents.add( entryNumber );
            }
            // Return it in the form of a string array
            return listOfStudents.toArray( new String[listOfStudents.size()] );
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return new String[]{};
        }
    }

    @Override
    public boolean isCurrentEventOffering( int currentYear, int currentSemester ) {
        try {
            // SQL query to check if the current event in the given session is enrolling
            PreparedStatement offeringCheckQuery = databaseConnection.prepareStatement("SELECT * FROM current_year_and_semester WHERE year = ? AND semester = ? AND current_event = 'OFFERING'");
            offeringCheckQuery.setInt( 1, currentYear );
            offeringCheckQuery.setInt( 2, currentSemester );
            ResultSet offeringCheckQueryResult = offeringCheckQuery.executeQuery();

            // If there exists such a record, it implies that the current session is offering
            return offeringCheckQueryResult.next();
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return false;
        }
    }

    @Override
    public boolean isCurrentEventGradeSubmission( int currentYear, int currentSemester ) {
        try {
            // SQL query to check if the current event in the given session is enrolling
            PreparedStatement offeringCheckQuery = databaseConnection.prepareStatement("SELECT * FROM current_year_and_semester WHERE year = ? AND semester = ? AND current_event = 'GRADE SUBMISSION'");
            offeringCheckQuery.setInt( 1, currentYear );
            offeringCheckQuery.setInt( 2, currentSemester );
            ResultSet offeringCheckQueryResult = offeringCheckQuery.executeQuery();

            // If there exists such a record, it implies that the current session is offering
            return offeringCheckQueryResult.next();
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return false;
        }
    }

    @Override
    public boolean uploadCourseGrades( String courseCode, int year, int semester, String[] listOfStudents, String[] listOfGrades ) {
        try {
            // The database requires the course codes to be in uppercase
            courseCode = courseCode.toUpperCase();

            // Construct the SQL query that will be used to update course grades
            PreparedStatement uploadGradeQuery = databaseConnection.prepareStatement("UPDATE student_course_registration SET grade = ? WHERE entry_number = ? AND  course_code = ? AND year = ? AND semester = ?");
            uploadGradeQuery.setString( 3, courseCode );
            uploadGradeQuery.setInt( 4, year );
            uploadGradeQuery.setInt( 5, semester );

            // Now iterate through the list of students and grades and set the grade and entry number of the student
            for (int i=0; i<listOfStudents.length; i++) {
                uploadGradeQuery.setString( 1, listOfGrades[i] );
                uploadGradeQuery.setString( 2, listOfStudents[i] );
                uploadGradeQuery.executeUpdate();
            }
            return true;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please verify that all grades are valid" );
            return false;
        }
    }
}
