package org.example.dal;

import org.example.utils.Utils;

import java.sql.*;
import java.util.ArrayList;

public class StudentDAO {
    Connection databaseConnection;
    public StudentDAO(String connectionURL, String username, String password) {
        try {
            databaseConnection = DriverManager.getConnection(
                    connectionURL,
                    username,
                    password
            );
        } catch (Exception error) {
            System.out.println("Database error. Please try again later");
            System.exit(0);
        }
    }

    // Returns the current year and semester if successful
    // Otherwise returns default year and session i.e., 2020-1
    public int[] getCurrentAcademicSession() {
        try {
            PreparedStatement getSessionQuery = databaseConnection.prepareStatement("SELECT * FROM current_year_and_semester");
            ResultSet         currentSession  = getSessionQuery.executeQuery();

            currentSession.next();
            int currentYear     = currentSession.getInt(1);
            int currentSemester = currentSession.getInt(2);

            return new int[]{currentYear, currentSemester};
        } catch (SQLException error) {
            System.out.println(error.getMessage());
            return new int[]{2020, 1};
        }
    }

    public boolean checkCourseOffering(String courseCode, int currentYear, int currentSemester) {
        try {
            PreparedStatement checkQuery = databaseConnection.prepareStatement("SELECT course_code FROM course_offerings WHERE course_code = ? AND year = ? AND semester = ?");
            checkQuery.setString(1, courseCode);
            checkQuery.setInt(2, currentYear);
            checkQuery.setInt(3, currentSemester);
            ResultSet doesCourseExist = checkQuery.executeQuery();
            return doesCourseExist.next();
        } catch (SQLException error) {
            System.out.println(error.getMessage());
            return false;
        }
    }

    public boolean checkStudentPassStatus(String courseCode, int gradeCutoff, String id) {
        try {
            PreparedStatement gradeQuery = databaseConnection.prepareStatement("SELECT grade FROM student_course_registration WHERE entry_number = ? AND course_code = ?");
            gradeQuery.setString(1, id);
            gradeQuery.setString(2, courseCode);
            ResultSet courseQueryResult = gradeQuery.executeQuery();
            boolean   studentCredited   = courseQueryResult.next();
            if (!studentCredited) {
                return false;
            }
            String grade = courseQueryResult.getString(1);
            return Utils.getGradeValue(grade) >= gradeCutoff;
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return false;
        }
    }

    public String[] getCourseCatalogPrerequisites(String courseCode) {
        try {
            PreparedStatement prerequisiteQuery = databaseConnection.prepareStatement("SELECT pre_requisites FROM course_catalog WHERE course_code = ?");
            prerequisiteQuery.setString(1, courseCode);
            ResultSet courseCatalogResult = prerequisiteQuery.executeQuery();
            boolean   subjectExists       = courseCatalogResult.next();
            if (!subjectExists) {
                return null;
            }
            Array prerequisites = courseCatalogResult.getArray(1);
            if (prerequisites == null) return new String[]{};
            return (String[]) prerequisites.getArray();
        } catch (SQLException error) {
            System.out.println(error.getMessage());
            return null;
        }
    }

    public String[][] getInstructorPrerequisites(String courseCode, int year, int semester) {
        try {
            PreparedStatement prerequisiteQuery = databaseConnection.prepareStatement("SELECT instructor_prerequisites FROM course_offerings WHERE course_code = ? AND semester = ? AND year = ?");
            prerequisiteQuery.setString(1, courseCode);
            prerequisiteQuery.setInt(2, semester);
            prerequisiteQuery.setInt(3, year);
            ResultSet prerequisiteResult = prerequisiteQuery.executeQuery();

            boolean doesCourseExist = prerequisiteResult.next();
            if (!doesCourseExist) {
                return null;
            }

            Array prerequisites = prerequisiteResult.getArray(1);
            if (prerequisites == null) return new String[][]{};
            return (String[][]) prerequisites.getArray();
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return null;
        }
    }

    // The 24 is being returned to prevent you from enrolling in the course due to the exception that occurred
    public double getCreditsOfCourse(String courseCode) {
        try {
            PreparedStatement creditQuery = databaseConnection.prepareStatement("SELECT credits FROM course_catalog WHERE course_code = ?");
            creditQuery.setString(1, courseCode);
            ResultSet creditQueryResult  = creditQuery.executeQuery();
            boolean   querySuccessStatus = creditQueryResult.next();
            if (!querySuccessStatus) return 24;
            return creditQueryResult.getInt(1);
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return 24;
        }
    }

    // The minimum credit limit is set to 18 and the maximum is set to 24
    public double getCreditsInSession(String id, int currentYear, int currentSemester) {
        try {
            PreparedStatement creditQuery = databaseConnection.prepareStatement("SELECT SUM(credits) FROM course_catalog WHERE course_code IN (SELECT course_code FROM student_course_registration WHERE entry_number = ? AND year = ? AND semester = ?)");
            creditQuery.setString(1, id);
            creditQuery.setInt(2, currentYear);
            creditQuery.setInt(3, currentSemester);
            ResultSet creditQueryResult = creditQuery.executeQuery();

            boolean coursesFound = creditQueryResult.next();
            return (coursesFound) ? creditQueryResult.getInt(1) : 0;
        } catch (Exception error) {
            System.out.println(error.getMessage());
            // You should be setting the credit limit over here
            return 24;
        }
    }

    public boolean enroll(String courseCode, String id, int currentYear, int currentSemester) {
        try {
            PreparedStatement enrollmentQuery = databaseConnection.prepareStatement("INSERT INTO student_course_registration VALUES (?, ?, ?, ?)");
            enrollmentQuery.setString(1, id);
            enrollmentQuery.setString(2, courseCode);
            enrollmentQuery.setInt(3, currentYear);
            enrollmentQuery.setInt(4, currentSemester);
            // Maybe parameterize the default grade later on? Currently, the database will set the default grade by itself if you don't give it anything
            // Do I just assume that the call to insert was successful?
            enrollmentQuery.executeUpdate();
            return true;
        } catch (Exception error) {
            System.out.println("Enrollment Request Failed");
            return false;
        }
    }

    public boolean dropCourse(String courseCode, String id, int currentYear, int currentSemester) {
        try {
            PreparedStatement dropQuery = databaseConnection.prepareStatement("DELETE FROM student_course_registration WHERE course_code = ? AND entry_number = ? AND year = ? AND semester = ?");
            dropQuery.setString(1, courseCode);
            dropQuery.setString(2, id);
            dropQuery.setInt(3, currentYear);
            dropQuery.setInt(4, currentSemester);
            int dropQueryResult = dropQuery.executeUpdate();
            return dropQueryResult == 1;
        } catch (Exception error) {
            System.out.println("Database Error. Try again later");
            return false;
        }
    }

    public String[][] getGradesForSemester(String id, int year, int semester) {
        try {
            PreparedStatement gradeQuery = databaseConnection.prepareStatement("SELECT course_code, course_title, grade FROM student_course_registration NATURAL JOIN course_catalog WHERE entry_number = ? AND year = ? AND semester = ?");

            gradeQuery.setString(1, id);
            gradeQuery.setInt(2, year);
            gradeQuery.setInt(3, semester);
            ResultSet gradeQueryResult = gradeQuery.executeQuery();

            ArrayList<String[]> records = new ArrayList<>();
            while (gradeQueryResult.next()) {
                String courseCode  = gradeQueryResult.getString(1);
                String courseTitle = gradeQueryResult.getString(2);
                String grade       = gradeQueryResult.getString(3);
                records.add(new String[]{courseCode, courseTitle, grade});
            }
            return records.toArray(new String[records.size()][]);
        } catch (Exception error) {
            System.out.println("Database Error. Try again later");
            return new String[][]{};
        }
    }

    public int getBatch(String id) {
        try {
            PreparedStatement batchQuery = databaseConnection.prepareStatement("SELECT batch FROM student WHERE entry_number = ?");
            batchQuery.setString(1, id);
            ResultSet batchQueryResult = batchQuery.executeQuery();
            batchQueryResult.next();
            int batch = batchQueryResult.getInt(1);
            return batch;
        } catch (Exception error) {
            System.out.println("Database Error. Try again later");
            return -1;
        }
    }

    public String[][] getAllRecords(String id) {
        try {
            PreparedStatement recordsQuery = databaseConnection.prepareStatement("SELECT credits, grade FROM student_course_registration NATURAL JOIN course_catalog WHERE entry_number = ?");
            recordsQuery.setString(1, id);
            ResultSet recordsQueryResult = recordsQuery.executeQuery();

            ArrayList<String[]> records = new ArrayList<>();
            while (recordsQueryResult.next()) {
                String credits = recordsQueryResult.getString(1);
                String grade   = recordsQueryResult.getString(2);
                records.add(new String[]{credits, grade});
            }
            return records.toArray(new String[records.size()][]);
        } catch (Exception error) {
            System.out.println("Database Error. Try again later");
            return new String[][]{};
        }
    }
}
