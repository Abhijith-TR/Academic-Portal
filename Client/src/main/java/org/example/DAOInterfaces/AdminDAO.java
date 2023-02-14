package org.example.daoInterfaces;

public interface AdminDAO extends CommonDAO {
    String[][] getGradesOfCourse(String courseCode, int year, int semester);

    boolean insertFaculty(String facultyID, String name, String departmentID);
    boolean insertStudent(String entryNumber, String name, String departmentID, int batch);
    boolean insertCourse(String courseCode, String courseTitle, double[] creditStructure, String[] prerequisites, String departmentID);

    boolean checkAllPrerequisites(String[] prerequisites);

    boolean dropCourseFromCatalog(String courseCode);

    boolean createBatch(int batchYear);

    boolean createCurriculum(int batchYear, double[] creditRequirements);
}
