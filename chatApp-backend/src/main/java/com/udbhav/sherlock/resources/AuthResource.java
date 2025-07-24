package com.udbhav.sherlock.resources;

import com.udbhav.sherlock.dao.AuthUserDao;
import com.udbhav.sherlock.dao.UserDao;
import com.udbhav.sherlock.model.AuthUser;
import com.udbhav.sherlock.model.LoginResponse;
import com.udbhav.sherlock.service.AuthService;
import com.udbhav.sherlock.utils.Logger;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Optional;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final AuthService authService;

    public AuthResource(AuthUserDao userDao) {
        this.authService = new AuthService(userDao);
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(AuthUser user) {
        LoginResponse response = authService.register(user);
        return Response.ok(response).build();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(AuthUser loginRequest) {
        Optional<LoginResponse> loginResponse = authService.login(loginRequest.getEmailId(),
                loginRequest.getPassword());
        if (loginResponse.isPresent()) {
            return Response.ok(loginResponse.get()).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }
    }

}
