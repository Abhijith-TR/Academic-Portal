package org.example.users;

import org.example.dal.PostgresFacultyDAO;
import org.example.daoInterfaces.FacultyDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FacultyTest {
    Faculty faculty;
    @BeforeEach
    void setUp() {
        FacultyDAO facultyDAO = new PostgresFacultyDAO(
                "jdbc:postgresql://localhost:5432/mini_project",
                "postgres",
                "admin"
        );
        faculty = new Faculty( "FAC1", facultyDAO );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void offerCourse() {
    }

    @Test
    void setCGAndPrerequisites() {
    }

    @Test
    void dropCourseOffering() {
    }

    @Test
    void testSetCourseCategory() {
        assertTrue( faculty.setCourseCategory( "GE103", "PC", "CS", new int[]{ 2020 } ) );
        assertFalse( faculty.setCourseCategory( "GE103", "PC", "MNC", new int[]{ 2020 } ) );
        assertFalse( faculty.setCourseCategory( "CS101", "PC", "CS", new int[]{ 2020 } ) );
        assertFalse( faculty.setCourseCategory( "GE103", "PC", "CS", new int[]{ 2021 } ) );
    }
}