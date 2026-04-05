package com.chat.backend.resources;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.chat.backend.dao.MessageDao;
import com.chat.backend.dao.UserChatDao;
import com.chat.backend.utils.AuthUtil;
import com.chat.backend.utils.Logger;
import java.util.Optional;
import java.util.ArrayList;

@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MessageResource {
    private final MessageDao messageDao;
    private final UserChatDao userChatDao;

    public MessageResource(MessageDao messageDao, UserChatDao userChatDao) {
        this.messageDao = messageDao;
        this.userChatDao = userChatDao;
    }

    @GET
    @Path("/chat/{chatId}")
    public Response getMessagesForChat(@PathParam("chatId") Long chatId, @Context HttpHeaders headers) {
        Logger.info("Received request to fetch messages for chat ID: " + chatId);
        try {
            String authHeader = headers.getHeaderString("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Logger.error("Missing or invalid Authorization header in getMessagesForChat");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Authorization header must be provided with Bearer token")
                        .build();
            }
            String token = authHeader.substring("Bearer ".length()).trim();
            String idStr = AuthUtil.validateTokenAndGetUserId(token);
            if (idStr == null) {
                Logger.error("Invalid or expired token provided in getMessagesForChat");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Invalid or expired token")
                        .build();
            }
            
            Long userId = Long.parseLong(idStr);
            Logger.debug("User " + userId + " requesting messages for chat " + chatId);

            Optional<Integer> isMember = userChatDao.isUserInChat(chatId, userId);
            if (isMember.isEmpty()) {
                Logger.error("User " + userId + " is not a member of chat " + chatId);
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("User is not a member of this chat")
                        .build();
            }

            var messages = messageDao.getMessagesForChat(chatId);
            if (messages == null || messages.isEmpty()) {
                Logger.info("No messages found for chat ID: " + chatId);
                return Response.ok(new ArrayList<>()).build();
            }
            
            Logger.info("Successfully fetched " + messages.size() + " messages for chat ID: " + chatId);
            return Response.ok(messages).build();
        } catch (Exception e) {
            Logger.error("Error fetching messages for chat ID: " + chatId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Server error: " + e.getMessage())
                    .build();
        }
    }

    public static class ReadMessageRequest {
        public Long messageId;
    }

    @POST
    @Path("/chat/{chatId}/read")
    public Response markAsRead(@PathParam("chatId") Long chatId, ReadMessageRequest request, @Context HttpHeaders headers) {
        Logger.info("Received request to mark message ID: " + request.messageId + " as read in chat ID: " + chatId);
        try {
            String authHeader = headers.getHeaderString("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Logger.error("Missing or invalid Authorization header in markAsRead");
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            String token = authHeader.substring("Bearer ".length()).trim();
            String idStr = AuthUtil.validateTokenAndGetUserId(token);
            if (idStr == null) {
                Logger.error("Invalid or expired token provided in markAsRead");
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            
            Long userId = Long.parseLong(idStr);
            Logger.debug("User " + userId + " marking message " + request.messageId + " as read in chat " + chatId);
            
            Optional<Integer> isMember = userChatDao.isUserInChat(chatId, userId);
            if (isMember.isEmpty()) {
                Logger.error("User " + userId + " is not a member of chat " + chatId);
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            userChatDao.updateLastReadMessage(chatId, userId, request.messageId);
            Logger.info("Successfully marked message ID: " + request.messageId + " as read for user " + userId + " in chat " + chatId);
            return Response.ok().build();
        } catch (Exception e) {
            Logger.error("Error marking message as read in chat ID: " + chatId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Server error: " + e.getMessage())
                    .build();
        }
    }
}