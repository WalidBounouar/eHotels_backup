package com.ehotels.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.ehotels.models.ClientModel;
import com.ehotels.models.EmployeeModel;
import com.ehotels.models.RoomBasicModel;
import com.ehotels.models.UserModel;
import com.ehotels.utilities.SanityUtils;

public class PeopleManager {

    private String url = "jdbc:mysql://localhost:3306/";
    private String user = "REDACTED";
    private String dbName = "REDACTED";
    //TODO: possible point of failure between mySQL version. "com.mysql.cj.jdbc.Driver" vs "com.mysql.jdbc.Driver"
    private String driver = "com.mysql.cj.jdbc.Driver";
    private String password = "REDACTED";
    private Connection conn;
        
    public PeopleManager() {
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
    
    public void destroy() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public UserModel whoAmI(int userID) {
                
        String searchSQL = 
                "(SELECT client.id AS userid, ssn, lastname, middlename, firstname, streetnumber, streetName, city, state, zip \r\n" + 
                "    FROM logincred, client WHERE(logincred.id = client.logincredid AND logincred.id = ?))\r\n" + 
                "UNION\r\n" + 
                "(SELECT employee.id AS userid, ssn, lastname, middlename, firstname, streetnumber, streetName, city, state, zip \r\n" + 
                "    FROM logincred, employee WHERE(logincred.id = employee.logincredid AND logincred.id = ?))";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setInt(1, userID);
            preparedStatement.setInt(2, userID);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                UserModel user = new UserModel(rs.getInt(1), rs.getString(2), 
                        rs.getString(3), rs.getString(4), rs.getString(5), rs.getInt(6), 
                        rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10));
                
                preparedStatement.close();
                return user;
            }
            preparedStatement.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
        
    }

    public boolean ssnExists(String ssn, boolean isEmployee) {
        
        String searchSQL = 
                "(SELECT client.id AS userid\r\n" + 
                "FROM client WHERE(client.ssn = ?))";
        
        if(isEmployee) {
            searchSQL = 
                    "(SELECT employee.id AS userid \r\n" + 
                    "FROM employee WHERE(employee.ssn = ?))";
        }
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setString(1, ssn);
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
    
    public boolean ssnExistsForOtherUser(int id, String ssn, boolean isEmployee) {
        
        String searchSQL = 
                "(SELECT client.id AS userid\r\n" + 
                "FROM client WHERE(client.ssn = ? AND client.id <> ?))";
        
        if(isEmployee) {
            searchSQL = 
                    "(SELECT employee.id AS userid \r\n" + 
                    "FROM employee WHERE(employee.ssn = ? AND employee.id <> ?))";
        }
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setString(1, ssn);
            preparedStatement.setInt(2, id);
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
    
    public List<EmployeeModel> getAllManagers() {
        
        List<EmployeeModel> managers = new LinkedList<EmployeeModel>();
        
        //TODO maybe find more elegant way to reject the SYSTEM user (the one w/ id = 1)
        String searchSQL = 
                "SELECT * FROM employee\r\n" + 
                "WHERE(\r\n" + 
                "    id IN (SELECT employeeid FROM employeerole WHERE(role = 'Manager' OR role = 'manager'))\r\n" + 
                "    AND id <> 1\r\n" + 
                ")";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                //NOTE: I don't bother to go get the roles for this method. Only want manager info.
                EmployeeModel manager = new EmployeeModel(rs.getInt(1), rs.getString(2), 
                        rs.getString(3), rs.getString(4), rs.getString(5), rs.getInt(6), 
                        rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), null);
                managers.add(manager);
            }
            preparedStatement.close();
            return managers;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return managers;
        
    }
    
    public List<EmployeeModel> getAllEmployees() {
        
        List<EmployeeModel> employees = new LinkedList<EmployeeModel>();
        
        //TODO maybe find more elegant way to reject the SYSTEM user (the one w/ id = 1)
        String searchSQL = "SELECT * FROM employee WHERE id <> 1";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                //NOTE: I don't bother to go get the roles for this method. Only want manager info.
                EmployeeModel manager = new EmployeeModel(rs.getInt(1), rs.getString(2), 
                        rs.getString(3), rs.getString(4), rs.getString(5), rs.getInt(6), 
                        rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), null);
                employees.add(manager);
            }
            preparedStatement.close();
            return employees;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return employees;
        
    }
    
    public List<ClientModel> getAllClients() {
        
        List<ClientModel> clients = new LinkedList<ClientModel>();
        
        String searchSQL = "SELECT * FROM client";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                ClientModel client = new ClientModel(rs.getInt(1), rs.getString(2), 
                        rs.getString(3), rs.getString(4), rs.getString(5), rs.getInt(6), 
                        rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), 
                        rs.getString(11));
                clients.add(client);
            }
            preparedStatement.close();
            return clients;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return clients;
        
    }

    public boolean clientExists(int clientID) {
        
        String searchSQL = "SELECT id FROM client WHERE(id = ?)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setInt(1, clientID);
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
    
    public boolean employeeExists(int employeeID) {
        
        String searchSQL = "SELECT id FROM employee WHERE(id = ?)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setInt(1, employeeID);
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

    public boolean deleteClient(int clientID) {
        
        String query = 
                "DELETE logincred, client FROM logincred INNER JOIN client \r\n" + 
                "WHERE (logincred.id = client.logincredid \r\n" + 
                "AND client.id = ?)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, clientID);
            int result = preparedStatement.executeUpdate();
            if(result == 2) {
                //success
                return true;
            }
            preparedStatement.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
        
    }
    
    public boolean deleteEmployee(int employeeID) {
        
        String query = 
                "DELETE logincred, employee FROM logincred INNER JOIN employee \r\n" + 
                "WHERE (logincred.id = employee.logincredid \r\n" + 
                "AND employee.id = ?)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, employeeID);
            int result = preparedStatement.executeUpdate();
            if(result == 2) {
                //success
                return true;
            }
            preparedStatement.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
        
    }
    
    public boolean updateClient(UserModel model, boolean isEmployee) throws SQLException {
        
        String queryStart = isEmployee ? "UPDATE employee SET " : "UPDATE client SET ";
        
        StringBuilder queryBase = new StringBuilder(queryStart);
        List<Object> values = new ArrayList<Object>();
        
        //Add ssn
        if(!SanityUtils.isEmpty(model.getSsn())) {
            queryBase.append("ssn = ?,");
            values.add(model.getSsn());
        }
        
        //Add lastName
        if(!SanityUtils.isEmpty(model.getLastName())) {
            queryBase.append("lastname = ?,");
            values.add(model.getLastName());
        }
        
        //Add middleName
        if(!SanityUtils.isEmpty(model.getMiddleName())) {
            queryBase.append("middlename = ?,");
            values.add(model.getMiddleName());
        }
        
        //Add firstName
        if(!SanityUtils.isEmpty(model.getFirstName())) {
            queryBase.append("firstname = ?,");
            values.add(model.getFirstName());
        }
        
        //Add streetNumber
        if(model.getStreetNumber() != null) {
            queryBase.append("streetnumber = ?,");
            values.add(model.getStreetNumber());
        }
        
        //Add streetName
        if(!SanityUtils.isEmpty(model.getStreetName())) {
            queryBase.append("streetname = ?,");
            values.add(model.getStreetName());
        }
        
        //Add city
        if(!SanityUtils.isEmpty(model.getCity())) {
            queryBase.append("city = ?,");
            values.add(model.getCity());
        }
        
        //Add state
        if(!SanityUtils.isEmpty(model.getState())) {
            queryBase.append("state = ?,");
            values.add(model.getState());
        }
        
        //Add zip
        if(!SanityUtils.isEmpty(model.getZip())) {
            queryBase.append("zip = ?,");
            values.add(model.getZip());
        }
        
        //if we haven't added anything
        if(values.size() == 0) {
            throw new IllegalArgumentException("Can't update with no new field");
        }
        
        queryBase.deleteCharAt(queryBase.length()-1);
        queryBase.append(" WHERE id = ?");
        values.add(model.getID());
        System.out.println(queryBase.toString());
        
        PreparedStatement preparedStatement = conn.prepareStatement(queryBase.toString());
        
        //fill the parameters TODO: relying on proper order. Maybe find cleaner solution
        for(int i = 0; i < values.size(); i++) {
            preparedStatement.setObject(i+1, values.get(i));
        }
        
        int result = preparedStatement.executeUpdate();
        preparedStatement.close();
        if(result == 1) {
            return true;
        }
    
        return false;
        
    }
    
    public boolean insertRole(int roomID, String amenity) throws SQLException {
        
        String insertSQL = "INSERT INTO employeerole(employeeid, role) VALUES (?, ?)";
        
        PreparedStatement preparedStatement;
        preparedStatement = conn.prepareStatement(insertSQL);
        preparedStatement.setInt(1, roomID);
        preparedStatement.setString(2, amenity);
        int result = preparedStatement.executeUpdate();
        if(result == 1) {
            //success
            preparedStatement.close();
            return true;
        }
        preparedStatement.close();
        return false;
        
    }
    
    public boolean deleteRole(int id, String role) throws SQLException {
        
        String query = "DELETE FROM employeerole WHERE(employeeid = ? AND role = ?)";
        
        PreparedStatement preparedStatement;
        preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, id);
        preparedStatement.setString(2, role);
        int result = preparedStatement.executeUpdate();
        if(result >= 1) {
            //success
            preparedStatement.close();
            return true;
        }
        preparedStatement.close();

        return false;
        
    }
    
    public EmployeeModel getEmployeeDetails(int id) throws SQLException {
                
        String searchSQL = "SELECT * FROM employee WHERE id = ?";
        
        PreparedStatement preparedStatement;
        preparedStatement = conn.prepareStatement(searchSQL);
        preparedStatement.setInt(1,  id);
        ResultSet rs = preparedStatement.executeQuery();
        while(rs.next()) {
            
            List<String> roles = getRoles(id);

            EmployeeModel employee = new EmployeeModel(rs.getInt(1), rs.getString(2), 
                    rs.getString(3), rs.getString(4), rs.getString(5), rs.getInt(6), 
                    rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), roles);
            
            preparedStatement.close();        
            return employee;
        }
        
        preparedStatement.close();        
        return null;
    }
    
    private List<String> getRoles(int id) {
        
        List<String> roles = new LinkedList<String>();
        String searchSQL = "SELECT * FROM employeerole WHERE(employeeid = ?)";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                roles.add(rs.getString(3));
            }
            preparedStatement.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return roles;
        
    }
    
    public ClientModel getClientDetails(int id) throws SQLException {
        
        String searchSQL = "SELECT * FROM client WHERE id = ?";
        
        PreparedStatement preparedStatement;
        preparedStatement = conn.prepareStatement(searchSQL);
        preparedStatement.setInt(1,  id);
        ResultSet rs = preparedStatement.executeQuery();
        while(rs.next()) {
            
            ClientModel client = new ClientModel(rs.getInt(1), rs.getString(2), 
                    rs.getString(3), rs.getString(4), rs.getString(5), rs.getInt(6), 
                    rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), 
                    rs.getTimestamp(11).toString()); 
            
            preparedStatement.close();        
            return client;
        }
        
        preparedStatement.close();        
        return null;
    }
    
}
