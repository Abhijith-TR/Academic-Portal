package org.example.users;

import org.example.dal.StudentDAO;
import org.example.utils.Utils;

public class Student extends User {
    StudentDAO studentDAO;

    public Student(String id, StudentDAO studentDAO) {
        super(id);
        this.studentDAO = studentDAO;
    }

    public boolean updateProfile(int newPhoneNumber) {
        return true;
    }

    // The default cutoff of 4 is implemented here.
    private boolean checkCourseCatalogPrerequisites(String courseCode) {
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
    private boolean checkInstructorPrerequisites(String courseCode, int[] currentSession) {
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

    private boolean checkStudentEligibility(String courseCode, int[] currentSession) {
        boolean hasCompletedCourseCatalogPrerequisites = checkCourseCatalogPrerequisites(courseCode);
        if (!hasCompletedCourseCatalogPrerequisites) {
            return false;
        }
        return checkInstructorPrerequisites(courseCode, currentSession);
    }

    private boolean checkCreditLimit(String courseCode, int[] currentSession) {
        int currentYear     = currentSession[0];
        int currentSemester = currentSession[1];

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

    public String enroll(String courseCode) {
        int[] currentSession  = studentDAO.getCurrentAcademicSession();
        int   currentYear     = currentSession[0];
        int   currentSemester = currentSession[1];

        boolean doesCourseExist = studentDAO.checkCourseOffering(courseCode, currentYear, currentSemester);
        if (!doesCourseExist) return "Course Not Offered";
        // This checks if the course exists as well.
        boolean isStudentEligible = checkStudentEligibility(courseCode, currentSession);
        if (!isStudentEligible) return "Student Ineligible for Course";
        boolean creditLimitExceeded = checkCreditLimit(courseCode, currentSession);
        if (creditLimitExceeded) return "Credit Limit Exceeded";

        boolean enrollmentRequestStatus = studentDAO.enroll(courseCode, this.id, currentYear, currentSemester);
        if (enrollmentRequestStatus) return "Enrolled Successfully";
        else return "Course Enrollment Failed";
    }

    public String drop(String courseCode) {
        int[]   currentSession   = studentDAO.getCurrentAcademicSession();
        int     currentYear      = currentSession[0];
        int     currentSemester  = currentSession[1];
        boolean courseDropStatus = studentDAO.dropCourse(courseCode, id, currentYear, currentSemester);
        if (courseDropStatus) return "Enrollment Dropped Successfully";
        else return "Could not find enrollment in current semester";
    }

    public void getGrades() {
        int[] currentSession  = studentDAO.getCurrentAcademicSession();
        int   currentYear     = currentSession[0];
        int   currentSemester = currentSession[1];
        int   studentBatch    = studentDAO.getBatch(this.id);
        for (int year = studentBatch; year <= currentYear; year++) {
            for (int semester = 1; semester <= 2; semester++) {
                getGrades(year, semester);
                if (year == currentYear && semester == currentSemester) break;
            }
        }
    }

    public void getGrades(int year, int semester) {
        String[][] semesterGrades = studentDAO.getGradesForSemester(this.id, year, semester);
        if (semesterGrades.length == 0) {
            System.out.printf("No records found for session %d-%d\n\n", year, semester);
            return;
        }
        Utils.prettyPrintGrades(year, semester, semesterGrades);
    }

    public double getCGPA() {
        String[][] records       = studentDAO.getAllRecords(this.id);
        double     totalCredits  = 0;
        double     creditsEarned = 0;

        for (String[] record : records) {
            double credits        = Double.parseDouble(record[0]);
            int    gradeToCredits = Utils.getGradeValue(record[1]);
            totalCredits += credits;
            creditsEarned += (gradeToCredits) / 10.0 * credits;
        }
        return creditsEarned / totalCredits * 10;
    }
}
