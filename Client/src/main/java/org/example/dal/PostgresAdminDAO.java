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

            PreparedStatement insertPasswordQuery = databaseConnection.prepareStatement( "INSERT INTO common_user_details VALUES (?, ?, ?)" );
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

            PreparedStatement insertPasswordQuery = databaseConnection.prepareStatement( "INSERT INTO common_user_details VALUES (?, ?, ?)" );
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

    public boolean insertCourse( String courseCode, String courseTitle, double[] creditStructure, String[] prerequisites ) {
        try {
            PreparedStatement insertCourseQuery = databaseConnection.prepareStatement( "INSERT INTO course_catalog VALUES(?, ?, ?, ?, ?, ?, ?, ?)" );
            insertCourseQuery.setString( 1, courseCode );
            insertCourseQuery.setString( 2, courseTitle );
            insertCourseQuery.setDouble( 3, creditStructure[0] );
            insertCourseQuery.setDouble( 4, creditStructure[1] );
            insertCourseQuery.setDouble( 5, creditStructure[2] );
            insertCourseQuery.setDouble( 6, creditStructure[3] );
            insertCourseQuery.setDouble( 7, creditStructure[4] );
            insertCourseQuery.setObject( 8, prerequisites );
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
    public boolean insertCoreCourse( String courseCode, String[] departmentCodes, int batch, String courseCategory ) {
        try {
            int               insertCourseQueryResult = 1;
            PreparedStatement insertCourseQuery       = databaseConnection.prepareStatement( "INSERT INTO core_courses VALUES (?, ?, ?, ?)" );
            insertCourseQuery.setString( 1, courseCode );
            insertCourseQuery.setInt( 3, batch );
            insertCourseQuery.setString( 4, courseCategory );
            for ( String department : departmentCodes ) {
                insertCourseQuery.setString( 2, department );
                insertCourseQueryResult &= insertCourseQuery.executeUpdate();
            }
            return insertCourseQueryResult == 1;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please check that all courses exist in the course catalog" );
            return false;
        }
    }

    @Override
    public boolean resetCoreCoursesList( int batch ) {
        try {
            // Delete all the core courses of the particular batch from the core_courses table
            PreparedStatement resetCoreCoursesQuery = databaseConnection.prepareStatement( "DELETE FROM core_courses WHERE batch = ?" );
            resetCoreCoursesQuery.setInt( 1, batch );
            resetCoreCoursesQuery.executeUpdate();

            // If the query executes successfully return true i.e., the table is ready to be inserted into again
            return true;
        } catch ( Exception error ) {
            // If the table is not found, or the database connection has failed
            System.out.println( "Database Error. Please try again later" );
            // Return the fact that the database operation was not executed
            return false;
        }
    }

    @Override
    public boolean findEntryNumber( String entryNumber ) {
        try {
            // Generate the SQL query that will check if the student exists
            PreparedStatement findEntryNumberQuery = databaseConnection.prepareStatement("SELECT name FROM student WHERE entry_number = ?");
            findEntryNumberQuery.setString( 1, entryNumber );
            ResultSet findEntryNumberQueryResult = findEntryNumberQuery.executeQuery();

            // True if the query has found anything, false otherwise
            return findEntryNumberQueryResult.next();
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return false;
        }
    }

    @Override
    public String[] getCoreCourses( int batch, String studentDepartment ) {
        try {
            // Execute the SQL statement that will get all the core courses of a particular department in a particular year
            PreparedStatement getCoreCoursesQuery = databaseConnection.prepareStatement("SELECT course_code FROM core_courses WHERE batch = ? AND department_id = ?");
            getCoreCoursesQuery.setInt( 1, batch );
            getCoreCoursesQuery.setString( 2, studentDepartment );
            ResultSet getCoreCoursesQueryResult = getCoreCoursesQuery.executeQuery();

            // Now we have to fetch all the course codes into an arraylist that will be converted into a string array
            ArrayList<String> coreCourses = new ArrayList<>();
            while ( getCoreCoursesQueryResult.next() ) {
                String courseCode = getCoreCoursesQueryResult.getString( 1 );
                coreCourses.add( courseCode );
            }
            // Return the array list converted to a string array
            return coreCourses.toArray(new String[coreCourses.size()]);
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return null;
        }
    }

    @Override
    public String[] getListOfStudentsInBatch( int batch, String department ) {
        try {
            // Execute the SQL query to fetch all the students of this department from this batch
            PreparedStatement getStudentsQuery = databaseConnection.prepareStatement("SELECT entry_number FROM student WHERE batch = ? AND department_id = ?");
            getStudentsQuery.setInt( 1, batch );
            getStudentsQuery.setString( 2, department );
            ResultSet getStudentsQueryResult = getStudentsQuery.executeQuery();

            // Now get the list of students returned by the query into a string array
            ArrayList<String> studentsList = new ArrayList<>();

            // Get all the entry numbers from the result set
            while ( getStudentsQueryResult.next() ) {
                String entryNumber = getStudentsQueryResult.getString( 1 );
                studentsList.add( entryNumber );
            }

            // Return the array list converted into a string array
            return studentsList.toArray( new String[studentsList.size()] );
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return new String[]{};
        }
    }

    @Override
    public boolean checkIfSessionCompleted( int year, int semester ) {
        try  {
            // Get the status of the session that was provided
            PreparedStatement getSessionQuery = databaseConnection.prepareStatement( "SELECT current_event FROM current_year_and_semester WHERE year = ? AND semester = ?" );
            getSessionQuery.setInt( 1, year );
            getSessionQuery.setInt( 2, semester );
            ResultSet getSessionQueryResult = getSessionQuery.executeQuery();

            // Check if the given session contains an entry in the database
            boolean isSessionValid = getSessionQueryResult.next();
            if ( !isSessionValid ) return false;

            // If the session exists, we need to check if the session is completed
            String courseStatus = getSessionQueryResult.getString( 1 );
            return courseStatus.equals( "COMPLETED" );
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return false;
        }
    }

    @Override
    public boolean createNewSession( int newYear, int newSemester ) {
        try {
            // Create an SQL query to insert the session into the database
            PreparedStatement createSessionQuery = databaseConnection.prepareStatement( "INSERT INTO current_year_and_semester( year, semester ) VALUES(?, ?)" );
            createSessionQuery.setInt( 1, newYear );
            createSessionQuery.setInt( 2, newSemester );

            // Return true only if a new session was created in the database, false otherwise
            return createSessionQuery.executeUpdate() == 1;
        } catch ( Exception error ) {
            System.out.println( "Database error. Please try again later" );
            return false;
        }
    }

    @Override
    public boolean setSessionEvent( String event, int currentYear, int currentSemester ) {
        try {
            // These are the only statuses that are allowed. If the user has entered a completely different status, it must be rejected
            String[] availableEvents = new String[]{ "NONE", "ENROLLING", "OFFERING", "GRADE SUBMISSION", "COMPLETED" };

            // Go through all the valid events and check if this particular event is found in the list
            boolean stringFound = false;
            for ( String temp : availableEvents ) {
                stringFound |= temp.equals( event );
            }
            if ( !stringFound ) return false;

            // SQL query to update the event in the database
            PreparedStatement setStatusQuery = databaseConnection.prepareStatement("UPDATE current_year_and_semester SET current_event = ? WHERE year = ? AND semester = ?");
            setStatusQuery.setString( 1, event );
            setStatusQuery.setInt( 2, currentYear );
            setStatusQuery.setInt( 3, currentSemester );
            setStatusQuery.executeUpdate();

            // If the query was executed successfully the update was successful. The year and semester have been provided by us
            return true;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return false;
        }
    }

    @Override
    public boolean verifyNoMissingGrades( int currentYear, int currentSemester ) {
        try {
            // SQL query to check if there are any students in the previous semester who have not had their grades entered
            // '-' is used in the database to indicate that an entry has not yet been inserted
            PreparedStatement missingGradeQuery = databaseConnection.prepareStatement("SELECT * FROM student_course_registration WHERE year = ? AND semester = ? AND grade = '-'");
            missingGradeQuery.setInt( 1, currentYear );
            missingGradeQuery.setInt( 2, currentSemester );
            ResultSet missingGradeQueryResult = missingGradeQuery.executeQuery();

            // If any row was returned there is a missing grade in the session
            return !missingGradeQueryResult.next();
        } catch ( Exception error ) {
            System.out.println( "Database error. Please try again later" );
            return false;
        }
    }
}
