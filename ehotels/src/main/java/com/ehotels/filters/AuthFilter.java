package com.ehotels.filters;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.ehotels.db.AuthenticationManager;
import com.ehotels.enums.Permission;
import com.ehotels.models.AuthErrorModel;

public class AuthFilter implements ContainerRequestFilter {
    

    public void filter(ContainerRequestContext containerRequest) throws WebApplicationException{

        // Automatically allow certain requests.
        String method = containerRequest.getMethod();
        String path = containerRequest.getUriInfo().getPath(true);

        if (method.equals("OPTIONS")) {
            containerRequest.abortWith(Response.status(Status.OK).build());
            return;
        }

        System.out.println("LIGMA: " + path);
        //Skipping for 'public' endpoints
        if (path.equals("login") || path.equals("register")) {
            return;
        }

        System.out.println(path);
        
        // Get the authentication passed in HTTP headers parameters
        String sessionUUID = containerRequest.getHeaders().getFirst("authorization");
        if (sessionUUID == null) {
            containerRequest.abortWith(
                    Response.status(Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"realm\"")
                            .entity(new AuthErrorModel("Page requires login.")).build());
            return;
        }

        sessionUUID = sessionUUID.replace("Bearer ", "");

        AuthenticationManager manager = new AuthenticationManager();
        
        boolean validUUID = false;
        String errorMsg = "Invalid Session Token";
        //Different paths (employee, client or common) have different authentication checks
        //Client operation TODO: change 'true' to the right paths
        if(false) {
            validUUID = manager.uuidValid(sessionUUID, Permission.CLIENT);
            errorMsg = "Invalid Client Session Token";
            
        } else if(false) { //Employee operations TODO: change 'true' to the right paths
            validUUID = manager.uuidValid(sessionUUID, Permission.EMPLOYEE);
            errorMsg = "Invalid Employee Session Token";
            
        } else { //Common operations
            validUUID = manager.uuidValid(sessionUUID, Permission.COMMON);
            errorMsg = "Invalid Session Token";
        }

        if (!validUUID) {
            manager.destroy();
            System.out.println("Rejected login");
            containerRequest.abortWith(
                    Response.status(Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"realm\"")
                            .entity(new AuthErrorModel(errorMsg)).build());
        }
        
        manager.destroy();
    }
}
