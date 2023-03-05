package org.abhijith.ui;

import org.abhijith.users.Admin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class AdminUITest {
    private       InputStream           systemInput  = System.in;
    private final PrintStream           systemOutput = System.out;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    String  id      = "ADMIN1";
    AdminUI adminUI = new AdminUI( id );
    Admin   admin;

    @BeforeEach
    void setUp() {
        System.setOut( new PrintStream( outputStream ) );
        admin = Mockito.mock( Admin.class );
        adminUI.setAdmin( admin );
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
        output = output[output.length - 21 + index].split( splitter );
        return output[output.length - 1].trim();
    }

    @Test
    void insertStudentTest() {
        String input = "1\n2020CSB1062\nABHIJITH\nCS\n2020\n17\n";
        setInputToString( input );
        when( admin.insertStudent( "2020CSB1062", "ABHIJITH", "CS", 2020 ) ).thenReturn( true );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Student inserted successfully", extractOutput( ":", 0 ) );

        setInputToString( input );
        when( admin.insertStudent( "2020CSB1062", "ABHIJITH", "CS", 2020 ) ).thenReturn( false );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Student insertion failed", extractOutput( ":", 0 ) );
    }

    @Test
    void insertFacultyTest() {
        String input = "2\nFAC1\nSURESH\nCS\n17\n";
        setInputToString( input );
        when( admin.insertFaculty( "FAC1", "SURESH", "CS" ) ).thenReturn( true );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Faculty inserted successfully", extractOutput( ":", 0 ) );

        setInputToString( input );
        when( admin.insertFaculty( "FAC1", "SURESH", "CS" ) ).thenReturn( false );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Faculty insertion failed", extractOutput( ":", 0 ) );
    }

    @Test
    void insertCourseTest() {
        String input = "3\nCS301\nDATABASES\n1\n2\n3\n4\n5\n1\nCS101\n17\n";
        setInputToString( input );
        when( admin.insertCourseIntoCatalog( "CS301", "DATABASES", new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0 }, new String[]{ "CS101" } ) ).thenReturn( true );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Course inserted successfully", extractOutput( ":", 0 ) );

        setInputToString( input );
        when( admin.insertCourseIntoCatalog( "CS301", "DATABASES", new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0 }, new String[]{ "CS101" } ) ).thenReturn( false );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Course insertion failed. Please verify course details", extractOutput( ":", 0 ) );
    }

    @Test
    void removeCourseTest() {
        String input = "4\nCS101\n17\n";
        setInputToString( input );
        when( admin.dropCourseFromCatalog( "CS101" ) ).thenReturn( true );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Course dropped successfully", extractOutput( ":", 0 ) );

        setInputToString( input );
        when( admin.dropCourseFromCatalog( "CS101" ) ).thenReturn( false );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Course Drop Failed", extractOutput( ":", 0 ) );
    }

    @Test
    void getRecordsTest() {
        String       input   = "5\n2020CSB1062\n2020\n17\n";
        String[][][] records = new String[][][]{ { { "CS101", "DISCRETE MATHEMATICS", "A", "4.5" } } };
        setInputToString( input );
        when( admin.getGradesOfStudent( "2020CSB1062" ) ).thenReturn( records );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Records - Year: 2020 Semester: 1", extractOutput( "\\):", -3 ) );
        assertEquals( "Course Code   Course Title           Grade   Credits", extractOutput( "choice:", -2 ) );
        assertEquals( "CS101         DISCRETE MATHEMATICS   A       4.5", extractOutput( "choice:", -1 ) );
    }

    @Test
    void getCourseRecordsTest() {
        String     input   = "6\nCS101\n2020\n1\nCS\n17\n";
        String[][] records = new String[][]{ { "2020CSB1062", "A" } };
        when( admin.getGradesOfOffering( "CS101", 2020, 1, "CS" ) ).thenReturn( records );
        setInputToString( input );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Entry Number   Grade", extractOutput( ":", -2 ) );
        assertEquals( "2020CSB1062    A", extractOutput( ":", -1 ) );

        when( admin.getGradesOfOffering( "CS101", 2020, 1, "CS" ) ).thenReturn( new String[][]{} );
        setInputToString( input );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "No course found with given specifications", extractOutput( ":", 0 ) );
    }

    @Test
    void insertBatchTest() {
        String input = "7\n2020\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n17\n";
        when( admin.createBatch( 2020, new double[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 } ) ).thenReturn( true );
        setInputToString( input );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Curriculum successfully created for batch", extractOutput( ":", 0 ) );

        when( admin.createBatch( 2020, new double[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 } ) ).thenReturn( false );
        setInputToString( input );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Please check details and try again", extractOutput( ":", 0 ) );
    }

    @Test
    void checkGraduationTest() {
        String input = "9\n2020CSB1062\n17\n";
        setInputToString( input );
        when( admin.checkStudentPassStatus( "2020CSB1062" ) ).thenReturn( true );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Student eligible for graduation", extractOutput( ":", 0 ) );

        setInputToString( input );
        when( admin.checkStudentPassStatus( "2020CSB1062" ) ).thenReturn( false );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Student ineligible for graduation", extractOutput( ":", 0 ) );
    }

    @Test
    void generateTranscriptTest() {
        String input = "10\n2020\nCS\n17\n";
        setInputToString( input );
        when( admin.generateTranscripts( 2020, "CS" ) ).thenReturn( true );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Transcripts Generated Successfully", extractOutput( ":", 0 ) );

        setInputToString( input );
        when( admin.generateTranscripts( 2020, "CS" ) ).thenReturn( false );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Please try again later", extractOutput( ":", 0 ) );
    }

    @Test
    void updatePhoneNumberTest() {
        String input = "11\n99999\n17\n";
        when( admin.setPhoneNumber( "99999" ) ).thenReturn( true );
        setInputToString( input );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Phone Number Updated Successfully", extractOutput( ":", 0 ) );

        when( admin.setPhoneNumber( "99999" ) ).thenReturn( false );
        setInputToString( input );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Phone Number Update Failed", extractOutput( ":", 0 ) );
    }

    @Test
    void updateEmailTest() {
        String input = "12\nrandom@gmail.com\n17\n";
        when( admin.setEmail( "random@gmail.com" ) ).thenReturn( true );
        setInputToString( input );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Contact Email Updated Successfully", extractOutput( ":", 0 ) );

        when( admin.setEmail( "random@gmail.com" ) ).thenReturn( false );
        setInputToString( input );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Contact Email Not Updated", extractOutput( ":", 0 ) );
    }

    @Test
    void getContactDetailsTest() {
        String input = "13\nADMIN1\n17\n";
        when( admin.getContactDetails( "ADMIN1" ) ).thenReturn( new String[]{ "random@gmail.com", "99999" } );
        setInputToString( input );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Email: random@gmail.com", extractOutput( "user:", -1 ) );
        // Assumption: $ does not occur anywhere in the number
        assertEquals( "Phone: 99999", extractOutput( "$", 0 ) );

        when( admin.getContactDetails( "ADMIN1" ) ).thenReturn( new String[]{ "random@gmail.com" } );
        setInputToString( input );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Could not retrieve details of user", extractOutput( "user:", 0 ) );

        when( admin.getContactDetails( "ADMIN1" ) ).thenReturn( new String[]{ "random@gmail.com", null } );
        setInputToString( input );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Email: random@gmail.com", extractOutput( "user:", 0 ) );

        when( admin.getContactDetails( "ADMIN1" ) ).thenReturn( new String[]{ null, null } );
        setInputToString( input );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "User has not provided contact details", extractOutput( "user:", 0 ) );
    }

    @Test
    void updatePasswordTest() {
        String input = "14\nrandom\nrandom\n17\n";
        when( admin.setPassword( "random" ) ).thenReturn( true );
        setInputToString( input );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Password updated successfully", extractOutput( ":", 0 ) );

        when( admin.setPassword( "random" ) ).thenReturn( false );
        setInputToString( input );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Password Update Failed", extractOutput( ":", 0 ) );

        input = "14\nrandom\nother\n17\n";
        when( admin.setPassword( "random" ) ).thenReturn( true );
        setInputToString( input );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Please reenter the same password", extractOutput( ":", 0 ) );
    }

    @Test
    void startNewSessionTest() {
        String input = "15\n17\n";
        setInputToString( input );
        when( admin.startNewSession() ).thenReturn( true );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "New session inserted", extractOutput( ":", 0 ) );

        setInputToString( input );
        when( admin.startNewSession() ).thenReturn( false );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "New session insertion failed", extractOutput( ":", 0 ) );
    }

    @Test
    void setEventTest() {
        String input = "16\n1\n17\n";
        setInputToString( input );
        when( admin.setCurrentSessionStatus( "ENROLLING" )).thenReturn( true );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Event updated successfully", extractOutput( ":", 0 ) );

        setInputToString( input );
        when( admin.setCurrentSessionStatus( "ENROLLING" )).thenReturn( false );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Event update failed", extractOutput( ":", 0 ) );

        input = "16\n0\n17\n";
        setInputToString( input );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Invalid Choice", extractOutput( ":", 0 ) );

        input = "16\n6\n17\n";
        setInputToString( input );
        adminUI.adminInterfaceHomeScreen();
        assertEquals( "Invalid Choice", extractOutput( ":", 0 ) );
    }
}