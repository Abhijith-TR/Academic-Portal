package org.abhijith.users;

import org.abhijith.dal.PostgresAdminDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

class AdminTest {
    Admin admin = new Admin("ADMIN1");
    PostgresAdminDAO mockDAO;

    @BeforeEach
    void setUp() {
        mockDAO = Mockito.mock( PostgresAdminDAO.class );
        admin.setAdminDAO( mockDAO );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getGradesOfOffering() {
        // Returns empty array due to invalid arguments
        assertArrayEquals( new String[][]{}, admin.getGradesOfOffering( null, 2023, 2, "HS" ) );
        assertArrayEquals( new String[][]{}, admin.getGradesOfOffering( "CS301", 0, 2, "HS" ) );
        assertArrayEquals( new String[][]{}, admin.getGradesOfOffering( "CS301", 2023, 0, "HS" ) );
        assertArrayEquals( new String[][]{}, admin.getGradesOfOffering( "CS301", 2023, 2, null ) );

        // If the details are valid this function works as expected ( Mock object returns null and the functions returns this as expected )
        assertNull( admin.getGradesOfOffering( "CS301", 2023, 2, "CS" ) );
    }

    @Test
    void insertStudent() {
        // Returns false as the arguments are invalid
    }

    @Test
    void insertFaculty() {
    }

    @Test
    void insertCourseIntoCatalog() {
    }

    @Test
    void dropCourseFromCatalog() {
    }

    @Test
    void getGradesOfStudent() {
    }

    @Test
    void createBatch() {
    }

    @Test
    void insertCoreCourses() {
    }

    @Test
    void checkStudentPassStatus() {
    }

    @Test
    void generateTranscripts() {
    }

    @Test
    void startNewSession() {
    }

    @Test
    void setCurrentSessionStatus() {
    }
}