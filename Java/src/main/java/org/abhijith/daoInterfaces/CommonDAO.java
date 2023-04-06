package org.abhijith.daoInterfaces;

import java.util.HashMap;

public interface CommonDAO {
    // Returns a list of student grades in the form { { Course Code, Course Title, Grade, Credits } }
    String[][] getStudentGradesForSemester( String entryNumber, int year, int semester );

    // Returns the current year and semester if successful
    // Otherwise returns default year and session i.e., { 2020, 1 }
    int[] getCurrentAcademicSession();

    // Returns the batch of the mentioned student. -1 if the student is not found
    int getBatch( String entryNumber );

    // Returns true if the course code is found in the catalog. False otherwise
    boolean checkCourseCatalog( String courseCode );

    // Returns the department of the student. Returns "" for invalid arguments and errors
    String getStudentDepartment( String entryNumber );

    // Returns the UG Curriculum for the corresponding batch if successful. Returns null for invalid arguments and errors
    HashMap<String, Double> getUGCurriculum( int batch );

    // Returns the grade of the student. Returns "" for invalid arguments and errors. Returns "F" if no course is found
    String getCourseGrade( String entryNumber, String courseCode );

    // Returns the number of credits that the student has completed in each of the categories
    // Returns null for invalid arguments and errors
    HashMap<String, Double> getCreditsInAllCategories( String entryNumber );

    // Returns true if the phone number is successfully set. False otherwise
    boolean setPhoneNumber( String id, String phoneNumber );

    // Returns true if the email is successfully set. False otherwise
    boolean setEmail( String id, String email );

    // Returns the { Email, Phone Number } of the ID if found. Returns empty array for invalid arguments and errors
    String[] getContactDetails( String userID );

    // Returns true if the password was set successfully. False otherwise
    boolean setPassword( String id, String password );

    // Returns the course catalog in the form { { Course Code, Course Title, L, T, P, S, C, Prerequisites } }. Empty array if not found or errors
    String[][] getCourseCatalog();
}
