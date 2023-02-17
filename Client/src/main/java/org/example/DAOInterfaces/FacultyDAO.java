package org.example.daoInterfaces;

public interface FacultyDAO extends CommonDAO {
    boolean insertCourseOffering( String courseCode, int currentYear, int currentSemester, String departmentID, String facultyID );

    String getDepartment( String id );

    boolean setCGCriteria( String facultyID, String courseCode, double minimumCGPA, int[] currentSession );

    boolean setInstructorPrerequisites( String facultyID, String courseCode, String[][] prerequisites, int[] currentSession );

    boolean dropCourseOffering( String facultyID, String courseCode, int currentYear, int currentSemester );

    boolean checkIfOfferedBySelf( String facultyID, String courseCode, int currentYear, int currentSemester );

    boolean setCourseCategory( String courseCode, int currentYear, int currentSemester, String courseCategory, String department, int[] years );

    boolean verifyCore( String courseCode, String departmentID, int year );

    String[][] getGradesOfCourse( String courseCode, int year, int semester );

    String[][] getCourseEnrollmentsList( String courseCode, int year, int semester );

    String[] getListOfStudents( String courseCode, int year, int semester );

    boolean uploadCourseGrades( String courseCode, int year, int semester, String[] listOfStudents, String[] listOfGrades );
}
