package org.example.users;

import org.example.dal.PostgresFacultyDAO;
import org.example.daoInterfaces.FacultyDAO;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Faculty extends User {
    FacultyDAO facultyDAO;

    public Faculty( String id ) {
        super( id );
        this.facultyDAO = new PostgresFacultyDAO(
                "jdbc:postgresql://localhost:5432/mini_project",
                "postgres",
                "admin"
        );
        super.setCommonDAO( facultyDAO );
    }

    public boolean offerCourse( String courseCode ) {
        // Checking the department of the professor
        String departmentID = facultyDAO.getDepartment( this.id );
        if ( departmentID == "" ) return false;

        // Checking if the course is present in the course catalog and can be offered by this particular department
        boolean isCoursePresent = facultyDAO.checkCourseCatalog( courseCode );
        if ( isCoursePresent == false ) return false;

        // Getting the current academic session as the instructor will only be allowed to insert records in the current semester
        int[] currentSession  = facultyDAO.getCurrentAcademicSession();
        int   currentYear     = currentSession[0];
        int   currentSemester = currentSession[1];

        // Check if it is currently the offering event
        if ( !facultyDAO.isCurrentEventOffering( currentYear, currentSemester) ) return false;

        // Inserting the course into the database, being offered by the instructors department
        return facultyDAO.insertCourseOffering( courseCode, currentYear, currentSemester, departmentID, this.id );
    }

    public boolean setCGAndPrerequisites( String courseCode, double minimumCGPA, String[][] prerequisites ) {
        try {
            // Get the department ID of the faculty
            String departmentID = facultyDAO.getDepartment( this.id );

            // Get the current academic session
            int[] currentAcademicSession = facultyDAO.getCurrentAcademicSession();

            // Check if it is currently the offering event
            if ( !facultyDAO.isCurrentEventOffering( currentAcademicSession[0], currentAcademicSession[1]) ) return false;

            // Set the minimum CGPA requirement, if no such entry is found, it simply returns false
            boolean isMinimumCGPASet = facultyDAO.setCGCriteria( this.id, courseCode, minimumCGPA, currentAcademicSession, departmentID );
            if ( isMinimumCGPASet == false ) return false;
            boolean isPrerequisitesValid = true;
            for ( String[] courseBatch : prerequisites ) {
                for ( int i = 0; i < courseBatch.length; i += 2 ) {
                    isPrerequisitesValid &= facultyDAO.checkCourseCatalog( courseBatch[i] );
                }
            }
            if ( isPrerequisitesValid == false ) return false;
            boolean arePrerequisitesSet = facultyDAO.setInstructorPrerequisites( this.id, courseCode, prerequisites, currentAcademicSession );
            return arePrerequisitesSet;
        } catch ( Exception error ) {
            return false;
        }
    }

    public boolean dropCourseOffering( String courseCode ) {
        int[] currentAcademicSession = facultyDAO.getCurrentAcademicSession();
        int   currentYear            = currentAcademicSession[0];
        int   currentSemester        = currentAcademicSession[1];

        // Check if it is currently the offering event
        if ( !facultyDAO.isCurrentEventOffering( currentYear, currentSemester) ) return false;

        return facultyDAO.dropCourseOffering( this.id, courseCode, currentYear, currentSemester );
    }

    private boolean verifyCore( String courseCode, String departmentID, int[] years ) {
        boolean isCore = true;
        // Iterate through all the years and verify that it is a core course for all of them
        for ( int year : years ) {
            // If the course is not core for a particular department in a particular year
            isCore &= facultyDAO.verifyCore( courseCode, departmentID, year );
            // Then this request to set type is not valid
            if ( isCore == false ) return false;
        }
        return true;
    }

    public boolean setCourseCategory( String courseCode, String courseCategory, String departmentID, int[] years ) {
        // Get the current year and semester
        int[] currentSession  = facultyDAO.getCurrentAcademicSession();
        int   currentYear     = currentSession[0];
        int   currentSemester = currentSession[1];

        // Check if it is currently the offering event
        if ( !facultyDAO.isCurrentEventOffering( currentYear, currentSemester) ) return false;

        // Check if this course code has been offered by this instructor in the current session
        boolean isOwnCourse = facultyDAO.checkIfOfferedBySelf( this.id, courseCode, currentYear, currentSemester );

        // If not offered by this instructor, the course category cannot be set
        if ( isOwnCourse == false ) return false;

        // Get the faculty's department
        String facultyDepartment = facultyDAO.getDepartment( this.id );

        // If the course has been offered as a core course by the instructor, you have to verify that it is actually a core course for the particular year
        if ( courseCategory == "PC" || courseCategory == "SC" || courseCategory == "GR" || courseCategory == "HC" || courseCategory == "CP" || courseCategory == "II" || courseCategory == "NN" ) {
            boolean isValidCore = verifyCore( courseCode, departmentID, years );
            if ( isValidCore == false ) return false;
        }

        // Now that the course has been verified, you can update the course category
        return facultyDAO.setCourseCategory( courseCode, currentYear, currentSemester, courseCategory, departmentID, years, facultyDepartment );
    }

    public String[][][] getGradesOfStudent( String entryNumber ) {
        ArrayList<String[][]> completeStudentRecords = new ArrayList<>();
        int[]                 currentAcademicSession = facultyDAO.getCurrentAcademicSession();
        int                   studentBatch           = facultyDAO.getBatch( entryNumber );
        int                   currentYear            = currentAcademicSession[0];
        int                   currentSemester        = currentAcademicSession[1];

        for ( int year = studentBatch; year <= currentYear; year++ ) {
            for ( int semester = 1; semester <= 2; semester++ ) {
                String[][] semesterRecords = facultyDAO.getStudentGradesForSemester( entryNumber, year, semester );
                completeStudentRecords.add( semesterRecords );
                if ( year == currentYear && semester == currentSemester ) break;
            }
        }
        return completeStudentRecords.toArray( new String[completeStudentRecords.size()][][] );
    }

    public String[][] getGradesOfOffering( String courseCode, int year, int semester ) {
        return facultyDAO.getGradesOfCourse( courseCode, year, semester );
    }

    public void generateGradeCSV( String courseCode, int year, int semester ) {
        try {
            // Check if the course is offered by the currently logged in instructor
            boolean isOwnCourse = facultyDAO.checkIfOfferedBySelf( this.id, courseCode, year, semester );
            if ( isOwnCourse == false ) {
                System.out.println( "You can only generate files for your own courses" );
                return;
            }

            // Get the names of all students enrolled in this particular course offering
            String[][] records = facultyDAO.getCourseEnrollmentsList( courseCode, year, semester );

            // Create a new file to insert the name and the entry number ( only the entry number is relevant for use in the program )
            String fileName        = courseCode + "_" + year + "_" + semester + ".csv";
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
            System.out.println( "File generated successfully" );
        } catch ( Exception error ) {
            System.out.println( error.getMessage() );
        }
    }

    public boolean uploadGrades( String courseCode, int year, int semester, BufferedReader gradeCSVFile ) {
        try {
            // First we need to verify that the faculty is trying to upload grades for a course offered in the current semester only
            int[] currentSession = facultyDAO.getCurrentAcademicSession();
            if ( year != currentSession[0] || semester != currentSession[1] ) return false;

            if ( !facultyDAO.isCurrentEventGradeSubmission( year, semester ) ) return false;

            // Check if the course has been offered by this particular faculty in the current semester
            boolean isOwnCourse = facultyDAO.checkIfOfferedBySelf( this.id, courseCode, year, semester );
            if ( isOwnCourse == false ) return false;

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
            String[] listOfStudentsInDatabase = facultyDAO.getListOfStudents( courseCode, year, semester );
            Arrays.sort( listOfStudentsInDatabase );

            // These two lists must contain the same entry numbers because that was the file generated by the program
            if ( listOfEntryNumbers.length != listOfStudentsInDatabase.length ) return false;
            for ( int i = 0; i < arraylistOfEntryNumbers.size(); i++ ) {
                if ( !arraylistOfEntryNumbers.get( i ).equals( listOfStudentsInDatabase[i] ) ) return false;
            }

            // The course has been verified and the students have been verified. Now we can insert the records into the database
            return facultyDAO.uploadCourseGrades( courseCode, year, semester, listOfEntryNumbers, arraylistOfGrades.toArray( new String[arraylistOfGrades.size()] ) );
        } catch ( Exception error ) {
            return false;
        }
    }
}
