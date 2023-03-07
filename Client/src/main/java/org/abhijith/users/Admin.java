package org.abhijith.users;

import org.abhijith.dal.PostgresAdminDAO;
import org.abhijith.daoInterfaces.AdminDAO;
import org.abhijith.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Admin extends User {
    private AdminDAO adminDAO;

    public Admin( String name ) {
        super( name );
        this.adminDAO = new PostgresAdminDAO();
        super.setCommonDAO( adminDAO );
    }

    public void setAdminDAO( AdminDAO adminDAO ) {
        this.adminDAO = adminDAO;
    }

    public String[][] getGradesOfOffering( String courseCode, int year, int semester, String departmentID ) {
        // Verify the arguments
        if ( courseCode == null || year < 0 || semester < 0 || departmentID == null ) return new String[][]{};
        // Returning the value returned by the DAO
        return adminDAO.getGradesOfCourse( courseCode, year, semester, departmentID );
    }

    public boolean insertStudent( String entryNumber, String name, String departmentID, int batch ) {
        // Verify the arguments
        if ( entryNumber == null || name == null || departmentID == null || batch < 0 ) return false;
        // Returning the value returned by the DAO
        return adminDAO.insertStudent( entryNumber, name, departmentID, batch );
    }

    public boolean insertFaculty( String facultyID, String name, String departmentID ) {
        // Verify the arguments
        if ( facultyID == null || name == null || departmentID == null ) return false;
        // Returning the value returned by the DAO
        return adminDAO.insertFaculty( facultyID, name, departmentID );
    }

    public boolean insertCourseIntoCatalog( String courseCode, String courseTitle, double[] creditStructure, String[] prerequisites ) {
        // Verify the arguments
        if ( courseCode == null || courseTitle == null || creditStructure == null || prerequisites == null )
            return false;

        // Verify that all of the course codes mentioned as prerequisites are valid
        boolean allPrerequisitesFound = adminDAO.checkAllPrerequisites( prerequisites );
        if ( !allPrerequisitesFound ) return false;

        // Returning the value returned by the DAO
        return adminDAO.insertCourse( courseCode, courseTitle, creditStructure, prerequisites );
    }

    public boolean dropCourseFromCatalog( String courseCode ) {
        // Verify the arguments
        if ( courseCode == null ) return false;
        // Returning the value retuned by the DAO
        return adminDAO.dropCourseFromCatalog( courseCode );
    }

    public String[][][] getGradesOfStudent( String entryNumber ) {
        // Verify the arguments
        if ( entryNumber == null ) return new String[][][]{};

        // Get the current session and the batch of the student
        ArrayList<String[][]> completeStudentRecords = new ArrayList<>();
        int[]                 currentAcademicSession = adminDAO.getCurrentAcademicSession();
        int                   studentBatch           = adminDAO.getBatch( entryNumber );
        if ( studentBatch == -1 ) return new String[][][]{};

        int currentYear     = currentAcademicSession[0];
        int currentSemester = currentAcademicSession[1];

        // Iterate through all the semesters from the batch of the student till the current semester
        for ( int year = studentBatch; year <= currentYear; year++ ) {
            for ( int semester = 1; semester <= 2; semester++ ) {
                // Fetch the records of every session
                String[][] semesterRecords = adminDAO.getStudentGradesForSemester( entryNumber, year, semester );
                completeStudentRecords.add( semesterRecords );
                // If you have reached the current semester, then stop
                if ( year == currentYear && semester == currentSemester ) break;
            }
        }
        // Return the records that were fetched
        return completeStudentRecords.toArray( new String[completeStudentRecords.size()][][] );
    }

    public boolean createBatch( int batchYear, double[] creditRequirements ) {
        // Verify the arguments
        if ( batchYear < 0 || creditRequirements == null ) return false;

        // First create the corresponding batch
        boolean createBatchStatus = adminDAO.createBatch( batchYear );
        if ( !createBatchStatus ) return false;

        // Once the batch is created, insert the curriculum of the batch into the database
        return adminDAO.createCurriculum( batchYear, creditRequirements );
    }

    public boolean insertCoreCourses( int batch, BufferedReader courseCSVFile ) {
        try {
            // Verify the arguments
            if ( batch < 0 || courseCSVFile == null ) return false;

            // Remove all the core courses of the batch if they already exist
            boolean isCoreListRemoved = adminDAO.resetCoreCoursesList( batch );
            if ( !isCoreListRemoved ) return false;
            boolean coursesInserted = true;

            // Iterate through the entries in the file
            String courseRecord;
            while ( ( courseRecord = courseCSVFile.readLine() ) != null ) {
                // Extract the contents of the file
                String[] lineContents    = courseRecord.split( "," );
                String   courseCode      = lineContents[0];
                String   courseCategory  = lineContents[1];
                String[] departmentCodes = Arrays.copyOfRange( lineContents, 2, lineContents.length );

                // Verify that the course exists in the course catalog
                boolean  courseExists    = adminDAO.checkCourseCatalog( courseCode );
                if ( !courseExists ) return false;

                // If the course exists, then insert it as a core course for all the departments mentioned
                coursesInserted &= adminDAO.insertCoreCourse( courseCode, departmentCodes, batch, courseCategory );
            }
            return coursesInserted;
        } catch ( Exception error ) {
            return false;
        }
    }

    public boolean checkStudentPassStatus( String entryNumber ) {
        // Verify the arguments
        if ( entryNumber == null ) return false;

        // Check if the entry number that has been entered exists in the database.
        // This is done because the hashmap operation is expensive
        boolean isEntryNumberValid = adminDAO.findEntryNumber( entryNumber );
        if ( !isEntryNumberValid ) return false;

        // We check if the student has credited all the core courses he should have done
        int batch = adminDAO.getBatch( entryNumber );
        if ( batch == -1 ) return false;

        String studentDepartment = adminDAO.getStudentDepartment( entryNumber );
        if ( studentDepartment.equals( "" ) ) return false;

        String[] listOfCoreCourses = adminDAO.getCoreCourses( batch, studentDepartment );
        if ( listOfCoreCourses == null ) return false;

        // Iterate through all the core courses and verify that the student has passed all the courses
        for ( String courseCode : listOfCoreCourses ) {
            String grade = adminDAO.getCourseGrade( entryNumber, courseCode );
            if ( Utils.getGradeValue( grade ) < 4 ) {
                return false;
            }
        }

        // Get the credit requirements that are left for this particular student
        HashMap<String, Double> creditsLeft = getRemainingCreditRequirements( entryNumber, batch );
        if ( creditsLeft == null ) return false;

        // Iterate through the UG curriculum requirements left. If all entries are 0, the student is eligible to pass
        for ( String category : creditsLeft.keySet() ) {
            if ( Double.compare( creditsLeft.get( category ), 0.0 ) != 0 ) return false;
        }

        // If the student is valid, and he has done all the credits, he is eligible to pass
        return true;
    }

    private HashMap<String, Double> getRemainingCreditRequirements( String entryNumber, int batch ) {
        HashMap<String, Double> ugCurriculum = adminDAO.getUGCurriculum( batch );
        if ( ugCurriculum == null ) return null;

        // Now we have to fetch the course categories and the corresponding credits done by the student
        HashMap<String, Double> categoryCreditsCompleted = adminDAO.getCreditsInAllCategories( entryNumber );
        if ( categoryCreditsCompleted == null ) return null;

        HashMap<String, Double> categoryCreditsLeft = new HashMap<>();

        // Additional credits in any category will count towards the open electives section
        double additionalCredits = 0;

        // Iterate through all the categories in the UG Curriculum
        for ( String category : ugCurriculum.keySet() ) {
            if ( categoryCreditsCompleted.containsKey( category ) ) {
                double creditsNeeded    = ugCurriculum.get( category );
                double creditsCompleted = categoryCreditsCompleted.get( category );

                // If student has done fewer credits than necessary, then store this value. It may be open electives that can be later compensated
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
                // If the student has done no credits in this particular category
                categoryCreditsLeft.put( category, ugCurriculum.get( category ) );
            }
        }

        // Now we have to get open elective requirements to 0 if possible using the additional credits we counted
        if ( categoryCreditsLeft.containsKey( "OE" ) ) {
            double creditsLeft = categoryCreditsLeft.get( "OE" );
            categoryCreditsLeft.put( "OE", Math.max( creditsLeft - additionalCredits, 0.0 ) );
        }

        return categoryCreditsLeft;
    }

    public boolean generateTranscripts( int batch, String department ) {
        try {
            // Verify the arguments
            if ( batch < 0 || department == null ) return false;

            // If the batch does not exist no students will be returned by the function and so no records will be generated
            String[] listOfStudents = adminDAO.getListOfStudentsInBatch( batch, department );
            if ( listOfStudents == null ) return false;

            // Create the directory where all the records will be stored
            String directoryName = "./" + batch + "-" + department;
            new File( directoryName ).mkdirs();

            // Iterate through the list of students and generate the transcript for each student
            for ( String entryNumber : listOfStudents ) {
                String[][][] records = getGradesOfStudent( entryNumber );

                // Create a new file to store all the records of the student
                String fileName       = entryNumber + ".txt";
                File   transcriptFile = new File( directoryName + "/" + fileName );

                // Create a PrintWriter to write into the newly created file
                PrintWriter fileWriter = new PrintWriter( transcriptFile );

                // Write the heading into the file
                writeHeading( fileWriter, entryNumber );

                // Write the records into the file
                int year     = batch;
                int semester = 1;

                // Set the headings of each record in the file
                String[] headings = new String[]{ "Course Code", "Course Title", "Grade", "Credits Earned" };

                // Print the records of every semester into the file
                for ( String[][] record : records ) {
                    writeRecordsIntoFile( headings, record, year, semester, fileWriter );
                    if ( semester == 2 ) year++;
                    semester = ( semester == 1 ) ? 2 : 1;
                }

                // The CGPA of the student will be mentioned at the end
                fileWriter.printf( "CGPA: %.2f\n", getCGPA( records ) );

                // If the student is eligible for graduation, then there is no need to mention anything in the transcript
                if ( !checkStudentPassStatus( entryNumber ) ) fileWriter.printf( "INELIGIBLE FOR GRADUATION\n" );

                // Close the fileWriter object after use to prevent locking issues
                fileWriter.close();
            }

            // Return true once all the students have been processed
            return true;
        } catch ( Exception error ) {
            return false;
        }
    }

    public boolean startNewSession() {
        // Get the current academic session
        int[] currentSession  = adminDAO.getCurrentAcademicSession();
        int   currentYear     = currentSession[0];
        int   currentSemester = currentSession[1];

        // Check if this particular session is in the completed stage
        boolean isPreviousSessionOver = adminDAO.checkIfSessionCompleted( currentYear, currentSemester );
        if ( !isPreviousSessionOver ) return false;

        // If the previous session is over, we just create the new session in the database
        int newYear     = ( currentSemester == 2 ) ? currentYear + 1 : currentYear;
        int newSemester = ( currentSemester == 1 ) ? 2 : 1;
        return adminDAO.createNewSession( newYear, newSemester );
    }

    public boolean setCurrentSessionStatus( String event ) {
        // Get the current academic session
        int[] currentSession  = adminDAO.getCurrentAcademicSession();
        int   currentYear     = currentSession[0];
        int   currentSemester = currentSession[1];

        // If the event is "COMPLETED" we have to verify that all students have been assigned grades in the previous session
        if ( event.equals( "COMPLETED" ) ) {
            boolean allGradesEntered = adminDAO.verifyNoMissingGrades( currentYear, currentSemester );
            if ( !allGradesEntered ) return false;
        }

        // Set the status in the database
        return adminDAO.setSessionEvent( event, currentYear, currentSemester );
    }

    private double computeSGPA( String[][] records ) {
        double totalCredits  = 0;
        double creditsEarned = 0;

        // Iterate through all the records of the student and calculate the SGPA.
        for ( String[] record : records ) {
            double credits             = Double.parseDouble( record[3] );
            double numericalGradeValue = Utils.getGradeValue( record[2] );

            // The SGPA takes into account courses even if you have not passed them
            totalCredits += credits;
            creditsEarned += ( numericalGradeValue ) / 10 * credits;
        }

        // If the student has not done any courses, simply return 0
        if ( totalCredits == 0 ) return 0;
        return creditsEarned / totalCredits * 10;
    }

    private void writeRecordsIntoFile( String[] headings, String[][] records, int year, int semester, PrintWriter fileWriter ) {
        // Find out the length of the records
        int rows = records.length;
        if ( rows == 0 ) return;

        // Print the heading of a particular semester into the file
        fileWriter.println( "Year: " + year + "    Semester: " + semester + "     SGPA: " + computeSGPA( records ) );
        int   columns            = records[0].length;

        // Formatting the lengths for proper output
        int[] maximumFieldLength = new int[]{ 15, 60, 10, 10 };

        // Building the format string to print the output in the proper format
        StringBuilder formatString = new StringBuilder();
        for ( int i = 0; i < columns; i++ ) {
            formatString.append( "%-" ).append( maximumFieldLength[i] ).append( "s" );
        }
        formatString.append( "\n" );

        // Once the format string is created, print the objects into the file
        fileWriter.format( formatString.toString(), (Object[]) headings );
        for ( final Object[] rowContent : records ) {
            fileWriter.format( formatString.toString(), rowContent );
        }
        fileWriter.println();
    }

    private void writeHeading( PrintWriter fileWriter, String entryNumber ) {
        fileWriter.println( "INDIAN INSTITUTE OF TECHNOLOGY, ROPAR" );
        fileWriter.println( "STUDENT GRADE TRANSCRIPT\n" );
        fileWriter.println( "Entry Number: " + entryNumber );
        fileWriter.println();
    }

    private double getCGPA( String[][][] records ) {
        double totalCredits  = 0;
        double creditsEarned = 0;
        // Iterate through all the records
        for ( String[][] record : records ) {
            for ( String[] subjectRecord : record ) {
                // Get the numerical value of the credits and the grade earned
                double credits             = Double.parseDouble( subjectRecord[3] );
                int    numericalGradeValue = Utils.getGradeValue( subjectRecord[2] );

                // E and F grades are not considered in the computation of CGPA
                if ( numericalGradeValue < 4 ) continue;
                totalCredits += credits;
                creditsEarned += ( numericalGradeValue ) / 10.0 * credits;
            }
        }
        if ( totalCredits == 0 ) return 0;
        return creditsEarned / totalCredits * 10;
    }
}
