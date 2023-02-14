package org.example.ui;

import org.example.dal.PostgresStudentDAO;
import org.example.daoInterfaces.StudentDAO;
import org.example.users.Student;
import org.example.utils.CustomScanner;
import org.example.utils.Utils;

public class StudentUI {
    final String[] studentChoices = {
            "Update Profile",
            "Enroll",
            "Drop",
            "View Grades for Entire Course",
            "View Grades for Particular Session",
            "Get CGPA",
            "View Available Courses"
    };

    public StudentUI(String connectionURL, String username, String password, String id) {
        StudentDAO databaseConnection = new PostgresStudentDAO(
                connectionURL,
                username,
                password
        );
        StudentInterfaceHomeScreen(databaseConnection, id);
    }

    public void StudentInterfaceHomeScreen(StudentDAO databaseConnection, String id) {
        CustomScanner keyboardInput = new CustomScanner();
        Student       student       = new Student(id, databaseConnection);
        while (true) {
            System.out.println();
            System.out.println("Select an option");
            for (int i = 1; i <= studentChoices.length; i++) {
                System.out.println(i + ". " + studentChoices[i - 1]);
            }
            // Read the user choice and remove the newline that comes after the integer
            int studentChoice = keyboardInput.integerInput("Enter your choice");

            if (studentChoice == 1) {
                // Read the phone number and the newline that follows it
                int     newPhoneNumber = keyboardInput.integerInput("Enter the new phone number");
                boolean status         = student.updateProfile(newPhoneNumber);

                if (!status) System.out.println("Profile Update Failed");
                else System.out.println("Profile Updated Successfully");
            } else if (studentChoice == 2) {
                // Read the course code
                String courseCode = keyboardInput.stringInput("Enter the course code");
                // Print the response from the enroll function
                System.out.println(student.enroll(courseCode));
            } else if (studentChoice == 3) {
                // Read the course code
                String courseCode = keyboardInput.stringInput("Enter the course code");
                // Print the response from the drop function
                System.out.println(student.drop(courseCode));
            } else if (studentChoice == 4) {
                String[][][] completeStudentRecords = student.getGradesForCourse();
                int          year                   = student.getBatch();
                int          semester               = 1;
                int          i                      = 0;
                for (String[][] records : completeStudentRecords) {
                    i = 1-i;
                    if (i == 1) year++;
                    Utils.prettyPrintGrades(year, semester, student.computeSGPA(records), records);
                }
            } else if (studentChoice == 5) {
                int        year           = keyboardInput.integerInput("Enter the year");
                int        semester       = keyboardInput.integerInput("Enter the semester");
                String[][] semesterGrades = student.getGrades(year, semester);
                if (semesterGrades.length == 0) {
                    System.out.printf("No records found for session %d-%d\n\n", year, semester);
                    continue;
                }
                // computeSGPA access the database again to compute the SGPA. Try and avoid this later on
                Utils.prettyPrintGrades(year, semester, student.computeSGPA(semesterGrades), semesterGrades);
            } else if (studentChoice == 6) {
                System.out.printf("CGPA: %.2f", student.getCGPA());
            } else if (studentChoice == 7) {
                String[][] coursesOffered = student.getAvailableCourses();
                if (coursesOffered.length == 0) {
                    System.out.println("No courses offered this session");
                    return;
                }
                Utils.prettyPrint(new String[]{"Course Code", "Course Title", "Instructor", "Prerequisites"}, coursesOffered);
            } else {
                break;
            }
            System.out.println();
        }
    }
}
