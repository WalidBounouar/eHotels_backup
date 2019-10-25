package com.ehotels.controllers;

import java.sql.SQLException;
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
import com.ehotels.models.HotelChainBasicModel;
import com.ehotels.models.HotelDetailModel;
import com.ehotels.models.HotelListModel;
import com.ehotels.models.IdValueModel;
import com.ehotels.utilities.SanityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Path("hotels")
public class HotelController {
    
    @Path("/employee/allHotels")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllHotels() {
        
        HotelManager hotelManager = new HotelManager();
        List<HotelListModel> hotels = hotelManager.getAllHotels();
        hotelManager.destroy();
        return Response.status(Status.OK).entity(hotels).build();
        
    }
    
    @Path("/employee/hotelDetail/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHotelDetail(@PathParam("id") int id) {
        
        HotelManager hotelManager = new HotelManager();
        HotelDetailModel hotel = hotelManager.getHotelDetails(id);
        hotelManager.destroy();
        return Response.status(Status.OK).entity(hotel).build();
        
    }
    
    @Path("/employee/allChains")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllChains() {
        
        HotelManager hotelManager = new HotelManager();
        List<HotelChainBasicModel> hotels = hotelManager.getAllChains();
        hotelManager.destroy();
        return Response.status(Status.OK).entity(hotels).build();
        
    }
    
    @Path("/employee/addHotel")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addHotel(HotelDetailModel model) {
        
        //First step, basic checks
        if(model.getChainID() == null) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide chainID.\"}").build();
        }
        if(model.getManagerID() == null) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide managerID.\"}").build();
        }
        if(model.getStarRating() == null) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide starRating.\"}").build();
        }
        if(model.getStreetNumber() == null) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide streetNumber.\"}").build();
        }
        if(SanityUtils.isEmpty(model.getStreetName())) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide streetName.\"}").build();
        }
        if(SanityUtils.isEmpty(model.getCity())) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide city.\"}").build();
        }
        if(SanityUtils.isEmpty(model.getState())) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide state.\"}").build();
        }
        if(SanityUtils.isEmpty(model.getZip())) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide zip.\"}").build();
        }
        if(model.getStarRating() < 0 || model.getStarRating() > 5) {
            return Response.status(Status.BAD_REQUEST).entity("\"Rating should be between 0 and 5.\"").build();
        }
                
        HotelManager hotelManager = new HotelManager();
        
        //Second step, check if hotel exists. Criteria is ZIP.
        boolean hotelExists = hotelManager.hotelExists(model.getZip());
        if(hotelExists) {
            hotelManager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"A hotel with the same ZIP already exists\"}").build();
        }
                
        //Third step, try to insert
        boolean insertSuccess = false;
        try {
            insertSuccess = hotelManager.insertHotel(model);
        } catch (SQLException e) {
            e.printStackTrace();
            hotelManager.destroy();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\": \"" + e.getMessage() + "\"}").build();
        }
        hotelManager.destroy();
        
        if(!insertSuccess) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"Hotel insertion was not successful. "
                    + "Maybe check that the manager you gave is really a manager\"}").build();
        } else {
            return Response.status(Status.OK).entity("{\"message\":\"Hotel added.\"}").build();
        }
        
    }
    
    @Path("/employee/updateHotel")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateHotel(HotelDetailModel model) {
        
        //First step, basic checks
        if(model.getHotelID() == null) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide the id of the hotel.\"}").build();
        }
        if(model.getStarRating() != null && (model.getStarRating() < 0 || model.getStarRating() > 5)) {
            return Response.status(Status.BAD_REQUEST).entity("\"Rating should be between 0 and 5.\"").build();
        }
                
        HotelManager hotelManager = new HotelManager();
        
        //Second step, check if hotel exists. Criteria is ZIP.
        boolean hotelExists = hotelManager.hotelExists(model.getHotelID());
        if(!hotelExists) {
            hotelManager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"No hotel with the given ID\"}").build();
        }
        
        //Third step, check we won't duplicate zip
        if(model.getZip() != null) {
            boolean hotelWithSameZipExists = hotelManager.hotelExists(model.getZip(), model.getHotelID());
            if(hotelWithSameZipExists) {
                hotelManager.destroy();
                return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"A hotel with the same ZIP already exists\"}").build();
            }
        }
        
        //fourth step, do the work
        boolean updateSuccess = false;
        try {
            updateSuccess = hotelManager.updateHotel(model);
        } catch (SQLException e) {
            e.printStackTrace();
            hotelManager.destroy();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\": \"" + e.getMessage() + "\"}").build();
        } catch(IllegalArgumentException ex) {
            hotelManager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"" + ex.getMessage() + ".\"}").build();
        } 
        
        hotelManager.destroy();
        if(!updateSuccess) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"Could not update Hotel. "
                    + "Check that the chainID and managerID you gave were correct.\"}").build();
        } else {
            return Response.status(Status.OK).entity("{\"message\":\"Hotel updated.\"}").build();
        }
        
    }
    
    @Path("/employee/addHotelPhone")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addHotelPhoneNumber(IdValueModel model) {
                
        //First check, no null values
        if(model.getOwnerID() == null) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide an id.\"}").build();
        }
        if(SanityUtils.isEmpty(model.getValue())) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide the phone number.\"}").build();
        }
        //TODO check phone number format
        
        HotelManager hotelManager = new HotelManager();
        
        //Second step, check if hotel exists. Criteria is ZIP.
        boolean hotelExists = hotelManager.hotelExists(model.getOwnerID());
        if(!hotelExists) {
            hotelManager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"No hotel with the given ID\"}").build();
        }
        
        //Third step, try to insert
        boolean insertSuccess = hotelManager.insertHotelPhoneNumber(model.getOwnerID(), model.getValue());
        hotelManager.destroy();
        
        if(!insertSuccess) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"Phone number insert failed.\"}").build();
        } else {
            return Response.status(Status.OK).entity("{\"message\":\"Phone number added.\"}").build();
        }
        
    }
    
    @Path("/employee/deleteHotelPhone")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteHotelPhoneNumber(@QueryParam("hotelID")Integer hotelID, @QueryParam("pNumber")String phoneNumber) {
                
        //First check, no null values
        if(hotelID == null) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide an id.\"}").build();
        }
        if(SanityUtils.isEmpty(phoneNumber)) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide the phone number.\"}").build();
        }
        
        HotelManager hotelManager = new HotelManager();
        
        //Second step, check if hotel exists. Criteria is ZIP.
        boolean hotelExists = hotelManager.hotelExists(hotelID);
        if(!hotelExists) {
            hotelManager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"No hotel with the given ID\"}").build();
        }
        
        //Do the work
        boolean deleteSuccess = hotelManager.deleteHotelPhoneNumber(hotelID, phoneNumber);
        hotelManager.destroy();
        
        if(!deleteSuccess) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"Could not delete phone number for hotel.\"}").build();
        } else {
            return Response.status(Status.OK).entity("{\"message\":\"Phone number deleted.\"}").build();
        }
        
    }
    
    @Path("/employee/deleteHotel/{hotelID}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteHotel(@PathParam("hotelID")int hotelID) {
        
        HotelManager hotelManager = new HotelManager();
        
        //First step, check if hotel exists.
        boolean hotelExists = hotelManager.hotelExists(hotelID);
        if(!hotelExists) {
            hotelManager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"No hotel with the given ID\"}").build();
        }
        
        //Second step, look for all booking that are linked to this hotel
        RoomManager roomManager = new RoomManager();
        List<BookingListModel> bookingsRentings = roomManager.getHotelBookingOrRentings(hotelID);
        roomManager.destroy();
        
        if(bookingsRentings.size() > 0 ) {
            hotelManager.destroy();
            return Response.status(Status.UNAUTHORIZED).entity(bookingsRentings).build();
        }
        
        boolean deleteSuccess = hotelManager.deleteHotel(hotelID);
        
        if(!deleteSuccess) {
            roomManager.destroy();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"Delete not successful\"}").build();
        }
        
        roomManager.destroy();
        return Response.status(Status.OK).entity("{\"message\":\"Delete successful\"}").build();
        
    }
    
    @Path("/citiesAndStates")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCitiesAndStates() {
        
        HotelManager hotelManager = new HotelManager();
        List<String> cities = hotelManager.getCities();
        List<String> states = hotelManager.getStates();
        List<String> chains = hotelManager.getChains();
        hotelManager.destroy();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode output = mapper.createObjectNode();
        ((ObjectNode) output).putPOJO("cities", cities);
        ((ObjectNode) output).putPOJO("states", states);
        ((ObjectNode) output).putPOJO("chains", chains);
        return Response.status(Status.OK).entity(output).build();
        
    }

}
