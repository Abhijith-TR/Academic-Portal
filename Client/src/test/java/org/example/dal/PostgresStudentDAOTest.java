package org.example.dal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class PostgresStudentDAOTest {
    PostgresStudentDAO student;

    boolean compareHashMap( HashMap<String, String[]> actual, HashMap<String, String[]> expected ) {
        if ( actual.size() != expected.size() ) return false;
        for ( String key : actual.keySet() ) {
            if ( !expected.containsKey( key ) ) return false;
            String[] actualArray   = actual.get( key );
            String[] expectedArray = expected.get( key );
            if ( actualArray.length != expectedArray.length ) return false;
            Arrays.sort( actualArray );
            Arrays.sort( expectedArray );
            for ( int i = 0; i < actualArray.length; i++ ) {
                if ( !actualArray[i].equals( expectedArray[i] ) ) return false;
            }
        }
        return true;
    }

    @BeforeEach
    void setUp() {
        student = new PostgresStudentDAO(
                "jdbc:postgresql://localhost:5432/mini_project",
                "postgres",
                "admin"
        );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getCurrentAcademicSession() {
    }

    @Test
    void checkCourseOffering() {
    }

    @Test
    void isStudentEligible() {
//        assertTrue(student.checkStudentEligibility("CS301", "2020csb1062"));
//        assertFalse(student.checkStudentEligibility("CS305", "2020csb1062"));
    }

    @Test
    void enroll() {
        // The request should succeed as the student has completed all the necessary courses and is in the correct year and semester
        assertFalse( student.enroll( "CS301", "2020csb1062", 2022, 1 ) );
        // The request should fail as it is in the wrong year and semester (i.e., academic session)
        assertFalse( student.enroll( "CS305", "2020csb1062", 2022, 1 ) );
    }

    @Test
    void testGetCurrentAcademicSession() {
    }

    @Test
    void testCheckCourseOffering() {
    }

    @Test
    void checkStudentPassStatus() {
    }

    @Test
    void getCourseCatalogPrerequisites() {
    }

    @Test
    void getInstructorPrerequisites() {
    }

    @Test
    void getCreditsOfCourse() {
    }

    @Test
    void getCreditsInSession() {
    }

    @Test
    void testEnroll() {
    }

    @Test
    void dropCourse() {
    }

    @Test
    void getGradesForSemester() {
        assertArrayEquals( new String[]{}, student.getStudentGradesForSemester( "2020csb1062", 2020, 1 ) );
    }

    @Test
    void getOfferedCourses() {
        student.getOfferedCourses( 2022, 1 );
    }

    @Test
    void getAllOfferings() {
        String[]                  categories    = new String[]{ "SC", "SE", "GR", "PC", "PE", "HC", "HE", "CP", "II", "NN", "OE" };
        HashMap<String, String[]> expectedValue = new HashMap<>();
        for ( String category : categories ) expectedValue.put( category, new String[]{} );

        expectedValue.put( "PC", new String[]{ "2020-CS" } );
        HashMap<String, String[]> actualValue = student.getAllOfferings( "GE103", 2020, 1 );
        assertTrue( compareHashMap( actualValue, expectedValue ) );
    }

    @Test
    void getCreditsInAllCategories() {
        HashMap<String, Double> expectedValue = new HashMap<>();
        expectedValue.put( "PC", 20.5 );
        HashMap<String, Double> actualValue = student.getCreditsInAllCategories( "2020CSB1062" );
        assertTrue( actualValue.equals( expectedValue ) );
    }
}