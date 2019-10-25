package com.ehotels.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.ehotels.models.HotelChainBasicModel;
import com.ehotels.models.HotelDetailModel;
import com.ehotels.models.HotelListModel;
import com.ehotels.utilities.SanityUtils;

public class HotelManager {

    private String url = "jdbc:mysql://localhost:3306/";
    private String user = "REDACTED";
    private String dbName = "REDACTED";
    //TODO: possible point of failure between mySQL version. "com.mysql.cj.jdbc.Driver" vs "com.mysql.jdbc.Driver"
    private String driver = "com.mysql.cj.jdbc.Driver";
    private String password = "REDACTED";
    private Connection conn;
        
    public HotelManager() {
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
    
    public List<HotelListModel> getAllHotels() {
        
        List<HotelListModel> hotels = new LinkedList<HotelListModel>();
        
        String searchSQL = 
                "SELECT hotel.id, hotel.streetnumber, hotel.streetname, hotel.city, hotel.state, hotel.zip, chainid, chainname FROM hotel, hotelchain\r\n" + 
                "WHERE(hotel.chainid = hotelchain.id)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                HotelListModel hotel = new HotelListModel(rs.getInt(1), rs.getInt(2), 
                        rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6),
                        rs.getInt(7), rs.getString(8));
                hotels.add(hotel);
            }
            preparedStatement.close();
            return hotels;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return hotels;
    }

    public HotelDetailModel getHotelDetails(int id) {
        
        String searchSQL = 
                "SELECT hotel.id, managerid, starrating, hotel.streetnumber, hotel.streetname, hotel.city, hotel.state, hotel.zip, chainid, chainname\r\n" + 
                "FROM hotel, hotelchain \r\n" + 
                "WHERE(hotel.chainid = hotelchain.id AND hotel.id = ?)";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                
                List<String> phoneNumbers = getHotelPhoneNumbers(id);
                
                HotelDetailModel hotelDetails = new HotelDetailModel(rs.getInt(1), 
                        rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getString(5), 
                        rs.getString(6), rs.getString(7), rs.getString(8), rs.getInt(9), 
                        rs.getString(10), phoneNumbers);
                
                preparedStatement.close();
                return hotelDetails;
            }
            preparedStatement.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
        
    }
    
    private List<String> getHotelPhoneNumbers(int id) {
        
        List<String> phones = new LinkedList<String>();
        String searchSQL = "SELECT phonenumber FROM hotelphonenumbers WHERE(hotelid = ?)";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                phones.add(rs.getString(1));
            }
            preparedStatement.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return phones;
        
    }
    
    public List<HotelChainBasicModel> getAllChains(){
        
        List<HotelChainBasicModel> chains = new LinkedList<HotelChainBasicModel>();
        String searchSQL = "SELECT id, chainname FROM hotelchain";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                chains.add(new HotelChainBasicModel(rs.getInt(1), rs.getString(2)));
            }
            preparedStatement.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return chains;
        
    }
    
    public boolean hotelExists(String zip) {
        String searchSQL = "SELECT id FROM hotel WHERE zip = ?";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setString(1, zip);
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
    
    public boolean hotelExists(String zip, int id) {
        String searchSQL = "SELECT id FROM hotel WHERE (zip = ? AND id <> ?)";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setString(1, zip);
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
    
    public boolean hotelExists(int id) {
        String searchSQL = "SELECT id FROM hotel WHERE id = ?";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setInt(1, id);
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
     * assumes all good values in model
     * @throws SQLException 
     */
    public boolean insertHotel(HotelDetailModel model) throws SQLException {
        
        String insertQuery = 
                "INSERT INTO hotel(chainid, managerid, starrating, streetnumber, streetname, city, state, zip)\r\n" + 
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        
        PreparedStatement preparedStatement;
        preparedStatement = conn.prepareStatement(insertQuery);
        preparedStatement.setInt(1, model.getChainID());
        preparedStatement.setInt(2, model.getManagerID());
        preparedStatement.setInt(3, model.getStarRating());
        preparedStatement.setInt(4, model.getStreetNumber());
        preparedStatement.setString(5, model.getStreetName());
        preparedStatement.setString(6, model.getCity());
        preparedStatement.setString(7, model.getState());
        preparedStatement.setString(8, model.getZip());
        
        int result = preparedStatement.executeUpdate();
        if(result == 1) {
            //success
            preparedStatement.close();
            return true;
        }
        preparedStatement.close();
        return false;
        
    }
    
    /**
     * assumes all good values in model
     * @throws SQLException 
     */
    public boolean updateHotel(HotelDetailModel model) throws SQLException {
        
        StringBuilder queryBase = new StringBuilder("UPDATE hotel SET ");
        List<Object> values = new ArrayList<Object>();
        
        //Add chainID
        if(model.getChainID() != null) {
            queryBase.append("chainid = ?,");
            values.add(model.getChainID());
        }
        
        //Add managerID
        if(model.getManagerID() != null) {
            queryBase.append("managerid = ?,");
            values.add(model.getManagerID());
        }
        
        //Add starrating
        if(model.getStarRating() != null) {
            queryBase.append("starrating = ?,");
            values.add(model.getStarRating());
        }
        
        //Add streetnumber
        if(model.getStreetNumber() != null) {
            queryBase.append("streetnumber = ?,");
            values.add(model.getStreetNumber());
        }
        
        //Add streetname
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
        values.add(model.getHotelID());
        System.out.println(queryBase.toString());
        
        PreparedStatement preparedStatement;
        preparedStatement = conn.prepareStatement(queryBase.toString());
        
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
    
    public boolean insertHotelPhoneNumber(int id, String phoneNumber) {
        
        String insertSQL = "INSERT INTO hotelphonenumbers(hotelid, phonenumber) VALUES (?, ?)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(insertSQL);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, phoneNumber);
            int result = preparedStatement.executeUpdate();
            if(result == 1) {
                //success
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
    
    public boolean deleteHotelPhoneNumber(int id, String phoneNumber) {
        
        String query = "DELETE FROM hotelphonenumbers WHERE(hotelid = ? AND phonenumber = ?)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, phoneNumber);
            int result = preparedStatement.executeUpdate();
            if(result == 1) {
                //success
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
    
    public boolean deleteHotel(int hotelID) {
        
        String searchSQL = "DELETE FROM hotel WHERE(id = ?)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setInt(1, hotelID);
            int result = preparedStatement.executeUpdate();
            if(result == 1) {
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
    
    public List<HotelDetailModel> getManagerHotels(int managerID) {
        
        List<HotelDetailModel> hotels = new LinkedList<HotelDetailModel>();
        
        String searchSQL = 
                "SELECT hotel.id, managerid, starrating, hotel.streetnumber, hotel.streetname, hotel.city, hotel.state, hotel.zip, chainid, chainname\r\n" + 
                "FROM hotel, hotelchain \r\n" + 
                "WHERE(hotel.chainid = hotelchain.id AND hotel.managerID = ?)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setInt(1, managerID);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                HotelDetailModel hotelDetails = new HotelDetailModel(rs.getInt(1), 
                        rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getString(5), 
                        rs.getString(6), rs.getString(7), rs.getString(8), rs.getInt(9), 
                        rs.getString(10), null); //we don't care about phones right now
                hotels.add(hotelDetails);
            }
            preparedStatement.close();
            return hotels;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return hotels;
    }
    
    public List<String> getCities() {
        
        List<String> cities = new LinkedList<String>();
        String searchSQL = "SELECT DISTINCT city FROM hotel";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                cities.add(rs.getString(1));
            }
            preparedStatement.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return cities;
        
    }
    
    public List<String> getStates() {
        
        List<String> states = new LinkedList<String>();
        String searchSQL = "SELECT DISTINCT state FROM hotel";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                states.add(rs.getString(1));
            }
            preparedStatement.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return states;
        
    }
    
    public List<String> getChains() {
        
        List<String> chains = new LinkedList<String>();
        String searchSQL = "SELECT DISTINCT chainname FROM hotelchain";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                chains.add(rs.getString(1));
            }
            preparedStatement.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return chains;
        
    }
    
}
