package org.example.users;

public class User {
    protected final String id;
    public User(String id) {
        this.id = id;
    }

    public String getID() {
        return this.id;
    }
}
