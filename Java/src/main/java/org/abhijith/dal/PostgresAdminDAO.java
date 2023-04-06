package org.abhijith.dal;

import org.abhijith.daoInterfaces.AdminDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class PostgresAdminDAO extends PostgresCommonDAO implements AdminDAO {
    public PostgresAdminDAO() {
        super();
    }

    public String[][] getGradesOfCourse( String courseCode, int year, int semester, String offeringDepartment ) {
        try {
            if ( courseCode == null || offeringDepartment == null || year < 0 || semester < 0 )
                return new String[][]{};
            courseCode = courseCode.toUpperCase();
            offeringDepartment = offeringDepartment.toUpperCase();

            // SQL query to fetch the records for a student from the database
            PreparedStatement getGradesQuery = databaseConnection.prepareStatement( "SELECT entry_number, grade FROM student_course_registration WHERE course_code = ? AND year = ? AND semester = ? AND department_id = ? ORDER BY entry_number" );
            getGradesQuery.setString( 1, courseCode );
            getGradesQuery.setInt( 2, year );
            getGradesQuery.setInt( 3, semester );
            getGradesQuery.setString( 4, offeringDepartment );
            ResultSet getGradesQueryResult = getGradesQuery.executeQuery();

            // Get the records from the ResultSet and put them into the required format
            ArrayList<String[]> records = new ArrayList<>();
            while ( getGradesQueryResult.next() ) {
                // Every array is going to be { Entry Number, Grade }
                String entry_number = getGradesQueryResult.getString( 1 );
                String grade        = getGradesQueryResult.getString( 2 );
                records.add( new String[]{ entry_number, grade } );
            }

            // Convert the ArrayList into an array before returning
            return records.toArray( new String[records.size()][] );
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return new String[][]{};
        }
    }

    public boolean insertStudent( String entryNumber, String name, String departmentID, int batch ) {
        try {
            if ( entryNumber == null || name == null || departmentID == null || batch < 0 ) return false;
            entryNumber = entryNumber.toUpperCase();
            name = name.toUpperCase();
            departmentID = departmentID.toUpperCase();

            // The default username and password for the STUDENT role
            final String DEFAULT_PASSWORD = "iitropar";
            final String STUDENT_ROLE     = "STUDENT";

            // SQL query to insert a student into the student relation which would allow him to enroll in various courses
            PreparedStatement insertStudentQuery = databaseConnection.prepareStatement( "INSERT INTO student(entry_number, name, department_id, batch) VALUES (?, ?, ?, ?)" );
            insertStudentQuery.setString( 1, entryNumber );
            insertStudentQuery.setString( 2, name );
            insertStudentQuery.setString( 3, departmentID );
            insertStudentQuery.setInt( 4, batch );
            int successStatus = insertStudentQuery.executeUpdate();

            // SQL query to insert the student into the common_user_details relation to allow him to log into the database
            PreparedStatement insertPasswordQuery = databaseConnection.prepareStatement( "INSERT INTO common_user_details VALUES (?, ?, ?)" );
            insertPasswordQuery.setString( 1, entryNumber );
            // The default password is set here
            insertPasswordQuery.setString( 2, DEFAULT_PASSWORD );
            insertPasswordQuery.setString( 3, STUDENT_ROLE );
            successStatus &= insertPasswordQuery.executeUpdate();

            // Returns true only if both queries execute successfully
            return successStatus == 1;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return false;
        }
    }

    public boolean insertFaculty( String facultyID, String name, String departmentID ) {
        try {
            if ( facultyID == null || name == null || departmentID == null ) return false;
            facultyID = facultyID.toUpperCase();
            name = name.toUpperCase();
            departmentID = departmentID.toUpperCase();

            // The default password for the FACULTY is set here
            final String DEFAULT_PASSWORD = "iitropar";
            final String FACULTY_ROLE     = "FACULTY";

            // SQL query to insert a faculty into the faculty table
            PreparedStatement insertFacultyQuery = databaseConnection.prepareStatement( "INSERT INTO faculty(faculty_id, name, department_id) VALUES (?, ?, ?)" );
            insertFacultyQuery.setString( 1, facultyID );
            insertFacultyQuery.setString( 2, name );
            insertFacultyQuery.setString( 3, departmentID );
            int successStatus = insertFacultyQuery.executeUpdate();

            // SQL table to insert the login details for the new FACULTY
            PreparedStatement insertPasswordQuery = databaseConnection.prepareStatement( "INSERT INTO common_user_details VALUES (?, ?, ?)" );
            insertPasswordQuery.setString( 1, facultyID );
            // the default password is set here
            insertPasswordQuery.setString( 2, DEFAULT_PASSWORD );
            insertPasswordQuery.setString( 3, FACULTY_ROLE );
            successStatus &= insertPasswordQuery.executeUpdate();

            // Returns true only if both statements execute successfully
            return successStatus == 1;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return false;
        }
    }

    public boolean insertCourse( String courseCode, String courseTitle, double[] creditStructure, String[] prerequisites ) {
        try {
            // Verify that the input parameters are valid
            if ( courseCode == null || courseTitle == null || creditStructure == null || creditStructure.length != 5 || prerequisites == null )
                return false;
            // The course parameters cannot be negative
            for ( double field : creditStructure ) if ( field < 0 ) return false;
            // Converting all the course codes to uppercase
            for ( int i = 0; i < prerequisites.length; i++ ) {
                if ( prerequisites[i] == null ) return false;
                prerequisites[i] = prerequisites[i].toUpperCase();
            }
            courseCode = courseCode.toUpperCase();
            courseTitle = courseTitle.toUpperCase();

            // SQL query to insert a course into the course catalog
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

            // Returns 1 if the course could not be inserted into the course catalog
            return insertCourseQueryResult == 1;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return false;
        }
    }

    @Override
    public boolean checkAllPrerequisites( String[] prerequisites ) {
        try {
            if ( prerequisites == null ) return false;
            for ( int i = 0; i < prerequisites.length; i++ ) {
                if ( prerequisites[i] == null ) return false;
                prerequisites[i] = prerequisites[i].toUpperCase();
            }

            // Iterate through the list of prerequisites
            boolean coursesFound = true;
            for ( String course : prerequisites ) {
                // True only if all the course codes are found in the course catalog
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
            if ( courseCode == null ) return false;

            // SQL query to delete the course from the course catalog
            PreparedStatement dropCourseQuery = databaseConnection.prepareStatement( "DELETE FROM course_catalog WHERE course_code = ?" );
            dropCourseQuery.setString( 1, courseCode );
            int dropCourseQueryResult = dropCourseQuery.executeUpdate();

            // Returns true if the course was deleted, false otherwise
            return dropCourseQueryResult == 1;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return false;
        }
    }

    @Override
    public boolean createBatch( int batchYear ) {
        try {
            if ( batchYear < 0 ) return false;

            // SQL query to insert a batch into the database
            PreparedStatement createBatchQuery = databaseConnection.prepareStatement( "INSERT INTO batch VALUES(?)" );
            createBatchQuery.setInt( 1, batchYear );
            int createBatchQueryResult = createBatchQuery.executeUpdate();

            // Returns true as long as the course executes. If a batch is repeated, the error is triggered and a false is returned
            return createBatchQueryResult == 1;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Something went wrong" );
            return false;
        }
    }

    @Override
    public boolean createCurriculum( int batchYear, double[] creditRequirements ) {
        try {
            if ( batchYear < 0 || creditRequirements == null || creditRequirements.length != 11 ) return false;
            for ( double creditRequirement : creditRequirements ) if ( creditRequirement < 0 ) return false;

            // SQL query to insert the curriculum into the database for the corresponding batch
            PreparedStatement createCurriculumQuery = databaseConnection.prepareStatement( "INSERT INTO ug_curriculum VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" );
            createCurriculumQuery.setInt( 1, batchYear );

            // Iterate through all the categories and set them
            for ( int i = 2; i <= 12; i++ ) {
                createCurriculumQuery.setDouble( i, creditRequirements[i - 2] );
            }
            int createCurriculumQueryResult = createCurriculumQuery.executeUpdate();
            // Returns true if the curriculum was inserted successfully
            return createCurriculumQueryResult == 1;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Something went wrong" );
            return false;
        }
    }

    @Override
    public boolean insertCoreCourse( String courseCode, String[] departmentCodes, int batch, String courseCategory ) {
        try {
            if ( courseCode == null || departmentCodes == null || batch < 0 || courseCategory == null ) return false;
            for ( int i = 0; i < departmentCodes.length; i++ ) {
                if ( departmentCodes[i] == null ) return false;
                departmentCodes[i] = departmentCodes[i].toUpperCase();
            }
            courseCategory = courseCategory.toUpperCase();
            courseCode = courseCode.toUpperCase();

            int               insertCourseQueryResult = 1;
            // SQL query to insert the course code into the database
            PreparedStatement insertCourseQuery       = databaseConnection.prepareStatement( "INSERT INTO core_courses VALUES (?, ?, ?, ?)" );
            insertCourseQuery.setString( 1, courseCode );
            insertCourseQuery.setInt( 3, batch );
            insertCourseQuery.setString( 4, courseCategory );
            // Set the department of the corresponding course into the database
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
            if ( entryNumber == null ) return false;

            // Generate the SQL query that will check if the student exists
            PreparedStatement findEntryNumberQuery = databaseConnection.prepareStatement( "SELECT name FROM student WHERE entry_number = ?" );
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
            if ( batch < 0 || studentDepartment == null ) return null;

            // Execute the SQL statement that will get all the core courses of a particular department in a particular year
            PreparedStatement getCoreCoursesQuery = databaseConnection.prepareStatement( "SELECT course_code FROM core_courses WHERE batch = ? AND department_id = ?" );
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
            return coreCourses.toArray( new String[coreCourses.size()] );
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return null;
        }
    }

    @Override
    public String[] getListOfStudentsInBatch( int batch, String department ) {
        try {
            if ( batch < 0 || department == null ) return null;

            // Execute the SQL query to fetch all the students of this department from this batch
            PreparedStatement getStudentsQuery = databaseConnection.prepareStatement( "SELECT entry_number FROM student WHERE batch = ? AND department_id = ?" );
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
            return null;
        }
    }

    @Override
    public boolean checkIfSessionCompleted( int year, int semester ) {
        try {
            if ( year < 0 || semester < 0 ) return false;

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
            if ( newYear < 0 || newSemester < 0 ) return false;

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
            if ( event == null || currentSemester < 0 || currentYear < 0 ) return false;
            // These are the only statuses that are allowed. If the user has entered a completely different status, it must be rejected
            String[] availableEvents = new String[]{ "RUNNING", "ENROLLING", "OFFERING", "GRADE SUBMISSION", "COMPLETED" };

            // Go through all the valid events and check if this particular event is found in the list
            boolean stringFound = false;
            for ( String temp : availableEvents ) {
                stringFound |= temp.equals( event );
            }
            if ( !stringFound ) return false;

            // SQL query to update the event in the database
            PreparedStatement setStatusQuery = databaseConnection.prepareStatement( "UPDATE current_year_and_semester SET current_event = ? WHERE year = ? AND semester = ?" );
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
            if ( currentYear < 0 || currentSemester < 0 ) return false;

            // SQL query to check if there are any students in the previous semester who have not had their grades entered
            // '-' is used in the database to indicate that an entry has not yet been inserted
            PreparedStatement missingGradeQuery = databaseConnection.prepareStatement( "SELECT * FROM student_course_registration WHERE year = ? AND semester = ? AND grade = '-'" );
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
