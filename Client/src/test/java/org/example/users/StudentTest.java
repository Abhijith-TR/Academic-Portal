package org.example.users;

import org.example.dal.PostgresAdminDAO;
import org.example.dal.PostgresFacultyDAO;
import org.example.dal.PostgresStudentDAO;
import org.example.daoInterfaces.AdminDAO;
import org.example.daoInterfaces.FacultyDAO;
import org.example.daoInterfaces.StudentDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {
    Student    student;
    AdminDAO   adminDAO;
    FacultyDAO facultyDAO;

    @BeforeEach
    void setUp() {
        adminDAO = new PostgresAdminDAO(
                "jdbc:postgresql://localhost:5432/mini_project",
                "postgres",
                "admin"
        );
        facultyDAO = new PostgresFacultyDAO(
                "jdbc:postgresql://localhost:5432/mini_project",
                "postgres",
                "admin"
        );
        student = new Student( "2021CSB1062" );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void enroll() {
        adminDAO.setSessionEvent( "RUNNING", 2023, 2 );
        assertFalse( student.enroll( "HS507", "HS" ) );

        adminDAO.setSessionEvent( "ENROLLING", 2023, 2 );
        assertFalse( student.enroll( "CS101", "CS" ) );

        assertFalse( student.enroll( "CP303", "CS" ) );
        assertFalse( student.enroll( "CS539", "CS" ) );

        facultyDAO.setCGCriteria( "FAC38", "HS507", 10, new int[]{ 2023, 2 }, "HS" );
        assertFalse( student.enroll( "HS507", "HS" ) );
        facultyDAO.setCGCriteria( "FAC38", "HS507", 0, new int[]{ 2023, 2 }, "HS" );

        assertFalse( student.enroll( "CS999", "CS" ) );
        assertFalse( student.enroll("HS507", "CS") );

        facultyDAO.setCourseCategory( "HS507", 2023, 2, "HE", "CS", new int[]{ 2021 }, "HS" );
        assertTrue( student.enroll( "HS507", "HS" ) );
        assertFalse( student.enroll( "HS507", "HS" ) );
        facultyDAO.dropCourseOffering( "FAC38", "HS507", 2023, 2 );
        facultyDAO.insertCourseOffering( "HS507", 2023, 2, "HS", "FAC38" );
    }

    @Test
    void drop() {
    }

    @Test
    void getGradesForCourse() {
    }

    @Test
    void getGrades() {
    }

    @Test
    void getBatch() {
    }

    @Test
    void computeSGPA() {
    }

    @Test
    void getCGPA() {
    }

    @Test
    void getAvailableCourses() {
    }

    @Test
    void getRemainingCreditRequirements() {
    }
}