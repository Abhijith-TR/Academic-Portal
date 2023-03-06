package org.abhijith.ui;

import org.abhijith.users.Faculty;
import org.abhijith.utils.CustomScanner;
import org.abhijith.utils.Utils;

import java.io.BufferedReader;
import java.util.ArrayList;

public class FacultyUI {
    private Faculty       faculty;
    private CustomScanner keyboardInput;
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
            "View Course Catalog",
            "View Instructor prerequisites",
            "Any other number to logout"
    };

    public FacultyUI( String id ) {
        faculty = new Faculty( id );
        keyboardInput = new CustomScanner();
    }

    public FacultyUI( String id, CustomScanner keyboardInput ) {
        faculty = new Faculty( id );
        this.keyboardInput = keyboardInput;
    }

    public void setFaculty( Faculty faculty ) {
        this.faculty = faculty;
    }

    public void facultyInterfaceHomeScreen() {
        while ( true ) {
            System.out.println();
            System.out.println( "Select an option" );
            for ( int i = 1; i <= facultyChoices.length; i++ ) {
                System.out.println( i + ". " + facultyChoices[i - 1] );
            }
            int facultyChoice = keyboardInput.integerInput( "Enter your choice" );
            if ( facultyChoice == 1 ) {
                String  courseCode   = keyboardInput.stringInput( "Enter the course code" );
                String  departmentID = keyboardInput.stringInput( "Enter the offering department" );
                boolean status       = faculty.offerCourse( courseCode, departmentID );
                if ( status )
                    System.out.println( "Course offered. Please update prerequisites and CG criteria manually" );
                else
                    System.out.println( "Course not offered. Please verify that the course exists in the course catalog" );
            }

            else if ( facultyChoice == 2 ) {
                String courseCode   = keyboardInput.stringInput( "Enter the course code" );
                String departmentID = keyboardInput.stringInput( "Enter the offering department" );
                double minimumCGPA;
                minimumCGPA = keyboardInput.doubleInput( "Enter the minimum CGPA criteria" );

                ArrayList<String[]> prerequisites         = new ArrayList<>();
                int                 numberOfPrerequisites = keyboardInput.integerInput( "Enter the number of prerequisites" );

                for ( int i = 1; i <= numberOfPrerequisites; i++ ) {
                    System.out.println();
                    int               numberOfCourses = keyboardInput.integerInput( "Enter the number of courses of type " + i );
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
                    prerequisites.add( courses.toArray( new String[courses.size()] ) );
                }
                String[][] prerequisitesArray = prerequisites.toArray( new String[prerequisites.size()][] );
                if ( faculty.setCGAndPrerequisites( courseCode, departmentID, minimumCGPA, prerequisitesArray ) )
                    System.out.println( "Details Updated Successfully" );
                else System.out.println( "Criteria Update Failed" );
            }

            else if ( facultyChoice == 3 ) {
                String courseCode         = keyboardInput.stringInput( "Enter the course code" );
                String offeringDepartment = keyboardInput.stringInput( "Enter the offering department" );
                String courseCategory     = keyboardInput.stringInput( "Enter the course category" );
                String department         = keyboardInput.stringInput( "Enter the department for which you wish to offer the course" );
                int    numberOfYears      = keyboardInput.integerInput( "Enter the number of years" );
                int[]  years              = new int[numberOfYears];
                for ( int i = 1; i <= numberOfYears; i++ ) {
                    years[i - 1] = keyboardInput.integerInput( "Enter year " + i );
                }
                if ( faculty.setCourseCategory( courseCode, offeringDepartment, courseCategory, department, years ) )
                    System.out.println( "Category set successfully" );
                else
                    System.out.println( "Category setting failed. Please verify that you are not offering for the same batch twice." );
            }

            else if ( facultyChoice == 4 ) {
                String courseCode   = keyboardInput.stringInput( "Enter the course code" );
                String departmentID = keyboardInput.stringInput( "Enter the offering department" );
                if ( faculty.dropCourseOffering( courseCode, departmentID ) )
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
                    if ( semester == 2 ) year++;
                    semester = ( semester == 1 ) ? 2 : 1;
                }
            }

            else if ( facultyChoice == 6 ) {
                String     courseCode   = keyboardInput.stringInput( "Enter the course code" );
                int        year         = keyboardInput.integerInput( "Enter the year" );
                int        semester     = keyboardInput.integerInput( "Enter the semester" );
                String     departmentID = keyboardInput.stringInput( "Enter the offering department" );
                String[][] records      = faculty.getGradesOfOffering( courseCode, year, semester, departmentID );
                if ( records.length == 0 ) {
                    System.out.println( "No course found with given specifications" );
                    continue;
                }
                // You can add a sort function later to sort the entries by the entry number before printing to the screen
                Utils.prettyPrint( new String[]{ "Entry Number", "Grade" }, records );
            }

            else if ( facultyChoice == 7 ) {
                String  courseCode    = keyboardInput.stringInput( "Enter the course code" );
                int     year          = keyboardInput.integerInput( "Enter the year" );
                int     semester      = keyboardInput.integerInput( "Enter the semester" );
                String  departmentID  = keyboardInput.stringInput( "Enter the offering department" );
                boolean isFileCreated = faculty.generateGradeCSV( courseCode, year, semester, departmentID );
                if ( isFileCreated ) System.out.println( "File Generated Successfully" );
                else System.out.println( "File Not Generated. Only generate files for your own courses" );
            }

            else if ( facultyChoice == 8 ) {
                String courseCode   = keyboardInput.stringInput( "Enter the course code" );
                int    year         = keyboardInput.integerInput( "Enter the year" );
                int    semester     = keyboardInput.integerInput( "Enter the semester" );
                String departmentID = keyboardInput.stringInput( "Enter the offering department" );
                try {
                    BufferedReader gradeCSVFile = keyboardInput.openCourseCSVFile( courseCode, year, semester, departmentID, "Enter the file path" );
                    if ( gradeCSVFile == null ) {
                        System.out.println( "Enter valid file path" );
                        continue;
                    }
                    if ( faculty.uploadGrades( courseCode, year, semester, gradeCSVFile, departmentID ) )
                        System.out.println( "Grades inserted successfully" );
                    else
                        System.out.println( "Please verify that all students exist and the course is offered by this id" );
                    gradeCSVFile.close();
                } catch ( Exception error ) {
                    System.out.println( "Please enter valid course code, year and semester" );
                }
            }

            else if ( facultyChoice == 9 ) {
                String newPhoneNumber = keyboardInput.stringInput( "Enter the new phone number" );
                if ( faculty.setPhoneNumber( newPhoneNumber ) )
                    System.out.println( "Phone Number Updated Successfully" );
                else System.out.println( "Phone Number Update Failed" );
            }

            else if ( facultyChoice == 10 ) {
                String email = keyboardInput.stringInput( "Enter the email" );
                if ( faculty.setEmail( email ) ) System.out.println( "Contact Email Updated Successfully" );
                else System.out.println( "Contact Email Not Updated" );
            }

            else if ( facultyChoice == 11 ) {
                String   userID        = keyboardInput.stringInput( "Enter the id of the user" );
                String[] emailAndPhone = faculty.getContactDetails( userID );

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

            else if ( facultyChoice == 12 ) {
                String password       = keyboardInput.stringInput( "Enter your new password" );
                String verifyPassword = keyboardInput.stringInput( "Reenter your password" );

                if ( !password.equals( verifyPassword ) ) {
                    System.out.println( "Please reenter the same password" );
                    continue;
                }

                if ( faculty.setPassword( password ) ) System.out.println( "Password updated successfully" );
                else System.out.println( "Password Update Failed" );
            }

            else if ( facultyChoice == 13 ) {
                String[][] courses = faculty.getCourseCatalog();
                Utils.prettyPrint( new String[]{ "Course Code", "Course Title", "L", "T", "P", "S", "C", "Prerequisites" }, courses );
            }

            else if ( facultyChoice == 14 ) {
                String courseCode   = keyboardInput.stringInput( "Enter the course code" );
                int    year         = keyboardInput.integerInput( "Enter the year" );
                int    semester     = keyboardInput.integerInput( "Enter the semester" );
                String departmentID = keyboardInput.stringInput( "Enter the department ID" );

                String[][] prerequisiteCourses = faculty.getInstructorPrerequisites( courseCode, year, semester, departmentID );
                Utils.prettyPrint( new String[]{ "Course Code", "Grade Cutoff" }, prerequisiteCourses );
            }

            else break;
        }
    }
}
