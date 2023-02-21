package org.abhijith.users;

import org.abhijith.dal.PostgresFacultyDAO;
import org.abhijith.daoInterfaces.FacultyDAO;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

public class Faculty extends User {
    private FacultyDAO facultyDAO;

    public Faculty( String id ) {
        super( id );
        try {
            Properties databaseConfig = new Properties();
            ClassLoader classLoader = Faculty.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream( "config.properties" );
            databaseConfig.load( inputStream );

            String connectionURL = databaseConfig.getProperty( "faculty.connectionURL" );
            String username      = databaseConfig.getProperty( "faculty.username" );
            String password      = databaseConfig.getProperty( "faculty.password" );

            this.facultyDAO = new PostgresFacultyDAO(
                    connectionURL,
                    username,
                    password
            );
            super.setCommonDAO( facultyDAO );
        } catch ( Exception error ) {
            System.out.println( "Could not connect to database" );
        }
    }

    public void setFacultyDAO( FacultyDAO facultyDAO ) {
        this.facultyDAO = facultyDAO;
    }

    public boolean offerCourse( String courseCode, String offeringDepartment ) {
        // Checking the input
        if ( courseCode == null || offeringDepartment == null ) return false;

        // Checking if the course is present in the course catalog
        boolean isCoursePresent = facultyDAO.checkCourseCatalog( courseCode );
        if ( !isCoursePresent ) return false;

        // Getting the current academic session as the instructor will only be allowed to insert records in the current semester
        int[] currentSession  = facultyDAO.getCurrentAcademicSession();
        int   currentYear     = currentSession[0];
        int   currentSemester = currentSession[1];

        // Check if it is currently the offering event
        if ( !facultyDAO.isCurrentEventOffering( currentYear, currentSemester ) ) return false;

        // Check if the course is offered by your department
        if ( facultyDAO.isCourseAlreadyOffered( courseCode, currentYear, currentSemester, offeringDepartment ) ) return false;

        // Inserting the course into the database, being offered by the instructors department
        return facultyDAO.insertCourseOffering( courseCode, currentYear, currentSemester, offeringDepartment, this.id );
    }

    public boolean setCGAndPrerequisites( String courseCode, String offeringDepartment, double minimumCGPA, String[][] prerequisites ) {
        try {
            // Checking the input that is given
            if ( courseCode == null || minimumCGPA < 0 || minimumCGPA > 10 || prerequisites == null || offeringDepartment == null ) return false;

            // Get the current academic session
            int[] currentAcademicSession = facultyDAO.getCurrentAcademicSession();
            int   currentYear            = currentAcademicSession[0];
            int   currentSemester        = currentAcademicSession[1];

            // Check if it is currently the offering event
            if ( !facultyDAO.isCurrentEventOffering( currentAcademicSession[0], currentAcademicSession[1] ) ) {
                return false;
            }

            // Check if this particular faculty is offering such a course
            if ( !facultyDAO.checkIfOfferedBySelf( this.id, courseCode, currentYear, currentSemester, offeringDepartment ) ) return false;

            // Set the minimum CGPA requirement, if no such entry is found, it simply returns false
            boolean isMinimumCGPASet = facultyDAO.setCGCriteria( this.id, courseCode, minimumCGPA, currentAcademicSession, offeringDepartment );
            if ( !isMinimumCGPASet ) return false;

            // Check if all the courses entered as prerequisites are in the course catalog
            boolean isPrerequisitesValid = true;
            for ( String[] courseBatch : prerequisites ) {
                if ( courseBatch == null ) return false;
                for ( int i = 0; i < courseBatch.length; i += 2 ) {
                    isPrerequisitesValid &= facultyDAO.checkCourseCatalog( courseBatch[i] );
                }
            }
            if ( !isPrerequisitesValid ) return false;
            return facultyDAO.setInstructorPrerequisites( offeringDepartment, courseCode, prerequisites, currentAcademicSession );
        } catch ( Exception error ) {
            return false;
        }
    }

    public boolean dropCourseOffering( String courseCode, String offeringDepartment ) {
        if ( courseCode == null || offeringDepartment == null ) return false;

        int[] currentAcademicSession = facultyDAO.getCurrentAcademicSession();
        int   currentYear            = currentAcademicSession[0];
        int   currentSemester        = currentAcademicSession[1];

        // Check if it is currently the offering event
        if ( !facultyDAO.isCurrentEventOffering( currentYear, currentSemester ) ) return false;

        // Check if the course has been offered by this faculty
        if ( !facultyDAO.checkIfOfferedBySelf( this.id, courseCode, currentYear, currentSemester, offeringDepartment ) ) return false;

        return facultyDAO.dropCourseOffering( this.id, courseCode, currentYear, currentSemester, offeringDepartment );
    }

    private boolean verifyCore( String courseCode, String departmentID, int[] years ) {
        boolean isCore;
        // Iterate through all the years and verify that it is a core course for all of them
        for ( int year : years ) {
            // If the course is not core for a particular department in a particular year
            isCore = facultyDAO.verifyCore( courseCode, departmentID, year );
            // Then this request to set type is not valid
            if ( !isCore ) return false;
        }
        return true;
    }

    public boolean setCourseCategory( String courseCode, String offeringDepartment, String courseCategory, String departmentID, int[] years ) {
        if ( courseCode == null || courseCategory == null || departmentID == null || years == null || offeringDepartment == null ) return false;

        // Get the current year and semester
        int[] currentSession  = facultyDAO.getCurrentAcademicSession();
        int   currentYear     = currentSession[0];
        int   currentSemester = currentSession[1];

        // Check if it is currently the offering event
        if ( !facultyDAO.isCurrentEventOffering( currentYear, currentSemester ) ) return false;

        // Check if this course code has been offered by this instructor in the current session
        boolean isOwnCourse = facultyDAO.checkIfOfferedBySelf( this.id, courseCode, currentYear, currentSemester, offeringDepartment );

        // If not offered by this instructor, the course category cannot be set
        if ( !isOwnCourse ) return false;

        // If the course has been offered as a core course by the instructor, you have to verify that it is actually a core course for the particular year
        if ( courseCategory.equals( "PC" ) || courseCategory.equals( "SC" ) || courseCategory.equals( "GR" ) || courseCategory.equals( "HC" ) || courseCategory.equals( "CP" ) || courseCategory.equals( "II" ) || courseCategory.equals( "NN" ) ) {
            boolean isValidCore = verifyCore( courseCode, departmentID, years );
            if ( !isValidCore ) return false;
        }

        // Now that the course has been verified, you can update the course category
        return facultyDAO.setCourseCategory( courseCode, currentYear, currentSemester, courseCategory, departmentID, years, offeringDepartment );
    }

    public String[][][] getGradesOfStudent( String entryNumber ) {
        if ( entryNumber == null ) return new String[][][]{};
        // Arraylist to conveniently store records before returning to the UI layer
        ArrayList<String[][]> completeStudentRecords = new ArrayList<>();

        // Getting the current academic session and the batch of the student
        int[]                 currentAcademicSession = facultyDAO.getCurrentAcademicSession();
        int                   studentBatch           = facultyDAO.getBatch( entryNumber );
        int                   currentYear            = currentAcademicSession[0];
        int                   currentSemester        = currentAcademicSession[1];

        // Go through all the semesters from the students joining batch till the current academic session
        for ( int year = studentBatch; year <= currentYear; year++ ) {
            for ( int semester = 1; semester <= 2; semester++ ) {
                // Fetches the records for the particular student for this particular year and semester
                String[][] semesterRecords = facultyDAO.getStudentGradesForSemester( entryNumber, year, semester );
                completeStudentRecords.add( semesterRecords );

                // If we have reached the current semester, then break
                if ( year == currentYear && semester == currentSemester ) break;
            }
        }
        return completeStudentRecords.toArray( new String[completeStudentRecords.size()][][] );
    }

    public String[][] getGradesOfOffering( String courseCode, int year, int semester, String offeringDepartment ) {
        if ( courseCode == null || offeringDepartment == null || year < 0 || semester <= 0 ) return new String[][]{};
        return facultyDAO.getGradesOfCourse( courseCode, year, semester, offeringDepartment );
    }

    public boolean generateGradeCSV( String courseCode, int year, int semester, String offeringDepartment ) {
        try {
            if ( courseCode == null || offeringDepartment == null || year < 0 || semester <= 0 ) return false;

            // Check if the course is offered by the currently logged in instructor
            boolean isOwnCourse = facultyDAO.checkIfOfferedBySelf( this.id, courseCode, year, semester, offeringDepartment );
            if ( !isOwnCourse ) {
                return false;
            }

            // Get the names of all students enrolled in this particular course offering
            String[][] records = facultyDAO.getCourseEnrollmentsList( courseCode, year, semester, offeringDepartment );

            // Create a new file to insert the name and the entry number ( only the entry number is relevant for use in the program )
            String fileName        = courseCode + "_" + year + "_" + semester + "_" + offeringDepartment + ".csv";
            File   gradeUploadFile = new File( fileName );

            // Create a printWriter to write into the newly created file
            PrintWriter fileWriter = new PrintWriter( gradeUploadFile );

            // Iterate through all the name and entry numbers and insert them into the file separated by commas
            for ( String[] record : records ) {
                String lineContent = String.join( ",", record ) + ",";
                fileWriter.println( lineContent );
            }

            // Close the file that was written into
            fileWriter.close();
            return true;
        } catch ( Exception error ) {
            return false;
        }
    }

    public boolean uploadGrades( String courseCode, int year, int semester, BufferedReader gradeCSVFile, String offeringDepartment ) {
        try {
            // First we need to verify that the faculty is trying to upload grades for a course offered in the current semester only
            int[] currentSession = facultyDAO.getCurrentAcademicSession();
            if ( year != currentSession[0] || semester != currentSession[1] ) return false;

            if ( !facultyDAO.isCurrentEventGradeSubmission( year, semester ) ) return false;

            // Check if the course has been offered by this particular faculty in the current semester
            boolean isOwnCourse = facultyDAO.checkIfOfferedBySelf( this.id, courseCode, year, semester, offeringDepartment );
            if ( !isOwnCourse ) return false;

            ArrayList<String> arraylistOfEntryNumbers = new ArrayList<>();
            ArrayList<String> arraylistOfGrades       = new ArrayList<>();
            String            courseRecord;

            while ( ( courseRecord = gradeCSVFile.readLine() ) != null ) {
                String[] lineContents = courseRecord.split( "," );
                arraylistOfEntryNumbers.add( lineContents[1] );
                arraylistOfGrades.add( lineContents[2] );
            }
            String[] listOfEntryNumbers = arraylistOfEntryNumbers.toArray( new String[arraylistOfEntryNumbers.size()] );

            // Sorted list of the entry numbers in the CSV file
            Collections.sort( arraylistOfEntryNumbers );

            // Sorted list of the entry numbers in the database
            String[] listOfStudentsInDatabase = facultyDAO.getListOfStudents( courseCode, year, semester, offeringDepartment );
            Arrays.sort( listOfStudentsInDatabase );

            // These two lists must contain the same entry numbers because that was the file generated by the program
            if ( listOfEntryNumbers.length != listOfStudentsInDatabase.length ) return false;
            for ( int i = 0; i < arraylistOfEntryNumbers.size(); i++ ) {
                if ( !arraylistOfEntryNumbers.get( i ).equals( listOfStudentsInDatabase[i] ) ) return false;
            }

            // The course has been verified and the students have been verified. Now we can insert the records into the database
            return facultyDAO.uploadCourseGrades( courseCode, year, semester, offeringDepartment, listOfEntryNumbers, arraylistOfGrades.toArray( new String[arraylistOfGrades.size()] ) );
        } catch ( Exception error ) {
            return false;
        }
    }
}
