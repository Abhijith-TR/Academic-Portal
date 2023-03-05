package org.abhijith.users;

import org.abhijith.dal.PostgresStudentDAO;
import org.abhijith.daoInterfaces.StudentDAO;
import org.abhijith.utils.Utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class Student extends User {
    private StudentDAO studentDAO;

    public Student( String id ) {
        super( id );
        this.studentDAO = new PostgresStudentDAO();
        super.setCommonDAO( studentDAO );
    }

    public void setStudentDAO( StudentDAO studentDAO ) {
        this.studentDAO = studentDAO;
    }

    // The default cutoff of 4 is implemented here.
    private boolean checkCourseCatalogPrerequisites( String courseCode ) {
        String[] prerequisites = studentDAO.getCourseCatalogPrerequisites( courseCode );
        if ( prerequisites == null ) return false;

        for ( String course : prerequisites ) {
            boolean hasPassed = studentDAO.checkStudentPassStatus( course, 4, id );
            if ( !hasPassed ) {
                return false;
            }
        }
        return true;
    }

    // A database error and no prerequisites will both return null. But student should not be allowed to enroll if there is a database error
    private boolean checkInstructorPrerequisites( String courseCode, int[] currentSession, String courseDepartment ) {
        int        currentYear     = currentSession[0];
        int        currentSemester = currentSession[1];
        String[][] prerequisites   = studentDAO.getInstructorPrerequisites( courseCode, currentYear, currentSemester, courseDepartment );
        if ( prerequisites == null ) return false;

        for ( String[] listOfCourses : prerequisites ) {
            boolean isEligible = false;
            for ( int i = 0; i < listOfCourses.length; i += 2 ) {
                String course      = listOfCourses[i];
                int    gradeCutoff = Integer.parseInt( listOfCourses[i + 1] );
                isEligible |= studentDAO.checkStudentPassStatus( course, gradeCutoff, id );
            }
            if ( !isEligible ) return false;
        }
        return true;
    }

    private boolean checkStudentEligibility( String courseCode, int[] currentSession, String courseDepartment ) {
        boolean hasCompletedCourseCatalogPrerequisites = checkCourseCatalogPrerequisites( courseCode );
        if ( !hasCompletedCourseCatalogPrerequisites ) {
            return false;
        }
        return checkInstructorPrerequisites( courseCode, currentSession, courseDepartment );
    }

    private boolean checkCreditLimit( String courseCode, int[] currentSession ) {
        int currentYear     = currentSession[0];
        int currentSemester = currentSession[1];

        // We might want to store this in the database if necessary
        double minimumCreditLimit = 18;
        double maximumCreditLimit = 24;

        double creditsInCurrentSemester = studentDAO.getCreditsInSession( id, currentYear, currentSemester );
        double creditsInPreviousSemester, creditsInSemesterBefore;

        if ( currentSemester == 2 ) {
            creditsInPreviousSemester = studentDAO.getCreditsInSession( id, currentYear, currentSemester - 1 );
            creditsInSemesterBefore = studentDAO.getCreditsInSession( id, currentYear - 1, 2 );
        }
        else {
            creditsInPreviousSemester = studentDAO.getCreditsInSession( id, currentYear - 1, 2 );
            creditsInSemesterBefore = studentDAO.getCreditsInSession( id, currentYear - 1, 1 );
        }

        double creditLimit = ( creditsInSemesterBefore + creditsInPreviousSemester ) / 2 * 1.25;
        creditLimit = Math.max( creditLimit, minimumCreditLimit );
        creditLimit = Math.min( creditLimit, maximumCreditLimit );

        double creditsOfCourse = studentDAO.getCreditsOfCourse( courseCode );
        return ( creditsInCurrentSemester + creditsOfCourse ) > creditLimit;
    }

    public boolean enroll( String courseCode, String courseDepartment ) {
        if ( courseCode == null || courseDepartment == null ) return false;

        // Gets the current year and semester
        int[] currentSession  = studentDAO.getCurrentAcademicSession();
        int   currentYear     = currentSession[0];
        int   currentSemester = currentSession[1];

        // Check if it is currently the enrolling event
        if ( !studentDAO.isCurrentEventEnrolling( currentYear, currentSemester ) ) return false;

        // Checks whether this particular course has been offered in the current session by the mentioned department
        boolean doesCourseExist = studentDAO.checkCourseOffering( courseCode, currentYear, currentSemester, courseDepartment );
        if ( !doesCourseExist ) return false;

        // Checks whether the student has got any grade in this course before
        String courseGrade = studentDAO.getCourseGrade( this.id, courseCode );

        // '-' indicates that the student is already enrolled in this course in this semester
        // "" indicates that something has gone wrong while fetching the grade
        if ( Utils.getGradeValue( courseGrade ) >= 4 || courseGrade.equals( "-" ) || courseGrade.equals( "" ) ) {
            return false;
        }

        // Checks whether the student has completed all the prerequisites i.e., instructor prerequisites and course catalog prerequisites
        boolean isStudentEligible = checkStudentEligibility( courseCode, currentSession, courseDepartment );
        if ( !isStudentEligible ) return false;

        // Check the CGPA criteria
        double CGPA         = getCGPA();
        double CGPACriteria = studentDAO.getCGPACriteria( courseCode, currentYear, currentSemester, courseDepartment );
        if ( CGPA < CGPACriteria ) return false;

        // Checks whether enrolling in this course would exceed the credit limit
        boolean creditLimitExceeded = checkCreditLimit( courseCode, currentSession );
        if ( creditLimitExceeded ) return false;

        // Getting the department of the student and the department of the course
        String studentDepartment = studentDAO.getStudentDepartment( this.id );
        if ( studentDepartment.equals( "" ) ) return false;

        // Getting the batch of the student
        int batch = studentDAO.getBatch( this.id );
        if ( batch == -1 ) return false;

        // Getting the course category. If the batch and department combination does not exist, the student is not eligible for the offering
        String courseCategory = studentDAO.getCourseCategory( courseCode, currentYear, currentSemester, courseDepartment, studentDepartment, batch );
        if ( courseCategory.equals( "" ) ) return false;

        // Once all of the above conditions are fulfilled, the student is eligible to enroll in the course
        // The request to enroll is sent to the database
        return studentDAO.enroll( courseCode, this.id, currentYear, currentSemester, courseDepartment, courseCategory );
    }

    public boolean drop( String courseCode ) {
        // Checking input parameters
        if ( courseCode == null ) return false;

        // Get the current year and semester
        int[] currentSession  = studentDAO.getCurrentAcademicSession();
        int   currentYear     = currentSession[0];
        int   currentSemester = currentSession[1];

        // Check if it is currently the enrolling event
        if ( !studentDAO.isCurrentEventEnrolling( currentYear, currentSemester ) )
            return false;

        // Returns true if the course drop was successful. False otherwise
        return studentDAO.dropCourse( courseCode, id, currentYear, currentSemester );
    }

    // Return format: { { { courseCode, courseTitle, grade, credits } } }
    // The inner array will contain all the records of a particular semester
    public String[][][] getGradesForDegree() {
        // Array to store the records
        ArrayList<String[][]> studentCourseRecords = new ArrayList<>();

        // Gets the current academic session. We iterate from his batch upto the current session
        int[] currentSession  = studentDAO.getCurrentAcademicSession();
        int   currentYear     = currentSession[0];
        int   currentSemester = currentSession[1];

        // Getting the batch of the student. We start from batch - 1 as the first session
        int studentBatch = studentDAO.getBatch( this.id );
        if ( studentBatch == -1 ) return new String[][][]{};

        // We go through all years and semesters upto the current session
        for ( int year = studentBatch; year <= currentYear; year++ ) {
            for ( int semester = 1; semester <= 2; semester++ ) {
                // We use the get grades function to get the grades of a particular semester and year ( i.e., session )
                studentCourseRecords.add( getGrades( year, semester ) );
                // We have reached the current session, we should not be getting any more records
                if ( year == currentYear && semester == currentSemester ) break;
            }
        }

        // Returns the arraylist after converting it into an array
        return studentCourseRecords.toArray( new String[studentCourseRecords.size()][][] );
    }

    // Return format: { { courseCode, courseTitle, grade, credits } }
    public String[][] getGrades( int year, int semester ) {
        // Checking input parameters
        if ( year < 0 || semester < 0 ) return new String[][]{};

        return studentDAO.getStudentGradesForSemester( this.id, year, semester );
    }

    public int getBatch() {
        return studentDAO.getBatch( this.id );
    }

    // The record format expected is { { courseCode, courseTitle, grade, credits } }
    // Note that all of the above are expected to be strings and parsable to double
    public double getSGPA( String[][] records ) {
        // Checking input parameters
        if ( records == null ) return 0.0;

        double totalCredits  = 0;
        double creditsEarned = 0;

        // We iterate through all the records
        for ( String[] record : records ) {
            // Checking input parameters
            if ( record == null || record.length != 4 ) continue;

            // We get the grades and the credits of the course
            double credits             = Double.parseDouble( record[3] );
            double numericalGradeValue = Utils.getGradeValue( record[2] );

            // Computing the SGPA ( Note that E and F grades are considered in SGPA calculation );
            totalCredits += credits;
            creditsEarned += ( numericalGradeValue ) / 10 * credits;
        }
        // If no credits are done, return 0
        if ( totalCredits == 0 ) return 0;

        return creditsEarned / totalCredits * 10;
    }

    public double getCGPA() {
        // Get all the records of the student
        String[][] records       = studentDAO.getAllRecords( this.id );
        double     totalCredits  = 0;
        double     creditsEarned = 0;

        // Go through every record of the student
        for ( String[] record : records ) {
            // If the record is not in the expected format, ignore
            if ( record.length != 2 ) continue;
            double credits             = Double.parseDouble( record[0] );
            int    numericalGradeValue = Utils.getGradeValue( record[1] );

            // E and F grades are not considered in the computation of CGPA
            if ( numericalGradeValue < 4 ) continue;
            totalCredits += credits;
            creditsEarned += ( numericalGradeValue ) / 10.0 * credits;
        }

        // If the student has not done any courses, return 0
        if ( totalCredits == 0 ) return 0;
        return creditsEarned / totalCredits * 10;
    }

    public String[][] getAvailableCourses() {
        // Getting the current year and semester
        int[]  currentSession    = studentDAO.getCurrentAcademicSession();
        int    currentYear       = currentSession[0];
        int    currentSemester   = currentSession[1];

        String studentDepartment = studentDAO.getStudentDepartment( this.id );
        int    studentBatch      = studentDAO.getBatch( this.id );
        if ( studentDepartment.equals( "" ) || studentBatch == -1 ) return new String[][]{};

        // Getting the courses that were offered in the current session
        String[][] coursesOffered = studentDAO.getOfferedCourses( currentYear, currentSemester );
        for ( String[] course : coursesOffered ) {
            course[course.length - 1] = studentDAO.getCourseCategory( course[0], currentYear, currentSemester, course[4], studentDepartment, studentBatch );
        }
        return coursesOffered;
    }

    public HashMap<String, Double> getRemainingCreditRequirements() {
        // Get the batch of the student and retrieve the UG curriculum of the corresponding batch
        int                     batch        = studentDAO.getBatch( this.id );
        if ( batch == -1 ) return new HashMap<>();

        HashMap<String, Double> ugCurriculum = studentDAO.getUGCurriculum( batch );
        if ( ugCurriculum == null ) return new HashMap<>();

        // Now we have to fetch the course categories and the corresponding credits done by the student
        HashMap<String, Double> categoryCreditsCompleted = studentDAO.getCreditsInAllCategories( this.id );
        if ( categoryCreditsCompleted == null ) return new HashMap<>();

        HashMap<String, Double> categoryCreditsLeft      = new HashMap<>();

        // Additional credits in any category will count towards the open electives section
        double additionalCredits = 0;
        for ( String category : ugCurriculum.keySet() ) {
            if ( categoryCreditsCompleted.containsKey( category ) ) {
                double creditsNeeded    = ugCurriculum.get( category );
                double creditsCompleted = categoryCreditsCompleted.get( category );

                // If student has done fewer credits than necessary, return false
                if ( creditsNeeded > creditsCompleted ) {
                    categoryCreditsLeft.put( category, creditsNeeded - creditsCompleted );
                }
                // If student has done more credits than necessary, those can be used for open electives
                else {
                    categoryCreditsLeft.put( category, 0.0 );
                    additionalCredits += creditsCompleted - creditsNeeded;
                }
            }
            else {
                categoryCreditsLeft.put( category, ugCurriculum.get( category ) );
            }
        }

        // Now we have to get open elective requirements to 0 if required
        if ( categoryCreditsLeft.containsKey( "OE" ) ) {
            double creditsLeft = categoryCreditsLeft.get( "OE" );
            categoryCreditsLeft.put( "OE", Math.max( creditsLeft - additionalCredits, 0.0 ) );
        }

        return categoryCreditsLeft;
    }
}
