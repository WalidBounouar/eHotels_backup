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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.ehotels.db.AuthenticationManager;
import com.ehotels.db.HotelManager;
import com.ehotels.db.PeopleManager;
import com.ehotels.db.RoomManager;
import com.ehotels.models.BookingListModel;
import com.ehotels.models.ClientModel;
import com.ehotels.models.EmployeeModel;
import com.ehotels.models.HotelDetailModel;
import com.ehotels.models.IdValueModel;
import com.ehotels.models.RegisterModel;
import com.ehotels.models.UserModel;
import com.ehotels.utilities.SanityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Path("users")
public class PeopleController {
    
    @Path("/whoAmI")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMyBookings(@Context HttpHeaders httpheaders) {       
        String uuid = httpheaders.getHeaderString("authorization");
        uuid = uuid.replace("Bearer ", "");
        int loginCredID = AuthenticationManager.getLoginCredId(uuid);
        //TODO check in DB if usedID = -1;
        
        PeopleManager peopleManager = new PeopleManager();
        UserModel user = peopleManager.whoAmI(loginCredID);
        peopleManager.destroy();
        return Response.status(Status.OK).entity(user).build();
        
    }
    
    @Path("/employee/managers")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllManager() {
        
        PeopleManager peopleManager = new PeopleManager();
        List<EmployeeModel> managers = peopleManager.getAllManagers();
        peopleManager.destroy();
        return Response.status(Status.OK).entity(managers).build();
        
    }
    
    @Path("/employee/clients")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllClients() {
        
        PeopleManager peopleManager = new PeopleManager();
        List<ClientModel> clients = peopleManager.getAllClients();
        peopleManager.destroy();
        return Response.status(Status.OK).entity(clients).build();
        
    }
    
    @Path("/employee/deleteClient/{clientID}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteClient(@PathParam("clientID")int clientID) {
        
        PeopleManager peopleManager = new PeopleManager();
        
        //First step, check if cleint exists.
        boolean clientExists = peopleManager.clientExists(clientID);
        if(!clientExists) {
            peopleManager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"No client with the given ID exists.\"}").build();
        }
        
        //Second step, look for all booking that are linked to this client
        RoomManager roomManager = new RoomManager();
        List<BookingListModel> bookingsRentings = roomManager.getClientBookingOrRentings(clientID);
        roomManager.destroy();
        if(bookingsRentings.size() > 0 ) {
            return Response.status(Status.UNAUTHORIZED).entity(bookingsRentings).build();
        }
        
        boolean deleteSuccess = peopleManager.deleteClient(clientID);
        
        if(!deleteSuccess) {
            peopleManager.destroy();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"Delete not successful\"}").build();
        }
        
        peopleManager.destroy();
        return Response.status(Status.OK).entity("{\"message\":\"Delete successful\"}").build();
        
    }
    
    @Path("/employee/addEmployee")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addEmployee(RegisterModel model) { 
        AuthenticationManager manager = new AuthenticationManager();
        boolean emailExists = manager.emailExists(model.getEmail());
        if(emailExists) {
            manager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\":\"Account already exists\"}").build();
        }
        
        PeopleManager peopleManager = new PeopleManager();
        boolean ssnExists = peopleManager.ssnExists(model.getSsn(), true);
        peopleManager.destroy();
        if(ssnExists) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\":\"employee SSN already in system\"}").build();
        }
        
        try {
            manager.createEmployeeAccount(model.getEmail(), model.getPassword(), 
                        model.getSsn(), model.getLastName(), model.getMiddleName(), 
                        model.getFirstName(), model.getStreetNumber(), model.getStreetName(), 
                        model.getCity(), model.getState(), model.getZip());
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            manager.destroy();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"" + e.getMessage() + "\"}").build();
        }
        manager.destroy();
        return Response.status(Status.OK).entity("{\"message\":\"Account created\"}").build();
            
    }

    @Path("/employee/deleteEmployee/{employeeID}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteEmployee(@PathParam("employeeID")int employeeID) {
        
        //Step -1, reject deletes of employee 0, aka the SYSTEM.
        //TODO this is cheap, maybe find elegant solution
        if(employeeID == 1) {
            return Response.status(Status.UNAUTHORIZED).entity("{\"message\": \"Not allowed to delete employee 1.\"}").build();
        }
                
        PeopleManager peopleManager = new PeopleManager();
        
        //Step 0 step, check if employee exists.
        boolean employeeExists = peopleManager.employeeExists(employeeID);
        if(!employeeExists) {
            peopleManager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"No employee with the given ID exists.\"}").build();
        }
        
        //Step 1, check if employee manages Hotels
        HotelManager hotelManager = new HotelManager();
        List<HotelDetailModel> hotels = hotelManager.getManagerHotels(employeeID);
        hotelManager.destroy();
        if(hotels.size() > 0 ) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode output = mapper.createObjectNode();
            ((ObjectNode) output).put("message", "Employee manages hotels");
            ((ObjectNode) output).putPOJO("hotels", hotels);
            return Response.status(Status.UNAUTHORIZED).entity(output).build();
        }
        
        //Step 2, look for all booking that are linked to this employee
        RoomManager roomManager = new RoomManager();
        List<BookingListModel> bookingsRentings = roomManager.getEmployeeBookingOrRentings(employeeID);
        roomManager.destroy();
        if(bookingsRentings.size() > 0 ) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode output = mapper.createObjectNode();
            ((ObjectNode) output).put("message", "Employee is responsible for bookings/rentings");
            ((ObjectNode) output).putPOJO("bookingsRentings", bookingsRentings);
            return Response.status(Status.UNAUTHORIZED).entity(output).build();
        }
        
        boolean deleteSuccess = peopleManager.deleteEmployee(employeeID);
        
        if(!deleteSuccess) {
            peopleManager.destroy();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"Delete not successful\"}").build();
        }
        
        peopleManager.destroy();
        return Response.status(Status.OK).entity("{\"message\":\"Delete successful\"}").build();
        
    }
    
    @Path("/employee/allEmployees")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEmployees() {
        
        PeopleManager peopleManager = new PeopleManager();
        List<EmployeeModel> employees = peopleManager.getAllEmployees();
        peopleManager.destroy();
        return Response.status(Status.OK).entity(employees).build();
        
    }
    
    @Path("/employee/updateClient")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateClient(UserModel model) {
        
        return updateUser(model, false);
        
    }
    
    @Path("/employee/updateEmployee")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEmployee(UserModel model) {
        
        //TODO this is cheap, maybe find elegant solution
        if(model.getID() == 1) {
            return Response.status(Status.UNAUTHORIZED).entity("{\"message\": \"Not allowed to change employee 1.\"}").build();
        }
        
        return updateUser(model, true);
        
    }
    
    private Response updateUser(UserModel model, boolean isEmployee) {
        
        String role = isEmployee ? "employee" : "client";
        
        //First step, basic checks
        if(model.getID() == null) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide the id of the " + role + ".\"}").build();
        }
        
        PeopleManager peopleManager = new PeopleManager();
        
        //Second step, check if client exists.
        boolean userExists = false;
        if(isEmployee) {
            userExists = peopleManager.employeeExists(model.getID());
        } else {
            userExists = peopleManager.clientExists(model.getID());
        }
        if(!userExists) {
            peopleManager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"No " + role + " with the given ID exists.\"}").build();
        }
        
        //Third step, don't duplicate ssn
        boolean ssnExistsForOtherUser = peopleManager.ssnExistsForOtherUser(model.getID(), model.getSsn(), isEmployee);
        if(ssnExistsForOtherUser) {
            peopleManager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\":\"This request would introduce duplicate ssn in system.\"}").build();
        }
        
        //fourth step, do the work
        boolean updateSuccess = false;
        try {
            updateSuccess = peopleManager.updateClient(model, isEmployee);
        } catch (SQLException e) {
            e.printStackTrace();
            peopleManager.destroy();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\": \"" + e.getMessage() + "\"}").build();
        } catch(IllegalArgumentException ex) {
            peopleManager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"" + ex.getMessage() + ".\"}").build();
        } 
        
        peopleManager.destroy();
        if(!updateSuccess) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"Could not update " + role + ".\"}").build();
        } else {
            return Response.status(Status.OK).entity("{\"message\":\"" + role + " updated.\"}").build();
        }
        
    }
    
    @Path("/employee/addRole")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addEmployeeRole(IdValueModel model) {
        
        //TODO this is cheap, maybe find elegant solution
        if(model.getOwnerID() == 1) {
            return Response.status(Status.UNAUTHORIZED).entity("{\"message\": \"Not allowed to change employee 1.\"}").build();
        }
                
        //First check, no null values
        if(model.getOwnerID() == null) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide an id.\"}").build();
        }
        if(SanityUtils.isEmpty(model.getValue())) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide a role.\"}").build();
        }
        
        PeopleManager peopleManager = new PeopleManager();
        
        //Second step, check if hotel exists.
        boolean employeeExists = peopleManager.employeeExists(model.getOwnerID());
        if(!employeeExists) {
            peopleManager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"No employee with the given ID\"}").build();
        }
        
        //Third step, try to insert
        boolean insertSuccess;
        try {
            insertSuccess = peopleManager.insertRole(model.getOwnerID(), model.getValue());
        } catch (SQLException e) {
            e.printStackTrace();
            peopleManager.destroy();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\": \"" + e.getMessage() + "\"}").build();
        }
        peopleManager.destroy();
        
        if(!insertSuccess) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"Role insert failed.\"}").build();
        } else {
            return Response.status(Status.OK).entity("{\"message\":\"Role added.\"}").build();
        }
        
    }
    
    @Path("/employee/deleteRole")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRole(@QueryParam("employeeID")Integer employeeID, @QueryParam("role")String role) {
        
        //TODO this is cheap, maybe find elegant solution
        if(employeeID == 1) {
            return Response.status(Status.UNAUTHORIZED).entity("{\"message\": \"Not allowed to change employee 1.\"}").build();
        }
        
        //First check, no null values
        if(employeeID == null) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide an id.\"}").build();
        }
        if(SanityUtils.isEmpty(role)) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"Please provide the role.\"}").build();
        }
        
        PeopleManager peopleManager = new PeopleManager();
        
        //Second step, check if employee exists.
        boolean employeeExists = peopleManager.employeeExists(employeeID);
        if(!employeeExists) {
            peopleManager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"No employee with the given ID\"}").build();
        }
        
        //Do the work
        boolean deleteSuccess = false;
        try {
            deleteSuccess = peopleManager.deleteRole(employeeID, role);
        } catch (SQLException e) {
            e.printStackTrace();
            peopleManager.destroy();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\": \"" + e.getMessage() + "\"}").build();
        }
        peopleManager.destroy();
        
        if(!deleteSuccess) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"Could not delete role for employee.\"}").build();
        } else {
            return Response.status(Status.OK).entity("{\"message\":\"Role deleted.\"}").build();
        }
        
    }
    
    @Path("/employee/employeeDetails/{employeeID}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEmployeeDetail(@PathParam("employeeID")int employeeID) {
        
        PeopleManager peopleManager = new PeopleManager();
        
        boolean employeeExists = peopleManager.employeeExists(employeeID);
        if(!employeeExists) {
            peopleManager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"No employee with the given ID\"}").build();
        }
        
        EmployeeModel model;
        try {
            model = peopleManager.getEmployeeDetails(employeeID);
        } catch (SQLException e) {
            e.printStackTrace();
            peopleManager.destroy();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\": \"" + e.getMessage() + "\"}").build();
        }
        peopleManager.destroy();
        return Response.status(Status.OK).entity(model).build();
        
    }
    
    @Path("/employee/clientDetails/{clientID}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientDetail(@PathParam("clientID")int clientID) {
        
        PeopleManager peopleManager = new PeopleManager();
        
        boolean clientExists = peopleManager.clientExists(clientID);
        if(!clientExists) {
            peopleManager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\": \"No client with the given ID\"}").build();
        }
        
        ClientModel model;
        try {
            model = peopleManager.getClientDetails(clientID);
        } catch (SQLException e) {
            e.printStackTrace();
            peopleManager.destroy();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"message\": \"" + e.getMessage() + "\"}").build();
        }
        peopleManager.destroy();
        return Response.status(Status.OK).entity(model).build();
        
    }
}
