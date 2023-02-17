package org.example.daoInterfaces;

import java.util.HashMap;

public interface CommonDAO {
    String[][] getStudentGradesForSemester( String entryNumber, int year, int semester );

    // Returns the current year and semester if successful
    // Otherwise returns default year and session i.e., 2020-1
    int[] getCurrentAcademicSession();

    int getBatch( String entryNumber );

    String getCurrentEvent();

    boolean checkCourseCatalog( String courseCode );

    String getStudentDepartment( String entryNumber );

    HashMap<String, Double> getUGCurriculum( int batch );

    String getCourseGrade( String entryNumber, String courseCode );

    HashMap<String, Double> getCreditsInAllCategories( String entryNumber );
}
