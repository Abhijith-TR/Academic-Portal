package org.example.ui;

import org.example.dal.PostgresAdminDAO;
import org.example.daoInterfaces.AdminDAO;
import org.example.users.Admin;
import org.example.utils.CustomScanner;
import org.example.utils.Utils;

import java.io.BufferedReader;

public class AdminUI {
    // Add functionality to view the course catalog
    String[] adminChoices = {
            "Add Student",
            "Add Faculty",
            "Add Course",
            "Remove Course",
            "View Grade of Specific Student",
            "View Grade of Specific Course Offering",
            // When you are inserting a student, check whether the batch exists or not. Add this feature later
            "Add New Batch",
            "Add Core Courses List",
            "Check Pass Status",
            "Generate Transcript",
            "Any other number to log out"
    };

    public AdminUI( String connectionURL, String username, String password, String id ) {
        AdminDAO adminDAO = new PostgresAdminDAO(
                connectionURL,
                username,
                password
        );
        adminInterfaceHomeScreen( id, adminDAO );
    }

    public void adminInterfaceHomeScreen( String id, AdminDAO adminDAO ) {
        CustomScanner keyboardInput = new CustomScanner();
        Admin         admin         = new Admin( id, adminDAO );
        while ( true ) {
            System.out.println();
            System.out.println( "Select an option" );
            for ( int i = 1; i <= adminChoices.length; i++ ) {
                System.out.println( i + ". " + adminChoices[i - 1] );
            }
            int adminChoice = keyboardInput.integerInput( "Enter your choice" );

            if ( adminChoice == 1 ) {
                String entryNumber  = keyboardInput.stringInput( "Enter the entry number" );
                String name         = keyboardInput.stringInput( "Enter the name" );
                String departmentID = keyboardInput.stringInput( "Enter the department ID" );
                int    batch        = keyboardInput.integerInput( "Enter the batch" );
                if ( admin.insertStudent( entryNumber, name, departmentID, batch ) ) {
                    System.out.println( "Student inserted successfully" );
                }
                else System.out.println( "Student insertion failed" );
            }

            else if ( adminChoice == 2 ) {
                String facultyID    = keyboardInput.stringInput( "Enter the faculty ID" );
                String name         = keyboardInput.stringInput( "Enter the name" );
                String departmentID = keyboardInput.stringInput( "Enter the department ID" );
                if ( admin.insertFaculty( facultyID, name, departmentID ) ) {
                    System.out.println( "Faculty inserted successfully" );
                }
                else System.out.println( "Faculty insertion failed" );
            }

            else if ( adminChoice == 3 ) {
                String   courseCode      = keyboardInput.stringInput( "Enter the course code" );
                String   courseTitle     = keyboardInput.stringInput( "Enter the course title" );
                double[] creditStructure = new double[5];
                creditStructure[0] = keyboardInput.doubleInput( "Enter the lecture hours" );
                creditStructure[1] = keyboardInput.doubleInput( "Enter the tutorial hours" );
                creditStructure[2] = keyboardInput.doubleInput( "Enter the practical hours" );
                creditStructure[3] = keyboardInput.doubleInput( "Enter the self study hours" );
                creditStructure[4] = keyboardInput.doubleInput( "Enter the credits" );
                int      numberOfPrerequisites = keyboardInput.integerInput( "Enter the number of prerequisites" );
                String[] prerequisites         = new String[numberOfPrerequisites];
                for ( int i = 0; i < numberOfPrerequisites; i++ )
                    prerequisites[i] = keyboardInput.stringInput( "Enter the prerequisite" );
                String departmentID = keyboardInput.stringInput( "Enter the department ID" );
                if ( admin.insertCourseIntoCatalog( courseCode, courseTitle, creditStructure, prerequisites, departmentID ) )
                    System.out.println( "Course inserted successfully" );
                else System.out.println( "Course insertion failed. Please verify course details" );
            }

            else if ( adminChoice == 4 ) {
                System.out.println( "Warning! Removing course will also remove all records corresponding to the course" );
                String courseCode = keyboardInput.stringInput( "Enter course code to be removed" );
                if ( admin.dropCourseFromCatalog( courseCode ) ) System.out.println( "Course dropped successfully" );

            }

            else if ( adminChoice == 5 ) {
                String       entryNumber = keyboardInput.stringInput( "Enter the entry number" );
                int          year        = keyboardInput.integerInput( "Enter the batch (year)" );
                String[][][] records     = admin.getGradesOfStudent( entryNumber );
                int          semester    = 1;
                for ( String[][] record : records ) {
                    Utils.prettyPrintGrades( year, semester, record );
                    semester = ( semester == 1 ) ? 2 : 1;
                    if ( semester == 2 ) year++;
                }
            }

            else if ( adminChoice == 6 ) {
                String     courseCode = keyboardInput.stringInput( "Enter the course code" );
                int        year       = keyboardInput.integerInput( "Enter the year" );
                int        semester   = keyboardInput.integerInput( "Enter the semester" );
                String[][] records    = admin.getGradesOfOffering( courseCode, year, semester );
                if ( records.length == 0 ) {
                    System.out.println( "No course found with given specifications" );
                    continue;
                }
                // You can add a sort function later to sort the entries by the entry number before printing to the screen
                Utils.prettyPrint( new String[]{ "Entry Number", "Grade" }, records );
            }

            else if ( adminChoice == 7 ) {
                int      batchYear          = keyboardInput.integerInput( "Enter the new batch year" );
                double[] creditRequirements = new double[11];
                creditRequirements[0] = keyboardInput.doubleInput( "Science Core credit requirements" );
                creditRequirements[1] = keyboardInput.doubleInput( "Science Elective credit requirements" );
                creditRequirements[2] = keyboardInput.doubleInput( "General Engineering credit requirements" );
                creditRequirements[3] = keyboardInput.doubleInput( "Program Core credit requirements" );
                creditRequirements[4] = keyboardInput.doubleInput( "Program Elective credit requirements" );
                creditRequirements[5] = keyboardInput.doubleInput( "Humanities Core credit requirements" );
                creditRequirements[6] = keyboardInput.doubleInput( "Humanities Elective credit requirements" );
                creditRequirements[7] = keyboardInput.doubleInput( "Capstone credit requirements" );
                creditRequirements[8] = keyboardInput.doubleInput( "Industrial Internship credit requirements" );
                creditRequirements[9] = keyboardInput.doubleInput( "NSS/NSO/NCC credit requirements" );
                creditRequirements[10] = keyboardInput.doubleInput( "Open Elective credit requirements" );
                if ( admin.createBatch( batchYear, creditRequirements ) )
                    System.out.println( "Curriculum successfully created for batch" );
                else System.out.println( "Please check details and try again" );
            }

            else if ( adminChoice == 8 ) {
                try {
                    int            batch         = keyboardInput.integerInput( "Enter the batch" );
                    BufferedReader courseCSVFile = keyboardInput.CSVFileInput( "Enter the CSV file path" );
                    if ( courseCSVFile == null ) {
                        continue;
                    }
                    if ( admin.insertCoreCourses( batch, courseCSVFile ) )
                        System.out.println( "Courses inserted successfully" );
                    else System.out.println( "Insertion failed. Please verify that the file is in the right format" );
                    courseCSVFile.close();
                } catch ( Exception error ) {
                    System.out.println( "Something went wrong while closing the file" );
                }
            }

            else if ( adminChoice == 9 ) {
                String  entryNumber      = keyboardInput.stringInput( "Enter the entry number" );
                boolean hasStudentPassed = admin.checkStudentPassStatus( entryNumber );
                if ( hasStudentPassed ) System.out.println( "Student eligible for graduation" );
                else System.out.println( "Student ineligible for graduation" );
            }

            else if ( adminChoice == 10 ) {
                int     batch                 = keyboardInput.integerInput( "Enter the batch" );
                String  department            = keyboardInput.stringInput( "Enter the department" );
                boolean isTranscriptGenerated = admin.generateTranscripts( batch, department );
                if ( isTranscriptGenerated ) System.out.println( "Transcripts Generated Successfully");
                else System.out.println( "Please try again later" );
            }

            else break;
        }
    }
}
