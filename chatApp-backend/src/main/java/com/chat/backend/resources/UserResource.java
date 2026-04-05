package com.chat.backend.resources;

import com.chat.backend.dao.UserDao;
import com.chat.backend.model.User;
import com.chat.backend.model.UserStatusDTO;
import com.chat.backend.utils.AuthUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    
    private final UserDao userDao;

    public UserResource(UserDao userDao) {
        this.userDao = userDao;
    }

    @GET
    @Path("/username")
    public Response getUsername(@Context HttpHeaders headers) {
        String authHeader = headers.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\": \"Unauthorized\"}").build();
        }
        String token = authHeader.substring("Bearer ".length()).trim();
        String idStr = AuthUtil.validateTokenAndGetUserId(token);
        if (idStr == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\": \"Invalid or expired token\"}").build();
        }

        Long userId = Long.parseLong(idStr);

        Optional<User> userOptional = userDao.getUserById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Wrap in JSON to be properly consumed by front-end clients
            String jsonResponse = String.format("{\"username\": \"%s\"}", user.getUserName());
            return Response.ok(jsonResponse).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"error\": \"User not found\"}")
                           .build();
        }
    }
    
    @GET
    @Path("/getUser")
    public Response getUser(@Context HttpHeaders headers) {
        String authHeader = headers.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\": \"Unauthorized\"}").build();
        }
        String token = authHeader.substring("Bearer ".length()).trim();
        String idStr = AuthUtil.validateTokenAndGetUserId(token);
        if (idStr == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\": \"Invalid or expired token\"}").build();
        }

        Long userId = Long.parseLong(idStr);

        Optional<User> userOptional = userDao.getUserById(userId);
        if (userOptional.isPresent()) {
            return Response.ok(userOptional.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"error\": \"User not found\"}")
                           .build();
        }
    }

    @GET
    @Path("/{userId}")
    public Response getUserById(@PathParam("userId") Long targetUserId, @Context HttpHeaders headers) {
        String authHeader = headers.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\": \"Unauthorized\"}").build();
        }
        String token = authHeader.substring("Bearer ".length()).trim();
        String idStr = AuthUtil.validateTokenAndGetUserId(token);
        if (idStr == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\": \"Invalid or expired token\"}").build();
        }

        Optional<User> userOptional = userDao.getUserById(targetUserId);
        if (userOptional.isPresent()) {
            return Response.ok(userOptional.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"error\": \"User not found\"}")
                           .build();
        }
    }

    @GET
    @Path("/{userId}/lastSeen")
    public Response getUserLastSeen(@PathParam("userId") Long targetUserId, @Context HttpHeaders headers) {
        String authHeader = headers.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\": \"Unauthorized\"}").build();
        }
        String token = authHeader.substring("Bearer ".length()).trim();
        String idStr = AuthUtil.validateTokenAndGetUserId(token);
        if (idStr == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\": \"Invalid or expired token\"}").build();
        }

        Optional<User> userOptional = userDao.getUserById(targetUserId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            System.out.println("user: " + user.getUserId() + " | " + user.isOnline() + " | " + user.getLastSeen());
            UserStatusDTO statusDTO = new UserStatusDTO(user.getLastSeen(), user.isOnline());
            return Response.ok(statusDTO).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"error\": \"User not found\"}")
                           .build();
        }
    }

}
