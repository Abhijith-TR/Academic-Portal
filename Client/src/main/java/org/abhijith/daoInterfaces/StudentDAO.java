package org.abhijith.daoInterfaces;

public interface StudentDAO extends CommonDAO {
    // Returns true if the course has been offered in the current semester. False otherwise
    boolean checkCourseOffering( String courseCode, int currentYear, int currentSemester, String courseDepartment );

    // Returns true if the student has passed in a particular course with a grade >= the cutoff. False otherwise
    boolean checkStudentPassStatus( String courseCode, int gradeCutoff, String entryNumber );

    // Returns the prerequisites in the course catalog in the form as an array of course codes. Null on invalid arguments and errors
    String[] getCourseCatalogPrerequisites( String courseCode );

    // Returns the prerequisites of the instructor in the form { { Course Code, Cutoff, Course Code, ... } } where each entry in a particular array represents an OR of courses and every other array represents an AND condition
    // Returns null if arguments are invalid or error occurs
    String[][] getInstructorPrerequisites( String courseCode, int year, int semester, String courseDepartment );

    // Returns the credits of the corresponding course if the credits are found. Returns 25 is the course is not found as the maximum limit is 24
    double getCreditsOfCourse( String courseCode );

    // Returns the credits that the student has already enrolled in the current semester. Returns 25 to signify credit limit exceed on errors or invalid arguments
    double getCreditsInSession( String entryNumber, int currentYear, int currentSemester );

    // Returns true if the enrollment was successful. False otherwise
    boolean enroll( String courseCode, String entryNumber, int currentYear, int currentSemester, String offeringDepartment, String courseCategory );

    // Returns true if the course was dropped. False otherwise
    boolean dropCourse( String courseCode, String entryNumber, int currentYear, int currentSemester );

    // Returns an array of all records of this particular student in the form { { Credits, Grade } }. Empty array if the arguments are invalid or error occurs
    String[][] getAllRecords( String entryNumber );

    // Returns all the courses offered in the current semester in the form { { Course Code, Course Title, Faculty Name, Pre requisites, Offering Department, ""} }. Returns empty array on invalid arguments or errors
    String[][] getOfferedCourses( int currentYear, int currentSemester );

    // Returns the category that the course is for this particular batch and department. Returns "" on invalid arguments or errors
    String getCourseCategory( String courseCode, int year, int semester, String courseDepartment, String studentDepartment, int batch );

    // Returns true if the current event is enrolling. False otherwise
    boolean isCurrentEventEnrolling( int currentYear, int currentSemester );

    // Returns the CGPA criteria of the particular course. Returns 11 on error to prevent enrollment on invalid arguments or errors
    double getCGPACriteria( String courseCode, int currentYear, int currentSemester, String courseDepartment );
}
