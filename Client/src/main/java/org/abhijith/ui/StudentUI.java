package org.abhijith.ui;

import org.abhijith.users.Student;
import org.abhijith.utils.CustomScanner;
import org.abhijith.utils.Utils;

import java.util.HashMap;

public class StudentUI {
    final String[] studentChoices = {
            "Enroll",
            "Drop",
            "View Grades for Entire Course",
            "View Grades for Particular Session",
            "Get CGPA",
            "View Available Courses",
            "View Credit Requirements Left",
            "Change Phone Number",
            "Change Contact Email",
            "View Contact Details",
            "Change Password",
            "Any other number to log out"
    };

    public StudentUI( String id ) {
        StudentInterfaceHomeScreen( id );
    }

    public void StudentInterfaceHomeScreen( String id ) {
        CustomScanner keyboardInput = new CustomScanner();
        Student       student       = new Student( id );
        while ( true ) {
            System.out.println();
            System.out.println( "Select an option" );
            for ( int i = 1; i <= studentChoices.length; i++ ) {
                System.out.println( i + ". " + studentChoices[i - 1] );
            }
            // Read the user choice and remove the newline that comes after the integer
            int studentChoice = keyboardInput.integerInput( "Enter your choice" );

           if ( studentChoice == 1 ) {
                // Read the course code
                String courseCode = keyboardInput.stringInput( "Enter the course code" );

                // Read the course department
                String courseDepartment = keyboardInput.stringInput( "Enter the offering department" );

                // Print the response from the enroll function
                boolean enrollStatus = student.enroll( courseCode, courseDepartment );
                if ( enrollStatus ) System.out.println( "Enrolled Successfully" );
                else System.out.println( "Enrollment Failed. Please check if you are eligible to enroll" );
            }

            else if ( studentChoice == 2 ) {
                // Read the course code
                String courseCode = keyboardInput.stringInput( "Enter the course code" );
                // Print the response from the drop function
                if ( student.drop( courseCode ) ) System.out.println( "Course dropped successfully" );
                else System.out.println( "Course drop failed. Please verify that you are enrolled in this course" );
            }

            else if ( studentChoice == 3 ) {
                String[][][] completeStudentRecords = student.getGradesForCourse();
                int          year                   = student.getBatch();
                int          semester               = 1;
                int          i                      = 0;
                for ( String[][] records : completeStudentRecords ) {
                    i = 1 - i;
                    if ( i == 1 ) year++;
                    Utils.prettyPrintGrades( year, semester, student.getSGPA( records ), records );
                }
            }

           else if ( studentChoice == 4 ) {
               int        year           = keyboardInput.integerInput( "Enter the year" );
                int        semester       = keyboardInput.integerInput( "Enter the semester" );
                String[][] semesterGrades = student.getGrades( year, semester );
                if ( semesterGrades.length == 0 ) {
                    System.out.printf( "No records found for session %d-%d\n\n", year, semester );
                    continue;
                }
                // computeSGPA access the database again to compute the SGPA. Try and avoid this later on
                Utils.prettyPrintGrades( year, semester, student.getSGPA( semesterGrades ), semesterGrades );
            }

            else if ( studentChoice == 5 ) {
                System.out.printf( "CGPA: %.2f", student.getCGPA() );
            }

            else if ( studentChoice == 6 ) {
                String[][] coursesOffered = student.getAvailableCourses();
                if ( coursesOffered.length == 0 ) {
                    System.out.println( "No courses offered this session" );
                    return;
                }
                Utils.prettyPrint( new String[]{ "Course Code", "Course Title", "Instructor", "Prerequisites", "Department", "Category" }, coursesOffered );
            }

            else if ( studentChoice == 7 ) {
                HashMap<String, Double> creditRequirementsLeft = student.getRemainingCreditRequirements();
                Utils.prettyPrintCreditRequirements( creditRequirementsLeft );
            }

            else if ( studentChoice == 8 ) {
                String newPhoneNumber = keyboardInput.stringInput( "Enter the new phone number" );
                if ( student.setPhoneNumber( newPhoneNumber ) ) System.out.println( "Phone Number Updated Successfully" );
                else System.out.println( "Phone Number Update Failed" );
            }

            else if ( studentChoice == 9 ) {
                String email = keyboardInput.stringInput("Enter the email");
                if ( student.setEmail( email ) ) System.out.println("Contact Email Updated Successfully");
                else System.out.println("Contact Email Not Updated");
            }

            else if ( studentChoice == 10 ) {
                String   userID        = keyboardInput.stringInput( "Enter the id of the user" );
                String[] emailAndPhone = student.getContactDetails( userID );

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

            else if ( studentChoice == 11 ) {
                String password = keyboardInput.stringInput( "Enter your new password" );
                String verifyPassword = keyboardInput.stringInput( "Reenter your password" );

                if ( !password.equals( verifyPassword )) {
                    System.out.println( "Please reenter the same password" );
                    continue;
                }

                if ( student.setPassword( password )) System.out.println( "Password updated successfully" );
                else System.out.println( "Password Update Failed" );
            }

            else {
                break;
            }
        }
    }
}
