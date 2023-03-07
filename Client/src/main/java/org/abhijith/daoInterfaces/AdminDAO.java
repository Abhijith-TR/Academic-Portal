package org.abhijith.daoInterfaces;

public interface AdminDAO extends CommonDAO {
    // Returns an array of arrays in the format { { Entry Number, Grade } }. Returns [][] if any error occurs
    String[][] getGradesOfCourse(String courseCode, int year, int semester, String offeringDepartment );

    // Returns true if the insertion is successful, false otherwise
    boolean insertFaculty(String facultyID, String name, String departmentID);

    // Returns true if the insertion is successful, false otherwise
    boolean insertStudent(String entryNumber, String name, String departmentID, int batch);

    // Returns true if the insertion is successful, false otherwise
    boolean insertCourse(String courseCode, String courseTitle, double[] creditStructure, String[] prerequisites);

    // Returns true if all the prerequisites are found in the course catalog, false otherwise
    boolean checkAllPrerequisites(String[] prerequisites);

    // Returns true if the course was dropped from the catalog. False otherwise
    boolean dropCourseFromCatalog(String courseCode);

    // Returns true if the batch was created, false otherwise
    boolean createBatch(int batchYear);

    // Returns true if the curriculum was created, false otherwise
    boolean createCurriculum(int batchYear, double[] creditRequirements);

    // Returns true if all the courses were inserted, false otherwise
    boolean insertCoreCourse( String courseCode, String[] departmentCodes, int batch, String courseCategory );

    // Returns true if the core courses were deleted from the database, false otherwise
    boolean resetCoreCoursesList( int batch );

    // Returns true if the entry number exists. False otherwise
    boolean findEntryNumber( String entryNumber );

    // Returns a list of core courses as an array of course codes. Returns null for invalid arguments and errors
    String[] getCoreCourses( int batch, String studentDepartment );

    // Returns a list of entry numbers in the form of an array. Returns null for invalid arguments and errors
    String[] getListOfStudentsInBatch( int batch, String department );

    // Returns true if the session has been marked as completed, false otherwise
    boolean checkIfSessionCompleted( int year, int semester );

    // Returns true if a new session is created, False otherwise
    boolean createNewSession( int newYear, int newSemester );

    // Returns true if the mentioned event could be set, False otherwise
    boolean setSessionEvent( String event, int currentYear, int currentSemester );

    // Returns true if there are no unentered grades in the mentioned year and semester, False otherwise
    boolean verifyNoMissingGrades( int currentYear, int currentSemester );
}
