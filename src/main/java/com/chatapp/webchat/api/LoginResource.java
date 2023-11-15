package com.chatapp.webchat.api;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.chatapp.webchat.JWT.JWTUtils;
import com.chatapp.webchat.database.DatabaseRepository;
import com.chatapp.webchat.entities.User;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.naming.NamingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

// Login Endpoint
@Path("/login")
public class LoginResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(User user) {
        // Validate credentials

        try {
            User storedUser = DatabaseRepository.getUserByUsername(user.getUsername());

            if (storedUser != null && user.getPasswordHash().equals(storedUser.getPasswordHash())) {
                // Credentials are valid, generate Auth0 JWT
                String token = new JWTUtils().generateAuthToken(storedUser);
                System.out.println("LoginResource user is " + storedUser.getUserId());

                // Return the JWT in the response
                Map<String, String> response = new HashMap<>();
                response.put("token", token);

                return Response.ok().entity(response).build();
            } else {
                // Invalid credentials
                return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid username or password").build();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

}