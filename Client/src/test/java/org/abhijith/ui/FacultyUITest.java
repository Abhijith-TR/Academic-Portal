package org.abhijith.ui;

import org.abhijith.users.Faculty;
import org.abhijith.users.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class FacultyUITest {
    private InputStream systemInput = System.in;

    private final PrintStream           systemOutput = System.out;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    String    id        = "FAC1";
    FacultyUI facultyUI = new FacultyUI( id );
    Faculty   faculty;

    @BeforeEach
    void setUp() {
        System.setOut( new PrintStream( outputStream ) );
        faculty = Mockito.mock( Faculty.class );
        facultyUI.setFaculty( faculty );
    }

    @AfterEach
    void tearDown() {
        try {
            outputStream.flush();
        } catch ( Exception error ) {
        }
        System.setOut( systemOutput );
        System.setIn( systemInput );
    }

    private void setInputToString( String input ) {
        InputStream in = new ByteArrayInputStream( input.getBytes() );
        System.setIn( in );
    }

    private String extractOutput( String splitter, int index ) {
        String[] output = outputStream.toString().split( "\\r?\\n" );
        output = output[output.length - 17 + index].split( splitter );
        return output[output.length - 1].trim();
    }

    @Test
    void offerCourseTest() {
        String input = "1\nCS101\nCS\n13\n";
        setInputToString( input );
        when( faculty.offerCourse( "CS101", "CS" ) ).thenReturn( true );
        facultyUI.facultyInterfaceHomeScreen();
        assertEquals( "Course offered. Please update prerequisites and CG criteria manually", extractOutput( ":", 0 ) );

        setInputToString( input );
        when( faculty.offerCourse( "CS101", "CS" ) ).thenReturn( false );
        facultyUI.facultyInterfaceHomeScreen();
        assertEquals( "Course not offered. Please verify that the course exists in the course catalog", extractOutput( ":", 0 ) );
    }

    @Test
    void insertPrerequisitesTest() {
        String input = "2\nCS101\nCS\n9\n1\n2\nCS101\n11\nCS101\n5\n13\n";
        setInputToString( input );
        when( faculty.setCGAndPrerequisites( eq( "CS101" ), eq( "CS" ), eq( 9.0 ), eq( new String[][]{ { "CS101", "5" } } ) ) ).thenReturn( true );
        facultyUI.facultyInterfaceHomeScreen();
        assertEquals( "Details Updated Successfully", extractOutput( ":", 0 ) );

        setInputToString( input );
        when( faculty.setCGAndPrerequisites( eq( "CS101" ), eq( "CS" ), eq( 9.0 ), eq( new String[][]{ { "CS101", "5" } } ) ) ).thenReturn( false );
        facultyUI.facultyInterfaceHomeScreen();
        assertEquals( "Criteria Update Failed", extractOutput( ":", 0 ) );
    }

    @Test
    void setCourseCategoryTest() {
        String input = "3\nCS101\nCS\nPC\nCS\n1\n2020\n13\n";
        setInputToString( input );
        when( faculty.setCourseCategory( "CS101", "CS", "PC", "CS", new int[]{ 2020 } ) ).thenReturn( true );
        facultyUI.facultyInterfaceHomeScreen();
        assertEquals( "Category set successfully", extractOutput( ":", 0 ) );

        setInputToString( input );
        when( faculty.setCourseCategory( "CS101", "CS", "PC", "CS", new int[]{ 2020 } ) ).thenReturn( false );
        facultyUI.facultyInterfaceHomeScreen();
        assertEquals( "Category setting failed. Please verify that you are not offering for the same batch twice.", extractOutput( ":", 0 ) );
    }

    @Test
    void dropOfferingTest() {
        String input = "4\nCS101\nCS\n13\n";
        setInputToString( input );
        when( faculty.dropCourseOffering( "CS101", "CS" ) ).thenReturn( true );
        facultyUI.facultyInterfaceHomeScreen();
        assertEquals( "Course offering dropped successfully", extractOutput( ":", 0 ) );

        setInputToString( input );
        when( faculty.dropCourseOffering( "CS101", "CS" ) ).thenReturn( false );
        facultyUI.facultyInterfaceHomeScreen();
        assertEquals( "Course offering not dropped. Please verify the course code", extractOutput( ":", 0 ) );
    }

    @Test
    void getStudentGradesTest() {
        String[][][] records = new String[][][]{ { { "CS101", "DISCRETE MATHEMATICS", "A", "4.5" } } };

        String input = "5\n2020CSB1062\n2020\n13\n";
        setInputToString( input );
        when( faculty.getGradesOfStudent( "2020CSB1062" ) ).thenReturn( records );
        facultyUI.facultyInterfaceHomeScreen();
        assertEquals( "Records - Year: 2020 Semester: 1", extractOutput( "\\):", -3 ) );
        assertEquals( "Course Code   Course Title           Grade   Credits", extractOutput( "choice:", -2 ) );
        assertEquals( "CS101         DISCRETE MATHEMATICS   A       4.5", extractOutput( "choice:", -1 ) );
    }

    @Test
    void getCourseGrades() {
        String[][] records = new String[][]{ { "2020CSB1062", "A" } };

        String input = "6\nCS101\n2020\n2\nCS\n13\n";
        setInputToString( input );
        when( faculty.getGradesOfOffering( "CS101", 2020, 2, "CS" ) ).thenReturn( records );
        facultyUI.facultyInterfaceHomeScreen();
        assertEquals( "Entry Number   Grade", extractOutput( ":", -2 ) );
        assertEquals( "2020CSB1062    A", extractOutput( ":", -1 ) );

        setInputToString( input );
        when( faculty.getGradesOfOffering( "CS101", 2020, 2, "CS" ) ).thenReturn( new String[][]{} );
        facultyUI.facultyInterfaceHomeScreen();
        assertEquals( "No course found with given specifications", extractOutput( ":", 0 )  );
    }

    @Test
    void generateGradeCSVTest() {
        String input = "7\nCS101\n2020\n2\nCS\n13\n";
        setInputToString( input );
        when( faculty.generateGradeCSV( "CS101", 2020, 2, "CS" )).thenReturn( true );
        facultyUI.facultyInterfaceHomeScreen();
        assertEquals( "File Generated Successfully", extractOutput( ":", 0 ) );

        setInputToString( input );
        when( faculty.generateGradeCSV( "CS101", 2020, 2, "CS" )).thenReturn( false );
        facultyUI.facultyInterfaceHomeScreen();
        assertEquals( "File Not Generated. Only generate files for your own courses", extractOutput( ":", 0 ) );
    }

    @Test
    void updatePhoneNumberTest() {
        String input = "9\n99999\n13\n";
        when( faculty.setPhoneNumber( "99999" ) ).thenReturn( true );
        setInputToString( input );
        facultyUI.facultyInterfaceHomeScreen();
        assertEquals( "Phone Number Updated Successfully", extractOutput( ":", 0 ) );

        when( faculty.setPhoneNumber( "99999" ) ).thenReturn( false );
        setInputToString( input );
        facultyUI.facultyInterfaceHomeScreen();
        assertEquals( "Phone Number Update Failed", extractOutput( ":", 0 ) );
    }

    @Test
    void updateEmailTest() {
        String input = "10\nrandom@gmail.com\n13\n";
        when( faculty.setEmail( "random@gmail.com" ) ).thenReturn( true );
        setInputToString( input );
        facultyUI.facultyInterfaceHomeScreen();
        assertEquals( "Contact Email Updated Successfully", extractOutput( ":", 0 ) );

        when( faculty.setEmail( "random@gmail.com" ) ).thenReturn( false );
        setInputToString( input );
        facultyUI.facultyInterfaceHomeScreen();
        assertEquals( "Contact Email Not Updated", extractOutput( ":", 0 ) );
    }

    @Test
    void getContactDetailsTest() {
        String input = "11\nADMIN1\n13\n";
        when( faculty.getContactDetails( "ADMIN1" ) ).thenReturn( new String[]{ "random@gmail.com", "99999" } );
        setInputToString( input );
        facultyUI.facultyInterfaceHomeScreen();
        assertEquals( "Email: random@gmail.com", extractOutput( "user:", -1 ) );
        // Assumption: $ does not occur anywhere in the number
        assertEquals( "Phone: 99999", extractOutput( "$", 0 ) );

        when( faculty.getContactDetails( "ADMIN1" ) ).thenReturn( new String[]{ "random@gmail.com" } );
        setInputToString( input );
        facultyUI.facultyInterfaceHomeScreen();
        assertEquals( "Could not retrieve details of user", extractOutput( "user:", 0 ) );

        when( faculty.getContactDetails( "ADMIN1" ) ).thenReturn( new String[]{ "random@gmail.com", null } );
        setInputToString( input );
        facultyUI.facultyInterfaceHomeScreen();
        assertEquals( "Email: random@gmail.com", extractOutput( "user:", 0 ) );

        when( faculty.getContactDetails( "ADMIN1" ) ).thenReturn( new String[]{ null, null } );
        setInputToString( input );
        facultyUI.facultyInterfaceHomeScreen();
        assertEquals( "User has not provided contact details", extractOutput( "user:", 0 ) );
    }

    @Test
    void updatePasswordTest() {
        String input = "12\nrandom\nrandom\n13\n";
        when( faculty.setPassword( "random" ) ).thenReturn( true );
        setInputToString( input );
        facultyUI.facultyInterfaceHomeScreen();
        assertEquals( "Password updated successfully", extractOutput( ":", 0 ) );

        when( faculty.setPassword( "random" ) ).thenReturn( false );
        setInputToString( input );
        facultyUI.facultyInterfaceHomeScreen();
        assertEquals( "Password Update Failed", extractOutput( ":", 0 ) );

        input = "12\nrandom\nother\n13\n";
        when( faculty.setPassword( "random" ) ).thenReturn( true );
        setInputToString( input );
        facultyUI.facultyInterfaceHomeScreen();
        assertEquals( "Please reenter the same password", extractOutput( ":", 0 ) );
    }
}