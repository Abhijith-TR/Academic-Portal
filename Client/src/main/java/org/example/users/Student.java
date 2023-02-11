package org.example.users;

import org.example.dal.Database;
import org.example.dal.StudentDAO;
import org.example.utils.Utils;

public class Student extends User {
    public Student(String id) {
        super(id);
    }

    public boolean updateProfile(int newPhoneNumber, Database databaseConnection) {
        return databaseConnection.updatePhoneNumber("student", newPhoneNumber);
    }

    // The default cutoff of 4 is implemented here.
    private boolean checkCourseCatalogPrerequisites(String courseCode, StudentDAO studentDAO) {
        String[] prerequisites = studentDAO.getCourseCatalogPrerequisites(courseCode);
        if (prerequisites == null) return false;

        for (String course : prerequisites) {
            boolean hasPassed = studentDAO.checkStudentPassStatus(course, 4, id);
            if (!hasPassed) {
                return false;
            }
        }
        return true;
    }

    // A database error and no prerequisites will both return null. But student should not be allowed to enroll if there is a database error
    private boolean checkInstructorPrerequisites(String courseCode, StudentDAO studentDAO, int[] currentSession) {
        int        currentYear     = currentSession[0];
        int        currentSemester = currentSession[1];
        String[][] prerequisites   = studentDAO.getInstructorPrerequisites(courseCode, currentYear, currentSemester);
        if (prerequisites == null) return false;

        for (String[] listOfCourses : prerequisites) {
            boolean isEligible = false;
            for (int i = 0; i < listOfCourses.length; i += 2) {
                String course = listOfCourses[i];
                // PostgreSQL allows only matrices i.e., jagged arrays are not allowed. So some arrays might have additional useless entries.
                if (course.length() == 0) break;
                int gradeCutoff = Utils.getGradeValue(listOfCourses[i + 1]);
                isEligible |= studentDAO.checkStudentPassStatus(course, gradeCutoff, id);
            }
            if (!isEligible) return false;
        }
        return true;
    }

    private boolean checkStudentEligibility(String courseCode, StudentDAO studentDAO, int[] currentSession) {
        boolean hasCompletedCourseCatalogPrerequisites = checkCourseCatalogPrerequisites(courseCode, studentDAO);
        if (!hasCompletedCourseCatalogPrerequisites) {
            return false;
        }
        return checkInstructorPrerequisites(courseCode, studentDAO, currentSession);
    }

    private boolean checkCreditLimit(String courseCode, int[] currentSession, StudentDAO studentDAO) {
        int    currentYear        = currentSession[0];
        int    currentSemester    = currentSession[1];

        // We might want to store this in the database if necessary
        double minimumCreditLimit = 18;
        double maximumCreditLimit = 24;

        double creditsInCurrentSemester = studentDAO.getCreditsInSession(id, currentYear, currentSemester);
        double creditsInPreviousSemester, creditsInSemesterBefore;

        if (currentSemester == 2) {
            creditsInPreviousSemester = studentDAO.getCreditsInSession(id, currentYear, currentSemester - 1);
            creditsInSemesterBefore = studentDAO.getCreditsInSession(id, currentYear - 1, 2);
        } else {
            creditsInPreviousSemester = studentDAO.getCreditsInSession(id, currentYear - 1, 2);
            creditsInSemesterBefore = studentDAO.getCreditsInSession(id, currentYear - 1, 1);
        }

        double creditLimit = (creditsInSemesterBefore + creditsInPreviousSemester) / 2 * 1.25;
        creditLimit = Math.max(creditLimit, minimumCreditLimit);
        creditLimit = Math.min(creditLimit, maximumCreditLimit);

        double creditsOfCourse = studentDAO.getCreditsOfCourse(courseCode);
        return (creditsInCurrentSemester + creditsOfCourse) > creditLimit;
    }

    public String enroll(String courseCode, StudentDAO studentDAO) {
        int[] currentSession  = studentDAO.getCurrentAcademicSession();
        int   currentYear     = currentSession[0];
        int   currentSemester = currentSession[1];

        boolean doesCourseExist = studentDAO.checkCourseOffering(courseCode, currentYear, currentSemester);
        if (!doesCourseExist) return "Course Not Offered";
        // This checks if the course exists as well.
        boolean isStudentEligible   = checkStudentEligibility(courseCode, studentDAO, currentSession);
        if (!isStudentEligible) return "Student Ineligible for Course";
        boolean creditLimitExceeded = checkCreditLimit(courseCode, currentSession, studentDAO);
        if (creditLimitExceeded) return "Credit Limit Exceeded";

        boolean enrollmentRequestStatus = studentDAO.enroll(courseCode, this.id, currentYear, currentSemester);
        if (enrollmentRequestStatus == true) return "Enrolled Successfully";
        else return "Course Enrollment Failed";
    }

    public String drop(String courseCode, StudentDAO studentDAO) {
        int[] currentSession = studentDAO.getCurrentAcademicSession();
        int currentYear = currentSession[0];
        int currentSemester = currentSession[1];
        boolean courseDropStatus = studentDAO.dropCourse(courseCode, id, currentYear, currentSemester);
        if (courseDropStatus == true) return "Enrollment Dropped Successfully";
        else return "Could not find enrollment in current semester";
    }

    public void getGrades() {

    }

    public void getGrades(int year, int semester, StudentDAO studentDAO) {
        String[][] semesterGrades = studentDAO.getGradesForSemester(this.id, year, semester);
        Utils.prettyPrint(semesterGrades);
    }
}
