package com.chatapp.webchat.api;

import com.chatapp.webchat.database.DatabaseRepository;
import com.chatapp.webchat.entities.User;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.naming.NamingException;
import java.sql.SQLException;

// Registration Endpoint
@Path("/register")
public class RegistrationResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(User user) {
        // Validate input
        if (user.getUsername() == null || user.getPasswordHash() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input").build();
        }


        // Store user in the database (Assuming you have a UserRepository class)
        try {
            DatabaseRepository.createUser(user.getUsername(), user.getPasswordHash());
        } catch (SQLException | NamingException e) {
            throw new RuntimeException(e);
        }

        return Response.status(Response.Status.CREATED).entity("User registered successfully").build();
    }
}