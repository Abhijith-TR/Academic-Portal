package org.example.admin;

import org.example.database.Database;

public class Admin {
    Database databaseConnection;
    public Admin(Database databaseConnection) {
        this.databaseConnection = databaseConnection;
    }
}
