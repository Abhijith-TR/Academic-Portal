package org.example.users;

import org.example.daoInterfaces.AdminDAO;

import java.util.ArrayList;

public class Admin extends User {
    AdminDAO adminDAO;

    public Admin(String name, AdminDAO adminDAO) {
        super(name);
        this.adminDAO = adminDAO;
    }

    public String[][] getGradesOfOffering(String courseCode, int year, int semester) {
        return adminDAO.getGradesOfCourse(courseCode, year, semester);
    }

    public boolean insertStudent(String entryNumber, String name, String departmentID, int batch) {
        return adminDAO.insertStudent(entryNumber, name, departmentID, batch);
    }

    public boolean insertFaculty(String facultyID, String name, String departmentID) {
        return adminDAO.insertFaculty(facultyID, name, departmentID);
    }

    public boolean insertCourseIntoCatalog(String courseCode, String courseTitle, double[] creditStructure, String[] prerequisites, String departmentID) {
        boolean allPrerequisitesFound = adminDAO.checkAllPrerequisites(prerequisites);
        if (allPrerequisitesFound == false) return false;
        return adminDAO.insertCourse(courseCode, courseTitle, creditStructure, prerequisites, departmentID);
    }

    public boolean dropCourseFromCatalog(String courseCode) {
        return adminDAO.dropCourseFromCatalog(courseCode);
    }

    public String[][][] getGradesOfStudent(String entryNumber) {
        ArrayList<String[][]> completeStudentRecords = new ArrayList<>();
        int[]        currentAcademicSession = adminDAO.getCurrentAcademicSession();
        int          studentBatch           = adminDAO.getBatch(entryNumber);
        int          currentYear            = currentAcademicSession[0];
        int          currentSemester        = currentAcademicSession[1];

        int i = 0;
        for (int year = studentBatch; year <= currentYear; year++) {
            for (int semester = 1; semester <= 2; semester++) {
                String[][] semesterRecords = adminDAO.getStudentGradesForSemester(entryNumber, year, semester);
                completeStudentRecords.add(semesterRecords);
                i++;
                if (year == currentYear && semester == currentSemester) break;
            }
        }
        return completeStudentRecords.toArray(new String[completeStudentRecords.size()][][]);
    }

    public boolean createBatch(int batchYear, double[] creditRequirements) {
        boolean createBatchStatus = adminDAO.createBatch(batchYear);
        if (!createBatchStatus) return false;
        return adminDAO.createCurriculum(batchYear, creditRequirements);
    }
}
