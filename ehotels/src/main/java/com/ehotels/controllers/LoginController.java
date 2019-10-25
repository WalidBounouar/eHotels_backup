package com.ehotels.controllers;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.ehotels.db.AuthenticationManager;
import com.ehotels.enums.Permission;
import com.ehotels.enums.UuidVerifResult;
import com.ehotels.models.LoginModel;

@Path("login")
public class LoginController {
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postLogin(LoginModel model) {   
        AuthenticationManager manager = new AuthenticationManager();
        System.out.println("login for " + model.getUsername() + model.getPassword());
        UUID uuid = manager.login(model.getUsername(), model.getPassword(), model.getPermission());
        manager.destroy();
        if(uuid == null) {
            //could not login
            return Response.status(Status.FORBIDDEN).entity("{\"message\":\"Invalid Credentials\"}").build();
        } else {
            //we good
            return Response.status(Status.OK).entity("{\"session\":\""+uuid.toString()+"\"}").build();
        }
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response isSessionValid(@Context HttpHeaders httpheaders) {
        String uuid = httpheaders.getHeaderString("authorization");
        uuid = uuid.replace("Bearer ", "");
        AuthenticationManager manager = new AuthenticationManager();
        if(manager.uuidValid(uuid, Permission.COMMON) == UuidVerifResult.OK) {
               return Response.status(Status.OK).build();
        } else {
               return Response.status(Status.FORBIDDEN).build();
        }
    }

}