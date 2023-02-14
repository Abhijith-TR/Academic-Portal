package org.example.daoInterfaces;

public interface StudentDAO extends CommonDAO {

    boolean checkCourseOffering( String courseCode, int currentYear, int currentSemester );

    boolean checkStudentPassStatus( String courseCode, int gradeCutoff, String entryNumber );

    String[] getCourseCatalogPrerequisites( String courseCode );

    String[][] getInstructorPrerequisites( String courseCode, int year, int semester );

    // The 24 is being returned to prevent you from enrolling in the course due to the exception that occurred
    double getCreditsOfCourse( String courseCode );

    // The minimum credit limit is set to 18 and the maximum is set to 24
    double getCreditsInSession( String entryNumber, int currentYear, int currentSemester );

    boolean enroll( String courseCode, String entryNumber, int currentYear, int currentSemester );

    boolean dropCourse( String courseCode, String entryNumber, int currentYear, int currentSemester );

    String[][] getAllRecords( String entryNumber );

    String[][] getOfferedCourses( int currentYear, int currentSemester );

    String getCourseGrade( String entryNumber, String courseCode );
}
