package org.example.users;

import org.example.daoInterfaces.CommonDAO;

public class User {
    protected final String id;
    private CommonDAO commonDAO;
    public User(String id) {
        this.id = id;
    }

    public void setCommonDAO( CommonDAO commonDAO ) {
        this.commonDAO = commonDAO;
    }

    public String getID() {
        return this.id;
    }

    public boolean setPhoneNumber( String phoneNumber ) {
        return commonDAO.setPhoneNumber( this.id, phoneNumber );
    }

    public boolean setEmail( String email ) {
        return commonDAO.setEmail( this.id, email );
    }

    public String[] getContactDetails( String userID ) {
        return commonDAO.getContactDetails( userID );
    }

    public boolean setPassword( String password ) {
        return commonDAO.setPassword( this.id, password );
    }
}
