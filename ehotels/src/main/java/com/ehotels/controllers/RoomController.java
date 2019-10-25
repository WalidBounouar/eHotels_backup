package com.ehotels.controllers;

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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.ehotels.db.HotelManager;
import com.ehotels.db.RoomManager;
import com.ehotels.models.BookingListModel;
import com.ehotels.models.IdValueModel;
import com.ehotels.models.RoomBasicModel;
import com.ehotels.models.RoomDetailModel;
import com.ehotels.models.RoomListModel;
import com.ehotels.models.RoomSearchModel;
import com.ehotels.models.RoomsPerAreaModel;
import com.ehotels.utilities.SanityUtils;

@Path("rooms")
public class RoomController {
    
    @Path("search")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response searhRooms(RoomSearchModel searchModel) {
        
        System.out.println(searchModel);
        
        String sanityCheck = searchModelCheck(searchModel);
        
        if(sanityCheck != null) {
            return Response.status(Status.BAD_REQUEST).entity(sanityCheck).build();
        }
        
        RoomManager roomManager = new RoomManager();
        List<RoomListModel> rooms = roomManager.searchForRooms(searchModel);
        roomManager.destroy();
        
        return Response.status(Status.OK).entity(rooms).build();
        
    }

    /**
     * Tests whether the model given is valid.
     * If so, returns null;
     * If not, return a String with appropriate error message.
     * @param searchModel
     * @return
     */
    private String searchModelCheck(RoomSearchModel searchModel) {
        
        //Check startDate. Note: this is quick and dirty. I'm just mimicking what I'll do to insert in DB
        if(!SanityUtils.isEmpty(searchModel.getStartDate())) {
            try {
                Timestamp.valueOf(searchModel.getStartDate());
            } catch (IllegalArgumentException ex) {
                return "{\"error\": \"wrong startDate format. Should be yyyy-[m]m-[d]d hh:mm:ss[.f...]\"}";
            }
        }
        
        //Check endDate. Note: this is quick and dirty. I'm just mimicking what I'll do to insert in DB
        if(!SanityUtils.isEmpty(searchModel.getEndDate())) {
            try {
                Timestamp.valueOf(searchModel.getEndDate());
            } catch (IllegalArgumentException ex) {
                return "{\"error\": \"wrong endDate format. Should be yyyy-[m]m-[d]d hh:mm:ss[.f...]\"}";
            }
        }
        
        //Check date order
        if(!SanityUtils.isEmpty(searchModel.getStartDate()) && !SanityUtils.isEmpty(searchModel.getEndDate())) {
            Timestamp startDate = Timestamp.valueOf(searchModel.getStartDate());
            Timestamp endDate = Timestamp.valueOf(searchModel.getEndDate());
            
            if(!endDate.after(startDate)) { //TODO: check that after works like expected. Doc sucks.
                return "{\"error\": \"endDate should be after startDates.\"}";
            }
        }
        
        //Check minCapacity
        Integer minCapacity = searchModel.getMinCapacity();
        if(minCapacity != null && minCapacity.intValue() < 1) {
            return "{\"error\": \"minCapacity should be >= 1.\"}";
        }
        
        //Check maxCapacity
        Integer maxCapacity = searchModel.getMaxCapacity();
        if(maxCapacity != null && maxCapacity.intValue() < 1) {
            return "{\"error\": \"maxCapacity should be >= 1.\"}";
        }
        
        //Check minCapacity vs maxCapacity
        if(minCapacity != null && maxCapacity != null && minCapacity.intValue() > maxCapacity.intValue()) {
            return "{\"error\": \"minCapacity should be <= maxCapacity.\"}";
        }
        
       //Check minRating
        Integer minRating = searchModel.getMinRating();
        if(minRating != null && (minRating.intValue() < 0 || minRating.intValue() > 5)) {
            return "{\"error\": \"minRating should be between 0 and 5.\"}";
        }
        
        //Check maxRating
        Integer maxRating = searchModel.getMaxRating();
        if(maxRating != null && (maxRating.intValue() < 0 || maxRating.intValue() > 5)) {
            return "{\"error\": \"maxRating should be between 0 and 5.\"}";
        }
        
        //Check minRating vs maxRating
        if(minRating != null && maxRating != null && minRating.intValue() > maxRating.intValue()) {
            return "{\"error\": \"minRating should be <= maxRating.\"}";
        }
        
        //Check minNumberRooms
        Integer minNumberRooms = searchModel.getMinNumberRooms();
        if(minNumberRooms != null && minNumberRooms.intValue() < 1) {
            return "{\"error\": \"minNumberRooms should be >= 1.\"}";
        }
        
        //Check maxNumberRooms
        Integer maxNumberRooms = searchModel.getMaxNumberRooms();
        if(maxNumberRooms != null && maxNumberRooms.intValue() < 1) {
            return "{\"error\": \"maxNumberRooms should be >= 1.\"}";
        }
        
        //Check minNumberRooms vs maxNumberRooms
        if(minNumberRooms != null && maxNumberRooms != null && minNumberRooms.intValue() > maxNumberRooms.intValue()) {
            return "{\"error\": \"minNumberRooms should be <= maxNumberRooms.\"}";
        }
        
        //Check minPrice
        Float minPrice = searchModel.getMinPrice();
        if(minPrice != null && minPrice.intValue() < 1) {
            return "{\"error\": \"minPrice should be >= 1.\"}";
        }
        
        //Check maxPrice
        Float maxPrice = searchModel.getMaxPrice();
        if(maxPrice != null && maxPrice.intValue() < 1) {
            return "{\"error\": \"maxPrice should be >= 1.\"}";
        }
        
        //Check minPrice vs maxPrice
        if(minPrice != null && maxPrice != null && minPrice.intValue() > maxPrice.intValue()) {
            return "{\"error\": \"minPrice should be <= maxPrice.\"}";
        }
        
        return null;

    }

    @Path("details/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response roomDetails(@PathParam("id") int id) {
        
        RoomManager roomManager = new RoomManager();
        RoomDetailModel roomDetails = roomManager.getRoomDetails(id);
        roomManager.destroy();
        
        return Response.status(Status.OK).entity(roomDetails).build();        
    }
    
    @Path("/employee/addRoom")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addRoom(RoomBasicModel model) {
        
        //First step, basic checks
        if(model.getHotelID() == null) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide hotelID.\"}").build();
        }
        if(model.getRoomNumber() == null) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide roomNumber.\"}").build();
        }
        if(model.getCapacity() == null) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide capacity.\"}").build();
        }
        if(model.getPrice() == null) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide price.\"}").build();
        }
        if(model.getExtendable() == null) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide extendable.\"}").build();
        }
        if(SanityUtils.isEmpty(model.getView())) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide view.\"}").build();
        }
        if(model.getCapacity() <= 0) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide capacity over 0.\"}").build();
        }
        if(model.getPrice() <= 0) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide price over 0.\"}").build();
        }
        if(!model.getView().equals("seaside") && !model.getView().equals("mountain")) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide view as either 'seaside' or 'mountain'.\"}").build();
        }
        
        RoomManager roomManager = new RoomManager();
        
        //Second step, check that no room with same number in hotel
        boolean roomExists = roomManager.roomExists(model.getHotelID(), model.getRoomNumber());
        
        if(roomExists) {
            roomManager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"A room with the same roomNumber in the give hotel already exists\"}").build();
        }
        
        //Third step, do the work
        boolean insertSuccess = roomManager.insertRoom(model);
        roomManager.destroy();
        
        if(!insertSuccess) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"Room insertion failed. "
                    + "Verify the hotel ID you gave is correct.\"}").build();
        } else {
            return Response.status(Status.OK).entity("{\"message\":\"Room added.\"}").build();
        }
        
    }

    @Path("/employee/deleteRoom/{roomID}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteHotel(@PathParam("roomID")int roomID) {
        
        RoomManager roomManager = new RoomManager();
        
        //First step, check if room exists.
        boolean roomExists = roomManager.roomExists(roomID);
        if(!roomExists) {
            roomManager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"No room with the given ID exists.\"}").build();
        }
        
        //Second step, look for all booking that are linked to this hotel
        List<BookingListModel> bookingsRentings = roomManager.getRoomBookingOrRentings(roomID);
        
        if(bookingsRentings.size() > 0 ) {
            roomManager.destroy();
            return Response.status(Status.UNAUTHORIZED).entity(bookingsRentings).build();
        }
        
        boolean deleteSuccess = roomManager.deleteRoom(roomID);
        
        if(!deleteSuccess) {
            roomManager.destroy();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"Delete not successful\"}").build();
        }
        
        roomManager.destroy();
        return Response.status(Status.OK).entity("{\"message\":\"Delete successful\"}").build();
        
    }

    @Path("/employee/updateRoom")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateHotel(RoomBasicModel model) {
        
        //First step, basic checks
        if(model.getRoomID() == null) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide the id of the room.\"}").build();
        }
        if((model.getRoomNumber() == null && model.getHotelID() != null) 
                || (model.getRoomNumber() != null && model.getHotelID() == null)) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"If roomNumber is given, hotelID is necessary, "
                    + "and vice-versa.\"}").build();
        }
        if(model.getCapacity() != null && model.getCapacity() <= 0) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide capacity over 0.\"}").build();
        }
        if(model.getPrice() != null && model.getPrice() <= 0) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide price over 0.\"}").build();
        }
        if(!SanityUtils.isEmpty(model.getView()) && (!model.getView().equals("seaside") && !model.getView().equals("mountain"))) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide view as either 'seaside' or 'mountain'.\"}").build();
        }
        
        //TODO foreign key checks
        
        RoomManager roomManager = new RoomManager();
        
        //Second step, check that room exists
        boolean roomExists = roomManager.roomExists(model.getRoomID());
        if(!roomExists) {
            roomManager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"No room with the given ID exists\"}").build();
        }
        
        //Third step, check we won't duplicate
        if(model.getRoomNumber() != null && model.getHotelID() != null) {
            boolean roomWithSameNumberExists = roomManager.roomExists(model.getHotelID(), model.getRoomNumber(), model.getRoomID());
            if(roomWithSameNumberExists) {
                roomManager.destroy();
                return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"A room with the same roomNumber in the give hotel already exists\"}").build();
            }
        }
        
        //fourth step, do the work
        boolean updateSuccess = false;
        try {
            updateSuccess = roomManager.updateRoom(model);
        } catch(IllegalArgumentException ex) {
            roomManager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"None of the possible parameters were given.\"}").build();
        }
        
        roomManager.destroy();
        if(!updateSuccess) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"Could not update room. "
                    + "Check that the given hotel id is correct, or the given room id is correct.\"}").build();
        } else {
            return Response.status(Status.OK).entity("{\"message\":\"Room updated.\"}").build();
        }
        
    }
    
    @Path("/employee/addAmenity")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addHotelAmenity(IdValueModel model) {
                
        //First check, no null values
        if(model.getOwnerID() == null) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide an id.\"}").build();
        }
        if(SanityUtils.isEmpty(model.getValue())) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide an amenity.\"}").build();
        }
        
        RoomManager roomManager = new RoomManager();
        
        //Second step, check if hotel exists.
        boolean roomExists = roomManager.roomExists(model.getOwnerID());
        if(!roomExists) {
            roomManager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"No room with the given ID\"}").build();
        }
        
        //Third step, try to insert
        boolean insertSuccess = roomManager.insertAmenity(model.getOwnerID(), model.getValue());
        roomManager.destroy();
        
        if(!insertSuccess) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"Amenity insert failed.\"}").build();
        } else {
            return Response.status(Status.OK).entity("{\"message\":\"Amenity added.\"}").build();
        }
        
    }
    
    @Path("/employee/addIssue")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addHotelIssue(IdValueModel model) {
                
        //First check, no null values
        if(model.getOwnerID() == null) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide an id.\"}").build();
        }
        if(SanityUtils.isEmpty(model.getValue())) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide an issue.\"}").build();
        }
        
        RoomManager roomManager = new RoomManager();
        
        //Second step, check if hotel exists.
        boolean roomExists = roomManager.roomExists(model.getOwnerID());
        if(!roomExists) {
            roomManager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"No room with the given ID\"}").build();
        }
        
        //Third step, try to insert
        boolean insertSuccess = roomManager.insertIssue(model.getOwnerID(), model.getValue());
        roomManager.destroy();
        
        if(!insertSuccess) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"Issue insert failed.\"}").build();
        } else {
            return Response.status(Status.OK).entity("{\"message\":\"Issue added.\"}").build();
        }
        
    }
    
    @Path("freeRoomsPerArea")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFreeRoomsPerArea() {
        
        RoomManager roomManager = new RoomManager();
        List<RoomsPerAreaModel> items = roomManager.getFreeRoomsPerArea();
        roomManager.destroy();
        return Response.status(Status.OK).entity(items).build();
        
    }
    
    @Path("/employee/deleteAmenity")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAmenity(@QueryParam("roomID")Integer roomID, @QueryParam("amenity")String amenity) {
        
        //First check, no null values
        if(roomID == null) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide an id.\"}").build();
        }
        if(SanityUtils.isEmpty(amenity)) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide the amenity.\"}").build();
        }
        
        RoomManager roomManager = new RoomManager();
        
        //Second step, check if hotel exists.
        boolean roomExists = roomManager.roomExists(roomID);
        if(!roomExists) {
            roomManager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"No room with the given ID\"}").build();
        }
        
        //Do the work
        boolean deleteSuccess = roomManager.deleteAmenity(roomID, amenity);
        roomManager.destroy();
        
        if(!deleteSuccess) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"Could not delete amenity for room.\"}").build();
        } else {
            return Response.status(Status.OK).entity("{\"message\":\"Amenity deleted.\"}").build();
        }
        
    }
    
    @Path("/employee/deleteIssue")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteIssue(@QueryParam("roomID")Integer roomID, @QueryParam("issue")String issue) {
        
        //First check, no null values
        if(roomID == null) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide an id.\"}").build();
        }
        if(SanityUtils.isEmpty(issue)) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide the issue.\"}").build();
        }
        
        RoomManager roomManager = new RoomManager();
        
        //Second step, check if hotel exists.
        boolean roomExists = roomManager.roomExists(roomID);
        if(!roomExists) {
            roomManager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"No room with the given ID\"}").build();
        }
        
        //Do the work
        boolean deleteSuccess = roomManager.deleteIssue(roomID, issue);
        roomManager.destroy();
        
        if(!deleteSuccess) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"Could not delete issue for room.\"}").build();
        } else {
            return Response.status(Status.OK).entity("{\"message\":\"Issue deleted.\"}").build();
        }
        
    }
    
    @Path("capacityPerRoom")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCapicityPerRoom() {
        
        RoomManager roomManager = new RoomManager();
        List<RoomListModel> items;
        try {
            items = roomManager.getCapacityPerRoom();
        } catch (SQLException e) {
            e.printStackTrace();
            roomManager.destroy();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\": \"" + e.getMessage() + "\"}").build();
        }
        roomManager.destroy();
        return Response.status(Status.OK).entity(items).build();
        
    }    
    
}



