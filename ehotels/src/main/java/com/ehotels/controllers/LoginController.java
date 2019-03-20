package com.ehotels.controllers;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.ehotels.db.AuthenticationManager;
import com.ehotels.models.LoginModel;

@Path("login")
public class LoginController {
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postLogin(LoginModel model) {   
        AuthenticationManager manager = new AuthenticationManager();
        System.out.println("login for " + model.getUsername() + model.getPassword());
        UUID uuid = manager.login(model.getUsername(), model.getPassword());
        manager.destroy();
        if(uuid == null) {
            //could not login
            return Response.status(Status.UNAUTHORIZED).entity("{\"message\":\"Invalid Credentials\"}").build();
        } else {
            //we good
            return Response.status(Status.OK).entity("{\"session\":\""+uuid.toString()+"\"}").build();
        }
    }

}