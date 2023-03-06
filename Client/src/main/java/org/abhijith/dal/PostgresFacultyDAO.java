package org.abhijith.dal;

import org.abhijith.daoInterfaces.FacultyDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class PostgresFacultyDAO extends PostgresCommonDAO implements FacultyDAO {
    public PostgresFacultyDAO() {
        super();
    }

    @Override
    public boolean insertCourseOffering( String courseCode, int currentYear, int currentSemester, String departmentID, String facultyID ) {
        try {
            if ( courseCode == null || currentSemester <= 0 || currentYear < 0 || departmentID == null || facultyID == null )
                return false;
            courseCode = courseCode.toUpperCase();
            departmentID = departmentID.toUpperCase();
            facultyID = facultyID.toUpperCase();

            // SQL query to insert a course into the course catalog
            PreparedStatement insertCourseQuery = databaseConnection.prepareStatement( "INSERT INTO course_offerings(course_code, faculty_id, year, semester, department_id) VALUES (?, ?, ?, ?, ?)" );
            insertCourseQuery.setString( 1, courseCode );
            insertCourseQuery.setString( 2, facultyID );
            insertCourseQuery.setInt( 3, currentYear );
            insertCourseQuery.setInt( 4, currentSemester );
            insertCourseQuery.setString( 5, departmentID );

            // Returns 1 if a new course was inserted, 0 otherwise
            int insertCourseQueryResult = insertCourseQuery.executeUpdate();
            return insertCourseQueryResult == 1;
        } catch ( Exception error ) {
            System.out.println( "Error. Please verify that the course is not already offered" );
            return false;
        }
    }

    @Override
    public String getDepartment( String facultyID ) {
        try {
            if ( facultyID == null ) return "";
            facultyID = facultyID.toUpperCase();

            // SQL query to retrieve the department ID of the faculty from the database
            PreparedStatement getDepartmentQuery = databaseConnection.prepareStatement( "SELECT department_id FROM faculty WHERE faculty_id = ?" );
            getDepartmentQuery.setString( 1, facultyID );
            ResultSet getDepartmentQueryResult = getDepartmentQuery.executeQuery();
            getDepartmentQueryResult.next();

            // Gets the department ID of the faculty from the ResultSet ( if the professor ID is valid )
            return getDepartmentQueryResult.getString( 1 );
        } catch ( Exception error ) {
            System.out.println( "Faculty ID not found in database." );
            return "";
        }
    }

    @Override
    public boolean setCGCriteria( String facultyID, String courseCode, double minimumCGPA, int[] currentSession, String departmentID ) {
        try {
            if ( facultyID == null || courseCode == null || minimumCGPA < 0 || minimumCGPA > 10 || currentSession == null || departmentID == null )
                return false;
            if ( currentSession.length != 2 || currentSession[0] < 0 || currentSession[1] < 0 ) return false;
            facultyID = facultyID.toUpperCase();
            courseCode = courseCode.toUpperCase();
            departmentID = departmentID.toUpperCase();

            // SQL query to update the CG requirements of the corresponding offering in the database
            PreparedStatement setCGQuery = databaseConnection.prepareStatement( "UPDATE course_offerings SET cgpa_criteria = ? WHERE course_code = ? AND faculty_id = ? AND year = ? AND semester = ? AND department_id = ?" );
            setCGQuery.setDouble( 1, minimumCGPA );
            setCGQuery.setString( 2, courseCode );
            setCGQuery.setString( 3, facultyID );
            setCGQuery.setInt( 4, currentSession[0] );
            setCGQuery.setInt( 5, currentSession[1] );
            setCGQuery.setString( 6, departmentID );
            setCGQuery.executeUpdate();

            // Returns true if the query was successful and the CG criteria was updated ( if it was change, there is no difference )
            return true;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please verify that the course offering exists in the current session" );
            return false;
        }
    }

    @Override
    public boolean setInstructorPrerequisites( String departmentID, String courseCode, String[][] prerequisites, int[] currentSession ) {
        try {
            if ( departmentID == null || courseCode == null || prerequisites == null || currentSession == null )
                return false;
            if ( currentSession.length != 2 || currentSession[0] < 0 || currentSession[1] <= 0 ) return false;
            departmentID = departmentID.toUpperCase();
            courseCode = courseCode.toUpperCase();

            // If prerequisites already exist for this course offering, those are dropped first
            int currentYear     = currentSession[0];
            int currentSemester = currentSession[1];
            int successStatus   = 1;

            // SQL query to drop the prerequisites for this course first
            PreparedStatement dropQuery = databaseConnection.prepareStatement( "DELETE FROM instructor_prerequisites WHERE course_code = ? AND year = ? AND semester = ? AND department_id = ?" );
            dropQuery.setString( 1, courseCode );
            dropQuery.setInt( 2, currentYear );
            dropQuery.setInt( 3, currentSemester );
            dropQuery.setString( 4, departmentID );
            dropQuery.executeUpdate();

            // Set the prerequisites again
            PreparedStatement insertQuery = databaseConnection.prepareStatement( "INSERT INTO instructor_prerequisites VALUES (?, ?, ?, ?, ?, ?, ?)" );
            insertQuery.setString( 1, courseCode );
            insertQuery.setInt( 2, currentYear );
            insertQuery.setInt( 3, currentSemester );
            insertQuery.setString( 4, departmentID );

            // j denotes the type i.e., the group of offerings
            int j = 1;

            // We iterate through all the prerequisites
            for ( String[] groups : prerequisites ) {

                // If an invalid length has somehow entered the function
                // All arrays must be strings of even length where the even parity items are courses and odd parity items are course grades
                if ( groups.length % 2 == 1 ) {
                    successStatus = 0;
                    continue;
                }

                // Iterate through all the courses entered
                for ( int i = 0; i < groups.length; i += 2 ) {
                    insertQuery.setString( 5, groups[i].toUpperCase() );
                    int gradeCriteria = Integer.parseInt( groups[i + 1] );

                    // The grade checking is done to ensure that the database does not complain
                    if ( gradeCriteria > 10 || gradeCriteria < 0 ) continue;
                    insertQuery.setInt( 6, gradeCriteria );
                    insertQuery.setInt( 7, j );

                    // Success status = 1 is delivered only if ALL the entries were inserted into the database
                    successStatus &= insertQuery.executeUpdate();
                }
                j++;
            }

            return successStatus == 1;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please verify details before trying again" );
            return false;
        }
    }

    @Override
    public boolean dropCourseOffering( String facultyID, String courseCode, int currentYear, int currentSemester, String departmentID ) {
        try {
            if ( facultyID == null || courseCode == null || currentSemester < 0 || currentYear < 0 || departmentID == null ) return false;
            facultyID = facultyID.toUpperCase();
            courseCode = courseCode.toUpperCase();
            departmentID = departmentID.toUpperCase();

            // SQL query to drop the course from the table if it exists ( note that it also drops all students of the course )
            PreparedStatement dropOfferingQuery = databaseConnection.prepareStatement( "DELETE FROM course_offerings WHERE faculty_id = ? AND course_code = ? AND year = ? AND semester = ? AND department_id = ?" );
            dropOfferingQuery.setString( 1, facultyID );
            dropOfferingQuery.setString( 2, courseCode );
            dropOfferingQuery.setInt( 3, currentYear );
            dropOfferingQuery.setInt( 4, currentSemester );
            dropOfferingQuery.setString( 5, departmentID );

            // If the course was dropped successfully return 1. If not return 0
            return dropOfferingQuery.executeUpdate() == 1;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return false;
        }
    }

    @Override
    public boolean checkIfOfferedBySelf( String facultyID, String courseCode, int currentYear, int currentSemester, String departmentID ) {
        try {
            if ( facultyID == null || courseCode == null || currentSemester < 0 || currentYear < 0 || departmentID == null ) return false;
            facultyID = facultyID.toUpperCase();
            courseCode = courseCode.toUpperCase();
            departmentID = departmentID.toUpperCase();

            // SQL query to fetch the course with these parameters if it exists
            PreparedStatement checkOfferingQuery = databaseConnection.prepareStatement( "SELECT course_code FROM course_offerings WHERE faculty_id = ? AND course_code = ? AND year = ? AND semester = ? AND department_id = ?" );
            checkOfferingQuery.setString( 1, facultyID );
            checkOfferingQuery.setString( 2, courseCode );
            checkOfferingQuery.setInt( 3, currentYear );
            checkOfferingQuery.setInt( 4, currentSemester );
            checkOfferingQuery.setString( 5, departmentID );
            ResultSet checkOfferingQueryResult = checkOfferingQuery.executeQuery();

            // True only if there exists a course with these properties
            return checkOfferingQueryResult.next();
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return false;
        }
    }

    @Override
    public boolean setCourseCategory( String courseCode, int currentYear, int currentSemester, String courseCategory, String department, int[] years, String offeringDepartment ) {
        try {
            if ( courseCode == null || currentYear < 0 || currentSemester < 0 || courseCategory == null || department == null || years == null || offeringDepartment == null )
                return false;
            courseCode = courseCode.toUpperCase();
            courseCategory = courseCategory.toUpperCase();
            department = department.toUpperCase();
            offeringDepartment = offeringDepartment.toUpperCase();

            // SQL query to insert all the entries into the database
            PreparedStatement insertCourseQuery = databaseConnection.prepareStatement( "INSERT INTO course_category VALUES (?, ?, ?, ?, ?, ?, ?)" );
            insertCourseQuery.setString( 1, courseCode );
            insertCourseQuery.setInt( 2, currentYear );
            insertCourseQuery.setInt( 3, currentSemester );
            insertCourseQuery.setString( 4, offeringDepartment );
            insertCourseQuery.setString( 5, courseCategory );
            insertCourseQuery.setString( 7, department );

            // Iterate through all the years and insert the entries for the corresponding years into the database
            boolean insertResult = true;
            for ( int year : years ) {
                if ( year < 0 ) {
                    insertResult = false;
                    continue;
                }
                insertCourseQuery.setInt( 6, year );
                insertCourseQuery.executeUpdate();
            }

            // If all the insert queries were executed without errors, return true
            return insertResult;
        } catch ( Exception error ) {
            System.out.println( "Database error. Please try again later" );
            return false;
        }
    }

    @Override
    public boolean verifyCore( String courseCode, String departmentID, int year ) {
        try {
            if ( courseCode == null || departmentID == null || year < 0 ) return false;
            courseCode = courseCode.toUpperCase();
            departmentID = departmentID.toUpperCase();

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

    public String[][] getGradesOfCourse( String courseCode, int year, int semester, String departmentID ) {
        try {
            if ( courseCode == null || year < 0 || semester < 0 || departmentID == null ) return new String[][]{};
            courseCode = courseCode.toUpperCase();
            departmentID = departmentID.toUpperCase();

            // SQL query to fetch the grades of a particular course from the database
            PreparedStatement getGradesQuery = databaseConnection.prepareStatement( "SELECT entry_number, grade FROM student_course_registration WHERE course_code = ? AND year = ? AND semester = ? AND department_id = ? ORDER BY entry_number" );
            getGradesQuery.setString( 1, courseCode );
            getGradesQuery.setInt( 2, year );
            getGradesQuery.setInt( 3, semester );
            getGradesQuery.setString( 4, departmentID );
            ResultSet getGradesQueryResult = getGradesQuery.executeQuery();

            // Once the results have been fetched, they must be converted to the proper return form
            ArrayList<String[]> records = new ArrayList<>();

            // The ResultSet contains entry numbers and grades
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
    public String[][] getCourseEnrollmentsList( String courseCode, int year, int semester, String offeringDepartment ) {
        try {
            if ( courseCode == null || year < 0 || semester < 0 ) return new String[][]{};
            courseCode = courseCode.toUpperCase();

            // SQL query to get the name and entry number for this particular course offeirng
            PreparedStatement getEnrollmentsQuery = databaseConnection.prepareStatement( "SELECT name, entry_number FROM student_course_registration NATURAL JOIN student WHERE course_code = ? AND year = ? AND semester = ? AND department_id = ?  ORDER BY entry_number" );
            getEnrollmentsQuery.setString( 1, courseCode );
            getEnrollmentsQuery.setInt( 2, year );
            getEnrollmentsQuery.setInt( 3, semester );
            getEnrollmentsQuery.setString( 4, offeringDepartment );
            ResultSet getEnrollmentsQueryResult = getEnrollmentsQuery.executeQuery();

            // Converting the result set that was obtained
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
    public String[] getListOfStudents( String courseCode, int year, int semester, String offeringDepartment ) {
        try {
            if ( courseCode == null || year < 0 || semester < 0 || offeringDepartment == null ) return new String[]{};
            courseCode = courseCode.toUpperCase();
            offeringDepartment = offeringDepartment.toUpperCase();

            // A list to store all the students enrolled in this course
            ArrayList<String> listOfStudents = new ArrayList<>();

            // SQL query to retrieve the list of students from the database
            PreparedStatement getStudentsQuery = databaseConnection.prepareStatement( "SELECT entry_number FROM student_course_registration WHERE course_code = ? AND year = ? AND semester = ? AND department_id = ?" );
            getStudentsQuery.setString( 1, courseCode );
            getStudentsQuery.setInt( 2, year );
            getStudentsQuery.setInt( 3, semester );
            getStudentsQuery.setString( 4, offeringDepartment );
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
            if ( currentYear < 0 || currentSemester <= 0 ) return false;

            // SQL query to check if the current event in the given session is enrolling
            PreparedStatement offeringCheckQuery = databaseConnection.prepareStatement( "SELECT * FROM current_year_and_semester WHERE year = ? AND semester = ? AND current_event = 'OFFERING'" );
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
            if ( currentYear < 0 || currentSemester <= 0 ) return false;

            // SQL query to check if the current event in the given session is enrolling
            PreparedStatement offeringCheckQuery = databaseConnection.prepareStatement( "SELECT * FROM current_year_and_semester WHERE year = ? AND semester = ? AND current_event = 'GRADE SUBMISSION'" );
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
    public boolean uploadCourseGrades( String courseCode, int year, int semester, String offeringDepartment, String[] listOfStudents, String[] listOfGrades ) {
        try {
            if ( courseCode == null || year < 0 || semester < 0 || listOfGrades == null || listOfStudents == null || offeringDepartment == null || listOfStudents.length != listOfGrades.length ) return false;
            courseCode = courseCode.toUpperCase();
            offeringDepartment = offeringDepartment.toUpperCase();

            // The database requires the course codes to be in uppercase
            courseCode = courseCode.toUpperCase();

            // Construct the SQL query that will be used to update course grades
            PreparedStatement uploadGradeQuery = databaseConnection.prepareStatement( "UPDATE student_course_registration SET grade = ? WHERE entry_number = ? AND  course_code = ? AND year = ? AND semester = ? AND department_id = ?" );
            uploadGradeQuery.setString( 3, courseCode );
            uploadGradeQuery.setInt( 4, year );
            uploadGradeQuery.setInt( 5, semester );
            uploadGradeQuery.setString( 6, offeringDepartment );

            // Now iterate through the list of students and grades and set the grade and entry number of the student
            boolean insertResult = true;
            for ( int i = 0; i < listOfStudents.length; i++ ) {
                if ( listOfGrades[i] == null || listOfStudents[i] == null ) {
                    insertResult = false;
                    continue;
                }
                uploadGradeQuery.setString( 1, listOfGrades[i] );
                uploadGradeQuery.setString( 2, listOfStudents[i].toUpperCase() );
                uploadGradeQuery.executeUpdate();
            }
            return insertResult;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please verify that all grades are valid" );
            return false;
        }
    }

    @Override
    public boolean isCourseAlreadyOffered( String courseCode, int currentYear, int currentSemester, String offeringDepartment ) {
        try {
            if ( courseCode == null || currentYear < 0 || currentSemester <= 0 || offeringDepartment == null ) return true;
            courseCode = courseCode.toUpperCase();
            offeringDepartment = offeringDepartment.toUpperCase();

            // SQL query to check if this course has been offered by your department already
            PreparedStatement checkOfferingQuery = databaseConnection.prepareStatement( "SELECT * FROM course_offerings WHERE course_code = ? AND year = ? AND semester = ? AND department_id = ?" );
            checkOfferingQuery.setString( 1, courseCode );
            checkOfferingQuery.setInt( 2, currentYear );
            checkOfferingQuery.setInt( 3, currentSemester );
            checkOfferingQuery.setString( 4, offeringDepartment );
            ResultSet checkOfferingQueryResult = checkOfferingQuery.executeQuery();

            // If the query returns a row, then the course has already been offered
            return checkOfferingQueryResult.next();
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return true;
        }
    }

    @Override
    public String[][] getInstructorPrerequisites( String courseCode, int year, int semester, String departmentID ) {
        try {
            if ( courseCode == null || year < 0 || semester < 0 || departmentID == null ) return new String[][]{};

            // SQL query to fetch the instructor prerequisites from the specified course
            PreparedStatement getPrerequisiteQuery = databaseConnection.prepareStatement("SELECT prereq, grade_criteria FROM instructor_prerequisites WHERE course_code = ? AND year = ? AND semester = ? AND department_id = ? ");
            getPrerequisiteQuery.setString( 1, courseCode );
            getPrerequisiteQuery.setInt( 2, year );
            getPrerequisiteQuery.setInt( 3, semester );
            getPrerequisiteQuery.setString( 4, departmentID );
            ResultSet getPrerequisiteQueryResult = getPrerequisiteQuery.executeQuery();

            ArrayList<String[]> courseList = new ArrayList<>();
            while ( getPrerequisiteQueryResult.next() ) {
                ArrayList<String> course = new ArrayList<>();
                course.add( getPrerequisiteQueryResult.getString( 1 ) );
                course.add( getPrerequisiteQueryResult.getString( 2 ) );
                courseList.add( course.toArray( new String[course.size()]) );
            }

            return courseList.toArray( new String[courseList.size()][] );
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return new String[][]{};
        }
    }
}
