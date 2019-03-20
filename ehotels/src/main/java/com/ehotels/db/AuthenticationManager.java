package com.ehotels.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import com.ehotels.enums.Permission;


public class AuthenticationManager {
    
    private String url = "jdbc:mysql://localhost:3306/";
    private String user = "ehotelsUser";
    private String dbName = "ehotels";
    //TODO: possible point of failure between mySQL version. "com.mysql.cj.jdbc.Driver" vs "com.mysql.jdbc.Driver"
    private String driver = "com.mysql.cj.jdbc.Driver";
    private String password = "v1ncentV@diner";
    private Connection conn;
    
    public AuthenticationManager() {
        connect();
    }
    
    private void connect() {
        try {
            if(conn == null || conn.isClosed()) {
                try {
                    Class.forName(driver).newInstance();
                    //TODO: look for better solution to "?autoReconnect=true&useSSL=false"
                    conn = DriverManager.getConnection(url + dbName 
                            + "?user=" + user 
                            +"&password=" + password 
                            + "&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC" );
                    /*
                     * conn = DriverManager.getConnection( url + dbName, user, password);
                     */
                } catch (InstantiationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public UUID login(String email, String password) {
        String searchSQL = "SELECT * FROM logincred WHERE email = ?";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setString(1, email);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                //now check the password
                //TODO: encrypt
                if(password.equals(rs.getString(3))) {
                    //if we get here, obviously there is one user that matches, lets generate an id for their session
                    UUID uniqueID = UUID.randomUUID(); //TODO im going quick and didn't bother to deal with the case where it's possible
                                                       //for two of the same id to be generated, very rare
                    int loginCredID = rs.getInt(1);
                    createSession(uniqueID, loginCredID);
                    preparedStatement.close();
                    return uniqueID;
                } else {
                    //the password was incorrect
                }
            }
            preparedStatement.close();
            return null;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public void createSession(UUID uuid, int loginCredID) {
        //TODO: Add cache stuff if needed
        String query = "INSERT INTO session (uuid, logincredid, expiration) VALUES (?,?,?);";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setInt(2, loginCredID);
            //1 hour session
            preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis() + 1000 * 60 * 60));
            int result = preparedStatement.executeUpdate();
            if (result == 1) {
                // success
            }
            preparedStatement.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean emailExists(String email) {
        String searchSQL = "SELECT * FROM logincred WHERE email = ?";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setString(1, email);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                preparedStatement.close();
                return true;
            }
            preparedStatement.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    /**
     * WARNING: Performs no security whatsoever. Assumes that data is good.
     * @throws SQLException 
     */
    public void createClientAccount(String email, String password, String ssn, 
            String lastName, String middleName, String firstName, int streetNumber, 
            String streetName, String city, String state, String zip) throws SQLException {
        
        String query = "{CALL clientregister(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        CallableStatement stmt;
        
        stmt = (CallableStatement) conn.prepareCall(query);
        stmt.setString(1,  email);
        stmt.setString(2,  password);
        stmt.setString(3,  ssn);
        stmt.setString(4,  lastName);
        stmt.setString(5,  middleName);
        stmt.setString(6,  firstName);
        stmt.setInt(7,  streetNumber);
        stmt.setString(8,  streetName);
        stmt.setString(9,  city);
        stmt.setString(10,  state);
        stmt.setString(11,  zip);
        stmt.setDate(12, Date.valueOf(java.time.LocalDate.now()));
        //TODO: find out if this returns something useful
        stmt.execute();
        stmt.close();
                
    }

    public boolean uuidValid(String sessionUUID, Permission permission) {
        //TODO: implement caching stuff
        //TODO: maybe look for more efficient option than joint
        String searchSQL;
        if(permission == Permission.CLIENT) {
            searchSQL = "SELECT expiration FROM session INNER JOIN client ON session.logincreid = client.logincredid WHERE (uuid = ?)";
        } else if(permission == Permission.EMPLOYEE) {
            searchSQL = "SELECT expiration FROM session INNER JOIN employee ON session.logincredid = employee.logincredid WHERE (uuid = ?)";
        } else { //permission == Permission.COMMON
            searchSQL = "SELECT expiration FROM session WHERE (uuid = ?)";
        }
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setString(1, sessionUUID);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                // lets check if the token is still valid
                boolean valid = rs.getTimestamp(1).after(new Timestamp(System.currentTimeMillis()));
                if (!valid) {
                    // remove it
                    String deleteStmt = "DELETE FROM session WHERE uuid = ?";
                    preparedStatement = conn.prepareStatement(deleteStmt);
                    preparedStatement.setString(1, sessionUUID);
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                } else {
                    //TODO: implement caching stuff
                    return true;
                }
            }
            preparedStatement.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    
    public void destroy() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
