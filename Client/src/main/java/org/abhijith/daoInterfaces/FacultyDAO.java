package org.abhijith.daoInterfaces;

public interface FacultyDAO extends CommonDAO {
    // Returns true if the offering was successfully inserted. False otherwise
    boolean insertCourseOffering( String courseCode, int currentYear, int currentSemester, String departmentID, String facultyID );

    // Returns the department ID of the corresponding faculty. Returns "" on invalid arguments or errors
    String getDepartment( String facultyID );

    // Returns true if the CG criteria was successfully set. False otherwise
    boolean setCGCriteria( String facultyID, String courseCode, double minimumCGPA, int[] currentSession, String departmentID );

    // Returns true if the prerequisites was set successfully. False otherwise
    boolean setInstructorPrerequisites( String departmentID, String courseCode, String[][] prerequisites, int[] currentSession );

    // Returns true if the course offering was dropped. False otherwise
    boolean dropCourseOffering( String facultyID, String courseCode, int currentYear, int currentSemester, String departmentID );

    // Returns true if the course is offered by this instructor himself. False otherwise
    boolean checkIfOfferedBySelf( String facultyID, String courseCode, int currentYear, int currentSemester, String departmentID );

    // Returns true if the category is successfully set. False otherwise
    boolean setCourseCategory( String courseCode, int currentYear, int currentSemester, String courseCategory, String department, int[] years, String offeringDepartment );

    // Returns true if the course is core for the mentioned department and year. False otherwise
    boolean verifyCore( String courseCode, String departmentID, int year );

    // Returns the grades of all students in the course in the form { { Entry Number, Grade } }. Returns empty array on errors and invalid arguments
    String[][] getGradesOfCourse( String courseCode, int year, int semester, String departmentID );

    // Returns the entry numbers and names of all students in the particular course in the form { { Name, Entry Number } }. Returns empty array on errors and invalid arguments
    String[][] getCourseEnrollmentsList( String courseCode, int year, int semester, String offeringDepartment );

    // Returns the entry numbers of all students in particular course in the form { { Name, Entry Number } }. Returns empty array on invalid arguments and errors
    String[] getListOfStudents( String courseCode, int year, int semester, String offeringDepartment );

    // Returns true if the current event is offering. False otherwise
    boolean isCurrentEventOffering( int currentYear, int currentSemester );

    // Returns true if the current event is grade submission. False otherwise
    boolean isCurrentEventGradeSubmission( int currentYear, int currentSemester );

    // Returns true if all the grades were successfully inserted into the database. False otherwise
    boolean uploadCourseGrades( String courseCode, int year, int semester, String departmentID, String[] listOfStudents, String[] listOfGrades );

    // Returns true if the course has already been offered by this particular department. False otherwise
    boolean isCourseAlreadyOffered( String courseCode, int currentYear, int currentSemester, String offeringDepartment );

    // Returns all the prerequisites of this particular course in the form { { Prerequisite, Grade Criteria } }. Returns empty array on errors and invalid arguments
    String[][] getInstructorPrerequisites( String courseCode, int year, int semester, String departmentID );
}
