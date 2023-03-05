package org.abhijith.users;

import org.abhijith.dal.PostgresStudentDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class StudentTest {
    String     entryNumber                = "2020CSB1062";
    String     studentDepartment          = "CS";
    int        studentBatch               = 2020;
    String     courseCode                 = "CS301";
    double     courseCredits              = 4;
    String     courseCategory             = "PC";
    String     courseDepartment           = "CS";
    int        currentYear                = 2023;
    int        currentSemester            = 2;
    int[]      currentAcademicSession     = new int[]{ currentYear, currentSemester };
    Student    student                    = new Student( entryNumber );
    String     courseCatalogPrerequisite  = "CS101";
    int        gradeCutoff                = 4;
    String[]   courseCatalogPrerequisites = new String[]{ courseCatalogPrerequisite };
    String     instructorPrerequisite     = "CS201";
    String[][] instructorPrerequisites    = new String[][]{ { instructorPrerequisite, Integer.toString( gradeCutoff ) } };
    String[][] records                    = new String[][]{ { "4.5", "A-" }, { "4", "F" } };
    String[][] sgpaRecords                = new String[][]{ { "", "", "A-", "4.5" }, { "", "", "F", "4" }, null };

    PostgresStudentDAO studentDAO;

    @BeforeEach
    void setUp() {
        studentDAO = Mockito.mock( PostgresStudentDAO.class );
        student.setStudentDAO( studentDAO );
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void enroll() {
        // Tests that fail at the first statement due to null values
        assertFalse( student.enroll( null, courseDepartment ) );
        assertFalse( student.enroll( courseCode, null ) );

        when( studentDAO.getCurrentAcademicSession() ).thenReturn( currentAcademicSession );
        // False because the current event is not enrolling
        when( studentDAO.isCurrentEventEnrolling( currentYear, currentSemester ) ).thenReturn( false );
        assertFalse( student.enroll( courseCode, courseDepartment ) );

        // False because the course offering does not exist
        when( studentDAO.isCurrentEventEnrolling( currentYear, currentSemester ) ).thenReturn( true );
        when( studentDAO.checkCourseOffering( courseCode, currentYear, currentSemester, courseDepartment ) ).thenReturn( false );
        assertFalse( student.enroll( courseCode, courseDepartment ) );

        // False because the student has already passed the course
        when( studentDAO.checkCourseOffering( courseCode, currentYear, currentSemester, courseDepartment ) ).thenReturn( true );
        when( studentDAO.getCourseGrade( entryNumber, courseCode ) ).thenReturn( "A" );
        assertFalse( student.enroll( courseCode, courseDepartment ) );

        // False because the student is currently enrolled in the course
        when( studentDAO.getCourseGrade( entryNumber, courseCode ) ).thenReturn( "-" );
        assertFalse( student.enroll( courseCode, courseDepartment ) );

        // False because the request could not be satisfied
        when( studentDAO.getCourseGrade( entryNumber, courseCode ) ).thenReturn( "" );
        assertFalse( student.enroll( courseCode, courseDepartment ) );

        // False because the course prerequisite request failed
        when( studentDAO.getCourseGrade( entryNumber, courseCode ) ).thenReturn( "F" );
        when( studentDAO.getCourseCatalogPrerequisites( courseCode ) ).thenReturn( null );
        assertFalse( student.enroll( courseCode, courseDepartment ) );

        // False because the student has not passed the course
        when( studentDAO.getCourseCatalogPrerequisites( courseCode ) ).thenReturn( courseCatalogPrerequisites );
        when( studentDAO.checkStudentPassStatus( courseCode, gradeCutoff, entryNumber ) ).thenReturn( false );
        assertFalse( student.enroll( courseCode, courseDepartment ) );

        // False because the instructor prerequisites returned null
        when( studentDAO.checkStudentPassStatus( courseCatalogPrerequisite, gradeCutoff, entryNumber ) ).thenReturn( true );
        when( studentDAO.getInstructorPrerequisites( courseCode, currentYear, currentSemester, courseDepartment ) ).thenReturn( null );
        assertFalse( student.enroll( courseCode, courseDepartment ) );

        // False because the instructor prerequisites have not been fulfilled
        when( studentDAO.getInstructorPrerequisites( courseCode, currentYear, currentSemester, courseDepartment ) ).thenReturn( instructorPrerequisites );
        when( studentDAO.checkStudentPassStatus( instructorPrerequisite, gradeCutoff, entryNumber ) ).thenReturn( false );
        assertFalse( student.enroll( courseCode, courseDepartment ) );

        // False because the CGPA criteria is higher than the students CGPA
        when( studentDAO.checkStudentPassStatus( instructorPrerequisite, gradeCutoff, entryNumber ) ).thenReturn( true );
        when( studentDAO.getAllRecords( entryNumber ) ).thenReturn( records );
        when( studentDAO.getCGPACriteria( courseCode, currentYear, currentSemester, courseDepartment ) ).thenReturn( 9.5 );
        assertFalse( student.enroll( courseCode, courseDepartment ) );

        // False because the student exceeds the credit limit
        when( studentDAO.getCGPACriteria( courseCode, currentYear, currentSemester, courseDepartment ) ).thenReturn( 7.0 );
        when( studentDAO.getCreditsInSession( entryNumber, currentYear, currentSemester ) ).thenReturn( 18.0 );

        // The next three statements depend on what you entered as the values of currentYear and currentSemester
        when( studentDAO.getCreditsInSession( entryNumber, currentYear, currentSemester - 1 ) ).thenReturn( 0.0 );
        when( studentDAO.getCreditsInSession( entryNumber, currentYear - 1, currentSemester ) ).thenReturn( 0.0 );
        when( studentDAO.getCreditsInSession( entryNumber, currentYear - 1, currentSemester - 1 ) ).thenReturn( 0.0 );
        when( studentDAO.getCreditsOfCourse( courseCode ) ).thenReturn( courseCredits );
        assertFalse( student.enroll( courseCode, courseDepartment ) );

        // To test the odd condition in the getting credit limit section
        when( studentDAO.getCurrentAcademicSession() ).thenReturn( new int[]{ currentYear, currentSemester - 1 } );
        when( studentDAO.isCurrentEventEnrolling( currentYear, currentSemester - 1 ) ).thenReturn( true );
        when( studentDAO.checkCourseOffering( courseCode, currentYear, currentSemester - 1, courseDepartment ) ).thenReturn( true );
        when( studentDAO.getInstructorPrerequisites( courseCode, currentYear, currentSemester - 1, courseDepartment ) ).thenReturn( new String[][]{} );
        when( studentDAO.getCGPACriteria( courseCode, currentYear, currentSemester - 1, courseDepartment ) ).thenReturn( 0.0 );
        when( studentDAO.getCreditsInSession( entryNumber, currentYear, currentSemester - 1 ) ).thenReturn( 18.0 );
        assertFalse( student.enroll( courseCode, courseDepartment ) );

        // False because the student department could not be fetched
        when( studentDAO.getCreditsInSession( entryNumber, currentYear, currentSemester ) ).thenReturn( 13.0 );
        when( studentDAO.getCurrentAcademicSession() ).thenReturn( currentAcademicSession );
        when( studentDAO.getStudentDepartment( entryNumber ) ).thenReturn( "" );
        assertFalse( student.enroll( courseCode, courseDepartment ) );

        // False because the batch could not be fetched
        when( studentDAO.getStudentDepartment( entryNumber ) ).thenReturn( studentDepartment );
        when( studentDAO.getBatch( entryNumber ) ).thenReturn( -1 );
        assertFalse( student.enroll( courseCode, courseDepartment ) );

        // False because the course is not offered for this particular batch and department
        when( studentDAO.getBatch( entryNumber ) ).thenReturn( studentBatch );
        when( studentDAO.getCourseCategory( courseCode, currentYear, currentSemester, courseDepartment, studentDepartment, studentBatch ) ).thenReturn( "" );
        assertFalse( student.enroll( courseCode, courseDepartment ) );

        // Successful enrollment
        when( studentDAO.getCourseCategory( courseCode, currentYear, currentSemester, courseDepartment, studentDepartment, studentBatch ) ).thenReturn( courseCategory );
        when( studentDAO.enroll( courseCode, entryNumber, currentYear, currentSemester, courseDepartment, courseCategory ) ).thenReturn( true );
        assertTrue( student.enroll( courseCode, courseDepartment ) );
    }

    @Test
    void drop() {
        // False because of invalid input parameters
        assertFalse( student.drop( null ) );

        // False because the current event is not enrolling
        when( studentDAO.getCurrentAcademicSession() ).thenReturn( currentAcademicSession );
        when( studentDAO.isCurrentEventEnrolling( currentYear, currentSemester ) ).thenReturn( false );
        assertFalse( student.drop( courseCode ) );

        // Successful
        when( studentDAO.isCurrentEventEnrolling( currentYear, currentSemester ) ).thenReturn( true );
        when( studentDAO.dropCourse( courseCode, entryNumber, currentYear, currentSemester ) ).thenReturn( true );
        assertTrue( student.drop( courseCode ) );
    }

    @Test
    void getGradesForDegree() {
        String[][] firstGrades = new String[][]{{"CS101", "DISCRETE", "A", "4"}};
        String[][] secondGrades = new String[][]{{"CS201", "DSA", "A", "4"}};
        String[][][] allGrades = new String[][][]{firstGrades, secondGrades};

        when( studentDAO.getCurrentAcademicSession()).thenReturn( currentAcademicSession );

        // [][][] as the student batch returned is -1
        when( studentDAO.getBatch( entryNumber )).thenReturn( -1 );
        assertArrayEquals( new String[][][]{}, student.getGradesForDegree() );

        // Expected result
        when( studentDAO.getBatch( entryNumber )).thenReturn( currentYear );
        when( studentDAO.getStudentGradesForSemester( entryNumber, currentYear, 1 )).thenReturn( firstGrades );
        when( studentDAO.getStudentGradesForSemester( entryNumber, currentYear, 2 )).thenReturn(secondGrades );
        assertArrayEquals( allGrades, student.getGradesForDegree() );
    }

    @Test
    void getGrades() {
        // [][] due to invalid input parameters
        assertArrayEquals( new String[][]{}, student.getGrades( currentYear, -1 ) );
        assertArrayEquals( new String[][]{}, student.getGrades( -1, currentSemester ) );

        // Returns the expected result
        when( studentDAO.getStudentGradesForSemester( entryNumber, currentYear, currentSemester )).thenReturn( sgpaRecords );
        assertArrayEquals( sgpaRecords, student.getGrades( currentYear, currentSemester ) );
    }

    @Test
    void getBatch() {
        // Only success is possible. Failure in the DAO would cause the method to return -1
        when( studentDAO.getBatch( entryNumber ) ).thenReturn( -1 );
        assertEquals( -1, student.getBatch() );

        when( studentDAO.getBatch( entryNumber ) ).thenReturn( studentBatch );
        assertEquals( studentBatch, student.getBatch() );
    }

    @Test
    void getSGPA() {
        // 0 because the argument is invalid
        assertEquals( 0.0, student.getSGPA( null ) );

        // 0 because there are no valid records
        assertEquals( 0.0, student.getSGPA( new String[][]{ null } ) );

        // Expected value
        assertEquals( 4.764705882352941, student.getSGPA( sgpaRecords ) );
    }

    @Test
    void getCGPA() {
        // 0.0 as there is no record returned
        when( studentDAO.getAllRecords( entryNumber )).thenReturn( new String[][]{} );
        assertEquals( 0.0, student.getCGPA() );

        // Expected value is returned
        when( studentDAO.getAllRecords( entryNumber )).thenReturn( records );
        assertEquals( 9.0, student.getCGPA() );
    }

    @Test
    void getAvailableCourses() {
        // Empty array due to inability to fetch student batch
        when( studentDAO.getCurrentAcademicSession()).thenReturn( currentAcademicSession );
        when( studentDAO.getStudentDepartment( entryNumber )).thenReturn( studentDepartment );
        when( studentDAO.getBatch( entryNumber )).thenReturn( -1 );
        assertArrayEquals( new String[][]{}, student.getAvailableCourses() );

        // Empty array due to inability to fetch student department
        when( studentDAO.getStudentDepartment( entryNumber )).thenReturn( "" );
        when( studentDAO.getBatch( entryNumber )).thenReturn( studentBatch );
        assertArrayEquals( new String[][]{}, student.getAvailableCourses() );

        // Expected output
        when( studentDAO.getStudentDepartment( entryNumber )).thenReturn( studentDepartment );
        when( studentDAO.getOfferedCourses( currentYear, currentSemester )).thenReturn( new String[][]{{ courseCode, "FINANCIAL DERIVATIVES PRICING", "DR ARUN KUMAR", "{}", courseDepartment, "" }} );
        when( studentDAO.getCourseCategory( courseCode, currentYear, currentSemester, courseDepartment, studentDepartment, studentBatch )).thenReturn( courseCategory );
        assertArrayEquals( new String[][]{ { courseCode, "FINANCIAL DERIVATIVES PRICING", "DR ARUN KUMAR", "{}", courseDepartment, courseCategory } }, student.getAvailableCourses() );
    }

    @Test
    void getRemainingCreditRequirements() {
        HashMap<String, Double> ugCurriculum = new HashMap<>();
        ugCurriculum.put( "PC", 5.0 );
        ugCurriculum.put( "PE", 10.0 );
        ugCurriculum.put( "OE", 5.0 );

        HashMap<String, Double> creditsCompleted = new HashMap<>();
        creditsCompleted.put( "PC", 10.0 );
        creditsCompleted.put( "PE", 9.0 );

        HashMap<String, Double> expectedResult = new HashMap<>();
        expectedResult.put( "PC", 0.0 );
        expectedResult.put( "PE", 1.0 );
        expectedResult.put( "OE", 0.0 );

        // Empty hashmap as the batch retrieval failed
        when( studentDAO.getBatch( entryNumber )).thenReturn( -1 );
        assertTrue( new HashMap<>().equals( student.getRemainingCreditRequirements() ) );

        // Empty hashmap as the curriculum could not be fetched
        when( studentDAO.getBatch( entryNumber )).thenReturn( studentBatch );
        when( studentDAO.getUGCurriculum( studentBatch )).thenReturn( null );
        assertTrue( new HashMap<>().equals( student.getRemainingCreditRequirements() ) );

        // Empty hashmap because the credits completed could not be fetched
        when( studentDAO.getUGCurriculum( studentBatch )).thenReturn( ugCurriculum );
        when( studentDAO.getCreditsInAllCategories( entryNumber )).thenReturn( null );
        assertTrue( new HashMap<>().equals( student.getRemainingCreditRequirements() ) );

        // Returns the expected result
        when( studentDAO.getCreditsInAllCategories( entryNumber )).thenReturn( creditsCompleted );
        assertTrue( expectedResult.equals( student.getRemainingCreditRequirements() ) );
    }
}