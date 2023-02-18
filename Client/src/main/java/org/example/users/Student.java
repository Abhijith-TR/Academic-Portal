package org.example.users;

import org.example.daoInterfaces.StudentDAO;
import org.example.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class Student extends User {
    final private StudentDAO studentDAO;

    public Student( String id, StudentDAO studentDAO ) {
        super( id, studentDAO );
        this.studentDAO = studentDAO;
    }

    public boolean updateProfile( int newPhoneNumber ) {
        return true;
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
    private boolean checkInstructorPrerequisites( String courseCode, int[] currentSession ) {
        int        currentYear     = currentSession[0];
        int        currentSemester = currentSession[1];
        String[][] prerequisites   = studentDAO.getInstructorPrerequisites( courseCode, currentYear, currentSemester );
        if ( prerequisites == null ) return false;

        for ( String[] listOfCourses : prerequisites ) {
            boolean isEligible = false;
            for ( int i = 0; i < listOfCourses.length; i += 2 ) {
                String course = listOfCourses[i];
                // PostgreSQL allows only matrices i.e., jagged arrays are not allowed. So some arrays might have additional useless entries.
                if ( course.length() == 0 ) break;
                int gradeCutoff = Utils.getGradeValue( listOfCourses[i + 1] );
                isEligible |= studentDAO.checkStudentPassStatus( course, gradeCutoff, id );
            }
            if ( !isEligible ) return false;
        }
        return true;
    }

    private boolean checkStudentEligibility( String courseCode, int[] currentSession ) {
        boolean hasCompletedCourseCatalogPrerequisites = checkCourseCatalogPrerequisites( courseCode );
        if ( !hasCompletedCourseCatalogPrerequisites ) {
            return false;
        }
        return checkInstructorPrerequisites( courseCode, currentSession );
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

    public boolean enroll( String courseCode ) {
        // Gets the current year and semester
        int[] currentSession  = studentDAO.getCurrentAcademicSession();
        int   currentYear     = currentSession[0];
        int   currentSemester = currentSession[1];

        // Checks whether this particular course has been offered in the current session
        boolean doesCourseExist = studentDAO.checkCourseOffering( courseCode, currentYear, currentSemester );

        // Checks whether the student has got any grade in this course before
        String courseGrade = studentDAO.getCourseGrade( this.id, courseCode );
        // U is used to denote an error in the database
        if ( courseGrade.equals( "U" )) return false;

        // Checks whether the student has completed all the prerequisites i.e., instructor prerequisites and course catalog prerequisites
        boolean isStudentEligible = checkStudentEligibility( courseCode, currentSession );

        // Checks whether enrolling in this course would exceed the credit limit
        boolean creditLimitExceeded = checkCreditLimit( courseCode, currentSession );

        // If any of the above four conditions prevents the student from enrolling in the course, his request to enroll is rejected
        if ( !doesCourseExist || Utils.getGradeValue( courseGrade ) >= 4 || !isStudentEligible || creditLimitExceeded )
            return false;

        // Getting the department of the student and the department of the course
        String studentDepartment = studentDAO.getStudentDepartment( this.id );
        if ( studentDepartment == "" ) return false;

        // Getting the batch of the student
        int batch = studentDAO.getBatch( this.id );

        // Getting the course category. If the batch and department combination does not exist, the student is not eligible for the offering
        String courseCategory = getCourseCategory( courseCode, currentYear, currentSemester, studentDepartment, batch );
        if ( courseCategory.equals( "" ) ) return false;

        // Once all of the above conditions are fulfilled, the student is eligible to enroll in the course
        // The request to enroll is sent to the database
        return studentDAO.enroll( courseCode, this.id, currentYear, currentSemester );
    }

    private String getCourseCategory( String courseCode, int year, int semester, String studentDepartment, int batch ) {
        // Contains all the categories and the corresponding departments and batches for which the course is offered
        HashMap<String, String[]> offerings = studentDAO.getAllOfferings( courseCode, year, semester );

        // Go through all the categories
        for ( String category : offerings.keySet() ) {
            // Go through all the departments and batches for which it was offered
            String[] offeredDepartments = offerings.get( category );
            for ( String batchDepartment : offeredDepartments ) {
                String[] temp       = batchDepartment.split( "-" );
                int      batchFound = Integer.parseInt( temp[0] );
                String   department = temp[1];
                // If the department is the students department and the batch is the students batch, return the corresponding category
                if ( batchFound == batch && department.equals( studentDepartment ) ) return department;
            }
        }

        // If the course has not been offered for this batch, return an empty string to denote that student is not eligible
        return "";
    }

    public String drop( String courseCode ) {
        int[]   currentSession   = studentDAO.getCurrentAcademicSession();
        int     currentYear      = currentSession[0];
        int     currentSemester  = currentSession[1];
        boolean courseDropStatus = studentDAO.dropCourse( courseCode, id, currentYear, currentSemester );
        if ( courseDropStatus ) return "Enrollment Dropped Successfully";
        else return "Could not find enrollment in current semester";
    }

    public String[][][] getGradesForCourse() {
        ArrayList<String[][]> studentCourseRecords = new ArrayList<>();

        int[] currentSession  = studentDAO.getCurrentAcademicSession();
        int   currentYear     = currentSession[0];
        int   currentSemester = currentSession[1];
        int   studentBatch    = studentDAO.getBatch( this.id );
        for ( int year = studentBatch; year <= currentYear; year++ ) {
            for ( int semester = 1; semester <= 2; semester++ ) {
                studentCourseRecords.add( getGrades( year, semester ) );
                if ( year == currentYear && semester == currentSemester ) break;
            }
        }
        return studentCourseRecords.toArray( new String[studentCourseRecords.size()][][] );
    }

    public String[][] getGrades( int year, int semester ) {
        String[][] semesterGrades = studentDAO.getStudentGradesForSemester( this.id, year, semester );
        return semesterGrades;
    }

    public int getBatch() {
        return studentDAO.getBatch( this.id );
    }

    public double computeSGPA( String[][] records ) {
        double totalCredits  = 0;
        double creditsEarned = 0;

        for ( String[] record : records ) {
            double credits             = Double.parseDouble( record[3] );
            double numericalGradeValue = Utils.getGradeValue( record[2] );
            totalCredits += credits;
            creditsEarned += ( numericalGradeValue ) / 10 * credits;
        }
        if ( totalCredits == 0 ) return 0;
        return creditsEarned / totalCredits * 10;
    }

    public double getCGPA() {
        String[][] records       = studentDAO.getAllRecords( this.id );
        double     totalCredits  = 0;
        double     creditsEarned = 0;

        for ( String[] record : records ) {
            double credits             = Double.parseDouble( record[0] );
            int    numericalGradeValue = Utils.getGradeValue( record[1] );
            // E and F grades are not considered in the computation of CGPA
            if ( numericalGradeValue < 4 ) continue;
            totalCredits += credits;
            creditsEarned += ( numericalGradeValue ) / 10.0 * credits;
        }
        if ( totalCredits == 0 ) return 0;
        return creditsEarned / totalCredits * 10;
    }

    public String[][] getAvailableCourses() {
        int[]      currentSession  = studentDAO.getCurrentAcademicSession();
        int        currentYear     = currentSession[0];
        int        currentSemester = currentSession[1];
        String[][] coursesOffered  = studentDAO.getOfferedCourses( currentYear, currentSemester );
        return coursesOffered;
    }

    public HashMap<String, Double> getRemainingCreditRequirements() {
        // Get the batch of the student and retrieve the UG curriculum of the corresponding batch
        int                     batch        = studentDAO.getBatch( this.id );
        HashMap<String, Double> ugCurriculum = studentDAO.getUGCurriculum( batch );

        // Now we have to fetch the course categories and the corresponding credits done by the student
        HashMap<String, Double> categoryCreditsCompleted = studentDAO.getCreditsInAllCategories( this.id );
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
