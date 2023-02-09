package org.example;

import org.example.database.Database;
import org.example.interfaces.Interface;

public class Main {
    public static void main(String[] args) {
        Database databaseConnection = new Database();
        Interface homeInterface = new Interface();
        homeInterface.mainInterface(databaseConnection);
    }
}