package com.udbhav.sherlock.resources;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.udbhav.sherlock.dao.MessageDao;
import com.udbhav.sherlock.utils.AuthUtil;
import com.udbhav.sherlock.utils.Logger;


@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MessageResource {
    private MessageDao messageDao;

    public MessageResource(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    @GET
    @Path("/history/{userId}")
    public Response getMessagesBetweenUsers(@PathParam("userId") String userId, @Context HttpHeaders headers) {
        String authHeader = headers.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Authorization header must be provided with Bearer token")
                    .build();
        }
        String token = authHeader.substring("Bearer ".length()).trim();
        Logger.info("Received token: " + token);
        // Validate token and extract userId (depends on your AuthService)
        String Id = AuthUtil.validateTokenAndGetUserId(token);
        if (Id == null) {
            Logger.error("Invalid or expired token: " + token);
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid or expired token")
                    .build();
        }
        // Fetch messages between the authenticated user and the specified user
        var messages = messageDao.getMessagesBetweenUsers(Id, userId);
        Logger.info("Fetched messages for userId: " + userId + " by user: " + Id);
        if (messages == null || messages.isEmpty()) {
            Logger.info("No messages found between userId: " + Id + " and userId: " + userId);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No messages found between the users")
                    .build();
        }
        return Response.ok(messages).build();
    }

}
