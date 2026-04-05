package com.chat.backend.resources;

import com.chat.backend.dao.UserDao;
import com.chat.backend.model.AuthUser;
import com.chat.backend.model.LoginResponse;
import com.chat.backend.service.AuthService;
import com.chat.backend.utils.Logger;

import javax.ws.rs.core.Response;
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

    public AuthResource(UserDao userDao) {
        this.authService = new AuthService(userDao);
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(AuthUser user) {
        Logger.info("Received request to register user with email: " + user.getEmailId());
        try {
            LoginResponse response = authService.register(user);
            Logger.info("Successfully registered user with email: " + user.getEmailId());
            return Response.ok(response).build();
        } catch (Exception e) {
            Logger.error("Failed to register user with email: " + user.getEmailId(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An error occurred during registration")
                    .build();
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(AuthUser loginRequest) {
        Logger.info("Received login request for email: " + loginRequest.getEmailId());
        try {
            Optional<LoginResponse> loginResponse = authService.login(loginRequest.getEmailId(),
                    loginRequest.getPassword());
            if (loginResponse.isPresent()) {
                Logger.info("Successfully logged in user with email: " + loginRequest.getEmailId());
                return Response.ok(loginResponse.get()).build();
            } else {
                Logger.error("Login attempt failed for email: " + loginRequest.getEmailId() + " due to invalid credentials.");
                return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
            }
        } catch (Exception e) {
            Logger.error("An error occurred during login for email: " + loginRequest.getEmailId(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An error occurred during login")
                    .build();
        }
    }
}
