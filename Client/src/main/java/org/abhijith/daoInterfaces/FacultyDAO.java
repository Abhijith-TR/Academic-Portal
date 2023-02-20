package org.abhijith.daoInterfaces;

public interface FacultyDAO extends CommonDAO {
    boolean insertCourseOffering( String courseCode, int currentYear, int currentSemester, String departmentID, String facultyID );

    String getDepartment( String facultyID );

    boolean setCGCriteria( String facultyID, String courseCode, double minimumCGPA, int[] currentSession, String departmentID );

    boolean setInstructorPrerequisites( String departmentID, String courseCode, String[][] prerequisites, int[] currentSession );

    boolean dropCourseOffering( String facultyID, String courseCode, int currentYear, int currentSemester );

    boolean checkIfOfferedBySelf( String facultyID, String courseCode, int currentYear, int currentSemester );

    boolean setCourseCategory( String courseCode, int currentYear, int currentSemester, String courseCategory, String department, int[] years, String facultyDepartment );

    boolean verifyCore( String courseCode, String departmentID, int year );

    String[][] getGradesOfCourse( String courseCode, int year, int semester );

    String[][] getCourseEnrollmentsList( String courseCode, int year, int semester );

    String[] getListOfStudents( String courseCode, int year, int semester );

    boolean isCurrentEventOffering( int currentYear, int currentSemester );

    boolean isCurrentEventGradeSubmission( int currentYear, int currentSemester );

    boolean uploadCourseGrades( String courseCode, int year, int semester, String[] listOfStudents, String[] listOfGrades );

    boolean isCourseAlreadyOffered( String courseCode, int currentYear, int currentSemester, String departmentID );
}
