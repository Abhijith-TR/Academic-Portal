package org.example.dal;

import org.example.daoInterfaces.FacultyDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostgresFacultyDAOTest {
    FacultyDAO facultyDAO;

    @BeforeEach
    void setUp() {
        facultyDAO = new PostgresFacultyDAO(
                "jdbc:postgresql://localhost:5432/mini_project",
                "postgres",
                "admin"
        );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void insertCourseOffering() {
        assertTrue( facultyDAO.insertCourseOffering( "CS301", 2020, 1, "CS", "FAC1" ) );
    }

    @Test
    void getDepartment() {
    }

    @Test
    void setCGCriteria() {
        assertTrue( facultyDAO.setCGCriteria( "FAC1", "GE103", 8.0, new int[]{ 2020, 1 } ) );
    }

    @Test
    void setInstructorPrerequisites() {
        assertTrue( facultyDAO.setInstructorPrerequisites( "FAC1", "GE103", new String[][]{ { "CS101", "8" }, { "CS201", "10", "CS203", "8" } }, new int[]{ 2020, 1 } ) );
    }

    @Test
    void dropCourseOffering() {
        assertFalse( facultyDAO.dropCourseOffering( "FAC1", "CS301", 2020, 1 ) );
    }

    @Test
    void setCourseCategory() {
        assertTrue( facultyDAO.setCourseCategory( "GE103", 2020, 1, "PC", "CS", new int[]{ 2020, 2021 } ) );
    }

    @Test
    void verifyCore() {
        assertTrue( facultyDAO.verifyCore( "GE103", "CS", 2020 ) );
        assertFalse( facultyDAO.verifyCore( "CH201", "CS", 2020 ));
    }
}