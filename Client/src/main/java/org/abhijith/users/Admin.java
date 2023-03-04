package org.abhijith.users;

import org.abhijith.dal.PostgresAdminDAO;
import org.abhijith.daoInterfaces.AdminDAO;
import org.abhijith.utils.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

public class Admin extends User {
    private AdminDAO adminDAO;

    public Admin( String name ) {
        super( name );
        try {
            Properties  databaseConfig = new Properties();
            ClassLoader classLoader    = Admin.class.getClassLoader();
            InputStream inputStream    = classLoader.getResourceAsStream( "config.properties" );
            databaseConfig.load( inputStream );

            String connectionURL = databaseConfig.getProperty( "admin.connectionURL" );
            String username      = databaseConfig.getProperty( "admin.username" );
            String password      = databaseConfig.getProperty( "admin.password" );
            this.adminDAO = new PostgresAdminDAO(
                    connectionURL,
                    username,
                    password
            );
            super.setCommonDAO( adminDAO );
        } catch ( Exception error ) {
            System.out.println( "Could not connect to database" );
        }
    }

    public void setAdminDAO( AdminDAO adminDAO ) {
        this.adminDAO = adminDAO;
    }

    public String[][] getGradesOfOffering( String courseCode, int year, int semester, String departmentID ) {
        if ( courseCode == null || year < 0 || semester <= 0 || departmentID == null ) return new String[][]{};
        return adminDAO.getGradesOfCourse( courseCode, year, semester, departmentID );
    }

    public boolean insertStudent( String entryNumber, String name, String departmentID, int batch ) {
        if ( entryNumber == null || name == null || departmentID == null || batch < 0 ) return false;
        return adminDAO.insertStudent( entryNumber, name, departmentID, batch );
    }

    public boolean insertFaculty( String facultyID, String name, String departmentID ) {
        if ( facultyID == null || name == null || departmentID == null ) return false;
        return adminDAO.insertFaculty( facultyID, name, departmentID );
    }

    public boolean insertCourseIntoCatalog( String courseCode, String courseTitle, double[] creditStructure, String[] prerequisites ) {
        if ( courseCode == null || courseTitle == null || creditStructure == null || prerequisites == null )
            return false;
        boolean allPrerequisitesFound = adminDAO.checkAllPrerequisites( prerequisites );
        if ( !allPrerequisitesFound ) return false;
        return adminDAO.insertCourse( courseCode, courseTitle, creditStructure, prerequisites );
    }

    public boolean dropCourseFromCatalog( String courseCode ) {
        if ( courseCode == null ) return false;
        return adminDAO.dropCourseFromCatalog( courseCode );
    }

    public String[][][] getGradesOfStudent( String entryNumber ) {
        if ( entryNumber == null ) return new String[][][]{};

        ArrayList<String[][]> completeStudentRecords = new ArrayList<>();
        int[]                 currentAcademicSession = adminDAO.getCurrentAcademicSession();
        int                   studentBatch           = adminDAO.getBatch( entryNumber );
        if ( studentBatch == -1 ) return new String[][][]{};

        int currentYear     = currentAcademicSession[0];
        int currentSemester = currentAcademicSession[1];

        for ( int year = studentBatch; year <= currentYear; year++ ) {
            for ( int semester = 1; semester <= 2; semester++ ) {
                String[][] semesterRecords = adminDAO.getStudentGradesForSemester( entryNumber, year, semester );
                completeStudentRecords.add( semesterRecords );
                if ( year == currentYear && semester == currentSemester ) break;
            }
        }
        return completeStudentRecords.toArray( new String[completeStudentRecords.size()][][] );
    }

    public boolean createBatch( int batchYear, double[] creditRequirements ) {
        if ( batchYear < 0 || creditRequirements == null ) return false;
        boolean createBatchStatus = adminDAO.createBatch( batchYear );
        if ( !createBatchStatus ) return false;
        return adminDAO.createCurriculum( batchYear, creditRequirements );
    }

    public boolean insertCoreCourses( int batch, BufferedReader courseCSVFile ) {
        try {
            if ( batch < 0 || courseCSVFile == null ) return false;

            boolean isCoreListRemoved = adminDAO.resetCoreCoursesList( batch );
            if ( !isCoreListRemoved ) return false;
            boolean coursesInserted = true;

            String courseRecord;
            while ( ( courseRecord = courseCSVFile.readLine() ) != null ) {
                String[] lineContents    = courseRecord.split( "," );
                String   courseCode      = lineContents[0];
                String   courseCategory  = lineContents[1];
                String[] departmentCodes = Arrays.copyOfRange( lineContents, 2, lineContents.length );
                boolean  courseExists    = adminDAO.checkCourseCatalog( courseCode );
                if ( !courseExists ) return false;
                coursesInserted &= adminDAO.insertCoreCourse( courseCode, departmentCodes, batch, courseCategory );
            }
            return coursesInserted;
        } catch ( Exception error ) {
            return false;
        }
    }

    public boolean checkStudentPassStatus( String entryNumber ) {
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

        // Iterate through all the courses and verify that the student has passed all the courses
        for ( String courseCode : listOfCoreCourses ) {
            String grade = adminDAO.getCourseGrade( entryNumber, courseCode );
            if ( Utils.getGradeValue( grade ) < 4 ) {
                return false;
            }
        }

        // Get the credit requirements that are left for this particular student
        HashMap<String, Double> creditsLeft = getRemainingCreditRequirements( entryNumber );
        if ( creditsLeft == null ) return false;

        // Iterate through the UG curriculum requirements left. If all entries are 0, the student is eligible to pass
        for ( String category : creditsLeft.keySet() ) {
            if ( Double.compare( creditsLeft.get( category ), 0.0 ) != 0 ) return false;
        }

        // If the student is valid, and he has done all the credits, he is eligible to pass
        return true;
    }

    private HashMap<String, Double> getRemainingCreditRequirements( String entryNumber ) {
        // Get the batch of the student and retrieve the UG curriculum of the corresponding batch
        int                     batch        = adminDAO.getBatch( entryNumber );
        if ( batch == -1 ) return null;

        HashMap<String, Double> ugCurriculum = adminDAO.getUGCurriculum( batch );
        if ( ugCurriculum == null ) return null;

        // Now we have to fetch the course categories and the corresponding credits done by the student
        HashMap<String, Double> categoryCreditsCompleted = adminDAO.getCreditsInAllCategories( entryNumber );
        if ( categoryCreditsCompleted == null ) return null;

        HashMap<String, Double> categoryCreditsLeft = new HashMap<>();

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

    public boolean generateTranscripts( int batch, String department ) {
        try {
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
                fileWriter.printf( "CGPA: %.2f", getCGPA( records ) );

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
        fileWriter.println( "Year: " + year + "    Semester: " + semester + "     SGPA: " + computeSGPA( records ) );
        int   columns            = records[0].length;
        int[] maximumFieldLength = new int[]{ 15, 60, 10, 10 };

        StringBuilder formatString = new StringBuilder();
        for ( int i = 0; i < columns; i++ ) {
            formatString.append( "%-" ).append( maximumFieldLength[i] ).append( "s" );
        }
        formatString.append( "\n" );

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
        for ( String[][] record : records ) {
            for ( String[] subjectRecord : record ) {
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
