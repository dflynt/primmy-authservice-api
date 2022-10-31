package org.dflynt.primmy.authservice.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import org.dflynt.primmy.authservice.models.User;
import org.dflynt.primmy.authservice.exceptions.IncorrectCredentialsException;
import org.dflynt.primmy.authservice.exceptions.InvalidRefreshTokenException;
import org.dflynt.primmy.authservice.exceptions.UserNotFoundException;
import org.dflynt.primmy.authservice.repositories.AuthenticationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationService {

    Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    AuthenticationRepository authRepository;

    private final String ACCESS_TOKEN = "37f3d42fd20eb72055aea46bbc31f00c5b49ca480b3c613a5288602f6d4af7a5";
    private final String REFRESH_TOKEN = "6d0e34ec58f18b8b18d952cc6a3d084410665a2ae382769faebf80680aba9215";
    byte[] accessKeyBytes;
    byte[] refreshKeyBytes;

    Key accessKey;
    Key refreshKey;

    public AuthenticationService(AuthenticationRepository repository) {
        authRepository = repository;
        accessKeyBytes = Decoders.BASE64.decode(ACCESS_TOKEN);
        refreshKeyBytes = Decoders.BASE64.decode(REFRESH_TOKEN);

        accessKey = Keys.hmacShaKeyFor(accessKeyBytes);
        refreshKey = Keys.hmacShaKeyFor(refreshKeyBytes);
    }

    public User attemptLogin(Map<String, String> credentials) throws UserNotFoundException, IncorrectCredentialsException {
        String email = credentials.get("email");
        String password = credentials.get("password");
        User u = authRepository.findByemail(email);
        if(u == null) {
            throw new UserNotFoundException("User not found");
        }

        //takes hash found on u.getPassword() and compares its unencrypted value with the password variable
        if(BCrypt.checkpw(password, u.getPassword())) {
            Map<String, String> response = new HashMap<>();

            //TODO change this to return the access token AND the refresh token instead of just the ACCESS token
            String authToken = Jwts.builder()
                                   .setSubject(u.getFirstName())
                                   .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60))) //2 hours
                                   .signWith(accessKey)
                                   .compact();

            String refreshToken = Jwts.builder()
                                      .setSubject(u.getFirstName())
                                      .signWith(refreshKey)
                                      .compact();
            u.setAuthToken(authToken);
            u.setRefreshToken(refreshToken);
            LOGGER.info("Setting auth token value auth[" + u.getAuthToken() + "] for user[" + email + "]");
            return u;
        }
        else {
            throw new IncorrectCredentialsException("Incorrect password");
        }
    }

    public Map<String, String> refreshToken(String authHeader) throws InvalidRefreshTokenException {
        Map<String, String> response = new HashMap<>();
        Jws<Claims> jws;
        String token = authHeader.split(" ")[1];
        //TODO decode token using this:
        try {
                jws = Jwts.parserBuilder()
                          .setSigningKey(refreshKey)
                          .build()
                          .parseClaimsJws(token);
                String name = jws.getBody().get("sub").toString();

                String newAuthToken = Jwts.builder()
                                          .setSubject(name)
                                          .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 2)))
                                          .signWith(accessKey)
                                          .compact();

                response.put("authToken", newAuthToken);

                return response;

        } catch(JwtException e) {
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }
    }

}
