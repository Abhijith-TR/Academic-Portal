package org.abhijith.users;

import org.abhijith.dal.PostgresAdminDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.StringReader;
import java.security.InvalidParameterException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AdminTest {
    Admin            admin = new Admin( "ADMIN1" );
    PostgresAdminDAO mockDAO;

    @BeforeEach
    void setUp() {
        mockDAO = Mockito.mock( PostgresAdminDAO.class );
        admin.setAdminDAO( mockDAO );
    }

    @Test
    void testConstructor() {
        Admin admin = new Admin( null );
    }

    @Test
    void getGradesOfOffering() {
        String     courseCode     = "CS301";
        int        year           = 2023;
        int        semester       = 2;
        String     departmentID   = "HS";
        String[][] expectedResult = new String[][]{ { "2020CSB1062", "A" } };

        // Returns empty array due to invalid arguments
        assertArrayEquals( new String[][]{}, admin.getGradesOfOffering( null, year, semester, departmentID ) );
        assertArrayEquals( new String[][]{}, admin.getGradesOfOffering( courseCode, -1, semester, departmentID ) );
        assertArrayEquals( new String[][]{}, admin.getGradesOfOffering( courseCode, year, -1, departmentID ) );
        assertArrayEquals( new String[][]{}, admin.getGradesOfOffering( courseCode, year, semester, null ) );

        // If the details are valid this function works as expected ( Mock object returns null and the functions returns this as expected )
        assertNull( admin.getGradesOfOffering( courseCode, year, semester, departmentID ) );

        when( mockDAO.getGradesOfCourse( courseCode, year, semester, departmentID ) ).thenReturn( expectedResult );
        assertArrayEquals( expectedResult, admin.getGradesOfOffering( courseCode, year, semester, departmentID ) );
    }

    @Test
    void insertStudent() {
        String entryNumber  = "2020CSB1062";
        String name         = "ABHIJITH";
        String departmentID = "CS";
        int    batch        = 2020;

        // Returns false as the arguments are invalid
        assertFalse( admin.insertStudent( null, name, departmentID, batch ) );
        assertFalse( admin.insertStudent( entryNumber, null, departmentID, batch ) );
        assertFalse( admin.insertStudent( entryNumber, name, null, batch ) );
        assertFalse( admin.insertStudent( entryNumber, name, departmentID, -1 ) );

        // Returns false if the DAO returns false
        when( mockDAO.insertStudent( entryNumber, name, departmentID, batch ) ).thenReturn( false );
        assertFalse( admin.insertStudent( entryNumber, name, departmentID, batch ) );

        // Returns true if the DAO returns true
        when( mockDAO.insertStudent( entryNumber, name, departmentID, batch ) ).thenReturn( true );
        assertTrue( admin.insertStudent( entryNumber, name, departmentID, batch ) );
    }

    @Test
    void insertFaculty() {
        String facultyID    = "FAC1";
        String name         = "SURESH";
        String departmentID = "CS";

        // False due to invalid input arguments
        assertFalse( admin.insertFaculty( null, name, departmentID ) );
        assertFalse( admin.insertFaculty( facultyID, null, departmentID ) );
        assertFalse( admin.insertFaculty( facultyID, name, null ) );

        // False if the DAO returns false
        when( mockDAO.insertFaculty( facultyID, name, departmentID ) ).thenReturn( false );
        assertFalse( admin.insertFaculty( facultyID, name, departmentID ) );

        // True if the DAO returns true
        when( mockDAO.insertFaculty( facultyID, name, departmentID ) ).thenReturn( true );
        assertTrue( admin.insertFaculty( facultyID, name, departmentID ) );
    }

    @Test
    void insertCourseIntoCatalog() {
        String   courseCode      = "HS999";
        String   courseTitle     = "RANDOM";
        double[] creditStructure = new double[]{ 3, 1, 0, 5, 3 };
        String[] prerequisites   = new String[]{ "CS101" };

        // False due to invalid input arguments
        assertFalse( admin.insertCourseIntoCatalog( null, courseTitle, creditStructure, prerequisites ) );
        assertFalse( admin.insertCourseIntoCatalog( courseCode, null, creditStructure, prerequisites ) );
        assertFalse( admin.insertCourseIntoCatalog( courseCode, courseTitle, null, prerequisites ) );
        assertFalse( admin.insertCourseIntoCatalog( courseCode, courseTitle, creditStructure, null ) );

        // False because the course prerequisites are invalid
        when( mockDAO.checkAllPrerequisites( new String[]{ "CS101" } ) ).thenReturn( false );
        assertFalse( admin.insertCourseIntoCatalog( courseCode, courseTitle, creditStructure, prerequisites ) );

        // False because the DAO returned false
        when( mockDAO.checkAllPrerequisites( new String[]{ "CS101" } ) ).thenReturn( true );
        when( mockDAO.insertCourse( courseCode, courseTitle, creditStructure, prerequisites ) ).thenReturn( false );
        assertFalse( admin.insertCourseIntoCatalog( courseCode, courseTitle, creditStructure, prerequisites ) );

        // Returns true because both request to the DAO returned true
        when( mockDAO.insertCourse( courseCode, courseTitle, creditStructure, prerequisites ) ).thenReturn( true );
        assertTrue( admin.insertCourseIntoCatalog( courseCode, courseTitle, creditStructure, prerequisites ) );
    }

    @Test
    void dropCourseFromCatalog() {
        String courseCode = "HS301";

        // False because of invalid argument
        assertFalse( admin.dropCourseFromCatalog( null ) );

        // False because the DAO returns false
        when( mockDAO.dropCourseFromCatalog( courseCode ) ).thenReturn( false );
        assertFalse( admin.dropCourseFromCatalog( courseCode ) );

        // True because the DAO returns true
        when( mockDAO.dropCourseFromCatalog( courseCode ) ).thenReturn( true );
        assertTrue( admin.dropCourseFromCatalog( courseCode ) );
    }

    @Test
    void getGradesOfStudent() {
        String       entryNumber            = "2020CSB1062";
        int          batch                  = 2020;
        int[]        currentYearAndSemester = new int[]{ 2020, 1 };
        int          year                   = 2020;
        int          semester               = 1;
        String[][]   semesterRecords        = new String[][]{ { "CS101", "DISCRETE MATHEMATICS", "A", "4.0" } };
        String[][][] expectedResult         = new String[][][]{ semesterRecords };

        // Empty because of invalid input argument
        assertArrayEquals( new String[][][]{}, admin.getGradesOfStudent( null ) );

        // Empty because the studentBatch was -1
        when( mockDAO.getCurrentAcademicSession() ).thenReturn( currentYearAndSemester );
        when( mockDAO.getBatch( entryNumber ) ).thenReturn( -1 );
        assertArrayEquals( new String[][][]{}, admin.getGradesOfStudent( entryNumber ) );

        // The batch is now correct
        when( mockDAO.getBatch( entryNumber ) ).thenReturn( batch );
        when( mockDAO.getStudentGradesForSemester( entryNumber, year, semester ) ).thenReturn( semesterRecords );
        assertArrayEquals( expectedResult, admin.getGradesOfStudent( entryNumber ) );
    }

    @Test
    void createBatch() {
        int      batch              = 2022;
        double[] creditRequirements = new double[]{ 24, 6, 23.5, 36, 12, 15, 6, 9, 3.5, 4, 6 };

        // False because of invalid input arguments
        assertFalse( admin.createBatch( -1, creditRequirements ) );
        assertFalse( admin.createBatch( batch, null ) );

        // False because batch cannot be created
        when( mockDAO.createBatch( batch ) ).thenReturn( false );
        assertFalse( admin.createBatch( batch, creditRequirements ) );

        // False because ug curriculum was not inserted
        when( mockDAO.createBatch( batch ) ).thenReturn( true );
        when( mockDAO.createCurriculum( batch, creditRequirements ) ).thenReturn( false );
        assertFalse( admin.createBatch( batch, creditRequirements ) );

        // True because all the parameters return true
        when( mockDAO.createCurriculum( batch, creditRequirements ) ).thenReturn( true );
        assertTrue( admin.createBatch( batch, creditRequirements ) );
    }

    @Test
    void insertCoreCourses() {
        int            batch           = 2020;
        String         fileContents    = "CS101,PC,CS\n";
        BufferedReader bufferedReader;
        String         courseCode      = "CS101";
        String         courseCategory  = "PC";
        String[]       departmentCodes = new String[]{ "CS" };

        try {
            bufferedReader = new BufferedReader( new StringReader( fileContents ) );

            // False because of invalid input arguments
            assertFalse( admin.insertCoreCourses( -1, bufferedReader ) );
            assertFalse( admin.insertCoreCourses( batch, null ) );

            // False because previous core courses could not be dropped
            when( mockDAO.resetCoreCoursesList( batch ) ).thenReturn( false );
            assertFalse( admin.insertCoreCourses( batch, bufferedReader ) );

            // False because the course does not exist
            when( mockDAO.resetCoreCoursesList( batch ) ).thenReturn( true );
            when( mockDAO.checkCourseCatalog( courseCode ) ).thenReturn( false );
            assertFalse( admin.insertCoreCourses( batch, bufferedReader ) );

            // False because the course could not be inserted
            bufferedReader = new BufferedReader( new StringReader( fileContents ) );
            when( mockDAO.checkCourseCatalog( courseCode ) ).thenReturn( true );
            when( mockDAO.insertCoreCourse( courseCode, departmentCodes, batch, courseCategory ) ).thenReturn( false );
            assertFalse( admin.insertCoreCourses( batch, bufferedReader ) );

            // True because everything so far has been successful
            bufferedReader = new BufferedReader( new StringReader( fileContents ) );
            when( mockDAO.insertCoreCourse( courseCode, departmentCodes, batch, courseCategory ) ).thenReturn( true );
            assertTrue( admin.insertCoreCourses( batch, bufferedReader ) );

            // False due to an exception
            when( mockDAO.resetCoreCoursesList( batch ) ).thenThrow( new InvalidParameterException() );
            assertFalse( admin.insertCoreCourses( batch, bufferedReader ) );
        } catch ( Exception error ) {
            System.out.println( error.getMessage() );
            fail( "BufferedReader could not be opened" );
        }
    }

    @Test
    void checkStudentPassStatus() {
        String                  entryNumber       = "2020CSB1062";
        int                     batch             = 2020;
        String                  studentDepartment = "CS";
        String                  coreCourse        = "CS101";
        String[]                listOfCoreCourses = new String[]{ coreCourse };
        HashMap<String, Double> ugCurriculum      = new HashMap<>();
        HashMap<String, Double> incompleteStudent = new HashMap<>();
        HashMap<String, Double> completeStudent   = new HashMap<>();
        ugCurriculum.put( "PC", 3.0 );
        ugCurriculum.put( "OE", 3.0 );
        incompleteStudent.put( "PC", 0.0 );
        completeStudent.put( "PC", 5.0 );
        completeStudent.put( "OE", 1.0 );

        // False because of invalid input arguments
        assertFalse( admin.checkStudentPassStatus( null ) );

        // False because the entry number is not valid
        when( mockDAO.findEntryNumber( entryNumber ) ).thenReturn( false );
        assertFalse( admin.checkStudentPassStatus( entryNumber ) );

        // False because the batch could not be found
        when( mockDAO.findEntryNumber( entryNumber ) ).thenReturn( true );
        when( mockDAO.getBatch( entryNumber ) ).thenReturn( -1 );
        assertFalse( admin.checkStudentPassStatus( entryNumber ) );

        // False because the department could not be found
        when( mockDAO.getBatch( entryNumber ) ).thenReturn( batch );
        when( mockDAO.getStudentDepartment( entryNumber ) ).thenReturn( "" );
        assertFalse( admin.checkStudentPassStatus( entryNumber ) );

        // False because the list of core courses is null
        when( mockDAO.getStudentDepartment( entryNumber ) ).thenReturn( studentDepartment );
        when( mockDAO.getCoreCourses( batch, studentDepartment ) ).thenReturn( null );
        assertFalse( admin.checkStudentPassStatus( entryNumber ) );

        // False because the student has failed the course
        when( mockDAO.getCoreCourses( batch, studentDepartment ) ).thenReturn( listOfCoreCourses );
        when( mockDAO.getCourseGrade( entryNumber, coreCourse ) ).thenReturn( "F" );
        assertFalse( admin.checkStudentPassStatus( entryNumber ) );

        // False because the UG curriculum could not be fetched
        when( mockDAO.getCourseGrade( entryNumber, coreCourse ) ).thenReturn( "A" );
        when( mockDAO.getUGCurriculum( batch ) ).thenReturn( null );
        assertFalse( admin.checkStudentPassStatus( entryNumber ) );

        // UG curriculum was fetched but the credits completed was not
        when( mockDAO.getUGCurriculum( batch ) ).thenReturn( ugCurriculum );
        when( mockDAO.getCreditsInAllCategories( entryNumber ) ).thenReturn( null );
        assertFalse( admin.checkStudentPassStatus( entryNumber ) );

        // Student has not completed the UG curriculum
        when( mockDAO.getCreditsInAllCategories( entryNumber ) ).thenReturn( incompleteStudent );
        assertFalse( admin.checkStudentPassStatus( entryNumber ) );

        // Student has completed the UG curriculum
        when( mockDAO.getCreditsInAllCategories( entryNumber ) ).thenReturn( completeStudent );
        assertTrue( admin.checkStudentPassStatus( entryNumber ) );
    }

    @Test
    void generateTranscripts() {
        int        batch          = 2023;
        String     department     = "CS";
        String     entryNumber    = "2023CSB1062";
        int        year           = 2023;
        int        semester       = 1;
        int[]      currentSession = new int[]{ year, semester };
        String[]   listOfStudents = new String[]{ entryNumber };
        String[][] studentGrades  = new String[][]{ { "CS101", "DISCRETE MATHEMATICS", "A", "4.0" } };

        // False because of invalid input arguments
        assertFalse( admin.generateTranscripts( -1, department ) );
        assertFalse( admin.generateTranscripts( batch, null ) );

        // False because the list of students could not be fetched
        when( mockDAO.getListOfStudentsInBatch( batch, department ) ).thenReturn( null );
        assertFalse( admin.generateTranscripts( batch, department ) );

        // Printing is successful
        when( mockDAO.getCurrentAcademicSession() ).thenReturn( currentSession );
        when( mockDAO.getBatch( entryNumber ) ).thenReturn( batch );
        when( mockDAO.getStudentGradesForSemester( entryNumber, year, semester ) ).thenReturn( studentGrades );
        when( mockDAO.findEntryNumber( entryNumber ) ).thenReturn( true );
        when( mockDAO.getStudentDepartment( entryNumber ) ).thenReturn( department );
        when( mockDAO.getCoreCourses( batch, department ) ).thenReturn( new String[]{} );
        when( mockDAO.getUGCurriculum( batch ) ).thenReturn( new HashMap<>() );
        when( mockDAO.getCreditsInAllCategories( entryNumber ) ).thenReturn( new HashMap<>() );
        when( mockDAO.getListOfStudentsInBatch( batch, department ) ).thenReturn( listOfStudents );
        assertTrue( admin.generateTranscripts( batch, department ) );

        // False because of an exception
        when( mockDAO.getCurrentAcademicSession() ).thenThrow( new RuntimeException() );
        assertFalse( admin.generateTranscripts( batch, department ) );
    }

    @Test
    void startNewSession() {
        int   year           = 2023;
        int   semester       = 2;
        int   nextYear       = ( semester == 2 ) ? year + 1 : year;
        int   nextSemester   = ( semester == 2 ) ? 1 : 2;
        int[] currentSession = new int[]{ year, semester };

        // False because the previous session is not yet completed
        when( mockDAO.getCurrentAcademicSession() ).thenReturn( currentSession );
        when( mockDAO.checkIfSessionCompleted( year, semester ) ).thenReturn( false );
        assertFalse( admin.startNewSession() );

        // False because the new session could not be set
        when( mockDAO.checkIfSessionCompleted( year, semester ) ).thenReturn( true );
        when( mockDAO.createNewSession( nextYear, nextSemester ) ).thenReturn( false );
        assertFalse( admin.startNewSession() );

        // True because the DAO returns true
        when( mockDAO.createNewSession( nextYear, nextSemester ) ).thenReturn( true );
        assertTrue( admin.startNewSession() );
    }

    @Test
    void setCurrentSessionStatus() {
        int    year           = 2023;
        int    semester       = 2;
        int[] currentSession = new int[]{ year, semester} ;
        String event1         = "RUNNING";
        String event2         = "COMPLETED";

        // Event is not completed but DAO returns false
        when( mockDAO.getCurrentAcademicSession()).thenReturn( currentSession  );
        when( mockDAO.setSessionEvent( event1, year, semester )).thenReturn( false );
        assertFalse( admin.setCurrentSessionStatus( event1 ) );

        // Event is not completed and DAO returns true
        when( mockDAO.setSessionEvent( event1, year, semester )).thenReturn( true );
        assertTrue( admin.setCurrentSessionStatus( event1 ) );

        // Event cannot be set as completed due to missing grades
        when( mockDAO.setSessionEvent( event2, year, semester )).thenReturn( false );
        assertFalse( admin.setCurrentSessionStatus( event2 ) );

        // No missing grades and the event is set
        when( mockDAO.setSessionEvent( event2, year, semester )).thenReturn( true );
        assertFalse( admin.setCurrentSessionStatus( event2 ) );
    }
}