package org.abhijith.ui;

import org.abhijith.users.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class StudentUITest {
    private InputStream systemInput = System.in;

    private final PrintStream           systemOutput = System.out;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    String    id        = "2020CSB1062";
    StudentUI studentUI = new StudentUI( id );
    Student   student;

    @BeforeEach
    void setUp() {
        System.setOut( new PrintStream( outputStream ) );
        student = Mockito.mock( Student.class );
        studentUI.setStudent( student );
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
        output = output[output.length - 16 + index].split( splitter );
        return output[output.length - 1].trim();
    }

    @Test
    void enrollTest() {
        String input = "1\nCS101\nCS\n12\n";
        when( student.enroll( "CS101", "CS" ) ).thenReturn( true );
        setInputToString( input );
        studentUI.studentInterfaceHomeScreen();
        assertEquals( "Enrolled Successfully", extractOutput( ":", 0 ) );

        when( student.enroll( "CS101", "CS" ) ).thenReturn( false );
        setInputToString( input );
        studentUI.studentInterfaceHomeScreen();
        assertEquals( "Enrollment Failed. Please check if you are eligible to enroll", extractOutput( ":", 0 ) );
    }

    @Test
    void dropTest() {
        String input = "2\nCS101\n12\n";
        when( student.drop( "CS101" ) ).thenReturn( true );
        setInputToString( input );
        studentUI.studentInterfaceHomeScreen();
        assertEquals( "Course dropped successfully", extractOutput( ":", 0 ) );

        setInputToString( input );
        when( student.drop( "CS101" ) ).thenReturn( false );
        studentUI.studentInterfaceHomeScreen();
        assertEquals( "Course drop failed. Please verify that you are enrolled in this course", extractOutput( ":", 0 ) );
    }

    @Test
    void viewGradesForEntireDegree() {
        String[][][] records = new String[][][]{ { { "CS101", "DISCRETE MATHEMATICS", "A", "4.5" } } };

        String input = "3\n12\n";
        when( student.getGradesForDegree() ).thenReturn( records );
        when( student.getBatch() ).thenReturn( 2020 );
        when( student.getSGPA( records[0] )).thenReturn( 10.00 );
        setInputToString( input );
        studentUI.studentInterfaceHomeScreen();
        assertEquals( "Records - Year: 2020 Semester: 1 SGPA: 10.00", extractOutput( "choice:", -3 ) );
        assertEquals( "Course Code   Course Title           Grade   Credits", extractOutput( "choice:", -2 ) );
        assertEquals( "CS101         DISCRETE MATHEMATICS   A       4.5", extractOutput( "choice:", -1 ) );
    }

    @Test
    void viewGradesForParticularSessionTest() {
        String[][] records = new String[][]{ { "CS101", "DISCRETE MATHEMATICS", "A", "4.5" } };

        String input = "4\n2020\n1\n12\n";
        when( student.getGrades( 2020, 1 ) ).thenReturn( new String[][]{} );
        setInputToString( input );
        studentUI.studentInterfaceHomeScreen();
        assertEquals( "No records found for session 2020-1", extractOutput( ":", -1 ) );

        setInputToString( input );
        when( student.getGrades( 2020, 1 ) ).thenReturn( records );
        when( student.getSGPA( records ) ).thenReturn( 10.0 );
        studentUI.studentInterfaceHomeScreen();
        assertEquals( "Records - Year: 2020 Semester: 1 SGPA: 10.00", extractOutput( "semester:", -3 ) );
        assertEquals( "Course Code   Course Title           Grade   Credits", extractOutput( ":", -2 ) );
        assertEquals( "CS101         DISCRETE MATHEMATICS   A       4.5", extractOutput( ":", -1 ) );
    }

    @Test
    void getCGPATest() {
        String input = "5\n12\n";
        when( student.getCGPA() ).thenReturn( 9.866 );
        setInputToString( input );
        studentUI.studentInterfaceHomeScreen();
        assertEquals( "CGPA: 9.87", extractOutput( "choice:", 1 ) );
    }

    @Test
    void getAvailableCoursesTest() {
        String[][] record = new String[][]{ { "CS101", "DISCRETE MATHEMATICS", "DR APURVA MUDGAL", "{}", "CS", "PC" } };

        String input = "6\n12\n";
        when( student.getAvailableCourses() ).thenReturn( new String[][]{} );
        setInputToString( input );
        studentUI.studentInterfaceHomeScreen();
        assertEquals( "No courses offered this session", extractOutput( ":", 0 ) );

        setInputToString( input );
        when( student.getAvailableCourses() ).thenReturn( record );
        studentUI.studentInterfaceHomeScreen();
        assertEquals( "Course Code   Course Title           Instructor         Prerequisites   Department   Category", extractOutput( ":", -2 ) );
        assertEquals( "CS101         DISCRETE MATHEMATICS   DR APURVA MUDGAL   {}              CS           PC", extractOutput( ":", -1 ) );
    }

    @Test
    void getRemainingCreditRequirementsTest() {
        HashMap<String, Double> remainingCredit = new HashMap<>();
        remainingCredit.put( "PC", 10.0 );
        remainingCredit.put( "PE", 4.5 );

        String input = "7\n12\n";
        when( student.getRemainingCreditRequirements() ).thenReturn( remainingCredit );
        setInputToString( input );
        studentUI.studentInterfaceHomeScreen();
        assertEquals( "PC     PE", extractOutput( ":", -2 ) );
        assertEquals( "10.0   4.5", extractOutput( ":", -1 ) );
    }

    @Test
    void updatePhoneNumberTest() {
        String input = "8\n99999\n12\n";
        when( student.setPhoneNumber( "99999" ) ).thenReturn( true );
        setInputToString( input );
        studentUI.studentInterfaceHomeScreen();
        assertEquals( "Phone Number Updated Successfully", extractOutput( ":", 0 ) );

        when( student.setPhoneNumber( "99999" ) ).thenReturn( false );
        setInputToString( input );
        studentUI.studentInterfaceHomeScreen();
        assertEquals( "Phone Number Update Failed", extractOutput( ":", 0 ) );
    }

    @Test
    void updateEmailTest() {
        String input = "9\nrandom@gmail.com\n12\n";
        when( student.setEmail( "random@gmail.com" ) ).thenReturn( true );
        setInputToString( input );
        studentUI.studentInterfaceHomeScreen();
        assertEquals( "Contact Email Updated Successfully", extractOutput( ":", 0 ) );

        when( student.setEmail( "random@gmail.com" ) ).thenReturn( false );
        setInputToString( input );
        studentUI.studentInterfaceHomeScreen();
        assertEquals( "Contact Email Not Updated", extractOutput( ":", 0 ) );
    }

    @Test
    void getContactDetailsTest() {
        String input = "10\nADMIN1\n12\n";
        when( student.getContactDetails( "ADMIN1" ) ).thenReturn( new String[]{ "random@gmail.com", "99999" } );
        setInputToString( input );
        studentUI.studentInterfaceHomeScreen();
        assertEquals( "Email: random@gmail.com", extractOutput( "user:", -1 ) );
        // Assumption: $ does not occur anywhere in the number
        assertEquals( "Phone: 99999", extractOutput( "$", 0 ) );

        when( student.getContactDetails( "ADMIN1" ) ).thenReturn( new String[]{ "random@gmail.com" } );
        setInputToString( input );
        studentUI.studentInterfaceHomeScreen();
        assertEquals(  "Could not retrieve details of user", extractOutput( "user:", 0 ) );

        when( student.getContactDetails( "ADMIN1" ) ).thenReturn( new String[]{ "random@gmail.com", null } );
        setInputToString( input );
        studentUI.studentInterfaceHomeScreen();
        assertEquals(  "Email: random@gmail.com", extractOutput( "user:", 0 ) );

        when( student.getContactDetails( "ADMIN1" ) ).thenReturn( new String[]{ null, null } );
        setInputToString( input );
        studentUI.studentInterfaceHomeScreen();
        assertEquals(  "User has not provided contact details", extractOutput( "user:", 0 ) );
    }

    @Test
    void updatePasswordTest() {
        String input = "11\nrandom\nrandom\n12\n";
        when( student.setPassword( "random" )).thenReturn( true );
        setInputToString( input );
        studentUI.studentInterfaceHomeScreen();
        assertEquals( "Password updated successfully", extractOutput( ":", 0 ) );

        when( student.setPassword( "random" )).thenReturn( false );
        setInputToString( input );
        studentUI.studentInterfaceHomeScreen();
        assertEquals( "Password Update Failed", extractOutput( ":", 0 ) );

        input = "11\nrandom\nother\n12\n";
        when( student.setPassword( "random" )).thenReturn( true );
        setInputToString( input );
        studentUI.studentInterfaceHomeScreen();
        assertEquals( "Please reenter the same password", extractOutput( ":", 0 ) );
    }
}