package com.ehotels.controllers;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.ehotels.db.AuthenticationManager;
import com.ehotels.db.RoomManager;
import com.ehotels.models.BookingListModel;
import com.ehotels.models.BookingReqModel;
import com.ehotels.utilities.SanityUtils;

@Path("bookingRenting")
public class BookingRentingController {
    
    @Path("/client/myBookings")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMyBookings(@Context HttpHeaders httpheaders) {       
        String uuid = httpheaders.getHeaderString("authorization");
        uuid = uuid.replace("Bearer ", "");
        int userID = AuthenticationManager.getUserId(uuid);
        //TODO check in DB if usedID = -1;
        
        RoomManager roomManager = new RoomManager();
        List<BookingListModel> bookings = roomManager.getBooking(userID);
        roomManager.destroy();
        
        return Response.status(Status.OK).entity(bookings).build();
        
    }
    
    @Path("/client/book")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response bookRoom(@Context HttpHeaders httpheaders, BookingReqModel model) {
        
        //Check startDate. Note: this is quick and dirty. I'm just mimicking what I'll do to insert in DB
        if(!SanityUtils.isEmpty(model.getStartDate())) {
            try {
                Timestamp.valueOf(model.getStartDate());
            } catch (IllegalArgumentException ex) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("{\"error\": \"wrong startDate format. Should be yyyy-[m]m-[d]d hh:mm:ss[.f...]\"}").build();
            }
        }
        
        //Check endDate. Note: this is quick and dirty. I'm just mimicking what I'll do to insert in DB
        if(!SanityUtils.isEmpty(model.getEndDate())) {
            try {
                Timestamp.valueOf(model.getEndDate());
            } catch (IllegalArgumentException ex) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("{\"error\": \"wrong endDate format. Should be yyyy-[m]m-[d]d hh:mm:ss[.f...]\"}").build();          
            }
        }
        
        //Check date order
        if(!SanityUtils.isEmpty(model.getStartDate()) && !SanityUtils.isEmpty(model.getEndDate())) {
            Timestamp startDate = Timestamp.valueOf(model.getStartDate());
            Timestamp endDate = Timestamp.valueOf(model.getEndDate());
            
            if(!endDate.after(startDate)) { //TODO: check that after works like expected. Doc sucks.
                return Response.status(Status.BAD_REQUEST)
                        .entity("{\"error\": \"end should be after start\"}").build();            
            }
            
            if(startDate.before(Date.valueOf(java.time.LocalDate.now()))) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("{\"error\": \"Cannot do booking in the past\"}").build();
            }
        }
        
        RoomManager roomManager = new RoomManager();
        boolean overlappingBooking = roomManager.existsOverlappingBooking(model.getStartDate(), model.getEndDate(), model.getRoomID());
        if(overlappingBooking) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("{\"error\": \"booking exists for that time slot\"}").build(); 
        }
        
        String uuid = httpheaders.getHeaderString("authorization");
        uuid = uuid.replace("Bearer ", "");
        int userID = AuthenticationManager.getUserId(uuid);
        //TODO check in DB if usedID = -1;
        
        boolean insertSuccess = roomManager.insertBooking(model.getRoomID(), model.getStartDate(), model.getEndDate(), userID);
        roomManager.destroy();
        
        if(!insertSuccess) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"Booking was not successful.\"}").build();
        } else {
            return Response.status(Status.OK).entity("{\"message\":\"Booking Completed.\"}").build();
        }
        
    }
    
    @Path("/employee/directRent")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response directRentRoom(@Context HttpHeaders httpheaders, BookingReqModel model) {
        
        if(model.getClientID() == null) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("{\"error\": \"Need client id\"}").build(); 
        }
        
        //Check startDate. Note: this is quick and dirty. I'm just mimicking what I'll do to insert in DB
        if(!SanityUtils.isEmpty(model.getStartDate())) {
            try {
                Timestamp.valueOf(model.getStartDate());
            } catch (IllegalArgumentException ex) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("{\"error\": \"wrong startDate format. Should be yyyy-[m]m-[d]d hh:mm:ss[.f...]\"}").build();
            }
        }
        
        //Check endDate. Note: this is quick and dirty. I'm just mimicking what I'll do to insert in DB
        if(!SanityUtils.isEmpty(model.getEndDate())) {
            try {
                Timestamp.valueOf(model.getEndDate());
            } catch (IllegalArgumentException ex) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("{\"error\": \"wrong endDate format. Should be yyyy-[m]m-[d]d hh:mm:ss[.f...]\"}").build();          
            }
        }
        
        //Check date order
        if(!SanityUtils.isEmpty(model.getStartDate()) && !SanityUtils.isEmpty(model.getEndDate())) {
            Timestamp startDate = Timestamp.valueOf(model.getStartDate());
            Timestamp endDate = Timestamp.valueOf(model.getEndDate());
            
            if(!endDate.after(startDate)) { //TODO: check that after works like expected. Doc sucks.
                return Response.status(Status.BAD_REQUEST)
                        .entity("{\"error\": \"end should be after start\"}").build();            
            }
            
            if(startDate.before(Date.valueOf(java.time.LocalDate.now()))) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("{\"error\": \"Cannot do booking in the past\"}").build();
            }
        }
        
        RoomManager roomManager = new RoomManager();
        boolean overlappingBooking = roomManager.existsOverlappingBooking(model.getStartDate(), model.getEndDate(), model.getRoomID());
        if(overlappingBooking) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("{\"error\": \"booking exists for that time slot\"}").build(); 
        }
        
        String uuid = httpheaders.getHeaderString("authorization");
        uuid = uuid.replace("Bearer ", "");
        int employeeID = AuthenticationManager.getUserId(uuid);
        //TODO check in DB if usedID = -1;
        
        boolean insertSuccess = roomManager.insertRenting(model.getRoomID(), model.getStartDate(), 
                model.getEndDate(), model.getClientID(), employeeID);
        roomManager.destroy();
        
        if(!insertSuccess) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"Renting was not successful.\"}").build();
        } else {
            return Response.status(Status.OK).entity("{\"message\":\"Renting Completed.\"}").build();
        }
        
    }

    @Path("/client/cancelBooking/{bookingID}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelBooking(@Context HttpHeaders httpheaders, @PathParam("bookingID") int bookingID) {
        
        String uuid = httpheaders.getHeaderString("authorization");
        uuid = uuid.replace("Bearer ", "");
        int userID = AuthenticationManager.getUserId(uuid);
        //TODO check in DB if usedID = -1;
        
        RoomManager roomManager = new RoomManager();
        boolean clientHasBooking = roomManager.clientHasBookingOrRenting(bookingID, userID, false);
        
        if(!clientHasBooking) {
            roomManager.destroy();
            return Response.status(Status.NOT_FOUND).entity("{\"message\":\"Client does not have a booking for specified room.\"}").build();
        }

        boolean deleteSuccess = roomManager.deleteClientBooking(bookingID, userID);
        
        if(!deleteSuccess) {
            roomManager.destroy();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"Delete not successful\"}").build();
        }
        
        roomManager.destroy();
        return Response.status(Status.OK).entity("{\"message\":\"Delete successful\"}").build();

    }
    
    @Path("/employee/allBookings")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBookings() {       
        
        RoomManager roomManager = new RoomManager();
        List<BookingListModel> bookings = roomManager.getAllBookingsOrRentings(false);
        roomManager.destroy();
        
        return Response.status(Status.OK).entity(bookings).build();
        
    }
    
    @Path("/employee/rent/{bookingID}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response rentBooking(@Context HttpHeaders httpheaders, @PathParam("bookingID") int bookingID) {
        
        String uuid = httpheaders.getHeaderString("authorization");
        uuid = uuid.replace("Bearer ", "");
        int userID = AuthenticationManager.getUserId(uuid);
        //TODO check in DB if usedID = -1;
        
        //TODO add security so you can't rent something before the actual date
        
        //TODO check if the room is already rented
        
        RoomManager roomManager = new RoomManager();
        boolean success = roomManager.rentBooking(bookingID, userID);
        roomManager.destroy();
        if(success) {
            //we good
            return Response.status(Status.OK).entity("{\"message\":\"Room is now being rented\"}").build();
        } else {
            //failure
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"Error occured while renting a room\"}").build();           
        }
        
    }
    
    @Path("/employee/allRentings")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRentings() {       
        
        RoomManager roomManager = new RoomManager();
        List<BookingListModel> rentings = roomManager.getAllBookingsOrRentings(true);
        roomManager.destroy();
        
        return Response.status(Status.OK).entity(rentings).build();
        
    }
    
    @Path("/employee/cancelRentingOrBooking/{brID}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelRenting(@Context HttpHeaders httpheaders, @PathParam("brID") int brID) {
                
        RoomManager roomManager = new RoomManager();
        boolean deleteSuccess = roomManager.deleteRenting(brID);
        
        if(!deleteSuccess) {
            roomManager.destroy();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"Delete not successful\"}").build();
        }
        
        roomManager.destroy();
        return Response.status(Status.OK).entity("{\"message\":\"Delete successful\"}").build();

    }
    
    @Path("/employee/pay/{bookingID}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response payRenting(@Context HttpHeaders httpheaders, @PathParam("bookingID") int bookingID) {
        
        //TODO add security so you can't pay something before the actual date
        
        //TODO check if the room is already paid
        
        RoomManager roomManager = new RoomManager();
        boolean success = false;
        try {
            success = roomManager.payRenting(bookingID);
        } catch (SQLException e) {
            e.printStackTrace();
            roomManager.destroy();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\": \"" + e.getMessage() + "\"}").build();
        }
        roomManager.destroy();
        if(success) {
            //we good
            return Response.status(Status.OK).entity("{\"message\":\"Room is now paid\"}").build();
        } else {
            //failure
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"Error occured while paying a room\"}").build();           
        }
        
    }
    
}
