package com.chatapp.webchat.JWT;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.chatapp.webchat.entities.User;

import java.util.Date;

public class JWTUtils {

    private Algorithm algorithm = Algorithm.HMAC256("bomba");
    public String generateAuthToken(User user) {

        return JWT.create()
                .withSubject(user.getUsername())
                .withClaim("user_id", user.getUserId())
                .withExpiresAt(new Date(System.currentTimeMillis() + 86400000)) // 1 day in milliseconds
                .sign(algorithm);
    }

    // You may also want to add a method to verify Auth0 tokens
    public DecodedJWT verifyAuth0Token(String token) {
        return JWT.require(algorithm)
                .build()
                .verify(token);
    }
}
