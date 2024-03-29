package org.abhijith.ui;

import org.abhijith.users.Admin;
import org.abhijith.utils.CustomScanner;
import org.abhijith.utils.Utils;

import java.io.BufferedReader;

public class AdminUI {
    private Admin         admin;
    private CustomScanner keyboardInput = null;

    String[] adminChoices = {
            "Add Student",
            "Add Faculty",
            "Add Course",
            "Remove Course",
            "View Grade of Specific Student",
            "View Grade of Specific Course Offering",
            "Add New Batch",
            "Add Core Courses List",
            "Check Pass Status",
            "Generate Transcript",
            "Change Phone Number",
            "Change Contact Email",
            "View Contact Details",
            "Change Password",
            "Add new session",
            "Modify Session",
            "View Course Catalog",
            "Any other number to log out"
    };

    // Constructor to be used if a new scanner is to be created whenever you call the user interface
    public AdminUI( String id ) {
        admin = new Admin( id );
    }

    // Constructor to be used if a scanner is to be shared between this class and the calling class
    public AdminUI( String id, CustomScanner keyboardInput ) {
        this.keyboardInput = keyboardInput;
        admin = new Admin( id );
    }

    public void setAdmin( Admin admin ) {
        this.admin = admin;
    }

    public void adminInterfaceHomeScreen() {
        // Creates a new scanner if one is not already available
        if ( keyboardInput == null ) keyboardInput = new CustomScanner();

        while ( true ) {

            // Display the available options to the user
            System.out.println();
            System.out.println( "Select an option" );
            for ( int i = 1; i <= adminChoices.length; i++ ) {
                System.out.println( i + ". " + adminChoices[i - 1] );
            }

            // Takes user input
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
                String courseCode  = keyboardInput.stringInput( "Enter the course code" );
                String courseTitle = keyboardInput.stringInput( "Enter the course title" );

                // Takes the LTPSC structure as input
                double[] creditStructure = new double[5];
                creditStructure[0] = keyboardInput.doubleInput( "Enter the lecture hours" );
                creditStructure[1] = keyboardInput.doubleInput( "Enter the tutorial hours" );
                creditStructure[2] = keyboardInput.doubleInput( "Enter the practical hours" );
                creditStructure[3] = keyboardInput.doubleInput( "Enter the self study hours" );
                creditStructure[4] = keyboardInput.doubleInput( "Enter the credits" );

                // Takes the course codes of the prerequisites as input
                int      numberOfPrerequisites = keyboardInput.integerInput( "Enter the number of prerequisites" );
                String[] prerequisites         = new String[numberOfPrerequisites];
                for ( int i = 0; i < numberOfPrerequisites; i++ )
                    prerequisites[i] = keyboardInput.stringInput( "Enter the prerequisite" );

                if ( admin.insertCourseIntoCatalog( courseCode, courseTitle, creditStructure, prerequisites ) )
                    System.out.println( "Course inserted successfully" );
                else System.out.println( "Course insertion failed. Please verify course details" );
            }

            else if ( adminChoice == 4 ) {
                System.out.println( "Warning! Removing course will also remove all records corresponding to the course" );
                String courseCode = keyboardInput.stringInput( "Enter course code to be removed" );

                if ( admin.dropCourseFromCatalog( courseCode ) ) System.out.println( "Course dropped successfully" );
                else System.out.println( "Course Drop Failed" );
            }

            else if ( adminChoice == 5 ) {
                String       entryNumber = keyboardInput.stringInput( "Enter the entry number" );
                int          year        = keyboardInput.integerInput( "Enter the batch (year)" );
                String[][][] records     = admin.getGradesOfStudent( entryNumber );
                int          semester    = 1;

                for ( String[][] record : records ) {
                    Utils.prettyPrintGrades( year, semester, record );
                    if ( semester == 2 ) year++;
                    semester = ( semester == 1 ) ? 2 : 1;
                }
            }

            else if ( adminChoice == 6 ) {
                String     courseCode   = keyboardInput.stringInput( "Enter the course code" );
                int        year         = keyboardInput.integerInput( "Enter the year" );
                int        semester     = keyboardInput.integerInput( "Enter the semester" );
                String     departmentID = keyboardInput.stringInput( "Enter the offering department" );
                String[][] records      = admin.getGradesOfOffering( courseCode, year, semester, departmentID );

                if ( records.length == 0 ) {
                    System.out.println( "No course found with given specifications" );
                    continue;
                }
                Utils.prettyPrint( new String[]{ "Entry Number", "Grade" }, records );
            }

            else if ( adminChoice == 7 ) {
                int batchYear = keyboardInput.integerInput( "Enter the new batch year" );

                // Takes all the category credit requirements as input
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
                        System.out.println( "Please enter valid file" );
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

                if ( isTranscriptGenerated ) System.out.println( "Transcripts Generated Successfully" );
                else System.out.println( "Please try again later" );
            }

            else if ( adminChoice == 11 ) {
                String newPhoneNumber = keyboardInput.stringInput( "Enter the new phone number" );

                if ( admin.setPhoneNumber( newPhoneNumber ) ) System.out.println( "Phone Number Updated Successfully" );
                else System.out.println( "Phone Number Update Failed" );
            }

            else if ( adminChoice == 12 ) {
                String email = keyboardInput.stringInput( "Enter the email" );

                if ( admin.setEmail( email ) ) System.out.println( "Contact Email Updated Successfully" );
                else System.out.println( "Contact Email Not Updated" );
            }

            else if ( adminChoice == 13 ) {
                String   userID        = keyboardInput.stringInput( "Enter the id of the user" );
                String[] emailAndPhone = admin.getContactDetails( userID );

                if ( emailAndPhone.length == 2 ) {
                    String email       = emailAndPhone[0];
                    String phoneNumber = emailAndPhone[1];

                    if ( email != null ) System.out.println( "Email: " + email );

                    if ( phoneNumber != null ) System.out.println( "Phone: " + phoneNumber );

                    if ( email == null && phoneNumber == null )
                        System.out.println( "User has not provided contact details" );
                }
                else System.out.println( "Could not retrieve details of user" );
            }

            else if ( adminChoice == 14 ) {
                String password       = keyboardInput.stringInput( "Enter your new password" );
                String verifyPassword = keyboardInput.stringInput( "Reenter your password" );

                if ( !password.equals( verifyPassword ) ) {
                    System.out.println( "Please reenter the same password" );
                    continue;
                }

                if ( admin.setPassword( password ) ) System.out.println( "Password updated successfully" );
                else System.out.println( "Password Update Failed" );
            }

            else if ( adminChoice == 15 ) {
                if ( admin.startNewSession() ) System.out.println( "New session inserted" );
                else System.out.println( "New session insertion failed" );
            }

            else if ( adminChoice == 16 ) {
                System.out.println( "List of possible Events" );
                String[] eventsList = new String[]{ "ENROLLING", "OFFERING", "GRADE SUBMISSION", "COMPLETED", "RUNNING" };
                for ( int i = 1; i <= eventsList.length; i++ ) {
                    System.out.println( i + ". " + eventsList[i - 1] );
                }
                int choice = keyboardInput.integerInput( "Enter your choice" );
                if ( choice <= 0 || choice > eventsList.length ) {
                    System.out.println( "Invalid Choice" );
                    continue;
                }

                if ( admin.setCurrentSessionStatus( eventsList[choice - 1] ) )
                    System.out.println( "Event updated successfully" );
                else System.out.println( "Event update failed" );
            }

            else if ( adminChoice == 17 ) {
                String[][] courses = admin.getCourseCatalog();
                Utils.prettyPrint( new String[]{ "Course Code", "Course Title", "L", "T", "P", "S", "C", "Prerequisites" }, courses );
            }

            else break;
        }
    }
}
