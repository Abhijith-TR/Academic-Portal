package org.example.dal;

import org.example.daoInterfaces.StudentDAO;
import org.example.utils.Utils;

import javax.xml.transform.Result;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PostgresStudentDAO extends PostgresCommonDAO implements StudentDAO {
    public PostgresStudentDAO( String connectionURL, String username, String password ) {
        super( connectionURL, username, password );
    }

    @Override
    public boolean checkCourseOffering( String courseCode, int currentYear, int currentSemester ) {
        try {
            PreparedStatement checkQuery = databaseConnection.prepareStatement( "SELECT course_code FROM course_offerings WHERE course_code = ? AND year = ? AND semester = ?" );
            checkQuery.setString( 1, courseCode );
            checkQuery.setInt( 2, currentYear );
            checkQuery.setInt( 3, currentSemester );
            ResultSet doesCourseExist = checkQuery.executeQuery();
            return doesCourseExist.next();
        } catch ( SQLException error ) {
            System.out.println( error.getMessage() );
            return false;
        }
    }

    @Override
    public boolean checkStudentPassStatus( String courseCode, int gradeCutoff, String entryNumber ) {
        try {
            PreparedStatement gradeQuery = databaseConnection.prepareStatement( "SELECT grade FROM student_course_registration WHERE entry_number = ? AND course_code = ?" );
            gradeQuery.setString( 1, entryNumber );
            gradeQuery.setString( 2, courseCode );
            ResultSet courseQueryResult = gradeQuery.executeQuery();
            boolean   studentCredited   = courseQueryResult.next();
            if ( !studentCredited ) {
                return false;
            }
            String grade = courseQueryResult.getString( 1 );
            return Utils.getGradeValue( grade ) >= gradeCutoff;
        } catch ( Exception error ) {
            System.out.println( error.getMessage() );
            return false;
        }
    }

    @Override
    public String[] getCourseCatalogPrerequisites( String courseCode ) {
        try {
            PreparedStatement prerequisiteQuery = databaseConnection.prepareStatement( "SELECT pre_requisites FROM course_catalog WHERE course_code = ?" );
            prerequisiteQuery.setString( 1, courseCode );
            ResultSet courseCatalogResult = prerequisiteQuery.executeQuery();
            boolean   subjectExists       = courseCatalogResult.next();
            if ( !subjectExists ) {
                return null;
            }
            Array prerequisites = courseCatalogResult.getArray( 1 );
            if ( prerequisites == null ) return new String[]{};
            return (String[]) prerequisites.getArray();
        } catch ( SQLException error ) {
            System.out.println( error.getMessage() );
            return null;
        }
    }

    @Override
    public String[][] getInstructorPrerequisites( String courseCode, int year, int semester ) {
        try {
            PreparedStatement prerequisiteQuery = databaseConnection.prepareStatement( "SELECT instructor_prerequisites FROM course_offerings WHERE course_code = ? AND semester = ? AND year = ?" );
            prerequisiteQuery.setString( 1, courseCode );
            prerequisiteQuery.setInt( 2, semester );
            prerequisiteQuery.setInt( 3, year );
            ResultSet prerequisiteResult = prerequisiteQuery.executeQuery();

            boolean doesCourseExist = prerequisiteResult.next();
            if ( !doesCourseExist ) {
                return null;
            }

            Array prerequisites = prerequisiteResult.getArray( 1 );
            if ( prerequisites == null ) return new String[][]{};
            return (String[][]) prerequisites.getArray();
        } catch ( Exception error ) {
            System.out.println( error.getMessage() );
            return null;
        }
    }

    // The 24 is being returned to prevent you from enrolling in the course due to the exception that occurred
    @Override
    public double getCreditsOfCourse( String courseCode ) {
        try {
            PreparedStatement creditQuery = databaseConnection.prepareStatement( "SELECT credits FROM course_catalog WHERE course_code = ?" );
            creditQuery.setString( 1, courseCode );
            ResultSet creditQueryResult  = creditQuery.executeQuery();
            boolean   querySuccessStatus = creditQueryResult.next();
            if ( !querySuccessStatus ) return 24;
            return creditQueryResult.getInt( 1 );
        } catch ( Exception error ) {
            System.out.println( error.getMessage() );
            return 24;
        }
    }

    // The minimum credit limit is set to 18 and the maximum is set to 24
    @Override
    public double getCreditsInSession( String entryNumber, int currentYear, int currentSemester ) {
        try {
            PreparedStatement creditQuery = databaseConnection.prepareStatement( "SELECT SUM(credits) FROM course_catalog WHERE course_code IN (SELECT course_code FROM student_course_registration WHERE entry_number = ? AND year = ? AND semester = ?)" );
            creditQuery.setString( 1, entryNumber );
            creditQuery.setInt( 2, currentYear );
            creditQuery.setInt( 3, currentSemester );
            ResultSet creditQueryResult = creditQuery.executeQuery();

            boolean coursesFound = creditQueryResult.next();
            return ( coursesFound ) ? creditQueryResult.getInt( 1 ) : 0;
        } catch ( Exception error ) {
            System.out.println( error.getMessage() );
            // You should be setting the credit limit over here
            return 24;
        }
    }

    @Override
    public boolean enroll( String courseCode, String entryNumber, int currentYear, int currentSemester ) {
        try {
            PreparedStatement enrollmentQuery = databaseConnection.prepareStatement( "INSERT INTO student_course_registration VALUES (?, ?, ?, ?)" );
            enrollmentQuery.setString( 1, entryNumber );
            enrollmentQuery.setString( 2, courseCode );
            enrollmentQuery.setInt( 3, currentYear );
            enrollmentQuery.setInt( 4, currentSemester );
            // Maybe parameterize the default grade later on? Currently, the database will set the default grade by itself if you don't give it anything
            // Do I just assume that the call to insert was successful?
            enrollmentQuery.executeUpdate();
            return true;
        } catch ( Exception error ) {
            System.out.println( "Enrollment Request Failed" );
            return false;
        }
    }

    @Override
    public boolean dropCourse( String courseCode, String entryNumber, int currentYear, int currentSemester ) {
        try {
            PreparedStatement dropQuery = databaseConnection.prepareStatement( "DELETE FROM student_course_registration WHERE course_code = ? AND entry_number = ? AND year = ? AND semester = ?" );
            dropQuery.setString( 1, courseCode );
            dropQuery.setString( 2, entryNumber );
            dropQuery.setInt( 3, currentYear );
            dropQuery.setInt( 4, currentSemester );
            int dropQueryResult = dropQuery.executeUpdate();
            return dropQueryResult == 1;
        } catch ( Exception error ) {
            System.out.println( "Database Error. Try again later" );
            return false;
        }
    }

    @Override
    public String[][] getAllRecords( String entryNumber ) {
        try {
            PreparedStatement recordsQuery = databaseConnection.prepareStatement( "SELECT credits, grade FROM student_course_registration NATURAL JOIN course_catalog WHERE entry_number = ?" );
            recordsQuery.setString( 1, entryNumber );
            ResultSet recordsQueryResult = recordsQuery.executeQuery();

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
            PreparedStatement getCoursesQuery = databaseConnection.prepareStatement( "SELECT course_code, course_title, name, pre_requisites FROM course_offerings NATURAL JOIN course_catalog NATURAL JOIN faculty WHERE year = ? AND semester = ?" );
            getCoursesQuery.setInt( 1, currentYear );
            getCoursesQuery.setInt( 2, currentSemester );
            ResultSet getCoursesQueryResult = getCoursesQuery.executeQuery();

            ArrayList<String[]> records = new ArrayList<>();
            while ( getCoursesQueryResult.next() ) {
                String course_code    = getCoursesQueryResult.getString( 1 );
                String course_title   = getCoursesQueryResult.getString( 2 );
                String faculty_name   = getCoursesQueryResult.getString( 3 );
                String pre_requisites = getCoursesQueryResult.getString( 4 );
                records.add( new String[]{ course_code, course_title, faculty_name, pre_requisites } );
            }
            return records.toArray( new String[records.size()][] );
        } catch ( Exception error ) {
            System.out.println( "Database Error. Try again later" );
            return new String[][]{};
        }
    }

    @Override
    public String getCourseGrade( String entryNumber, String courseCode ) {
        try {
            PreparedStatement getGradeQuery = databaseConnection.prepareStatement( "SELECT grade FROM student_course_registration WHERE entry_number = ? AND course_code = ?" );
            getGradeQuery.setString( 1, entryNumber );
            getGradeQuery.setString( 2, courseCode );
            ResultSet getGradeQueryResult = getGradeQuery.executeQuery();
            boolean   doesRecordExist     = getGradeQueryResult.next();
            if ( doesRecordExist == false ) return "-";
            return getGradeQueryResult.getString( 1 );
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return "A";
        }
    }

    @Override
    public String[] getStudentAndCourseDepartment( String entryNumber, String courseCode ) {
        try {
            PreparedStatement studentBranchQuery = databaseConnection.prepareStatement( "SELECT department.name FROM department NATURAL JOIN student WHERE entry_number = ?" );
            studentBranchQuery.setString( 1, entryNumber );
            ResultSet studentBranchQueryResult = studentBranchQuery.executeQuery();
            studentBranchQueryResult.next();
            String studentBranch = studentBranchQueryResult.getString( 1 );

            PreparedStatement courseBranchQuery = databaseConnection.prepareStatement( "SELECT department.name FROM department NATURAL JOIN course_catalog WHERE course_code = ?" );
            courseBranchQuery.setString( 1, courseCode );
            ResultSet courseBranchQueryResult = courseBranchQuery.executeQuery();
            courseBranchQueryResult.next();
            String courseBranch = courseBranchQueryResult.getString( 1 );

            return new String[]{ studentBranch, courseBranch };
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return new String[]{};
        }
    }

    @Override
    public boolean checkIfCore( String studentDepartment, int batch, String courseCode ) {
        try {
            String tableName = "core_courses_" + batch;
            PreparedStatement checkCoreQuery = databaseConnection.prepareStatement("SELECT category FROM " + tableName + " WHERE department_id = ? AND course_code = ?");
            checkCoreQuery.setString( 1, studentDepartment );
            checkCoreQuery.setString( 2, courseCode );
            ResultSet checkCoreQueryResult = checkCoreQuery.executeQuery();
            return checkCoreQueryResult.next();
        } catch ( Exception error ) {
            System.out.println( "Database Error. Please try again later" );
            return false;
        }
    }
}
