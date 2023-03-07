package org.abhijith.dal;

import org.abhijith.daoInterfaces.StudentDAO;
import org.abhijith.utils.Utils;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class PostgresStudentDAO extends PostgresCommonDAO implements StudentDAO {
    public PostgresStudentDAO() {
        super();
    }

    @Override
    public boolean checkCourseOffering( String courseCode, int currentYear, int currentSemester, String courseDepartment ) {
        try {
            if ( currentYear <= 0 || currentSemester <= 0 || courseCode == null || courseDepartment == null )
                return false;
            courseCode = courseCode.toUpperCase();
            courseDepartment = courseDepartment.toUpperCase();

            // SQL query to find the course in the course offerings database
            PreparedStatement checkQuery = databaseConnection.prepareStatement( "SELECT course_code FROM course_offerings WHERE course_code = ? AND year = ? AND semester = ? AND department_id = ?" );
            checkQuery.setString( 1, courseCode );
            checkQuery.setInt( 2, currentYear );
            checkQuery.setInt( 3, currentSemester );
            checkQuery.setString( 4, courseDepartment );
            ResultSet doesCourseExist = checkQuery.executeQuery();

            // Returns true if it manages to find such an entry
            return doesCourseExist.next();
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return false;
        }
    }

    @Override
    public boolean checkStudentPassStatus( String courseCode, int gradeCutoff, String entryNumber ) {
        try {
            if ( courseCode == null || gradeCutoff < 0 || gradeCutoff > 10 || entryNumber == null ) return false;
            courseCode = courseCode.toUpperCase();
            entryNumber = entryNumber.toUpperCase();

            // SQL query to fetch the grade of the corresponding student of the corresponding course code
            PreparedStatement gradeQuery = databaseConnection.prepareStatement( "SELECT grade FROM student_course_registration WHERE entry_number = ? AND course_code = ? ORDER BY grade" );
            gradeQuery.setString( 1, entryNumber );
            gradeQuery.setString( 2, courseCode );
            ResultSet courseQueryResult = gradeQuery.executeQuery();

            // The student may have enrolled in the course many times, return the highest possible grade
            while ( courseQueryResult.next() ) {
                String grade = courseQueryResult.getString( 1 );
                if ( Utils.getGradeValue( grade ) >= gradeCutoff ) return true;
            }
            // If no records are found, return false
            return false;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return false;
        }
    }

    @Override
    public String[] getCourseCatalogPrerequisites( String courseCode ) {
        try {
            if ( courseCode == null ) return null;
            courseCode = courseCode.toUpperCase();

            // SQL query to fetch all the prerequisites from the course catalog for this particular course code
            PreparedStatement prerequisiteQuery = databaseConnection.prepareStatement( "SELECT pre_requisites FROM course_catalog WHERE course_code = ?" );
            prerequisiteQuery.setString( 1, courseCode );
            ResultSet courseCatalogResult = prerequisiteQuery.executeQuery();
            boolean   subjectExists       = courseCatalogResult.next();
            // If the subject does not exist, return null
            if ( !subjectExists ) {
                return null;
            }
            Array prerequisites = courseCatalogResult.getArray( 1 );
            // If the course code exists but no prerequisites exist, return an empty string to denote there are no prerequisites
            if ( prerequisites == null ) return new String[]{};
            return (String[]) prerequisites.getArray();
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return null;
        }
    }

    @Override
    public String[][] getInstructorPrerequisites( String courseCode, int year, int semester, String courseDepartment ) {
        try {
            if ( courseCode == null || year < 0 || semester < 0 || courseDepartment == null ) return null;
            courseCode = courseCode.toUpperCase();
            courseDepartment = courseDepartment.toUpperCase();

            // SQL query to get the prerequisites of a particular offering from the database
            PreparedStatement prerequisiteQuery = databaseConnection.prepareStatement( "SELECT prereq, grade_criteria, type FROM instructor_prerequisites WHERE course_code = ? AND semester = ? AND year = ? AND department_id = ? ORDER BY type , course_code" );
            prerequisiteQuery.setString( 1, courseCode );
            prerequisiteQuery.setInt( 2, semester );
            prerequisiteQuery.setInt( 3, year );
            prerequisiteQuery.setString( 4, courseDepartment );
            ResultSet prerequisiteResult = prerequisiteQuery.executeQuery();

            // Convert the result set into the required format
            ArrayList<ArrayList<String>> prerequisites = new ArrayList<>();
            while ( prerequisiteResult.next() ) {
                // The array contains { Prerequisite Course Code, Grade Criteria }
                String course        = prerequisiteResult.getString( 1 );
                String gradeCriteria = prerequisiteResult.getString( 2 );
                int    type          = prerequisiteResult.getInt( 3 );
                // If the type has not been encountered so far, add a new entry into the array list
                if ( type > prerequisites.size() ) {
                    prerequisites.add( new ArrayList<>() );
                }
                prerequisites.get( type - 1 ).add( course );
                prerequisites.get( type - 1 ).add( gradeCriteria );
            }
            // Converting the array list to the proper 2D array format
            ArrayList<String[]> arrayToReturn = new ArrayList<>();
            for ( ArrayList<String> temp : prerequisites ) arrayToReturn.add( temp.toArray( new String[temp.size()] ) );
            return arrayToReturn.toArray( new String[arrayToReturn.size()][] );
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return null;
        }
    }

    @Override
    public double getCreditsOfCourse( String courseCode ) {
        try {
            if ( courseCode == null ) return 25;
            courseCode = courseCode.toUpperCase();

            // SQL query to fetch the credits for the corresponding course from the database
            PreparedStatement creditQuery = databaseConnection.prepareStatement( "SELECT credits FROM course_catalog WHERE course_code = ?" );
            creditQuery.setString( 1, courseCode );
            ResultSet creditQueryResult  = creditQuery.executeQuery();
            boolean   querySuccessStatus = creditQueryResult.next();
            // If the course code cannot exist, return 25 to denote the fact that nobody can enroll in it
            if ( !querySuccessStatus ) return 25;
            return creditQueryResult.getInt( 1 );
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return 25;
        }
    }

    @Override
    public double getCreditsInSession( String entryNumber, int currentYear, int currentSemester ) {
        try {
            if ( entryNumber == null || currentSemester < 0 || currentYear < 0 ) return 25;
            entryNumber = entryNumber.toUpperCase();

            // SQL query to fetch the total number of credits that the student has enrolled in the current semester
            PreparedStatement creditQuery = databaseConnection.prepareStatement( "SELECT SUM(credits) FROM course_catalog WHERE course_code IN (SELECT course_code FROM student_course_registration WHERE entry_number = ? AND year = ? AND semester = ?)" );
            creditQuery.setString( 1, entryNumber );
            creditQuery.setInt( 2, currentYear );
            creditQuery.setInt( 3, currentSemester );
            ResultSet creditQueryResult = creditQuery.executeQuery();

            // If no results are found, return 0 to indicate that the student has not enrolled in any course so far
            boolean coursesFound = creditQueryResult.next();
            return ( coursesFound ) ? creditQueryResult.getInt( 1 ) : 0;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return 25;
        }
    }

    @Override
    public boolean enroll( String courseCode, String entryNumber, int currentYear, int currentSemester, String offeringDepartment, String courseCategory ) {
        try {
            if ( courseCode == null || entryNumber == null || currentSemester <= 0 || currentYear <= 0 || offeringDepartment == null || courseCategory == null )
                return false;
            courseCode = courseCode.toUpperCase();
            entryNumber = entryNumber.toUpperCase();
            offeringDepartment = offeringDepartment.toUpperCase();
            courseCategory = courseCategory.toUpperCase();

            // SQL query to insert the enrollment into the student_course_registration_table in the database
            PreparedStatement enrollmentQuery = databaseConnection.prepareStatement( "INSERT INTO student_course_registration(entry_number, course_code, year, semester, department_id, category) VALUES (?, ?, ?, ?, ?, ?)" );
            enrollmentQuery.setString( 1, entryNumber );
            enrollmentQuery.setString( 2, courseCode );
            enrollmentQuery.setInt( 3, currentYear );
            enrollmentQuery.setInt( 4, currentSemester );
            enrollmentQuery.setString( 5, offeringDepartment );
            enrollmentQuery.setString( 6, courseCategory );
            int enrollmentResult = enrollmentQuery.executeUpdate();
            // Returns true only if the insertion was successful in the database
            return enrollmentResult == 1;
        } catch ( Exception error ) {
            System.out.println( "Enrollment Request Failed" );
            return false;
        }
    }

    @Override
    public boolean dropCourse( String courseCode, String entryNumber, int currentYear, int currentSemester ) {
        try {
            if ( courseCode == null || entryNumber == null || currentYear <= 0 || currentSemester <= 0 ) return false;
            courseCode = courseCode.toUpperCase();
            entryNumber = entryNumber.toUpperCase();

            // SQL query to delete the corresponding entry of the corresponding student from the database
            PreparedStatement dropQuery = databaseConnection.prepareStatement( "DELETE FROM student_course_registration WHERE course_code = ? AND entry_number = ? AND year = ? AND semester = ?" );
            dropQuery.setString( 1, courseCode );
            dropQuery.setString( 2, entryNumber );
            dropQuery.setInt( 3, currentYear );
            dropQuery.setInt( 4, currentSemester );
            int dropQueryResult = dropQuery.executeUpdate();
            // Returns true if the entry was deleted, false otherwise
            return dropQueryResult == 1;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Try again later" );
            return false;
        }
    }

    @Override
    public String[][] getAllRecords( String entryNumber ) {
        try {
            if ( entryNumber == null ) return new String[][]{};
            entryNumber = entryNumber.toUpperCase();

            // SQL query to fetch the credits and grade of the given entry number from the database
            PreparedStatement recordsQuery = databaseConnection.prepareStatement( "SELECT credits, grade FROM student_course_registration NATURAL JOIN course_catalog WHERE entry_number = ?" );
            recordsQuery.setString( 1, entryNumber );
            ResultSet recordsQueryResult = recordsQuery.executeQuery();

            // Converting data from the result set into the required format
            ArrayList<String[]> records = new ArrayList<>();
            while ( recordsQueryResult.next() ) {
                String credits = recordsQueryResult.getString( 1 );
                String grade   = recordsQueryResult.getString( 2 );
                records.add( new String[]{ credits, grade } );
            }
            return records.toArray( new String[records.size()][] );
        } catch ( Exception error ) {
            System.out.println( "Database Error. Try again later" );
            return new String[][]{};
        }
    }

    @Override
    public String[][] getOfferedCourses( int currentYear, int currentSemester ) {
        try {
            if ( currentYear <= 0 || currentSemester <= 0 ) return new String[][]{};

            PreparedStatement getCoursesQuery = databaseConnection.prepareStatement( "SELECT course_code, course_title, name, pre_requisites, department_id FROM course_offerings NATURAL JOIN course_catalog NATURAL JOIN faculty WHERE year = ? AND semester = ? ORDER BY course_code" );
            getCoursesQuery.setInt( 1, currentYear );
            getCoursesQuery.setInt( 2, currentSemester );
            ResultSet getCoursesQueryResult = getCoursesQuery.executeQuery();

            ArrayList<String[]> records = new ArrayList<>();
            while ( getCoursesQueryResult.next() ) {
                String course_code    = getCoursesQueryResult.getString( 1 );
                String course_title   = getCoursesQueryResult.getString( 2 );
                String faculty_name   = getCoursesQueryResult.getString( 3 );
                String pre_requisites = getCoursesQueryResult.getString( 4 );
                String departmentID   = getCoursesQueryResult.getString( 5 );
                records.add( new String[]{ course_code, course_title, faculty_name, pre_requisites, departmentID, "" } );
            }
            return records.toArray( new String[records.size()][] );
        } catch ( Exception error ) {
            System.out.println( "Database Error. Try again later" );
            return new String[][]{};
        }
    }

    @Override
    public boolean isCurrentEventEnrolling( int currentYear, int currentSemester ) {
        try {
            if ( currentYear <= 0 || currentSemester <= 0 ) return false;

            // SQL query to check if the current event in the given session is enrolling
            PreparedStatement offeringCheckQuery = databaseConnection.prepareStatement( "SELECT * FROM current_year_and_semester WHERE year = ? AND semester = ? AND current_event = 'ENROLLING'" );
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
    public double getCGPACriteria( String courseCode, int currentYear, int currentSemester, String courseDepartment ) {
        try {
            if ( courseCode == null || currentSemester <= 0 || currentYear <= 0 || courseDepartment == null ) return 11;

            // SQL query to fetch the CGPA criteria from the database
            PreparedStatement getCriteriaQuery = databaseConnection.prepareStatement( "SELECT cgpa_criteria FROM course_offerings WHERE course_code = ? AND year = ? AND semester = ? AND department_id = ?" );
            getCriteriaQuery.setString( 1, courseCode );
            getCriteriaQuery.setInt( 2, currentYear );
            getCriteriaQuery.setInt( 3, currentSemester );
            getCriteriaQuery.setString( 4, courseDepartment );
            ResultSet getCriteriaQueryResult = getCriteriaQuery.executeQuery();
            boolean courseExist = getCriteriaQueryResult.next();

            // If the course is not found in the database, it returns 11 to denote that you cannot enroll in the course
            if ( !courseExist ) return 11;
            // Returns the CGPA criteria if found from the database
            return getCriteriaQueryResult.getDouble( 1 );
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return 11;
        }
    }

    @Override
    public String getCourseCategory( String courseCode, int year, int semester, String courseDepartment, String studentDepartment, int batch ) {
        try {
            if ( courseCode == null || year < 0 || semester < 0 || courseDepartment == null || studentDepartment == null || batch < 0 )
                return "";

            // SQL query to fetch the course category for this particular student from the database
            PreparedStatement getCategoryQuery = databaseConnection.prepareStatement( "SELECT category FROM course_category WHERE course_code = ? AND year = ? AND semester = ? AND department_id = ? AND batch = ? AND department = ?" );
            getCategoryQuery.setString( 1, courseCode );
            getCategoryQuery.setInt( 2, year );
            getCategoryQuery.setInt( 3, semester );
            getCategoryQuery.setString( 4, courseDepartment );
            getCategoryQuery.setInt( 5, batch );
            getCategoryQuery.setString( 6, studentDepartment );
            ResultSet getCategoryQueryResult = getCategoryQuery.executeQuery();

            // If there is no such entry, student is not eligible
            if ( !getCategoryQueryResult.next() ) return "";
            return getCategoryQueryResult.getString( 1 );
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return "";
        }
    }
}
