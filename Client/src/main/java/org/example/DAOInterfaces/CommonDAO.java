package org.example.daoInterfaces;

public interface CommonDAO {
    String[][] getStudentGradesForSemester( String entryNumber, int year, int semester );

    // Returns the current year and semester if successful
    // Otherwise returns default year and session i.e., 2020-1
    int[] getCurrentAcademicSession();

    int getBatch( String entryNumber );

    boolean checkCourseCatalog( String courseCode );
}
