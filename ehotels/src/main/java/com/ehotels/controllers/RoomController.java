package com.ehotels.controllers;

import java.sql.Timestamp;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.ehotels.db.RoomManager;
import com.ehotels.models.RoomDetailModel;
import com.ehotels.models.RoomListModel;
import com.ehotels.models.RoomSearchModel;

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
        if(searchModel.getStartDate() != null) {
            try {
                Timestamp.valueOf(searchModel.getStartDate());
            } catch (IllegalArgumentException ex) {
                return "{\"error\": \"wrong startDate format. Should be yyyy-[m]m-[d]d hh:mm:ss[.f...]\"}";
            }
        }
        
        //Check endDate. Note: this is quick and dirty. I'm just mimicking what I'll do to insert in DB
        if(searchModel.getEndDate() != null) {
            try {
                Timestamp.valueOf(searchModel.getEndDate());
            } catch (IllegalArgumentException ex) {
                return "{\"error\": \"wrong endDate format. Should be yyyy-[m]m-[d]d hh:mm:ss[.f...]\"}";
            }
        }
        
        //Check date order
        if(searchModel.getStartDate() != null && searchModel.getEndDate() != null) {
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
}



