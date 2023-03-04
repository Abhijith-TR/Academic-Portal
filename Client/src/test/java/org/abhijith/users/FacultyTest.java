package org.abhijith.users;

import org.abhijith.dal.PostgresFacultyDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class FacultyTest {
    String             facultyID              = "FAC38";
    String             courseCode             = "CS301";
    String             offeringDepartment     = "CS";
    String             departmentID           = "CS";
    String             courseCategory         = "PC";
    int                currentYear            = 2023;
    int                currentSemester        = 2;
    int[]              currentAcademicSession = new int[]{ currentYear, currentSemester };
    double             minimumCGPA            = 8.0;
    String[][]         prerequisites          = new String[][]{ { courseCode, Double.toString( minimumCGPA ) } };
    Faculty            faculty                = new Faculty( facultyID );
    int                offeredYear            = 2021;
    int[]              offeredYears           = new int[]{ offeredYear };
    String             entryNumber            = "2020CSB1062";
    int                batch                  = 2023;
    PostgresFacultyDAO facultyDAO;

    @BeforeEach
    void setUp() {
        facultyDAO = Mockito.mock( PostgresFacultyDAO.class );
        faculty.setFacultyDAO( facultyDAO );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void offerCourse() {
        // Fails because of null
        assertFalse( faculty.offerCourse( null, offeringDepartment ) );
        assertFalse( faculty.offerCourse( courseCode, null ) );

        // Fails because the course code does not exist
        when( facultyDAO.checkCourseCatalog( courseCode ) ).thenReturn( false );
        assertFalse( faculty.offerCourse( courseCode, offeringDepartment ) );

        // Fails because it is not the offering event
        when( facultyDAO.checkCourseCatalog( courseCode ) ).thenReturn( true );
        when( facultyDAO.getCurrentAcademicSession() ).thenReturn( currentAcademicSession );
        when( facultyDAO.isCurrentEventOffering( currentYear, currentSemester ) ).thenReturn( false );
        assertFalse( faculty.offerCourse( courseCode, offeringDepartment ) );

        // Course has already been offered
        when( facultyDAO.isCurrentEventOffering( currentYear, currentSemester ) ).thenReturn( true );
        when( facultyDAO.isCourseAlreadyOffered( courseCode, currentYear, currentSemester, offeringDepartment ) ).thenReturn( true );
        assertFalse( faculty.offerCourse( courseCode, offeringDepartment ) );

        // Course successfully inserted
        when( facultyDAO.isCourseAlreadyOffered( courseCode, currentYear, currentSemester, offeringDepartment ) ).thenReturn( false );
        when( facultyDAO.insertCourseOffering( courseCode, currentYear, currentSemester, offeringDepartment, facultyID ) ).thenReturn( true );
        assertTrue( faculty.offerCourse( courseCode, offeringDepartment ) );
    }

    @Test
    void setCGAndPrerequisites() {
        // First 3 fail due to null arguments
        assertFalse( faculty.setCGAndPrerequisites( null, offeringDepartment, minimumCGPA, prerequisites ) );
        assertFalse( faculty.setCGAndPrerequisites( courseCode, offeringDepartment, -1, prerequisites ) );
        assertFalse( faculty.setCGAndPrerequisites( courseCode, offeringDepartment, minimumCGPA, null ) );
        assertFalse( faculty.setCGAndPrerequisites( courseCode, null, minimumCGPA, prerequisites ) );

        // Fails because it is not the offering event right now
        when( facultyDAO.getCurrentAcademicSession() ).thenReturn( currentAcademicSession );
        when( facultyDAO.isCurrentEventOffering( currentYear, currentSemester ) ).thenReturn( false );
        assertFalse( faculty.setCGAndPrerequisites( courseCode, offeringDepartment, minimumCGPA, prerequisites ) );

        // Fails because the course has not been offered by the instructor
        when( facultyDAO.isCurrentEventOffering( currentYear, currentSemester ) ).thenReturn( true );
        when( facultyDAO.checkIfOfferedBySelf( facultyID, courseCode, currentYear, currentSemester, offeringDepartment ) ).thenReturn( false );
        assertFalse( faculty.setCGAndPrerequisites( courseCode, offeringDepartment, minimumCGPA, prerequisites ) );

        // Fails because the CGPA was not successfully updated
        when( facultyDAO.checkIfOfferedBySelf( facultyID, courseCode, currentYear, currentSemester, offeringDepartment ) ).thenReturn( true );
        when( facultyDAO.setCGCriteria( facultyID, courseCode, minimumCGPA, currentAcademicSession, offeringDepartment ) ).thenReturn( false );
        assertFalse( faculty.setCGAndPrerequisites( courseCode, offeringDepartment, minimumCGPA, prerequisites ) );

        // Course not found in the catalog
        when( facultyDAO.setCGCriteria( facultyID, courseCode, minimumCGPA, currentAcademicSession, offeringDepartment ) ).thenReturn( true );
        when( facultyDAO.checkCourseCatalog( prerequisites[0][0] ) ).thenReturn( false );
        assertFalse( faculty.setCGAndPrerequisites( courseCode, offeringDepartment, minimumCGPA, prerequisites ) );

        // Successfully inserted
        when( facultyDAO.checkCourseCatalog( prerequisites[0][0] ) ).thenReturn( true );
        when( facultyDAO.setInstructorPrerequisites( offeringDepartment, courseCode, prerequisites, currentAcademicSession ) ).thenReturn( true );
        assertTrue( faculty.setCGAndPrerequisites( courseCode, offeringDepartment, minimumCGPA, prerequisites ) );

        // Testing the exception
        when( facultyDAO.getCurrentAcademicSession() ).thenThrow( new RuntimeException() );
        assertFalse( faculty.setCGAndPrerequisites( courseCode, offeringDepartment, minimumCGPA, prerequisites ) );
    }

    @Test
    void dropCourseOffering() {
        // Fails because of null
        assertFalse( faculty.dropCourseOffering( null, offeringDepartment ) );
        assertFalse( faculty.dropCourseOffering( courseCode, null ) );

        // Fails because it is not the offering event right now
        when( facultyDAO.getCurrentAcademicSession() ).thenReturn( currentAcademicSession );
        when( facultyDAO.isCurrentEventOffering( currentYear, currentSemester ) ).thenReturn( false );
        assertFalse( faculty.dropCourseOffering( courseCode, offeringDepartment ) );

        // Fails because no such course has been offered by the instructor
        when( facultyDAO.isCurrentEventOffering( currentYear, currentSemester ) ).thenReturn( true );
        when( facultyDAO.checkIfOfferedBySelf( facultyID, courseCode, currentYear, currentSemester, offeringDepartment ) ).thenReturn( false );
        assertFalse( faculty.dropCourseOffering( courseCode, offeringDepartment ) );

        // Successfully executed
        when( facultyDAO.checkIfOfferedBySelf( facultyID, courseCode, currentYear, currentSemester, offeringDepartment ) ).thenReturn( true );
        when( facultyDAO.dropCourseOffering( facultyID, courseCode, currentYear, currentSemester, offeringDepartment ) ).thenReturn( true );
        assertTrue( faculty.dropCourseOffering( courseCode, offeringDepartment ) );
    }

    @Test
    void setCourseCategory() {
        // Failures due to null
        assertFalse( faculty.setCourseCategory( null, offeringDepartment, courseCategory, departmentID, offeredYears ) );
        assertFalse( faculty.setCourseCategory( courseCode, offeringDepartment, null, departmentID, offeredYears ) );
        assertFalse( faculty.setCourseCategory( courseCode, offeringDepartment, courseCategory, null, offeredYears ) );
        assertFalse( faculty.setCourseCategory( courseCode, offeringDepartment, courseCategory, departmentID, null ) );
        assertFalse( faculty.setCourseCategory( courseCode, null, courseCategory, departmentID, offeredYears ) );

        // Fails because it is not the offering event
        when( facultyDAO.getCurrentAcademicSession() ).thenReturn( currentAcademicSession );
        when( facultyDAO.isCurrentEventOffering( currentYear, currentSemester ) ).thenReturn( false );
        assertFalse( faculty.setCourseCategory( courseCode, offeringDepartment, courseCategory, departmentID, offeredYears ) );

        // Fails because this has not been offered by the instructor
        when( facultyDAO.isCurrentEventOffering( currentYear, currentSemester ) ).thenReturn( true );
        when( facultyDAO.checkIfOfferedBySelf( facultyID, courseCode, currentYear, currentSemester, offeringDepartment ) ).thenReturn( false );
        assertFalse( faculty.setCourseCategory( courseCode, offeringDepartment, courseCategory, departmentID, offeredYears ) );

        // Fails because this particular course is not program core and hence cannot be made offered as program core
        when( facultyDAO.checkIfOfferedBySelf( facultyID, courseCode, currentYear, currentSemester, offeringDepartment ) ).thenReturn( true );
        when( facultyDAO.verifyCore( courseCode, departmentID, offeredYear ) ).thenReturn( false );
        assertFalse( faculty.setCourseCategory( courseCode, offeringDepartment, courseCategory, departmentID, offeredYears ) );

        // Successful as all parts of the query are valid
        when( facultyDAO.verifyCore( courseCode, departmentID, offeredYear ) ).thenReturn( true );
        when( facultyDAO.setCourseCategory( courseCode, currentYear, currentSemester, courseCategory, departmentID, offeredYears, offeringDepartment ) ).thenReturn( true );
        assertTrue( faculty.setCourseCategory( courseCode, offeringDepartment, courseCategory, departmentID, offeredYears ) );
    }

    @Test
    void getGradesOfStudent() {
        // Invalid input parameters
        assertArrayEquals( new String[][][]{}, faculty.getGradesOfStudent( null ) );

        // The batch function fails
        when( facultyDAO.getCurrentAcademicSession() ).thenReturn( currentAcademicSession );
        when( facultyDAO.getBatch( entryNumber ) ).thenReturn( -1 );
        assertArrayEquals( new String[][][]{}, faculty.getGradesOfStudent( entryNumber ) );

        // Successful
        when( facultyDAO.getBatch( entryNumber ) ).thenReturn( batch );
        when( facultyDAO.getStudentGradesForSemester( entryNumber, batch, 1 ) ).thenReturn( new String[][]{} );
        when( facultyDAO.getStudentGradesForSemester( entryNumber, batch, 2 ) ).thenReturn( new String[][]{ { "CS101", "8" } } );
        assertArrayEquals( new String[][][]{ {}, { { "CS101", "8" } } }, faculty.getGradesOfStudent( entryNumber ) );
    }

    @Test
    void getGradesOfOffering() {
        String[][] emptyArray  = new String[][]{};
        String[][] returnValue = new String[][]{ { "2020CSB1062", "A" } };

        // [][] because of invalid input parameters
        assertArrayEquals( emptyArray, faculty.getGradesOfOffering( null, currentYear, currentSemester, offeringDepartment ) );
        assertArrayEquals( emptyArray, faculty.getGradesOfOffering( courseCode, -1, currentSemester, offeringDepartment ) );
        assertArrayEquals( emptyArray, faculty.getGradesOfOffering( courseCode, currentYear, -1, offeringDepartment ) );
        assertArrayEquals( emptyArray, faculty.getGradesOfOffering( courseCode, currentYear, currentSemester, null ) );

        // Returns the expected value
        when( faculty.getGradesOfOffering( courseCode, currentYear, currentSemester, offeringDepartment ) ).thenReturn( returnValue );
        assertArrayEquals( returnValue, faculty.getGradesOfOffering( courseCode, currentYear, currentSemester, offeringDepartment ) );
    }

    @Test
    void generateGradeCSV() {
        String[][] listOfStudents = new String[][]{ { "ABHIJITH", entryNumber } };

        // False because of invalid input parameters
        assertFalse( faculty.generateGradeCSV( null, currentYear, currentSemester, offeringDepartment ) );
        assertFalse( faculty.generateGradeCSV( courseCode, -1, currentSemester, offeringDepartment ) );
        assertFalse( faculty.generateGradeCSV( courseCode, currentYear, -1, offeringDepartment ) );
        assertFalse( faculty.generateGradeCSV( courseCode, currentYear, currentSemester, null ) );

        // False because the course is not offered by this instructor
        when( facultyDAO.checkIfOfferedBySelf( facultyID, courseCode, currentYear, currentSemester, departmentID ) ).thenReturn( false );
        assertFalse( faculty.generateGradeCSV( courseCode, currentYear, currentSemester, offeringDepartment ) );

        // Successful
        when( facultyDAO.checkIfOfferedBySelf( facultyID, courseCode, currentYear, currentSemester, departmentID ) ).thenReturn( true );
        when( facultyDAO.getCourseEnrollmentsList( courseCode, currentYear, currentSemester, offeringDepartment ) ).thenReturn( listOfStudents );
        assertTrue( faculty.generateGradeCSV( courseCode, currentYear, currentSemester, offeringDepartment ) );

        // Note: The following test will fail if you decide to rename the file that is generated on a successful generateGradeCSV call
        try {
            String         fileName     = courseCode + "_" + currentYear + "_" + currentSemester + "_" + offeringDepartment + ".csv";
            BufferedReader outputFile   = new BufferedReader( new FileReader( fileName ) );
            String         line;
            String         expectedLine = listOfStudents[0][0] + "," + listOfStudents[0][1] + ",";
            while ( ( line = outputFile.readLine() ) != null ) {
                assertEquals( expectedLine, line );
            }
            outputFile.close();
        } catch ( Exception error ) {
            fail( "Could not open file" );
        }

        // False due to the exception
        when( facultyDAO.checkIfOfferedBySelf( facultyID, courseCode, currentYear, currentSemester, departmentID ) ).thenThrow( new RuntimeException() );
        assertFalse( faculty.generateGradeCSV( courseCode, currentYear, currentSemester, offeringDepartment ) );
    }

    @Test
    void uploadGrades() {
        String         gradeString    = "ABHIJITH," + entryNumber + ",A\n";
        String[]       listOfStudents = new String[]{ entryNumber };
        String[]       listOfGrades   = new String[]{ "A" };
        BufferedReader gradeFile      = new BufferedReader( new StringReader( gradeString ) );

        // False due to invalid input parameters
        assertFalse( faculty.uploadGrades( null, currentYear, currentSemester, gradeFile, offeringDepartment ) );
        assertFalse( faculty.uploadGrades( courseCode, -1, currentSemester, gradeFile, offeringDepartment ) );
        assertFalse( faculty.uploadGrades( courseCode, currentYear, -1, gradeFile, offeringDepartment ) );
        assertFalse( faculty.uploadGrades( courseCode, currentYear, currentSemester, null, offeringDepartment ) );
        assertFalse( faculty.uploadGrades( courseCode, currentYear, currentSemester, gradeFile, null ) );

        // False because of the wrong year and semester
        when( facultyDAO.getCurrentAcademicSession() ).thenReturn( new int[]{ 2022, 2 } );
        assertFalse( faculty.uploadGrades( courseCode, currentYear, currentSemester, gradeFile, offeringDepartment ) );

        // False because it is not the grade submission event
        when( facultyDAO.getCurrentAcademicSession() ).thenReturn( currentAcademicSession );
        when( facultyDAO.isCurrentEventGradeSubmission( currentYear, currentSemester ) ).thenReturn( false );
        assertFalse( faculty.uploadGrades( courseCode, currentYear, currentSemester, gradeFile, offeringDepartment ) );

        // False because the instructor has not offered this course
        when( facultyDAO.isCurrentEventGradeSubmission( currentYear, currentSemester ) ).thenReturn( true );
        when( facultyDAO.checkIfOfferedBySelf( facultyID, courseCode, currentYear, currentSemester, departmentID ) ).thenReturn( false );
        assertFalse( faculty.uploadGrades( courseCode, currentYear, currentSemester, gradeFile, offeringDepartment ) );

        // False because the size of the list of students is different
        when( facultyDAO.checkIfOfferedBySelf( facultyID, courseCode, currentYear, currentSemester, departmentID ) ).thenReturn( true );
        when( facultyDAO.getListOfStudents( courseCode, currentYear, currentSemester, offeringDepartment ) ).thenReturn( new String[]{} );
        assertFalse( faculty.uploadGrades( courseCode, currentYear, currentSemester, gradeFile, offeringDepartment ) );

        // False because the list of students itself is different
        gradeFile      = new BufferedReader( new StringReader( gradeString ) );
        when( facultyDAO.getListOfStudents( courseCode, currentYear, currentSemester, offeringDepartment ) ).thenReturn( new String[]{ "2020CSB" } );
        assertFalse( faculty.uploadGrades( courseCode, currentYear, currentSemester, gradeFile, offeringDepartment ) );

        // Successful because all conditions have been satisfied
        gradeFile      = new BufferedReader( new StringReader( gradeString ) );
        when( facultyDAO.getListOfStudents( courseCode, currentYear, currentSemester, offeringDepartment ) ).thenReturn( new String[]{ entryNumber } );
        when( facultyDAO.uploadCourseGrades( courseCode, currentYear, currentSemester, offeringDepartment, listOfStudents, listOfGrades) ).thenReturn( true );
        assertTrue( faculty.uploadGrades( courseCode, currentYear, currentSemester, gradeFile, offeringDepartment ) );

        // Failure because an exception is called
        when( facultyDAO.getCurrentAcademicSession()).thenThrow( new RuntimeException() );
        assertFalse( faculty.uploadGrades( courseCode, currentYear, currentSemester, gradeFile, offeringDepartment ) );
    }
}