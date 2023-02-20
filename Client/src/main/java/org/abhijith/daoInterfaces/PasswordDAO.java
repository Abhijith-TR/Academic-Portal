package org.abhijith.daoInterfaces;

public interface PasswordDAO {
     boolean authenticateUser( String username, String password, String role );

      boolean logLogoutEntry( String id, String role );

     boolean logoutPreviousUser();
}
