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
import com.ehotels.models.RegisterModel;

@Path("register")
public class RegisterController {
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postRegister(RegisterModel model) { 
        AuthenticationManager manager = new AuthenticationManager();
        boolean emailExists = manager.emailExists(model.getEmail());
        //TODO:  add security for SSN too.
        if(!emailExists) {
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
        } else {
            //otherwise error out
            manager.destroy();
            return Response.status(Status.BAD_REQUEST).entity("{\"message\":\"Account already exists\"}").build();
        }
    }

}
