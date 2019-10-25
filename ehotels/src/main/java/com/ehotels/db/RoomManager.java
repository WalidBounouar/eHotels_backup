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
import com.ehotels.models.BookingListModel;
import com.ehotels.models.RoomBasicModel;
import com.ehotels.models.RoomDetailModel;
import com.ehotels.models.RoomListModel;
import com.ehotels.models.RoomSearchModel;
import com.ehotels.models.RoomsPerAreaModel;
import com.ehotels.utilities.SanityUtils;

public class RoomManager {

    private String url = "jdbc:mysql://localhost:3306/";
    private String user = "REDACTED";
    private String dbName = "REDACTED";
    //TODO: possible point of failure between mySQL version. "com.mysql.cj.jdbc.Driver" vs "com.mysql.jdbc.Driver"
    private String driver = "com.mysql.cj.jdbc.Driver";
    private String password = "REDACTED";
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
        if(!SanityUtils.isEmpty(model.getTown())) {
            queryBuilder.append("AND hotel.city = ? ");
            values.add(model.getTown());
        }
        
        //Add the state
        if(!SanityUtils.isEmpty(model.getState())) {
            queryBuilder.append("AND hotel.state = ? ");
            values.add(model.getState());
        }
        
        //Add the chainName
        if(!SanityUtils.isEmpty(model.getHotelChain())) {
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
        if(!SanityUtils.isEmpty(model.getStartDate()) && !SanityUtils.isEmpty(model.getEndDate())) {
            String bigAssDateCondition  = 
                    "AND R1.id NOT IN(SELECT BR.roomID FROM bookingrenting AS BR WHERE(\r\n" + 
                    "   (BR.startdate >= ? AND BR.enddate <= ?) \r\n" + 
                    "    OR (BR.startdate <= ? AND BR.enddate > ?)\r\n" + 
                    "    OR (BR.startdate < ? AND BR.enddate >= ?)\r\n" + 
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
    
    public List<BookingListModel> getBooking(int clientID) {
        
        List<BookingListModel> bookings = new LinkedList<BookingListModel>();
        
        String searchString = 
                "SELECT bookingrenting.id, startdate, enddate, roomid, roomnumber, capacity, price, extendable, view, hotelid, starrating, h1.streetnumber, h1.streetname, h1.city, h1.state, h1.zip, chainid, chainname, clientid, firstname, lastname, paid, ssn\r\n" + 
                "FROM bookingrenting, room, hotel AS h1, hotelchain, client \r\n" + 
                "WHERE(bookingrenting.roomid = room.id AND room.hotelid = h1.id AND h1.chainid = hotelchain.id AND bookingrenting.clientid = client.id \r\n" + 
                "AND clientid = ? AND checkedin = FALSE)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchString);
            preparedStatement.setInt(1, clientID);
            
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                BookingListModel listItem = new BookingListModel(rs.getInt(4), rs.getInt(5), 
                        rs.getInt(6), rs.getFloat(7), rs.getBoolean(8), rs.getString(9), 
                        rs.getInt(10), rs.getInt(11), rs.getInt(12), rs.getString(13), 
                        rs.getString(14), rs.getString(15), rs.getString(16), 
                        rs.getInt(17), rs.getString(18), rs.getInt(1), rs.getTimestamp(2).toString(), 
                        rs.getTimestamp(3).toString(), rs.getBoolean(22), rs.getInt(19), rs.getString(20), rs.getString(21), 
                        rs.getString(23));
                bookings.add(listItem);
            }
            preparedStatement.close();
            return bookings;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bookings;
    }
    
    //we use term booking, but this method also looks at rentings
    public List<BookingListModel> getHotelBookingOrRentings(int hotelID) {
        
        List<BookingListModel> bookings = new LinkedList<BookingListModel>();
        
        String searchString = 
                "SELECT bookingrenting.id, startdate, enddate, roomid, roomnumber, capacity, price, extendable, view, hotelid, starrating, h1.streetnumber, h1.streetname, h1.city, h1.state, h1.zip, chainid, chainname, clientid, firstname, lastname, paid, ssn\r\n" + 
                "FROM bookingrenting, room, hotel AS h1, hotelchain, client \r\n" + 
                "WHERE(bookingrenting.roomid = room.id AND room.hotelid = h1.id AND h1.chainid = hotelchain.id AND bookingrenting.clientid = client.id \r\n" + 
                "AND h1.id = ?)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchString);
            preparedStatement.setInt(1, hotelID);
            
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                BookingListModel listItem = new BookingListModel(rs.getInt(4), rs.getInt(5), 
                        rs.getInt(6), rs.getFloat(7), rs.getBoolean(8), rs.getString(9), 
                        rs.getInt(10), rs.getInt(11), rs.getInt(12), rs.getString(13), 
                        rs.getString(14), rs.getString(15), rs.getString(16), 
                        rs.getInt(17), rs.getString(18), rs.getInt(1), rs.getTimestamp(2).toString(), 
                        rs.getTimestamp(3).toString(), rs.getBoolean(22), rs.getInt(19), rs.getString(20), rs.getString(21), 
                        rs.getString(23));
                bookings.add(listItem);
            }
            preparedStatement.close();
            return bookings;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bookings;
    }
    
    //we use term booking, but this method also looks at rentings
    public List<BookingListModel> getRoomBookingOrRentings(int roomID) {
        
        List<BookingListModel> bookings = new LinkedList<BookingListModel>();
        
        String searchString = 
                "SELECT bookingrenting.id, startdate, enddate, roomid, roomnumber, capacity, price, extendable, view, hotelid, starrating, h1.streetnumber, h1.streetname, h1.city, h1.state, h1.zip, chainid, chainname, clientid, firstname, lastname, paid, ssn\r\n" + 
                "FROM bookingrenting, room, hotel AS h1, hotelchain, client \r\n" + 
                "WHERE(bookingrenting.roomid = room.id AND room.hotelid = h1.id AND h1.chainid = hotelchain.id AND bookingrenting.clientid = client.id \r\n" + 
                "AND room.id = ?)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchString);
            preparedStatement.setInt(1, roomID);
            
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                BookingListModel listItem = new BookingListModel(rs.getInt(4), rs.getInt(5), 
                        rs.getInt(6), rs.getFloat(7), rs.getBoolean(8), rs.getString(9), 
                        rs.getInt(10), rs.getInt(11), rs.getInt(12), rs.getString(13), 
                        rs.getString(14), rs.getString(15), rs.getString(16), 
                        rs.getInt(17), rs.getString(18), rs.getInt(1), rs.getTimestamp(2).toString(), 
                        rs.getTimestamp(3).toString(), rs.getBoolean(22), rs.getInt(19), rs.getString(20), rs.getString(21), 
                        rs.getString(23));
                bookings.add(listItem);
            }
            preparedStatement.close();
            return bookings;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bookings;
    }

    //we use term booking, but this method also looks at rentings
    public List<BookingListModel> getClientBookingOrRentings(int clientID) {
        
        List<BookingListModel> bookings = new LinkedList<BookingListModel>();
        
        String searchString = 
                "SELECT bookingrenting.id, startdate, enddate, roomid, roomnumber, capacity, price, extendable, view, hotelid, starrating, h1.streetnumber, h1.streetname, h1.city, h1.state, h1.zip, chainid, chainname, clientid, firstname, lastname, paid, ssn\r\n" + 
                "FROM bookingrenting, room, hotel AS h1, hotelchain, client \r\n" + 
                "WHERE(bookingrenting.roomid = room.id AND room.hotelid = h1.id AND h1.chainid = hotelchain.id AND bookingrenting.clientid = client.id \r\n" + 
                "AND bookingrenting.clientid = ?)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchString);
            preparedStatement.setInt(1, clientID);
            
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                BookingListModel listItem = new BookingListModel(rs.getInt(4), rs.getInt(5), 
                        rs.getInt(6), rs.getFloat(7), rs.getBoolean(8), rs.getString(9), 
                        rs.getInt(10), rs.getInt(11), rs.getInt(12), rs.getString(13), 
                        rs.getString(14), rs.getString(15), rs.getString(16), 
                        rs.getInt(17), rs.getString(18), rs.getInt(1), rs.getTimestamp(2).toString(), 
                        rs.getTimestamp(3).toString(), rs.getBoolean(22), rs.getInt(19), rs.getString(20), rs.getString(21), 
                        rs.getString(23));
                bookings.add(listItem);
            }
            preparedStatement.close();
            return bookings;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bookings;
    }

  //we use term booking, but this method also looks at rentings
    public List<BookingListModel> getEmployeeBookingOrRentings(int employeeID) {
        
        List<BookingListModel> bookings = new LinkedList<BookingListModel>();
        
        String searchString = 
                "SELECT bookingrenting.id, startdate, enddate, roomid, roomnumber, capacity, price, extendable, view, hotelid, starrating, h1.streetnumber, h1.streetname, h1.city, h1.state, h1.zip, chainid, chainname, clientid, firstname, lastname, paid, ssn\r\n" + 
                "FROM bookingrenting, room, hotel AS h1, hotelchain, client \r\n" + 
                "WHERE(bookingrenting.roomid = room.id AND room.hotelid = h1.id AND h1.chainid = hotelchain.id AND bookingrenting.clientid = client.id \r\n" + 
                "AND bookingrenting.employeeid = ?)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchString);
            preparedStatement.setInt(1, employeeID);
            
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                BookingListModel listItem = new BookingListModel(rs.getInt(4), rs.getInt(5), 
                        rs.getInt(6), rs.getFloat(7), rs.getBoolean(8), rs.getString(9), 
                        rs.getInt(10), rs.getInt(11), rs.getInt(12), rs.getString(13), 
                        rs.getString(14), rs.getString(15), rs.getString(16), 
                        rs.getInt(17), rs.getString(18), rs.getInt(1), rs.getTimestamp(2).toString(), 
                        rs.getTimestamp(3).toString(), rs.getBoolean(22), rs.getInt(19), rs.getString(20), rs.getString(21),
                        rs.getString(23));
                bookings.add(listItem);
            }
            preparedStatement.close();
            return bookings;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bookings;
    }
    
    /**
     * Does no input validation
     */
    public boolean existsOverlappingBooking(String startDate, String endDate, int roomid) {
        
        String searchString = 
                "SELECT id FROM bookingrenting AS br WHERE( \r\n" + 
                "    ((br.startdate >= ? AND br.enddate <= ?) \r\n" + 
                "    OR (br.startdate <= ? AND br.enddate > ?) \r\n" + 
                "    OR (br.startdate < ? AND br.enddate >= ?) \r\n" + 
                "    OR (br.startdate <= ? AND br.enddate >= ?)) AND br.roomid = ?)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchString);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(startDate));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(endDate));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(startDate));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(startDate));
            preparedStatement.setTimestamp(5, Timestamp.valueOf(endDate));
            preparedStatement.setTimestamp(6, Timestamp.valueOf(endDate));
            preparedStatement.setTimestamp(7, Timestamp.valueOf(startDate));
            preparedStatement.setTimestamp(8, Timestamp.valueOf(endDate));
            preparedStatement.setInt(9, roomid);
            
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.isBeforeFirst()) {
                preparedStatement.close();
                System.out.println("returning true because set not emptys");
                return true;
            }
            preparedStatement.close();
            return false;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //if something fails, let's just stop insert
        System.out.println("returning true because something failed");
        return true;
        
    }
    
    /**
     * no security
     */
    public boolean insertBooking(int roomID, String startDate, String endDate, int clientID) {
        
        String insertQuery = 
                "INSERT INTO ehotels.bookingrenting(roomid, startdate, enddate, employeeid, clientid, checkedin, paid) \r\n" + 
                "VALUES(?, ?, ?, 1, ?, FALSE, FALSE)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(insertQuery);
            preparedStatement.setInt(1, roomID);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(startDate));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(endDate));
            preparedStatement.setInt(4, clientID);
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
    
    /**
     * no security
     */
    public boolean insertRenting(int roomID, String startDate, String endDate, int clientID, int employeeID) {
        
        String insertQuery = 
                "INSERT INTO ehotels.bookingrenting(roomid, startdate, enddate, employeeid, clientid, checkedin, paid) \r\n" + 
                "VALUES(?, ?, ?, ?, ?, TRUE, FALSE)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(insertQuery);
            preparedStatement.setInt(1, roomID);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(startDate));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(endDate));
            preparedStatement.setInt(4, employeeID);
            preparedStatement.setInt(5, clientID);
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
    
    public boolean clientHasBookingOrRenting(int bookingRentingID, int clientID, boolean isRenting) {
        
        String searchSQL = "SELECT id FROM bookingrenting WHERE(bookingrenting.id = ? AND clientid = ? AND checkedin = FALSE)";
        if(isRenting) {
            searchSQL = "SELECT id FROM bookingrenting WHERE(bookingrenting.id = ? AND clientid = ? AND checkedin = TRUE)";
        }
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setInt(1, bookingRentingID);
            preparedStatement.setInt(2, clientID);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                //it exists
                return true;
            }
            preparedStatement.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteClientBooking(int bookingID, int clientID) {

        String searchSQL = "DELETE FROM bookingrenting WHERE(bookingrenting.id = ? AND clientid = ? AND checkedin = FALSE)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setInt(1, bookingID);
            preparedStatement.setInt(2, clientID);            
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
    
    public boolean deleteRenting(int rentingID) {

        String searchSQL = "DELETE FROM bookingrenting WHERE(bookingrenting.id = ? )";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setInt(1, rentingID);
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
    
    public List<BookingListModel> getAllBookingsOrRentings(boolean isRenting) {
        
        List<BookingListModel> bookings = new LinkedList<BookingListModel>();
        
        String searchString = 
                "SELECT bookingrenting.id, startdate, enddate, roomid, roomnumber, capacity, price, extendable, view, hotelid, starrating, h1.streetnumber, h1.streetname, h1.city, h1.state, h1.zip, chainid, chainname, clientid, firstname, lastname, paid, ssn\r\n" + 
                "FROM bookingrenting, room, hotel AS h1, hotelchain, client \r\n" + 
                "WHERE(bookingrenting.roomid = room.id AND room.hotelid = h1.id AND h1.chainid = hotelchain.id AND bookingrenting.clientid = client.id \r\n" + 
                "AND checkedin = FALSE)";
        
        if(isRenting) {
            searchString = 
                "SELECT bookingrenting.id, startdate, enddate, roomid, roomnumber, capacity, price, extendable, view, hotelid, starrating, h1.streetnumber, h1.streetname, h1.city, h1.state, h1.zip, chainid, chainname, clientid, firstname, lastname, paid, ssn\r\n" + 
                "FROM bookingrenting, room, hotel AS h1, hotelchain, client \r\n" + 
                "WHERE(bookingrenting.roomid = room.id AND room.hotelid = h1.id AND h1.chainid = hotelchain.id AND bookingrenting.clientid = client.id \r\n" + 
                "AND checkedin = TRUE)";
        }
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchString);
            
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                BookingListModel listItem = new BookingListModel(rs.getInt(4), rs.getInt(5), 
                        rs.getInt(6), rs.getFloat(7), rs.getBoolean(8), rs.getString(9), 
                        rs.getInt(10), rs.getInt(11), rs.getInt(12), rs.getString(13), 
                        rs.getString(14), rs.getString(15), rs.getString(16), 
                        rs.getInt(17), rs.getString(18), rs.getInt(1), rs.getTimestamp(2).toString(), 
                        rs.getTimestamp(3).toString(), rs.getBoolean(22), rs.getInt(19), rs.getString(20), rs.getString(21),
                        rs.getString(23));
                bookings.add(listItem);
            }
            preparedStatement.close();
            return bookings;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bookings;
    }
    
    public boolean rentBooking(int bookingID, int employeeID) {

        String query = "UPDATE bookingrenting SET checkedin = TRUE, employeeid = ? WHERE (id = ?)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, employeeID);
            preparedStatement.setInt(2, bookingID);
            int result = preparedStatement.executeUpdate();
            preparedStatement.close();
            if(result == 1) {
                return true;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean payRenting(int bookingID) throws SQLException {

        String query = "UPDATE bookingrenting SET paid = TRUE WHERE (id = ? AND checkedin = TRUE)";
        
        PreparedStatement preparedStatement;
        preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, bookingID);
        int result = preparedStatement.executeUpdate();
        preparedStatement.close();
        if(result == 1) {
            return true;
        }
        
        return false;
    }

    public boolean roomExists(int hotelID, int roomNumber) {
        
        String searchSQL = "SELECT id FROM room WHERE(hotelid = ? AND roomnumber = ?)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setInt(1, hotelID);
            preparedStatement.setInt(2, roomNumber);
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
    
    public boolean roomExists(int roomID) {
        
        String searchSQL = "SELECT id FROM room WHERE(id = ?)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setInt(1, roomID);
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
    
    public boolean roomExists(int hotelID, int roomNumber, int roomID) {
        
        String searchSQL = "SELECT id FROM room WHERE(hotelid = ? AND roomnumber = ? AND id <> ?)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            preparedStatement.setInt(1, hotelID);
            preparedStatement.setInt(2, roomNumber);
            preparedStatement.setInt(3, roomID);
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
    
    public boolean insertRoom(RoomBasicModel model) {
        
        String insertQuery = 
                "INSERT INTO room(hotelid, roomnumber, capacity, price, extendable, view)\r\n" + 
                "VALUES(?, ?, ?, ?, ?, ?)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(insertQuery);
            preparedStatement.setInt(1, model.getHotelID());
            preparedStatement.setInt(2, model.getRoomNumber());
            preparedStatement.setInt(3, model.getCapacity());
            preparedStatement.setFloat(4, model.getPrice());
            preparedStatement.setBoolean(5, model.getExtendable());
            preparedStatement.setString(6, model.getView());
            
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
    
    public boolean deleteRoom(int roomID) {
        
        String query = "DELETE FROM room WHERE(id = ?)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, roomID);
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
    
    public boolean updateRoom(RoomBasicModel model) {
        
        StringBuilder queryBase = new StringBuilder("UPDATE room SET ");
        List<Object> values = new ArrayList<Object>();
        
        //Add hotelid
        if(model.getHotelID() != null) {
            queryBase.append("hotelid = ?,");
            values.add(model.getHotelID());
        }
        
        //Add roomnumber
        if(model.getRoomNumber() != null) {
            queryBase.append("roomnumber = ?,");
            values.add(model.getRoomNumber());
        }
        
        //Add capacity
        if(model.getCapacity() != null) {
            queryBase.append("capacity = ?,");
            values.add(model.getCapacity());
        }
        
        //Add price
        if(model.getPrice() != null) {
            queryBase.append("price = ?,");
            values.add(model.getPrice());
        }
        
        //Add extendable
        if(model.getExtendable() != null) {
            queryBase.append("extendable = ?,");
            values.add(model.getExtendable());
        }
        
        //Add view
        if(!SanityUtils.isEmpty(model.getView())) {
            queryBase.append("view = ?,");
            values.add(model.getView());
        }
        
        //if we haven't added anything
        if(values.size() == 0) {
            throw new IllegalArgumentException("Can't update with no new field");
        }
        
        queryBase.deleteCharAt(queryBase.length()-1);
        queryBase.append(" WHERE id = ?");
        values.add(model.getRoomID());
        System.out.println(queryBase.toString());
        
        PreparedStatement preparedStatement;
        try {
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
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return false;
        
    }
    
    public boolean insertAmenity(int roomID, String amenity) {
        
        String insertSQL = "INSERT INTO amenity(roomid, amenity) VALUES (?, ?)";
        
        PreparedStatement preparedStatement;
        try {
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
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
        
    }
    
    public boolean insertIssue(int roomID, String issue) {
        
        String insertSQL = "INSERT INTO issue(roomid, category, description, datereported, isfixed) VALUES (?, ?, '', ?, FALSE)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(insertSQL);
            preparedStatement.setInt(1, roomID);
            preparedStatement.setString(2, issue);
            preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
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
    
    public List<RoomsPerAreaModel> getFreeRoomsPerArea() {
        
        List<RoomsPerAreaModel> items = new LinkedList<RoomsPerAreaModel>();

        String searchSQL = "SELECT * FROM freeroomsperarea";

        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(searchSQL);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {

                RoomsPerAreaModel item = new RoomsPerAreaModel(rs.getInt(1), 
                        rs.getString(2), rs.getString(3));
                items.add(item);
                
            }
            preparedStatement.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return items;
        
    }
    
    public boolean deleteAmenity(int id, String amenity) {
        
        String query = "DELETE FROM amenity WHERE(roomid = ? AND amenity = ?)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, amenity);
            int result = preparedStatement.executeUpdate();
            if(result >= 1) {
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
    
    public boolean deleteIssue(int id, String issue) {
        
        String query = "DELETE FROM issue WHERE(roomid = ? AND category = ?)";
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, issue);
            int result = preparedStatement.executeUpdate();
            if(result >= 1) {
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
    
    public List<RoomListModel> getCapacityPerRoom() throws SQLException {
        
        List<RoomListModel> items = new LinkedList<RoomListModel>();

        String searchSQL = "SELECT * FROM capacityperroom";

        PreparedStatement preparedStatement;
        preparedStatement = conn.prepareStatement(searchSQL);
        ResultSet rs = preparedStatement.executeQuery();
        while(rs.next()) {

            RoomListModel item = new RoomListModel(rs.getInt(9), rs.getInt(10), rs.getInt(11), 
                    null, false, null, rs.getInt(3), -1, rs.getInt(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), 
                    rs.getInt(1), rs.getString(2));
            items.add(item);
            
        }
        preparedStatement.close();
        
        return items;
        
    }
    
}

