package org.abhijith.daoInterfaces;

public interface FacultyDAO extends CommonDAO {
    boolean insertCourseOffering( String courseCode, int currentYear, int currentSemester, String departmentID, String facultyID );

    String getDepartment( String facultyID );

    boolean setCGCriteria( String facultyID, String courseCode, double minimumCGPA, int[] currentSession, String departmentID );

    boolean setInstructorPrerequisites( String departmentID, String courseCode, String[][] prerequisites, int[] currentSession );

    boolean dropCourseOffering( String facultyID, String courseCode, int currentYear, int currentSemester, String departmentID );

    boolean checkIfOfferedBySelf( String facultyID, String courseCode, int currentYear, int currentSemester, String departmentID );

    boolean setCourseCategory( String courseCode, int currentYear, int currentSemester, String courseCategory, String department, int[] years, String offeringDepartment );

    boolean verifyCore( String courseCode, String departmentID, int year );

    String[][] getGradesOfCourse( String courseCode, int year, int semester, String departmentID );

    String[][] getCourseEnrollmentsList( String courseCode, int year, int semester, String offeringDepartment );

    String[] getListOfStudents( String courseCode, int year, int semester, String offeringDepartment );

    boolean isCurrentEventOffering( int currentYear, int currentSemester );

    boolean isCurrentEventGradeSubmission( int currentYear, int currentSemester );

    boolean uploadCourseGrades( String courseCode, int year, int semester, String departmentID, String[] listOfStudents, String[] listOfGrades );

    boolean isCourseAlreadyOffered( String courseCode, int currentYear, int currentSemester, String offeringDepartment );

    String[][] getInstructorPrerequisites( String courseCode, int year, int semester, String departmentID );
}
