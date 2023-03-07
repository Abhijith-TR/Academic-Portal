package org.abhijith.daoInterfaces;

public interface PasswordDAO {
    // Returns true if the password matches and the IN log is inserted. False otherwise
     boolean authenticateUser( String username, String password, String role );

     // Returns true if the OUT entry has been inserted into the database
      boolean logLogoutEntry( String id, String role );

      // Returns true if the previous user was logged out. False otherwise
     boolean logoutPreviousUser();
}
