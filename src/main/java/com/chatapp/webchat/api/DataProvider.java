package com.chatapp.webchat.api;

import com.chatapp.webchat.database.DatabaseRepository;
import com.chatapp.webchat.database.DatabaseRepositoryAsync;
import com.chatapp.webchat.entities.Message;
import com.chatapp.webchat.entities.User;
import com.chatapp.webchat.entities.UserChatRoom;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.naming.NamingException;
import java.sql.SQLException;
import java.util.List;
import com.chatapp.webchat.entities.ChatRoom;

@Path("/data-provider")
public class DataProvider {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("get-messages-for-chat-room")
    public List<Message> getAllMessagesForChatRoom(@QueryParam("chatRoomId") int chatRoomId) throws SQLException, NamingException {
        return DatabaseRepository.getMessages(chatRoomId);
    }
    // Method to retrieve the authenticated user's ID
    private int getAuthenticatedUserId() {
        // Implement the logic to get the authenticated user's ID
        // This might involve checking session information, tokens, etc.
        // For simplicity, let's assume you have a method to get the user ID.
        return 1; // Replace with actual logic
    }

    @POST
    @Path("create-message")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMessage(Message message) {
        try {

            // Create the message in the database
            DatabaseRepository.createMessage(message.getUserId(), message.getChatRoomId(), message.getContent());

            // Return a success response
            return Response.status(Response.Status.CREATED).entity("Message created successfully").build();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle other exceptions and return an appropriate response
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("create-message-async")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMessageAsync(Message message) {
        try {

            // Create the message in the database
            DatabaseRepositoryAsync.createMessageAsync(message.getUserId(), message.getChatRoomId(), message.getContent());

            // Return a success response
            return Response.status(Response.Status.CREATED).entity("Message created successfully").build();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle other exceptions and return an appropriate response
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


    @POST
    @Path("create-chat")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createChat(List<UserChatRoom> userChatRooms) {
        try {
            // Create the chat in the database
            DatabaseRepository.createChat(userChatRooms);

            // Return a success response
            return Response.status(Response.Status.CREATED).entity("Chat created successfully").build();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle other exceptions and return an appropriate response
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GET
    @Path("get-chats-by-user-id")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChatsByUserId(@QueryParam("userId") int userId) {
        try {

            // Retrieve chats from the database
            List<ChatRoom> chats = DatabaseRepository.getChatsByUserId(userId);

            // Return the chats as JSON
            return Response.ok().entity(chats).build();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle other exceptions and return an appropriate response
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("get-all-users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        try {
            List<User> users = DatabaseRepository.getAllUsers();
            return Response.ok(users).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


}