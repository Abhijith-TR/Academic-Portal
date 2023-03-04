package org.abhijith.daoInterfaces;

public interface AdminDAO extends CommonDAO {
    String[][] getGradesOfCourse(String courseCode, int year, int semester, String offeringDepartment );

    boolean insertFaculty(String facultyID, String name, String departmentID);
    boolean insertStudent(String entryNumber, String name, String departmentID, int batch);
    boolean insertCourse(String courseCode, String courseTitle, double[] creditStructure, String[] prerequisites);

    boolean checkAllPrerequisites(String[] prerequisites);

    boolean dropCourseFromCatalog(String courseCode);

    boolean createBatch(int batchYear);

    boolean createCurriculum(int batchYear, double[] creditRequirements);

    boolean insertCoreCourse( String courseCode, String[] departmentCodes, int batch, String courseCategory );

    boolean resetCoreCoursesList( int batch );

    boolean findEntryNumber( String entryNumber );

    String[] getCoreCourses( int batch, String studentDepartment );

    String[] getListOfStudentsInBatch( int batch, String department );

    boolean checkIfSessionCompleted( int year, int semester );

    boolean createNewSession( int newYear, int newSemester );

    boolean setSessionEvent( String event, int currentYear, int currentSemester );

    boolean verifyNoMissingGrades( int currentYear, int currentSemester );
}
