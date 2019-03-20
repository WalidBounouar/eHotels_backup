package com.ehotels.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.ehotels.enums.Permission;
import com.ehotels.models.RoomDetailModel;
import com.ehotels.models.RoomListModel;
import com.ehotels.models.RoomSearchModel;

public class RoomManager {

    private String url = "jdbc:mysql://localhost:3306/";
    private String user = "ehotelsUser";
    private String dbName = "ehotels";
    //TODO: possible point of failure between mySQL version. "com.mysql.cj.jdbc.Driver" vs "com.mysql.jdbc.Driver"
    private String driver = "com.mysql.cj.jdbc.Driver";
    private String password = "v1ncentV@diner";
    private Connection conn;
        
    public RoomManager() {
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
    
    public void destroy() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<RoomListModel> searchForRooms(RoomSearchModel model){
        
        List<RoomListModel> rooms = new LinkedList<RoomListModel>();
        
        //basic SQL
        StringBuilder queryBuilder = new StringBuilder("SELECT R1.id AS rooid, roomnumber, capacity, price, extendable, view, hotel.id AS hotelid, starrating, hotel.streetnumber, hotel.streetname, hotel.city, hotel.state, hotel.zip, hotelchain.id as chainid, chainname FROM room AS R1, hotel, hotelchain WHERE(R1.hotelid = hotel.id AND hotel.chainid = hotelchain.id ");
        List<Object> values = new ArrayList<Object>();
        
        //Add the minCapacity
        if(model.getMinCapacity() != null) {
            queryBuilder.append("AND R1.capacity >= ? ");
            values.add(model.getMinCapacity());
        }
        
        //Add the maxCapacity
        if(model.getMaxCapacity() != null) {
            queryBuilder.append("AND R1.capacity <= ? ");
            values.add(model.getMaxCapacity());
        }
        
        //Add the town
        if(model.getTown() != null) {
            queryBuilder.append("AND hotel.city = ? ");
            values.add(model.getTown());
        }
        
        //Add the state
        if(model.getState() != null) {
            queryBuilder.append("AND hotel.state = ? ");
            values.add(model.getState());
        }
        
        //Add the chainName
        if(model.getHotelChain() != null) {
            queryBuilder.append("AND hotelchain.chainname = ? ");
            values.add(model.getHotelChain());
        }
        
        //Add the minRating
        if(model.getMinRating() != null) {
            queryBuilder.append("AND hotel.starrating >= ? ");
            values.add(model.getMinRating());
        }
        
        //Add the maxRating
        if(model.getMaxRating() != null) {
            queryBuilder.append("AND hotel.starrating <= ? ");
            values.add(model.getMaxRating());
        }
        
        //Add the minNumberRooms
        if(model.getMinNumberRooms() != null) {
            queryBuilder.append("AND ? <= (SELECT count(*) FROM room AS R2 WHERE (R2.hotelid = R1.hotelid )) ");
            values.add(model.getMinNumberRooms());
        }
        
        //Add the maxNumberRooms
        if(model.getMaxNumberRooms() != null) {
            queryBuilder.append("AND ? >= (SELECT count(*) FROM room AS R2 WHERE (R2.hotelid = R1.hotelid )) ");
            values.add(model.getMaxNumberRooms());
        }
        
        //Add the minPrice
        if(model.getMinPrice() != null) {
            queryBuilder.append("AND R1.price >= ? ");
            values.add(model.getMinPrice());
        }
        
        //Add the maxPrice
        if(model.getMaxPrice() != null) {
            queryBuilder.append("AND R1.price <= ? ");
            values.add(model.getMaxPrice());
        }
        
        //Add start and end date TODO: do we allow only start or only end
        if(model.getStartDate() != null && model.getEndDate() != null) {
            String bigAssDateCondition  = 
                    "AND R1.id NOT IN(SELECT BR.roomID FROM bookingrenting AS BR WHERE(\r\n" + 
                    "   (BR.startdate >= ? AND BR.enddate <= ?) \r\n" + 
                    "    OR (BR.startdate <= ? AND BR.enddate >= ?)\r\n" + 
                    "    OR (BR.startdate <= ? AND BR.enddate >= ?)\r\n" + 
                    "    OR (BR.startdate <= ? AND BR.enddate >= ?)\r\n" + 
                    "))";
            queryBuilder.append(bigAssDateCondition);
            //ORDER OF ADD IS IMPORTANT TODO: maybe find better solution
            values.add(Timestamp.valueOf(model.getStartDate()));
            values.add(Timestamp.valueOf(model.getEndDate()));
            
            values.add(Timestamp.valueOf(model.getStartDate()));
            values.add(Timestamp.valueOf(model.getStartDate()));
            
            values.add(Timestamp.valueOf(model.getEndDate()));
            values.add(Timestamp.valueOf(model.getEndDate()));

            values.add(Timestamp.valueOf(model.getStartDate()));
            values.add(Timestamp.valueOf(model.getEndDate()));
            
        }
        
        queryBuilder.append(");");
        System.out.println(queryBuilder.toString());
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(queryBuilder.toString());
            
            //fill the parameters TODO: relying on proper order. Maybe find cleaner solution
            for(int i = 0; i < values.size(); i++) {
                preparedStatement.setObject(i+1, values.get(i));
            }
            
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                RoomListModel listItem = new RoomListModel(rs.getInt(1), rs.getInt(2), 
                        rs.getInt(3), rs.getFloat(4), rs.getBoolean(5), rs.getString(6), 
                        rs.getInt(7), rs.getInt(8), rs.getInt(9), rs.getString(10), 
                        rs.getString(11), rs.getString(12), rs.getString(13), 
                        rs.getInt(14), rs.getString(15));
                rooms.add(listItem);
            }
            preparedStatement.close();
            return rooms;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rooms;
        
    }
        
    public RoomDetailModel getRoomDetails(int id) {
        
        String searchSQL = "SELECT room.id, roomnumber, capacity, price, extendable, view, hotel.id AS hotelid, starrating, hotel.streetnumber, hotel.streetname, hotel.city, hotel.state, hotel.zip, hotelchain.id as chainid, chainname FROM room, hotel, hotelchain WHERE(room.hotelid = hotel.id AND hotel.chainid = hotelchain.id AND room.id = ?)";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                
                List<String> amenities = getRoomAmenities(id);
                List<String> issues = getRoomIssues(id);
                
                RoomDetailModel roomDetails = new RoomDetailModel(rs.getInt(1), rs.getInt(2), 
                        rs.getInt(3), rs.getFloat(4), rs.getBoolean(5), rs.getString(6), 
                        rs.getInt(7), rs.getInt(8), rs.getInt(9), rs.getString(10), 
                        rs.getString(11), rs.getString(12), rs.getString(13), 
                        rs.getInt(14), rs.getString(15), amenities, issues);
                
                preparedStatement.close();
                return roomDetails;
            }
            preparedStatement.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
        
    }
    
    private List<String> getRoomAmenities(int id) {
        
        List<String> amenities = new LinkedList<String>();
        String searchSQL = "SELECT * FROM amenity WHERE(roomid = ?)";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                amenities.add(rs.getString(3));
            }
            preparedStatement.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return amenities;
        
    }
    
    /**
     * returns a list with the category of the issue. Returning everything seemed like overkill.
     */
    private List<String> getRoomIssues(int id) {
        
        List<String> issues = new LinkedList<String>();
        String searchSQL = "SELECT * FROM issue WHERE(roomid = ? AND isfixed = false)";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                issues.add(rs.getString(3));
            }
            preparedStatement.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return issues;
        
    }
    
}

