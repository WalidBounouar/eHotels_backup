package com.ehotels.controllers;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.ehotels.db.AuthenticationManager;
import com.ehotels.db.PeopleManager;
import com.ehotels.models.RegisterModel;

@Path("register")
public class RegisterController {
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postRegister(RegisterModel model) { 
        AuthenticationManager manager = new AuthenticationManager();
        boolean emailExists = manager.emailExists(model.getEmail());
        if(emailExists) {
            manager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\":\"Account already exists\"}").build();
        }
        
        PeopleManager peopleManager = new PeopleManager();
        boolean ssnExists = peopleManager.ssnExists(model.getSsn(), false);
        peopleManager.destroy();
        if(ssnExists) {
            return Response.status(Status.BAD_REQUEST).entity("{\"message\":\"client SSN already in system\"}").build();
        }
        
        try {
            manager.createClientAccount(model.getEmail(), model.getPassword(), 
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

}
