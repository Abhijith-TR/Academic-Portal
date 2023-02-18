package org.example.users;

import org.example.daoInterfaces.AdminDAO;
import org.example.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Admin extends User {
    AdminDAO adminDAO;

    public Admin( String name, AdminDAO adminDAO ) {
        super( name, adminDAO );
        this.adminDAO = adminDAO;
    }

    public String[][] getGradesOfOffering( String courseCode, int year, int semester ) {
        return adminDAO.getGradesOfCourse( courseCode, year, semester );
    }

    public boolean insertStudent( String entryNumber, String name, String departmentID, int batch ) {
        return adminDAO.insertStudent( entryNumber, name, departmentID, batch );
    }

    public boolean insertFaculty( String facultyID, String name, String departmentID ) {
        return adminDAO.insertFaculty( facultyID, name, departmentID );
    }

    public boolean insertCourseIntoCatalog( String courseCode, String courseTitle, double[] creditStructure, String[] prerequisites, String departmentID ) {
        boolean allPrerequisitesFound = adminDAO.checkAllPrerequisites( prerequisites );
        if ( !allPrerequisitesFound ) return false;
        return adminDAO.insertCourse( courseCode, courseTitle, creditStructure, prerequisites, departmentID );
    }

    public boolean dropCourseFromCatalog( String courseCode ) {
        return adminDAO.dropCourseFromCatalog( courseCode );
    }

    public String[][][] getGradesOfStudent( String entryNumber ) {
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
        boolean createBatchStatus = adminDAO.createBatch( batchYear );
        if ( !createBatchStatus ) return false;
        return adminDAO.createCurriculum( batchYear, creditRequirements );
    }

    public boolean insertCoreCourses( int batch, BufferedReader courseCSVFile ) {
        try {
            boolean isCoreListRemoved = adminDAO.resetCoreCoursesList( batch );
            if ( !isCoreListRemoved ) return false;
            String courseRecord;
            while ( ( courseRecord = courseCSVFile.readLine() ) != null ) {
                String[] lineContents    = courseRecord.split( "," );
                String   courseCode      = lineContents[0];
                String   courseCategory  = lineContents[1];
                String[] departmentCodes = Arrays.copyOfRange( lineContents, 2, lineContents.length );
                boolean  courseExists    = adminDAO.checkCourseCatalog( courseCode );
                if ( courseExists == false ) return false;
                adminDAO.insertCoreCourse( courseCode, departmentCodes, batch, courseCategory );
            }
            return true;
        } catch ( Exception error ) {
            return false;
        }
    }

    public boolean checkStudentPassStatus( String entryNumber ) {
        // Check if the entry number that has been entered exists in the database.
        // This is done because the hashmap operation is expensive
        boolean isEntryNumberValid = adminDAO.findEntryNumber( entryNumber );
        if ( !isEntryNumberValid ) return false;

        // We check if the student has credited all the core courses he should have done
        int      batch             = adminDAO.getBatch( entryNumber );
        String   studentDepartment = adminDAO.getStudentDepartment( entryNumber );
        String[] listOfCoreCourses = adminDAO.getCoreCourses( batch, studentDepartment );

        // Iterate through all the courses and verify that the student has passed all the courses
        for ( String courseCode : listOfCoreCourses ) {
            String grade = adminDAO.getCourseGrade( entryNumber, courseCode );
            if ( Utils.getGradeValue( grade ) < 4 ) {
                return false;
            }
        }

        // Get the credit requirements that are left for this particular student
        HashMap<String, Double> creditsLeft = getRemainingCreditRequirements( entryNumber );
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
        HashMap<String, Double> ugCurriculum = adminDAO.getUGCurriculum( batch );

        // Now we have to fetch the course categories and the corresponding credits done by the student
        HashMap<String, Double> categoryCreditsCompleted = adminDAO.getCreditsInAllCategories( entryNumber );
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

    public boolean generateTranscripts( int batch, String department ) {
        try {
            // If the batch does not exist no students will be returned by the function and so no records will be generated
            String[] listOfStudents = adminDAO.getListOfStudentsInBatch( batch, department );

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
                boolean isHeadingWritten = writeHeading( fileWriter, entryNumber );
                if ( isHeadingWritten == false ) {
                    fileWriter.close();
                    continue;
                }

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
                fileWriter.printf( "CGPA: %.2f", getCGPA( records ));
                // Should we be printing the eligibility for graduation?

                // Close the fileWriter object after use to prevent locking issues
                fileWriter.close();
            }

            // Return true once all the students have been processed
            return true;
        } catch ( Exception error ) {
            return false;
        }
    }

    private double computeSGPA( String[][] records ) {
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

    private void writeRecordsIntoFile( String[] headings, String[][] records, int year, int semester, PrintWriter fileWriter ) {
        int rows = records.length;
        if ( rows == 0 ) return;
        fileWriter.println( "Year: " + year + "    Semester: " + semester + "     SGPA: " + computeSGPA( records ) );
        int   columns            = records[0].length;
        int[] maximumFieldLength = new int[]{ 15, 60, 10, 10 };

        String formatString = "";
        for ( int i = 0; i < columns; i++ ) {
            formatString += "%-" + maximumFieldLength[i] + "s";
        }
        formatString += "\n";

        fileWriter.format( formatString, (Object[]) headings );
        for ( final Object[] rowContent : records ) {
            fileWriter.format( formatString, rowContent );
        }
        fileWriter.println();
    }

    private boolean writeHeading( PrintWriter fileWriter, String entryNumber ) {
        try {
            fileWriter.println( "INDIAN INSTITUTE OF TECHNOLOGY, ROPAR" );
            fileWriter.println( "STUDENT GRADE TRANSCRIPT\n" );
            fileWriter.println( "Entry Number: " + entryNumber );
            fileWriter.println();
            return true;
        } catch ( Exception error ) {
            return false;
        }
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
