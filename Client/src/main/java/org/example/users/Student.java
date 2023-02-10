package org.example.users;

import org.example.dal.Database;
import org.example.dal.StudentDAO;

public class Student extends User {
    public Student(String id) {
        super(id);
    }

    public boolean updateProfile(int newPhoneNumber, Database databaseConnection) {
        return databaseConnection.updatePhoneNumber("student", newPhoneNumber);
    }

    public boolean enroll(String courseCode, StudentDAO studentDAO) {
//        studentDAO.enroll(courseCode);
        return true;
    }
}
