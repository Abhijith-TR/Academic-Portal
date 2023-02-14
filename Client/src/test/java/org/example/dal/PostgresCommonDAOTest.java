package org.example.dal;

import org.example.daoInterfaces.CommonDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostgresCommonDAOTest {
    CommonDAO commonDAO;
    @BeforeEach
    void setUp() {
        commonDAO = new PostgresCommonDAO(
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
    void getStudentGradesForSemester() {
        commonDAO.getStudentGradesForSemester("2020CSB1062", 2022, 1);
    }

    @Test
    void getBatch() {
    }
}