package org.example.dal;

public interface AdminDAO {
    String[][] getGradesOfCourse(String courseCode, int year, int semester);

    boolean insertFaculty(String facultyID, String name, String departmentID);
    boolean insertStudent(String entryNumber, String name, String departmentID, int batch);
}
