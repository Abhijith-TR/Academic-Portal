package org.example.users;

import org.example.database.Database;
import org.example.users.User;

public class Student extends User {
    public Student(String id) {
        super(id);
    }

    public boolean updateProfile(int newPhoneNumber, Database databaseConnection) {
        return databaseConnection.updatePhoneNumber("student", newPhoneNumber);
    }
}
