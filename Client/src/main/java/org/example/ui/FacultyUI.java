package org.example.ui;

import org.example.users.Faculty;
import org.example.utils.CustomScanner;
import org.example.utils.Utils;

import java.io.BufferedReader;
import java.util.ArrayList;

public class FacultyUI {
    String[] facultyChoices = {
            "Register Course",
            "Update CG and Prerequisites",
            "Set course category for offering",
            "Deregister Course",
            "View Grades of Specific Student",
            "View Grades of Specific Course Offering",
            "Generate Grade Upload Sheet",
            "Upload Course Grades",
            "Change Phone Number",
            "Change Contact Email",
            "View Contact Details",
            "Change Password",
            "Any other number to logout"
    };

    public FacultyUI( String id ) {
        FacultyUI( id );
    }

    public void FacultyUI( String id ) {
        CustomScanner keyboardInput = new CustomScanner();
        Faculty       faculty       = new Faculty( id );
        while ( true ) {
            System.out.println();
            System.out.println( "Select an option" );
            for ( int i = 1; i <= facultyChoices.length; i++ ) {
                System.out.println( i + ". " + facultyChoices[i - 1] );
            }
            int facultyChoice = keyboardInput.integerInput( "Enter your choice" );
            if ( facultyChoice == 1 ) {
                String  courseCode = keyboardInput.stringInput( "Enter the course code" );
                boolean status     = faculty.offerCourse( courseCode );
                if ( status == true )
                    System.out.println( "Course offered. Please update prerequisites and CG criteria manually" );
                else
                    System.out.println( "Course not offered. Please verify that the course exists in the course catalog" );
            }

            else if ( facultyChoice == 2 ) {
                String courseCode  = keyboardInput.stringInput( "Enter the course code" );
                double minimumCGPA = 0;
                minimumCGPA = keyboardInput.doubleInput( "Enter the minimum CGPA criteria" );
                ArrayList<String[]> prerequisites         = new ArrayList<>();
                int                 numberOfPrerequisites = keyboardInput.integerInput( "Enter the number of prerequisites" );
                for ( int i = 1; i <= numberOfPrerequisites; i++ ) {
                    int               numberOfCourses = keyboardInput.integerInput( "Enter the number of courses of type" + i );
                    ArrayList<String> courses         = new ArrayList<>();
                    for ( int j = 0; j < numberOfCourses; j++ ) {
                        String course = keyboardInput.stringInput( "Enter the course code" );
                        // Should the grade cutoff be in numbers or should it be a grade as a string?
                        int prerequisite = keyboardInput.integerInput( "Enter the grade cutoff (as a number)" );
                        if ( prerequisite > 10 || prerequisite < 0 ) {
                            System.out.println( "Invalid grade requirement" );
                            continue;
                        }
                        courses.add( course );
                        courses.add( Integer.toString( prerequisite ) );
                    }
                    if ( prerequisites.add( courses.toArray( new String[courses.size()] ) ) )
                        System.out.println( "Offering Updated Successfully" );
                    else
                        System.out.println( "Offering update failed. Only update courses that are offered by yourself and verify that all courses exist" );
                }
                faculty.setCGAndPrerequisites( courseCode, minimumCGPA, prerequisites.toArray( new String[prerequisites.size()][] ) );
            }

            else if ( facultyChoice == 3 ) {
                String courseCode     = keyboardInput.stringInput( "Enter the course code" );
                String courseCategory = keyboardInput.stringInput( "Enter the course category" );
                String department     = keyboardInput.stringInput( "Enter the department for which you wish to offer the course" );
                int    numberOfYears  = keyboardInput.integerInput( "Enter the number of years" );
                int[]  years          = new int[numberOfYears];
                for ( int i = 1; i <= numberOfYears; i++ ) {
                    years[i - 1] = keyboardInput.integerInput( "Enter year " + i );
                }
                faculty.setCourseCategory( courseCode, courseCategory, department, years );
            }

            else if ( facultyChoice == 4 ) {
                String courseCode = keyboardInput.stringInput( "Enter the course code" );
                if ( faculty.dropCourseOffering( courseCode ) )
                    System.out.println( "Course offering dropped successfully" );
                else System.out.println( "Course offering not dropped. Please verify the course code" );
            }

            else if ( facultyChoice == 5 ) {
                String       entryNumber = keyboardInput.stringInput( "Enter the entry number" );
                int          year        = keyboardInput.integerInput( "Enter the batch (year)" );
                String[][][] records     = faculty.getGradesOfStudent( entryNumber );
                int          semester    = 1;
                for ( String[][] record : records ) {
                    Utils.prettyPrintGrades( year, semester, record );
                    semester = ( semester == 1 ) ? 2 : 1;
                    if ( semester == 2 ) year++;
                }
            }

            else if ( facultyChoice == 6 ) {
                String     courseCode = keyboardInput.stringInput( "Enter the course code" );
                int        year       = keyboardInput.integerInput( "Enter the year" );
                int        semester   = keyboardInput.integerInput( "Enter the semester" );
                String[][] records    = faculty.getGradesOfOffering( courseCode, year, semester );
                if ( records.length == 0 ) {
                    System.out.println( "No course found with given specifications" );
                    continue;
                }
                // You can add a sort function later to sort the entries by the entry number before printing to the screen
                Utils.prettyPrint( new String[]{ "Entry Number", "Grade" }, records );
            }

            else if ( facultyChoice == 7 ) {
                String courseCode = keyboardInput.stringInput( "Enter the course code" );
                int    year       = keyboardInput.integerInput( "Enter the year" );
                int    semester   = keyboardInput.integerInput( "Enter the semester" );
                faculty.generateGradeCSV( courseCode, year, semester );
            }

            else if ( facultyChoice == 8 ) {
                String courseCode = keyboardInput.stringInput( "Enter the course code" );
                int    year       = keyboardInput.integerInput( "Enter the year" );
                int    semester   = keyboardInput.integerInput( "Enter the semester" );
                // You need to perform faculty authentication even here
                try {
                    BufferedReader gradeCSVFile = keyboardInput.openCourseCSVFile( courseCode, year, semester );
                    if ( gradeCSVFile == null ) {
                        continue;
                    }
                    if ( faculty.uploadGrades( courseCode, year, semester, gradeCSVFile ) ) System.out.println( "Grades inserted successfully" );
                    else System.out.println( "Please verify that all students exist and the course is offered by this id" );
                    gradeCSVFile.close();
                } catch ( Exception error ) {
                    System.out.println( "Please enter valid course code, year and semester");
                }
            }

            else if ( facultyChoice == 9 ) {
                String newPhoneNumber = keyboardInput.stringInput( "Enter the new phone number" );
                if ( faculty.setPhoneNumber( newPhoneNumber ) ) System.out.println( "Phone Number Updated Successfully" );
                else System.out.println( "Phone Number Update Failed" );
            }

            else if ( facultyChoice == 10 ) {
                String email = keyboardInput.stringInput("Enter the email");
                if ( faculty.setEmail( email ) ) System.out.println("Contact Email Updated Successfully");
                else System.out.println("Contact Email Not Updated");
            }

            else if ( facultyChoice == 11 ) {
                String   userID        = keyboardInput.stringInput( "Enter the id of the user" );
                String[] emailAndPhone = faculty.getContactDetails( userID );

                if ( emailAndPhone.length == 2 ) {
                    String   email         = emailAndPhone[0];
                    String   phoneNumber   = emailAndPhone[1];
                    if ( email != null ) System.out.println( "Email: " + email );
                    if ( phoneNumber != null ) System.out.println( "Phone: " + phoneNumber );
                    if ( email == null && phoneNumber == null )
                        System.out.println( "User has not provided contact details" );
                }
                else System.out.println( "Could not retrieve details of user" );
            }

            else if ( facultyChoice == 12 ) {
                String password = keyboardInput.stringInput( "Enter your new password" );
                String verifyPassword = keyboardInput.stringInput( "Reenter your password" );

                if ( !password.equals( verifyPassword )) {
                    System.out.println( "Please reenter the same password" );
                    continue;
                }

                if ( faculty.setPassword( password )) System.out.println( "Password updated successfully" );
                else System.out.println( "Password Update Failed" );
            }

            else break;
        }
    }
}
