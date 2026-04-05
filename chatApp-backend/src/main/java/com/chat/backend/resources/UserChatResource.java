package com.chat.backend.resources;

import javax.ws.rs.core.HttpHeaders;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import com.chat.backend.dao.UserChatDao;
import com.chat.backend.dao.UserDao;
import com.chat.backend.service.UserChatService;
import com.chat.backend.service.UserService;
import com.chat.backend.utils.AuthUtil;
import com.chat.backend.utils.Logger;
import com.chat.backend.model.Chat;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserChatResource {
    private final UserChatService userChatService;
    private final UserService userService;

    public UserChatResource(UserChatDao userChatDao, UserDao userDao) {
        this.userChatService = new UserChatService(userChatDao, userDao);
        this.userService = new UserService(userDao);
    }

    @GET
    @Path("/chat_list")
    public Response getChatList(@Context HttpHeaders headers) {
        Logger.info("Received request to fetch chat list");
        try {
            // Extract JWT token from Authorization header
            String authHeader = headers.getHeaderString("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Logger.error("Missing or invalid Authorization header in getChatList");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Authorization header must be provided with Bearer token")
                        .build();
            }
            String token = authHeader.substring("Bearer ".length()).trim();
            // Validate token and extract userId (depends on your AuthService)
            String idStr = AuthUtil.validateTokenAndGetUserId(token);

            if (idStr == null) {
                Logger.error("Invalid or expired token provided in getChatList");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Invalid or expired token")
                        .build();
            }
            Long userId = Long.parseLong(idStr);
            Logger.debug("Fetching chat list for user ID: " + userId);

            // Fetch chat list for the user
            List<Chat> chatList = userChatService.getUserChatListHistory(userId);
            Logger.info("Successfully fetched chat list for user ID: " + userId);
            return Response.ok(chatList).build();

        } catch (Exception e) {
            Logger.error("Error fetching chat list", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Server error: " + e.getMessage())
                    .build();
        }
    }

    public static class CreateDirectChatRequest {
        public Long user2Id;
    }

    @POST
    @Path("/create_direct_chat")
    public Response createDirectChat(CreateDirectChatRequest request, @Context HttpHeaders headers) {
        Logger.info("Received request to create direct chat");
        try {
            String authHeader = headers.getHeaderString("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Logger.error("Missing or invalid Authorization header in createDirectChat");
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            String token = authHeader.substring("Bearer ".length()).trim();
            String idStr = AuthUtil.validateTokenAndGetUserId(token);
            if (idStr == null) {
                Logger.error("Invalid or expired token provided in createDirectChat");
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            Long userId = Long.parseLong(idStr);
            Logger.debug("User " + userId + " requesting direct chat creation with user " + request.user2Id);
            
            Chat chat = userChatService.createDirectChat(userId, request.user2Id);
            Logger.info("Successfully created/fetched direct chat for users " + userId + " and " + request.user2Id);
            return Response.ok(chat).build();
        } catch (Exception e) {
            Logger.error("Error creating direct chat", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    public static class CreateGroupChatRequest {
        public String chatName;
        public List<Long> memberIds;
    }

    @POST
    @Path("/create_group_chat")
    public Response createGroupChat(CreateGroupChatRequest request, @Context HttpHeaders headers) {
        Logger.info("Received request to create group chat: " + request.chatName);
        try {
            String authHeader = headers.getHeaderString("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Logger.error("Missing or invalid Authorization header in createGroupChat");
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            String token = authHeader.substring("Bearer ".length()).trim();
            String idStr = AuthUtil.validateTokenAndGetUserId(token);
            if (idStr == null) {
                Logger.error("Invalid or expired token provided in createGroupChat");
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            Long userId = Long.parseLong(idStr);
            Logger.debug("User " + userId + " requesting group chat creation with " + request.memberIds.size() + " members");
            
            Chat chat = userChatService.createGroupChat(userId, request.chatName, request.memberIds);
            Logger.info("Successfully created group chat " + request.chatName);
            return Response.ok(chat).build();
        } catch (Exception e) {
            Logger.error("Error creating group chat", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/email/{emailId}")
    public Response getUserByEmail(@PathParam("emailId") String emailId, @Context HttpHeaders headers) {
        Logger.info("Received request to fetch user by email: " + emailId);
        try {
            String authHeader = headers.getHeaderString("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Logger.error("Missing or invalid Authorization header in getUserByEmail");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Authorization header must be provided with Bearer token")
                        .build();
            }
            String token = authHeader.substring("Bearer ".length()).trim();
            String idStr = AuthUtil.validateTokenAndGetUserId(token);
            if (idStr == null) {
                Logger.error("Invalid or expired token provided in getUserByEmail");
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            
            Logger.debug("Validating user retrieval request for email: " + emailId);
            var user = userService.getUserByEmail(emailId);
            if (user.isEmpty()) {
                Logger.info("User not found with email: " + emailId);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found with email: " + emailId)
                        .build();
            }
            
            Logger.info("Successfully fetched user by email: " + emailId);
            return Response.ok(user).build();
        } catch (Exception e) {
            Logger.error("Error fetching user by email: " + emailId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Server error: " + e.getMessage())
                    .build();
        }
    }
}