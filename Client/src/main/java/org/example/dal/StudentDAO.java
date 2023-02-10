package org.example.dal;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDAO extends Database{
    public StudentDAO(String connectionURL, String username, String password) {
        super(connectionURL, username, password);
    }

    // Returns the current year and semester if successful
    // Otherwise returns an empty array
    public int[] getCurrentAcademicSession() {
        try {
            PreparedStatement getSessionQuery = databaseConnection.prepareStatement("SELECT * FROM current_year_and_semester");
            ResultSet currentSession = getSessionQuery.executeQuery();

            currentSession.next();
            int currentYear = currentSession.getInt(1);
            int currentSemester = currentSession.getInt(2);

            return new int[]{currentYear, currentSemester};
        } catch (SQLException error) {
            System.err.println(error.getMessage());
        }
        return new int[]{};
    }

    public boolean checkCourseOffering(String courseCode) {
        try {
            int[] currentAcademicSession = getCurrentAcademicSession();
            int currentYear = currentAcademicSession[0];
            int currentSemester = currentAcademicSession[1];

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
            boolean studentCredited = courseQueryResult.next();
            if (studentCredited == false) {
                return false;
            }
            System.out.println(courseQueryResult.getString(1));
        } catch (Exception error) {
            System.err.println(error.getMessage());
        }
        return true;
    }
    public boolean checkCourseCatalogPrerequisites(String courseCode, String id) {
        try {
            PreparedStatement prerequisiteQuery = databaseConnection.prepareStatement("SELECT pre_requisites FROM course_catalog WHERE course_code = ?");
            prerequisiteQuery.setString(1, courseCode);
            ResultSet courseCatalogResult = prerequisiteQuery.executeQuery();
            courseCatalogResult.next();
            Array temp = courseCatalogResult.getArray(1);
            String[] prerequisites = (String[])temp.getArray();

            for (String course : prerequisites) {
                checkStudentPassStatus(course, 0, id);
            }

            return true;
        } catch (SQLException error) {
            System.out.println(error.getMessage());
            return false;
        }
    }
    public boolean checkStudentEligibility(String courseCode, String id) {
        try {
            boolean hasCompletedCourseCatalogPrerequisites = checkCourseCatalogPrerequisites(courseCode, id);
            if (hasCompletedCourseCatalogPrerequisites == false) {
                return false;
            }
//            boolean hasCompletedInstructorPrerequisites = checkInstructorPrerequisites(courseCode);
            return true;
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return false;
        }
    }
//    public boolean enroll(String courseCode) {
//        try {
//            boolean doesCourseExist = checkCourseOffering(courseCode);
//            if (doesCourseExist == false) {
//                return false;
//            }
//
//        } catch (SQLException error) {
//            System.out.println(error.getMessage());
//            return false;
//        }
//    }
}
