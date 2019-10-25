package com.ehotels.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.UUID;

import com.ehotels.cache.SessionInfo;
import com.ehotels.enums.Permission;
import com.ehotels.enums.UuidVerifResult;
import com.ehotels.models.BookingListModel;


public class AuthenticationManager {
    
    private String url = "jdbc:mysql://localhost:3306/";
    private String user = "REDACTED";
    private String dbName = "REDACTED";
    //TODO: possible point of failure between mySQL version. "com.mysql.cj.jdbc.Driver" vs "com.mysql.jdbc.Driver"
    private String driver = "com.mysql.cj.jdbc.Driver";
    private String password = "REDACTED";
    private Connection conn;
    public static final HashMap<String, SessionInfo> sessionIDs = new HashMap<String, SessionInfo>();
    
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
                            + "&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC&noAccessToProcedureBodies=true" );
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
    
    public UUID login(String email, String password, Permission permission) {
        
        String searchSQL;
        if(permission == Permission.EMPLOYEE) {
            searchSQL = "SELECT * FROM logincred,employee WHERE (logincred.id = employee.logincredid AND email = ?)";
        } else {
            searchSQL = "SELECT * FROM logincred,client WHERE (logincred.id = client.logincredid AND email = ?)";
        }
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
                    int clientOrEmployeeID = rs.getInt(4);
                    createSession(uniqueID, loginCredID, permission, clientOrEmployeeID);
                    preparedStatement.close();
                    return uniqueID;
                } else {
                    //the password was incorrect or wrong permission given (i.e. client tried to log on as employee)
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

    public void createSession(UUID uuid, int loginCredID, Permission permission, int clientOrEmployeeID) {
        String query = "INSERT INTO session (uuid, logincredid, expiration, permission) VALUES (?,?,?,?);";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setInt(2, loginCredID);
            //1 hour session
            preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis() + 1000 * 60 * 60));
            preparedStatement.setString(4, permission.toString());
            int result = preparedStatement.executeUpdate();
            if (result == 1) {
                SessionInfo info = new SessionInfo(new Timestamp(System.currentTimeMillis()+1000*60*60), loginCredID, permission, clientOrEmployeeID);
                sessionIDs.put(uuid.toString(), info);
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
    
    /**
     * WARNING: Performs no security whatsoever. Assumes that data is good.
     * @throws SQLException 
     */
    public void createEmployeeAccount(String email, String password, String ssn, 
            String lastName, String middleName, String firstName, int streetNumber, 
            String streetName, String city, String state, String zip) throws SQLException {
        
        String query = "{CALL employeeregister(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
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
        //TODO: find out if this returns something useful
        stmt.execute();
        stmt.close();
                
    }

    public UuidVerifResult uuidValid(String sessionUUID, Permission permission) {
        if (sessionIDs.containsKey(sessionUUID)) {
            SessionInfo info = sessionIDs.get(sessionUUID);
            if(permission != Permission.COMMON && permission != info.getPermission()) {
                System.out.println("Wrong permission found in cache. Wanted: " + permission + ", actual: " + info.getPermission());
                return UuidVerifResult.WRONG_PERM;
            } else if (!info.getExp().after(new Timestamp(System.currentTimeMillis()))) {
                sessionIDs.remove(sessionUUID);
                System.out.println("Expired uuid in cache");
                return UuidVerifResult.OTHER;
            } else {
                System.out.println("Valid uuid in cache");
                return UuidVerifResult.OK;
            }
        }

        String searchSQL = "SELECT expiration,logincredid,permission FROM session WHERE (uuid = ?)";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setString(1, sessionUUID);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                // lets check if the token is still valid
                boolean expired = rs.getTimestamp(1).before(new Timestamp(System.currentTimeMillis()));
                String realPermission = rs.getString(3);
                if (expired) {
                    // remove it
                    String deleteStmt = "DELETE FROM session WHERE uuid = ?";
                    preparedStatement = conn.prepareStatement(deleteStmt);
                    preparedStatement.setString(1, sessionUUID);
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                    System.out.println("Expired uuid in DB");
                    return UuidVerifResult.OTHER;
                } else if(!permission.equals(Permission.COMMON) && !realPermission.equals(permission.toString())) {
                    System.out.println("Wrong permission in DB. Wanted: " + permission + ", actual: " + realPermission);
                    preparedStatement.close();
                    return UuidVerifResult.WRONG_PERM;
                } else {
                    //This is gonna get ugly, sorry
                    int clientOrEmployeeID = -1;
                    if(Permission.valueOf(realPermission) == Permission.CLIENT) {
                        clientOrEmployeeID = getClientID(rs.getInt(2));
                    } else if(Permission.valueOf(realPermission) == Permission.EMPLOYEE) {
                        clientOrEmployeeID = getEmployeeID(rs.getInt(2));
                    } else {
                        clientOrEmployeeID = getClientID(rs.getInt(2));
                        if(clientOrEmployeeID == -1) {
                            clientOrEmployeeID = getEmployeeID(rs.getInt(2));
                        }
                    }
                    //TODO maybe security for clientOrEmployeeID = -1
                    sessionIDs.put(sessionUUID, new SessionInfo(rs.getTimestamp(1), rs.getInt(2), Permission.valueOf(realPermission), clientOrEmployeeID));
                    System.out.println("Valid uuid in DB");
                    preparedStatement.close();
                    return UuidVerifResult.OK;
                }
            }
            preparedStatement.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Did not even find uuid in DB, so invalid");
        return UuidVerifResult.OTHER;
    }
    
    private int getClientID(int loginCredID) {
        System.out.println("Had to look for clientID in DB");
        
        int clientID = -1;
        String searchString = "SELECT id FROM client WHERE (logincredid  = ?)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchString);
            preparedStatement.setInt(1, loginCredID);
            
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                clientID = rs.getInt(1);
            }
            preparedStatement.close();
            return clientID;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return clientID;
        
    }

    private int getEmployeeID(int loginCredID) {
        System.out.println("Had to look for employeeID in DB");
        
        int employeeID = -1;
        String searchString = "SELECT id FROM employee WHERE (logincredid  = ?)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchString);
            preparedStatement.setInt(1, loginCredID);
            
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                employeeID = rs.getInt(1);
            }
            preparedStatement.close();
            return employeeID;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return employeeID;
        
    }
    
    public static int getUserId(String uuid) {
        SessionInfo info = sessionIDs.get(uuid);
        if(info != null) {
            return info.getClientOrEmployeeID();
        } else {
            return -1;
        }
    }
    
    public static int getLoginCredId(String uuid) {
        SessionInfo info = sessionIDs.get(uuid);
        if(info != null) {
            return info.getLoginCredID();
        } else {
            return -1;
        }
    }
    
    public void destroy() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
