package org.example.users;

import org.example.dal.PostgresAdminDAO;
import org.example.daoInterfaces.AdminDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminTest {
    Admin admin;
    @BeforeEach
    void setUp() {
        AdminDAO adminDAO = new PostgresAdminDAO(
                "jdbc:postgresql://localhost:5432/mini_project",
                "postgres",
                "admin"
        );
        admin = new Admin("ADMIN1", adminDAO);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getGradesOfOffering() {
    }

    @Test
    void insertStudent() {
    }

    @Test
    void insertFaculty() {
    }

    @Test
    void insertCourse() {
//        admin.insertCourse("CS111", "")
    }
}